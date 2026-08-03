package career_Navigator_parent.coding.service.impl;

import career_Navigator_parent.coding.dto.request.ExecutionRequests.Submit;
import career_Navigator_parent.coding.dto.response.CodingResponses.Submission;
import career_Navigator_parent.coding.dto.response.CodingResponses.TestResult;
import career_Navigator_parent.coding.entity.CodeSubmission;
import career_Navigator_parent.coding.entity.CodingProblem;
import career_Navigator_parent.coding.entity.ProblemAttempt;
import career_Navigator_parent.coding.entity.ProblemTestCase;
import career_Navigator_parent.coding.entity.SubmissionTestCaseResult;
import career_Navigator_parent.coding.enums.ProblemAttemptStatus;
import career_Navigator_parent.coding.enums.ProblemStatus;
import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import career_Navigator_parent.coding.enums.SubmissionStatus;
import career_Navigator_parent.coding.enums.TestCaseVisibility;
import career_Navigator_parent.coding.event.SubmissionQueuedEvent;
import career_Navigator_parent.coding.execution.client.PistonClient;
import career_Navigator_parent.coding.execution.dto.PistonModels.ExecuteRequest;
import career_Navigator_parent.coding.execution.dto.PistonModels.ExecuteResponse;
import career_Navigator_parent.coding.execution.dto.PistonModels.File;
import career_Navigator_parent.coding.execution.mapper.PistonResultMapper;
import career_Navigator_parent.coding.repository.CodeSubmissionRepository;
import career_Navigator_parent.coding.repository.CodingProblemRepository;
import career_Navigator_parent.coding.repository.ProblemAttemptRepository;
import career_Navigator_parent.coding.service.SubmissionJudgingService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionJudgingServiceImpl
        implements SubmissionJudgingService {

 private static final int MAX_SOURCE_CODE_LENGTH = 200_000;
 private static final int MAX_SUBMISSION_PAGE_SIZE = 100;
 private static final int MAX_ERROR_MESSAGE_LENGTH = 2_000;


    private static final long DEFAULT_COMPILE_TIMEOUT_MILLISECONDS =
            10_000L;
 private static final long DEFAULT_COMPILE_MEMORY_LIMIT_BYTES =
         512L * 1024L * 1024L;

 private final PistonClient pistonClient;
 private final PistonResultMapper pistonResultMapper;

 private final CodingProblemRepository codingProblemRepository;
 private final CodeSubmissionRepository codeSubmissionRepository;
 private final ProblemAttemptRepository problemAttemptRepository;

 private final StudentRepository studentRepository;
 private final SecurityUtils securityUtils;

 private final ApplicationEventPublisher eventPublisher;

 @Override
 public Submission submit(
         Submit request
 ) {
  validateSubmissionRequest(request);

  Student student =
          getCurrentStudent();

  CodingProblem problem =
          codingProblemRepository
                  .findByIdAndStatusAndActiveTrue(
                          request.problemId(),
                          ProblemStatus.PUBLISHED
                  )
                  .orElseThrow(() ->
                          new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Published coding problem not found."
                          )
                  );

  List<ProblemTestCase> activeTestCases =
          getActiveTestCases(problem);

  if (activeTestCases.isEmpty()) {
   throw new ResponseStatusException(
           HttpStatus.CONFLICT,
           "No active test cases are configured for this problem."
   );
  }

  CodeSubmission submission =
          CodeSubmission.builder()
                  .student(student)
                  .problem(problem)
                  .programmingLanguage(
                          request.language()
                  )
                  /*
                   * Do not strip source code.
                   * Leading whitespace is meaningful for Python.
                   */
                  .sourceCode(
                          request.sourceCode()
                  )
                  .status(
                          SubmissionStatus.QUEUED
                  )
                  .passedTestCases(0)
                  .totalTestCases(
                          activeTestCases.size()
                  )
                  .submittedAt(
                          LocalDateTime.now()
                  )
                  .build();

  CodeSubmission savedSubmission =
          codeSubmissionRepository
                  .saveAndFlush(submission);

  recordQueuedAttempt(
          student,
          problem
  );

  /*
   * Publish only after transaction commit so the asynchronous
   * listener can safely load the saved submission.
   */
  publishSubmissionQueuedAfterCommit(
          savedSubmission.getId()
  );

  return toResponse(savedSubmission);
 }

 @Override
 @Transactional(readOnly = true)
 public Submission get(
         Long submissionId
 ) {
  validateSubmissionId(submissionId);

  Student student =
          getCurrentStudent();

  CodeSubmission submission =
          codeSubmissionRepository
                  .findByIdAndStudentId(
                          submissionId,
                          student.getId()
                  )
                  .orElseThrow(() ->
                          new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Submission not found."
                          )
                  );

  return toResponse(submission);
 }

 @Override
 @Transactional(readOnly = true)
 public Page<Submission> history(
         Long problemId,
         Pageable pageable
 ) {
  Student student =
          getCurrentStudent();

  Pageable normalizedPageable =
          normalizeSubmissionPageable(pageable);

  Page<CodeSubmission> submissions;

  if (problemId == null) {
   submissions =
           codeSubmissionRepository
                   .findByStudentIdOrderBySubmittedAtDesc(
                           student.getId(),
                           normalizedPageable
                   );

  } else {
   if (problemId <= 0) {
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "A valid problem ID is required."
    );
   }

   submissions =
           codeSubmissionRepository
                   .findByStudentIdAndProblemIdOrderBySubmittedAtDesc(
                           student.getId(),
                           problemId,
                           normalizedPageable
                   );
  }

  return submissions.map(
          this::toResponse
  );
 }

 @Override
 public void judge(
         Long submissionId
 ) {
  validateSubmissionId(submissionId);

  /*
   * The repository method should use a pessimistic write lock.
   * This prevents multiple async workers from judging the same
   * submission simultaneously.
   */
  CodeSubmission submission =
          codeSubmissionRepository
                  .findForJudging(submissionId)
                  .orElseThrow(() ->
                          new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Submission not found."
                          )
                  );

  if (isTerminalStatus(submission.getStatus())) {
   return;
  }

  if (
          submission.getStatus() != SubmissionStatus.QUEUED
                  && submission.getStatus()
                  != SubmissionStatus.PROCESSING
  ) {
   return;
  }

  submission.setStatus(
          SubmissionStatus.PROCESSING
  );

  codeSubmissionRepository
          .saveAndFlush(submission);

  CodingProblem problem =
          submission.getProblem();

  Student student =
          submission.getStudent();

  List<ProblemTestCase> testCases =
          getActiveTestCases(problem);

  if (testCases.isEmpty()) {
   completeAsFailed(
           submission,
           "No active test cases are configured."
   );

   return;
  }

  prepareResultsCollection(submission);

  int passedTestCases = 0;
  double awardedScore = 0.0;
  double maximumExecutionTime = 0.0;
  long maximumMemoryUsed = 0L;

  SubmissionStatus finalStatus =
          SubmissionStatus.ACCEPTED;

  for (ProblemTestCase testCase : testCases) {
   ExecuteRequest pistonRequest =
           buildPistonRequest(
                   submission,
                   problem,
                   testCase
           );

   ExecuteResponse pistonResponse;

   try {
    pistonResponse =
            pistonClient.execute(
                    pistonRequest
            );

   } catch (Exception exception) {
    completeAsFailed(
            submission,
            normalizeErrorMessage(
                    resolveExceptionMessage(
                            exception
                    )
            )
    );

    return;
   }

   SubmissionStatus providerStatus =
           pistonResultMapper.status(
                   pistonResponse
           );

   String actualOutput =
           normalizeOutput(
                   pistonResultMapper.stdout(
                           pistonResponse
                   )
           );

   String expectedOutput =
           normalizeOutput(
                   testCase.getExpectedOutput()
           );

   SubmissionStatus testStatus =
           resolveTestStatus(
                   providerStatus,
                   actualOutput,
                   expectedOutput
           );

   boolean passed =
           testStatus == SubmissionStatus.ACCEPTED;

   double testCaseScore =
           resolveScoreWeight(testCase);

   double scoreAwarded =
           passed
                   ? testCaseScore
                   : 0.0;

   if (passed) {
    passedTestCases++;
    awardedScore += scoreAwarded;

   } else if (
           finalStatus
                   == SubmissionStatus.ACCEPTED
   ) {
    finalStatus = testStatus;
   }

   Double executionTime =
           pistonResultMapper.timeSeconds(
                   pistonResponse
           );

   Long memoryUsed =
           pistonResultMapper.memoryKilobytes(
                   pistonResponse
           );

   if (
           executionTime != null
                   && executionTime >= 0
   ) {
    maximumExecutionTime =
            Math.max(
                    maximumExecutionTime,
                    executionTime
            );
   }

   if (
           memoryUsed != null
                   && memoryUsed >= 0
   ) {
    maximumMemoryUsed =
            Math.max(
                    maximumMemoryUsed,
                    memoryUsed
            );
   }

   SubmissionTestCaseResult testCaseResult =
           SubmissionTestCaseResult.builder()
                   .submission(submission)
                   .testCase(testCase)
                   .status(testStatus)
                   .passed(passed)
                   .actualOutput(actualOutput)
                   .expectedOutput(
                           testCase.getExpectedOutput()
                   )
                   .standardError(
                           normalizeOutput(
                                   pistonResultMapper.stderr(
                                           pistonResponse
                                   )
                           )
                   )
                   .compilerOutput(
                           normalizeOutput(
                                   pistonResultMapper.compilerOutput(
                                           pistonResponse
                                   )
                           )
                   )
                   .executionTimeSeconds(
                           executionTime
                   )
                   .memoryUsedKilobytes(
                           memoryUsed
                   )
                   .scoreAwarded(
                           scoreAwarded
                   )
                   .build();

   submission
           .getTestCaseResults()
           .add(testCaseResult);

   if (
           testStatus
                   == SubmissionStatus.COMPILATION_ERROR
   ) {
    submission.setCompilerOutput(
            normalizeOutput(
                    pistonResultMapper.compilerOutput(
                            pistonResponse
                    )
            )
    );

    break;
   }

   if (
           testStatus
                   == SubmissionStatus.RUNTIME_ERROR
                   || testStatus
                   == SubmissionStatus.TIME_LIMIT_EXCEEDED
                   || testStatus
                   == SubmissionStatus.MEMORY_LIMIT_EXCEEDED
   ) {
    submission.setRuntimeError(
            normalizeOutput(
                    firstNonBlank(
                            pistonResultMapper.stderr(
                                    pistonResponse
                            ),
                            pistonResultMapper.message(
                                    pistonResponse
                            )
                    )
            )
    );
   }

   if (
           testStatus
                   == SubmissionStatus.INTERNAL_ERROR
   ) {
    submission.setRuntimeError(
            normalizeOutput(
                    pistonResultMapper.message(
                            pistonResponse
                    )
            )
    );

    break;
   }
  }

  SubmissionStatus completedStatus =
          passedTestCases == testCases.size()
                  ? SubmissionStatus.ACCEPTED
                  : normalizeTerminalStatus(
                  finalStatus
          );

  submission.setPassedTestCases(
          passedTestCases
  );

  submission.setTotalTestCases(
          testCases.size()
  );

  submission.setStatus(
          completedStatus
  );

  submission.setExecutionTimeSeconds(
          maximumExecutionTime
  );

  submission.setMemoryUsedKilobytes(
          maximumMemoryUsed
  );

  submission.setJudgedAt(
          LocalDateTime.now()
  );

  CodeSubmission savedSubmission =
          codeSubmissionRepository
                  .saveAndFlush(submission);

  updateProblemStatistics(
          problem,
          completedStatus
  );

  updateAttempt(
          student,
          problem,
          savedSubmission,
          awardedScore
  );
 }

 @Override
 public void markAsFailed(
         Long submissionId,
         String errorMessage
 ) {
  validateSubmissionId(submissionId);

  CodeSubmission submission =
          codeSubmissionRepository
                  .findForJudging(submissionId)
                  .orElseThrow(() ->
                          new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Submission not found."
                          )
                  );

  if (isTerminalStatus(submission.getStatus())) {
   return;
  }

  completeAsFailed(
          submission,
          errorMessage
  );
 }

 private ExecuteRequest buildPistonRequest(
         CodeSubmission submission,
         CodingProblem problem,
         ProblemTestCase testCase
 ) {
  ProgrammingLanguage language =
          submission.getProgrammingLanguage();

  if (
          language == null
                  || !language.isPistonConfigured()
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Piston is not configured for the submission language."
   );
  }

  long runTimeoutMilliseconds =
          resolveRunTimeoutMilliseconds(
                  problem,
                  testCase
          );

  long runMemoryLimitBytes =
          resolveRunMemoryLimitBytes(
                  problem,
                  testCase
          );

  return new ExecuteRequest(
          language.getPistonLanguage(),
          language.getPistonVersion(),
          List.of(
                  new File(
                          language.getPistonSourceFileName(),
                          submission.getSourceCode()
                  )
          ),
          testCase.getInput() == null
                  ? ""
                  : testCase.getInput(),
          List.of(),
          DEFAULT_COMPILE_TIMEOUT_MILLISECONDS,
          runTimeoutMilliseconds,
          DEFAULT_COMPILE_MEMORY_LIMIT_BYTES,
          runMemoryLimitBytes
  );
 }

 private long resolveRunTimeoutMilliseconds(
         CodingProblem problem,
         ProblemTestCase testCase
 ) {
  Double customTimeLimitSeconds =
          testCase.getCustomTimeLimitSeconds();

  if (
          customTimeLimitSeconds != null
                  && customTimeLimitSeconds > 0
  ) {
   return Math.max(
           1L,
           Math.round(
                   customTimeLimitSeconds * 1_000.0
           )
   );
  }

  Integer problemTimeLimitMilliseconds =
          problem.getTimeLimitMilliseconds();

  if (
          problemTimeLimitMilliseconds == null
                  || problemTimeLimitMilliseconds <= 0
  ) {
   throw new ResponseStatusException(
           HttpStatus.CONFLICT,
           "The problem has an invalid time limit."
   );
  }

  return problemTimeLimitMilliseconds.longValue();
 }

 private long resolveRunMemoryLimitBytes(
         CodingProblem problem,
         ProblemTestCase testCase
 ) {
  Integer memoryLimitMegabytes =
          testCase.getCustomMemoryLimitMegabytes() != null
                  ? testCase.getCustomMemoryLimitMegabytes()
                  : problem.getMemoryLimitMegabytes();

  if (
          memoryLimitMegabytes == null
                  || memoryLimitMegabytes <= 0
  ) {
   throw new ResponseStatusException(
           HttpStatus.CONFLICT,
           "The problem has an invalid memory limit."
   );
  }

  try {
   return Math.multiplyExact(
           memoryLimitMegabytes.longValue(),
           1024L * 1024L
   );

  } catch (ArithmeticException exception) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Configured memory limit is too large.",
           exception
   );
  }
 }

 private SubmissionStatus resolveTestStatus(
         SubmissionStatus providerStatus,
         String actualOutput,
         String expectedOutput
 ) {
  if (
          providerStatus
                  != SubmissionStatus.ACCEPTED
  ) {
   return providerStatus == null
           ? SubmissionStatus.INTERNAL_ERROR
           : providerStatus;
  }

  return outputsMatch(
          actualOutput,
          expectedOutput
  )
          ? SubmissionStatus.ACCEPTED
          : SubmissionStatus.WRONG_ANSWER;
 }

 private boolean outputsMatch(
         String actualOutput,
         String expectedOutput
 ) {
  return normalizeForComparison(actualOutput)
          .equals(
                  normalizeForComparison(
                          expectedOutput
                  )
          );
 }

 private String normalizeForComparison(
         String output
 ) {
  if (output == null) {
   return "";
  }

  return output
          .replace("\r\n", "\n")
          .replace('\r', '\n')
          .stripTrailing();
 }

 private void completeAsFailed(
         CodeSubmission submission,
         String errorMessage
 ) {
  submission.setStatus(
          SubmissionStatus.INTERNAL_ERROR
  );

  submission.setRuntimeError(
          normalizeErrorMessage(errorMessage)
  );

  submission.setJudgedAt(
          LocalDateTime.now()
  );

  CodeSubmission savedSubmission =
          codeSubmissionRepository
                  .saveAndFlush(submission);

  updateProblemStatistics(
          savedSubmission.getProblem(),
          SubmissionStatus.INTERNAL_ERROR
  );

  updateAttempt(
          savedSubmission.getStudent(),
          savedSubmission.getProblem(),
          savedSubmission,
          0.0
  );
 }

 private void recordQueuedAttempt(
         Student student,
         CodingProblem problem
 ) {
  ProblemAttempt attempt =
          problemAttemptRepository
                  .findForUpdate(
                          student.getId(),
                          problem.getId()
                  )
                  .orElseGet(() ->
                          ProblemAttempt.builder()
                                  .student(student)
                                  .problem(problem)
                                  .status(
                                          ProblemAttemptStatus.ATTEMPTED
                                  )
                                  .attemptCount(0)
                                  .acceptedSubmissionCount(0)
                                  .firstAttemptedAt(
                                          LocalDateTime.now()
                                  )
                                  .lastAttemptedAt(
                                          LocalDateTime.now()
                                  )
                                  .build()
                  );

  attempt.recordQueuedSubmission();

  try {
   problemAttemptRepository
           .saveAndFlush(attempt);

  } catch (DataIntegrityViolationException exception) {
   ProblemAttempt existingAttempt =
           problemAttemptRepository
                   .findForUpdate(
                           student.getId(),
                           problem.getId()
                   )
                   .orElseThrow(() -> exception);

   existingAttempt.recordQueuedSubmission();

   problemAttemptRepository
           .save(existingAttempt);
  }
 }

 private void updateAttempt(
         Student student,
         CodingProblem problem,
         CodeSubmission submission,
         double awardedScore
 ) {
  LocalDateTime now =
          LocalDateTime.now();

  ProblemAttempt attempt =
          problemAttemptRepository
                  .findForUpdate(
                          student.getId(),
                          problem.getId()
                  )
                  .orElseGet(() ->
                          ProblemAttempt.builder()
                                  .student(student)
                                  .problem(problem)
                                  .status(
                                          ProblemAttemptStatus.ATTEMPTED
                                  )
                                  .attemptCount(0)
                                  .acceptedSubmissionCount(0)
                                  .firstAttemptedAt(now)
                                  .lastAttemptedAt(now)
                                  .build()
                  );

  attempt.recordCompletedAttempt();

  if (
          submission.getStatus()
                  == SubmissionStatus.ACCEPTED
  ) {
   Integer score =
           calculateSubmissionScore(
                   submission,
                   awardedScore
           );

   Long runtimeMilliseconds =
           convertSecondsToMilliseconds(
                   submission.getExecutionTimeSeconds()
           );

   attempt.recordAcceptedSubmission(
           submission,
           score,
           runtimeMilliseconds,
           submission.getMemoryUsedKilobytes()
   );

  } else {
   attempt.preserveSolvedStatusOrMarkAttempted();
  }

  problemAttemptRepository.save(attempt);
 }

 private List<ProblemTestCase> getActiveTestCases(
         CodingProblem problem
 ) {
  if (
          problem == null
                  || problem.getTestCases() == null
  ) {
   return List.of();
  }

  return problem
          .getTestCases()
          .stream()
          .filter(testCase ->
                  Boolean.TRUE.equals(
                          testCase.getActive()
                  )
          )
          .sorted(
                  Comparator.comparing(
                          ProblemTestCase::getDisplayOrder,
                          Comparator.nullsLast(
                                  Comparator.naturalOrder()
                          )
                  )
          )
          .toList();
 }

 private void prepareResultsCollection(
         CodeSubmission submission
 ) {
  if (submission.getTestCaseResults() == null) {
   throw new IllegalStateException(
           "CodeSubmission.testCaseResults must be initialized."
   );
  }

  submission
          .getTestCaseResults()
          .clear();

  submission.setCompilerOutput(null);
  submission.setRuntimeError(null);
  submission.setStandardOutput(null);
 }

 private void validateSubmissionRequest(
         Submit request
 ) {
  if (request == null) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Submission request is required."
   );
  }

  if (
          request.problemId() == null
                  || request.problemId() <= 0
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "A valid problem ID is required."
   );
  }

  if (request.language() == null) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Programming language is required."
   );
  }

  if (!request.language().isPistonConfigured()) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Piston is not configured for the selected language."
   );
  }

  if (
          request.sourceCode() == null
                  || request.sourceCode().isBlank()
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Source code is required."
   );
  }

  if (
          request.sourceCode().length()
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

 private void validateSubmissionId(
         Long submissionId
 ) {
  if (
          submissionId == null
                  || submissionId <= 0
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "A valid submission ID is required."
   );
  }
 }

 private double resolveScoreWeight(
         ProblemTestCase testCase
 ) {
  Double scoreWeight =
          testCase.getScoreWeight();

  if (
          scoreWeight == null
                  || scoreWeight < 0
  ) {
   return 0.0;
  }

  return scoreWeight;
 }

 private void updateProblemStatistics(
         CodingProblem problem,
         SubmissionStatus submissionStatus
 ) {
  long totalSubmissions =
          problem.getTotalSubmissions() == null
                  ? 0L
                  : problem.getTotalSubmissions();

  try {
   problem.setTotalSubmissions(
           Math.addExact(
                   totalSubmissions,
                   1L
           )
   );

   if (
           submissionStatus
                   == SubmissionStatus.ACCEPTED
   ) {
    long acceptedSubmissions =
            problem.getAcceptedSubmissions() == null
                    ? 0L
                    : problem.getAcceptedSubmissions();

    problem.setAcceptedSubmissions(
            Math.addExact(
                    acceptedSubmissions,
                    1L
            )
    );
   }

  } catch (ArithmeticException exception) {
   throw new ResponseStatusException(
           HttpStatus.INTERNAL_SERVER_ERROR,
           "Coding problem submission statistics overflowed.",
           exception
   );
  }

  codingProblemRepository.save(problem);
 }

 private Integer calculateSubmissionScore(
         CodeSubmission submission,
         double awardedScore
 ) {
  if (awardedScore > 0) {
   return (int) Math.min(
           100,
           Math.round(awardedScore)
   );
  }

  if (
          submission.getTotalTestCases() == null
                  || submission.getTotalTestCases() <= 0
  ) {
   return submission.getStatus()
           == SubmissionStatus.ACCEPTED
           ? 100
           : 0;
  }

  int passedTestCases =
          submission.getPassedTestCases() == null
                  ? 0
                  : submission.getPassedTestCases();

  double score =
          passedTestCases * 100.0
                  / submission.getTotalTestCases();

  return (int) Math.round(score);
 }

 private Long convertSecondsToMilliseconds(
         Double executionTimeSeconds
 ) {
  if (
          executionTimeSeconds == null
                  || executionTimeSeconds < 0
  ) {
   return null;
  }

  double milliseconds =
          executionTimeSeconds * 1_000.0;

  if (milliseconds > Long.MAX_VALUE) {
   return Long.MAX_VALUE;
  }

  return Math.round(milliseconds);
 }

 private Pageable normalizeSubmissionPageable(
         Pageable pageable
 ) {
  if (pageable == null) {
   return PageRequest.of(
           0,
           20
   );
  }

  int pageNumber =
          Math.max(
                  pageable.getPageNumber(),
                  0
          );

  int pageSize =
          Math.min(
                  Math.max(
                          pageable.getPageSize(),
                          1
                  ),
                  MAX_SUBMISSION_PAGE_SIZE
          );

  return PageRequest.of(
          pageNumber,
          pageSize
  );
 }

 private boolean isTerminalStatus(
         SubmissionStatus status
 ) {
  if (status == null) {
   return false;
  }

  return status != SubmissionStatus.QUEUED
          && status != SubmissionStatus.PROCESSING;
 }

 private SubmissionStatus normalizeTerminalStatus(
         SubmissionStatus status
 ) {
  if (
          status == null
                  || status == SubmissionStatus.QUEUED
                  || status == SubmissionStatus.PROCESSING
  ) {
   return SubmissionStatus.INTERNAL_ERROR;
  }

  return status;
 }

 private void publishSubmissionQueuedAfterCommit(
         Long submissionId
 ) {
  if (
          TransactionSynchronizationManager
                  .isSynchronizationActive()
  ) {
   TransactionSynchronizationManager
           .registerSynchronization(
                   new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                     eventPublisher.publishEvent(
                             new SubmissionQueuedEvent(
                                     submissionId
                             )
                     );
                    }
                   }
           );

   return;
  }

  eventPublisher.publishEvent(
          new SubmissionQueuedEvent(
                  submissionId
          )
  );
 }

 private Submission toResponse(CodeSubmission submission) {
  List<TestResult> testResults;

  if (submission.getTestCaseResults() == null) {
   testResults = List.of();

  } else {
   testResults =
           submission
                   .getTestCaseResults()
                   .stream()
                   .sorted(
                           Comparator.comparing(
                                   result ->
                                           result
                                                   .getTestCase()
                                                   .getDisplayOrder(),
                                   Comparator.nullsLast(
                                           Comparator.naturalOrder()
                                   )
                           )
                   )
                   .map(this::toTestResult)
                   .toList();
  }

     return new Submission(
             submission.getId(),
             submission.getProblem().getId(),
             submission.getProgrammingLanguage(),
             submission.getStatus(),
             submission.getPassedTestCases(),
             submission.getTotalTestCases(),
             submission.getExecutionTimeSeconds(),
             submission.getMemoryUsedKilobytes(),
             submission.getCompilerOutput(),
             submission.getRuntimeError(),
             isTerminalStatus(submission.getStatus()),
             submission.getSubmittedAt(),
             submission.getJudgedAt(),
             testResults
     );
 }

 private TestResult toTestResult(
         SubmissionTestCaseResult result
 ) {
  ProblemTestCase testCase =
          result.getTestCase();

  boolean sampleTestCase =
          testCase.getVisibility()
                  == TestCaseVisibility.SAMPLE;

  int displayNumber =
          testCase.getDisplayOrder() == null
                  ? 1
                  : testCase.getDisplayOrder();

  return new TestResult(
          displayNumber,
          result.getStatus(),
          Boolean.TRUE.equals(
                  result.getPassed()
          ),
          sampleTestCase
                  ? testCase.getInput()
                  : null,
          sampleTestCase
                  ? result.getExpectedOutput()
                  : null,
          sampleTestCase
                  ? result.getActualOutput()
                  : null,
          result.getExecutionTimeSeconds(),
          result.getMemoryUsedKilobytes()
  );
 }

 private Student getCurrentStudent() {
  Long currentUserId =
          securityUtils.getCurrentUserId();

  return studentRepository
          .findByUserId(currentUserId)
          .orElseThrow(() ->
                  new ResponseStatusException(
                          HttpStatus.NOT_FOUND,
                          "Student profile not found."
                  )
          );
 }

 private String normalizeOutput(
         String output
 ) {
  if (output == null) {
   return null;
  }

  String normalized =
          output
                  .replace("\r\n", "\n")
                  .replace('\r', '\n')
                  .stripTrailing();

  return normalized.isEmpty()
          ? null
          : normalized;
 }

 private String normalizeErrorMessage(
         String errorMessage
 ) {
  if (
          errorMessage == null
                  || errorMessage.isBlank()
  ) {
   return "Submission evaluation failed due to an internal error.";
  }

  String normalized =
          errorMessage.strip();

  return normalized.length()
          <= MAX_ERROR_MESSAGE_LENGTH
          ? normalized
          : normalized.substring(
          0,
          MAX_ERROR_MESSAGE_LENGTH
  );
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

 private String firstNonBlank(
         String... values
 ) {
  if (values == null) {
   return null;
  }

  for (String value : values) {
   if (
           value != null
                   && !value.isBlank()
   ) {
    return value;
   }
  }

  return null;
 }
}