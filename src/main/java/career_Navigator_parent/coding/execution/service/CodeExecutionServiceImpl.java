package career_Navigator_parent.coding.execution.service;

import career_Navigator_parent.coding.dto.request.ExecutionRequests.ProblemRun;
import career_Navigator_parent.coding.dto.request.ExecutionRequests.Run;
import career_Navigator_parent.coding.dto.response.CodingResponses.Execution;
import career_Navigator_parent.coding.entity.CodingProblem;
import career_Navigator_parent.coding.enums.ProblemStatus;
import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import career_Navigator_parent.coding.execution.client.Judge0Client;
import career_Navigator_parent.coding.execution.dto.Judge0Models.Request;
import career_Navigator_parent.coding.execution.dto.Judge0Models.Result;
import career_Navigator_parent.coding.execution.mapper.Judge0ResultMapper;
import career_Navigator_parent.coding.repository.CodingProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeExecutionServiceImpl
        implements CodeExecutionService {

 private static final int MAX_SOURCE_CODE_LENGTH = 100_000;
 private static final int MAX_CUSTOM_INPUT_LENGTH = 50_000;

 private static final double DEFAULT_TIME_LIMIT_SECONDS = 2.0;
 private static final int DEFAULT_MEMORY_LIMIT_MEGABYTES = 256;

 private final CodingProblemRepository codingProblemRepository;
 private final Judge0Client judge0Client;
 private final Judge0ResultMapper judge0ResultMapper;

 /**
  * Canonical execution endpoint:
  *
  * POST /api/student/coding/executions/run
  *
  * problemId is supplied inside the request body.
  */
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

 /**
  * Problem-scoped execution endpoint:
  *
  * POST /api/student/coding/problems/{problemId}/run
  *
  * problemId is supplied as a path variable.
  */
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

 /**
  * Shared execution flow used by both run endpoints.
  */
 private Execution execute(
         Long problemId,
         ProgrammingLanguage language,
         String sourceCode,
         String customInput
 ) {
  CodingProblem problem =
          getPublishedProblem(problemId);

  validateLanguageConfiguration(language);

  Request judgeRequest =
          buildJudgeRequest(
                  problem,
                  language,
                  sourceCode,
                  customInput
          );

  Result judgeResult =
          executeWithJudge0(judgeRequest);

  return mapExecutionResult(judgeResult);
 }

 /**
  * Calls Judge0 and converts provider/network failures into meaningful
  * gateway responses.
  */
 private Result executeWithJudge0(
         Request judgeRequest
 ) {
  try {
   Result judgeResult =
           judge0Client.execute(judgeRequest);

   if (judgeResult == null) {
    throw new ResponseStatusException(
            HttpStatus.BAD_GATEWAY,
            "Code execution provider returned an empty response."
    );
   }

   return judgeResult;

  } catch (ResponseStatusException exception) {
   /*
    * Preserve the original status and message returned by
    * Judge0Client.
    */
   throw exception;

  } catch (Exception exception) {
   String providerMessage =
           resolveExceptionMessage(exception);

   throw new ResponseStatusException(
           HttpStatus.BAD_GATEWAY,
           "Judge0 execution failed: "
                   + providerMessage,
           exception
   );
  }
 }

 /**
  * Only published and active problems can be executed by students.
  */
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

 /**
  * Creates the Judge0 execution request.
  *
  * custom runs do not supply expectedOutput because they are not judged
  * against hidden test cases.
  */
 private Request buildJudgeRequest(
         CodingProblem problem,
         ProgrammingLanguage language,
         String sourceCode,
         String customInput
 ) {
  double timeLimitSeconds =
          resolveTimeLimitSeconds(problem);

  int memoryLimitKilobytes =
          resolveMemoryLimitKilobytes(problem);

  return new Request(
          normalizeSourceCode(sourceCode),
          language.getJudge0LanguageId(),
          normalizeCustomInput(customInput),
          null,
          timeLimitSeconds,
          memoryLimitKilobytes
  );
 }

 /**
  * Maps the raw Judge0 response into the application response.
  */
 private Execution mapExecutionResult(
         Result result
 ) {
  try {
   return new Execution(
           judge0ResultMapper.status(result),
           result.stdout(),
           result.stderr(),
           result.compileOutput(),
           judge0ResultMapper.time(result),
           judge0ResultMapper.memory(result),
           result.message()
   );

  } catch (Exception exception) {
   throw new ResponseStatusException(
           HttpStatus.BAD_GATEWAY,
           "Unable to process the Judge0 execution response.",
           exception
   );
  }
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
  validateLanguageConfiguration(language);
  validateSourceCode(sourceCode);
  validateCustomInput(customInput);
 }

 private void validateProblemId(
         Long problemId
 ) {
  if (
          problemId == null
                  || problemId <= 0
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "A valid problem ID is required."
   );
  }
 }

 private void validateSourceCode(
         String sourceCode
 ) {
  if (
          sourceCode == null
                  || sourceCode.isBlank()
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Source code is required."
   );
  }

  if (
          sourceCode.length()
                  > MAX_SOURCE_CODE_LENGTH
  ) {
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

 private void validateLanguageConfiguration(
         ProgrammingLanguage language
 ) {
  if (language == null) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Programming language is required."
   );
  }

  Integer judge0LanguageId =
          language.getJudge0LanguageId();

  if (
          judge0LanguageId == null
                  || judge0LanguageId <= 0
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Judge0 is not configured for the selected language: "
                   + language
                   + "."
   );
  }
 }

 /**
  * Converts the problem time limit from milliseconds to seconds,
  * because Judge0 expects seconds.
  */
 private double resolveTimeLimitSeconds(
         CodingProblem problem
 ) {
  Integer timeLimitMilliseconds =
          problem.getTimeLimitMilliseconds();

  if (
          timeLimitMilliseconds == null
                  || timeLimitMilliseconds <= 0
  ) {
   return DEFAULT_TIME_LIMIT_SECONDS;
  }

  double timeLimitSeconds =
          timeLimitMilliseconds / 1000.0;

  /*
   * Prevent sub-millisecond configuration from becoming 0.
   */
  return Math.max(
          0.001,
          timeLimitSeconds
  );
 }

 /**
  * Converts the problem memory limit from MB to KB,
  * because Judge0 expects kilobytes.
  */
 private int resolveMemoryLimitKilobytes(
         CodingProblem problem
 ) {
  Integer memoryLimitMegabytes =
          problem.getMemoryLimitMegabytes();

  int resolvedMemoryMegabytes =
          memoryLimitMegabytes == null
                  || memoryLimitMegabytes <= 0
                  ? DEFAULT_MEMORY_LIMIT_MEGABYTES
                  : memoryLimitMegabytes;

  try {
   return Math.multiplyExact(
           resolvedMemoryMegabytes,
           1024
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
   * Do not use stripIndent() because indentation can be meaningful
   * for Python and other indentation-sensitive languages.
   */
  return sourceCode.trim();
 }

 private String normalizeCustomInput(
         String customInput
 ) {
  if (customInput == null) {
   return "";
  }

  /*
   * Preserve whitespace and line breaks because custom input is
   * consumed exactly by the submitted program.
   */
  return customInput;
 }

 private String resolveExceptionMessage(
         Exception exception
 ) {
  String message =
          exception.getMessage();

  if (
          message != null
                  && !message.isBlank()
  ) {
   return message;
  }

  Throwable cause =
          exception.getCause();

  if (
          cause != null
                  && cause.getMessage() != null
                  && !cause.getMessage().isBlank()
  ) {
   return cause.getMessage();
  }

  return exception
          .getClass()
          .getSimpleName();
 }
}