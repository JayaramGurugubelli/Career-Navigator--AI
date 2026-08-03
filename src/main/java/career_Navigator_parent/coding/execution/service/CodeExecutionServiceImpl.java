package career_Navigator_parent.coding.execution.service;

import career_Navigator_parent.coding.dto.request.ExecutionRequests.ProblemRun;
import career_Navigator_parent.coding.dto.request.ExecutionRequests.Run;
import career_Navigator_parent.coding.dto.response.CodingResponses.Execution;
import career_Navigator_parent.coding.entity.CodingProblem;
import career_Navigator_parent.coding.enums.ProblemStatus;
import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import career_Navigator_parent.coding.execution.client.PistonClient;
import career_Navigator_parent.coding.execution.dto.PistonModels.ExecuteRequest;
import career_Navigator_parent.coding.execution.dto.PistonModels.ExecuteResponse;
import career_Navigator_parent.coding.execution.dto.PistonModels.File;
import career_Navigator_parent.coding.execution.mapper.PistonResultMapper;
import career_Navigator_parent.coding.repository.CodingProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeExecutionServiceImpl
        implements CodeExecutionService {

 private static final int MAX_SOURCE_CODE_LENGTH = 100_000;
 private static final int MAX_CUSTOM_INPUT_LENGTH = 50_000;

 private static final int DEFAULT_TIME_LIMIT_MILLISECONDS = 2_000;
 private static final int DEFAULT_MEMORY_LIMIT_MEGABYTES = 256;



 private static final long DEFAULT_COMPILE_MEMORY_LIMIT_BYTES =
         512L * 1024L * 1024L;
 private static final long DEFAULT_COMPILE_TIMEOUT_MILLISECONDS =
         10_000L;
 private final CodingProblemRepository codingProblemRepository;
 private final PistonClient pistonClient;
 private final PistonResultMapper pistonResultMapper;

 @Override
 public Execution run(
         Run request
 ) {
  validateRunRequest(request);

  return execute(
          request.problemId(),
          request.language(),
          request.sourceCode(),
          request.customInput()
  );
 }

 @Override
 public Execution run(
         Long problemId,
         ProblemRun request
 ) {
  validateProblemRunRequest(
          problemId,
          request
  );

  return execute(
          problemId,
          request.language(),
          request.sourceCode(),
          request.customInput()
  );
 }

 private Execution execute(
         Long problemId,
         ProgrammingLanguage language,
         String sourceCode,
         String customInput
 ) {
  CodingProblem problem =
          getPublishedProblem(problemId);

  ExecuteRequest pistonRequest =
          buildPistonRequest(
                  problem,
                  language,
                  sourceCode,
                  customInput
          );

  ExecuteResponse pistonResponse;

  try {
   pistonResponse =
           pistonClient.execute(pistonRequest);

  } catch (ResponseStatusException exception) {
   throw exception;

  } catch (Exception exception) {
   throw new ResponseStatusException(
           HttpStatus.BAD_GATEWAY,
           "Piston execution failed: "
                   + resolveExceptionMessage(exception),
           exception
   );
  }

  return mapExecutionResult(pistonResponse);
 }

 private CodingProblem getPublishedProblem(
         Long problemId
 ) {
  validateProblemId(problemId);

  return codingProblemRepository
          .findByIdAndStatusAndActiveTrue(
                  problemId,
                  ProblemStatus.PUBLISHED
          )
          .orElseThrow(() ->
                  new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Published coding problem not found."
                  )
          );
 }

 private ExecuteRequest buildPistonRequest(
         CodingProblem problem,
         ProgrammingLanguage language,
         String sourceCode,
         String customInput
 ) {
  long runTimeoutMilliseconds =
          resolveTimeLimitMilliseconds(problem);

  long runMemoryLimitBytes =
          resolveMemoryLimitBytes(problem);

  return new ExecuteRequest(
          language.getPistonLanguage(),
          language.getPistonVersion(),
          List.of(
                  new File(
                          language.getPistonSourceFileName(),
                          normalizeSourceCode(sourceCode)
                  )
          ),
          normalizeCustomInput(customInput),
          List.of(),
          DEFAULT_COMPILE_TIMEOUT_MILLISECONDS,
          runTimeoutMilliseconds,
          DEFAULT_COMPILE_MEMORY_LIMIT_BYTES,
          runMemoryLimitBytes
  );
 }

 private Execution mapExecutionResult(
         ExecuteResponse response
 ) {
  return new Execution(
          pistonResultMapper.status(response),
          pistonResultMapper.stdout(response),
          pistonResultMapper.stderr(response),
          pistonResultMapper.compilerOutput(response),
          pistonResultMapper.timeSeconds(response),
          pistonResultMapper.memoryKilobytes(response),
          pistonResultMapper.message(response)
  );
 }

 private void validateRunRequest(
         Run request
 ) {
  if (request == null) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Execution request is required."
   );
  }

  validateCommonRequest(
          request.problemId(),
          request.language(),
          request.sourceCode(),
          request.customInput()
  );
 }

 private void validateProblemRunRequest(
         Long problemId,
         ProblemRun request
 ) {
  if (request == null) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Execution request is required."
   );
  }

  validateCommonRequest(
          problemId,
          request.language(),
          request.sourceCode(),
          request.customInput()
  );
 }

 private void validateCommonRequest(
         Long problemId,
         ProgrammingLanguage language,
         String sourceCode,
         String customInput
 ) {
  validateProblemId(problemId);
  validateLanguage(language);
  validateSourceCode(sourceCode);
  validateCustomInput(customInput);
 }

 private void validateProblemId(
         Long problemId
 ) {
  if (problemId == null || problemId <= 0) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "A valid problem ID is required."
   );
  }
 }

 private void validateLanguage(
         ProgrammingLanguage language
 ) {
  if (language == null) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Programming language is required."
   );
  }

  if (!language.isPistonConfigured()) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Piston is not configured for language: "
                   + language
                   + "."
   );
  }
 }

 private void validateSourceCode(
         String sourceCode
 ) {
  if (sourceCode == null || sourceCode.isBlank()) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Source code is required."
   );
  }

  if (sourceCode.length() > MAX_SOURCE_CODE_LENGTH) {
   throw new ResponseStatusException(
           HttpStatus.PAYLOAD_TOO_LARGE,
           "Source code cannot exceed "
                   + MAX_SOURCE_CODE_LENGTH
                   + " characters."
   );
  }
 }

 private void validateCustomInput(
         String customInput
 ) {
  if (
          customInput != null
                  && customInput.length()
                  > MAX_CUSTOM_INPUT_LENGTH
  ) {
   throw new ResponseStatusException(
           HttpStatus.PAYLOAD_TOO_LARGE,
           "Custom input cannot exceed "
                   + MAX_CUSTOM_INPUT_LENGTH
                   + " characters."
   );
  }
 }

 private long resolveTimeLimitMilliseconds(
         CodingProblem problem
 ) {
  Integer configured =
          problem.getTimeLimitMilliseconds();

  if (configured == null || configured <= 0) {
   return DEFAULT_TIME_LIMIT_MILLISECONDS;
  }

  return configured.longValue();
 }

 private long resolveMemoryLimitBytes(
         CodingProblem problem
 ) {
  Integer configured =
          problem.getMemoryLimitMegabytes();

  int megabytes =
          configured == null || configured <= 0
                  ? DEFAULT_MEMORY_LIMIT_MEGABYTES
                  : configured;

  try {
   return Math.multiplyExact(
           megabytes,
           1024L * 1024L
   );

  } catch (ArithmeticException exception) {
   throw new ResponseStatusException(
           HttpStatus.INTERNAL_SERVER_ERROR,
           "Configured memory limit is invalid.",
           exception
   );
  }
 }

 private String normalizeSourceCode(
         String sourceCode
 ) {
  /*
   * Do not call trim(), strip() or stripIndent().
   * Leading whitespace is meaningful in Python.
   */
  return sourceCode;
 }

 private String normalizeCustomInput(
         String customInput
 ) {
  return customInput == null
          ? ""
          : customInput;
 }

 private String resolveExceptionMessage(
         Exception exception
 ) {
  if (
          exception.getMessage() != null
                  && !exception.getMessage().isBlank()
  ) {
   return exception.getMessage();
  }

  return exception.getClass().getSimpleName();
 }
}