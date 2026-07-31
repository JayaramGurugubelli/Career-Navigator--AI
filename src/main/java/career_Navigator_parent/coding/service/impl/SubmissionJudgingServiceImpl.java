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
import career_Navigator_parent.coding.enums.SubmissionStatus;
import career_Navigator_parent.coding.enums.TestCaseVisibility;
import career_Navigator_parent.coding.event.SubmissionQueuedEvent;
import career_Navigator_parent.coding.execution.client.Judge0Client;
import career_Navigator_parent.coding.execution.dto.Judge0Models.Request;
import career_Navigator_parent.coding.execution.dto.Judge0Models.Result;
import career_Navigator_parent.coding.execution.mapper.Judge0ResultMapper;
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

 private static final int MAX_SOURCE_CODE_LENGTH =
         200_000;

 private static final int MAX_SUBMISSION_PAGE_SIZE =
         100;

 private static final int MAX_ERROR_MESSAGE_LENGTH =
         2_000;

 private final CodingProblemRepository
         codingProblemRepository;

 private final CodeSubmissionRepository
         codeSubmissionRepository;

 private final ProblemAttemptRepository
         problemAttemptRepository;

 private final StudentRepository
         studentRepository;

 private final SecurityUtils
         securityUtils;

 private final Judge0Client
         judge0Client;

 private final Judge0ResultMapper
         judge0ResultMapper;

 private final ApplicationEventPublisher
         eventPublisher;

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

  LocalDateTime now =
          LocalDateTime.now();

  CodeSubmission submission =
          CodeSubmission.builder()
                  .student(student)
                  .problem(problem)
                  .programmingLanguage(
                          request.language()
                  )
                  .sourceCode(
                          request.sourceCode().strip()
                  )
                  .status(
                          SubmissionStatus.QUEUED
                  )
                  .passedTestCases(0)
                  .totalTestCases(
                          activeTestCases.size()
                  )
                  .submittedAt(now)
                  .build();

  CodeSubmission savedSubmission =
          codeSubmissionRepository.saveAndFlush(
                  submission
          );

  recordQueuedAttempt(
          student,
          problem
  );

  /*
   * Publish only after the submission transaction commits.
   *
   * This prevents the background listener from trying to read
   * a submission that has not yet been committed.
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
   * Pessimistic locking prevents two async workers from
   * claiming and judging the same submission simultaneously.
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
          submission.getStatus()
                  != SubmissionStatus.QUEUED
                  && submission.getStatus()
                  != SubmissionStatus.PROCESSING
  ) {
   return;
  }

  submission.setStatus(
          SubmissionStatus.PROCESSING
  );

  codeSubmissionRepository.saveAndFlush(
          submission
  );

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
   Request judgeRequest =
           buildJudgeRequest(
                   submission,
                   problem,
                   testCase
           );

   Result judgeResult;

   try {
    judgeResult =
            judge0Client.execute(
                    judgeRequest
            );

   } catch (Exception exception) {
    completeAsFailed(
            submission,
            exception.getMessage()
    );

    return;
   }

   SubmissionStatus testStatus =
           judge0ResultMapper.status(
                   judgeResult
           );

   if (testStatus == null) {
    testStatus =
            SubmissionStatus.INTERNAL_ERROR;
   }

   boolean passed =
           testStatus
                   == SubmissionStatus.ACCEPTED;

   Double testCaseScore =
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
           judge0ResultMapper.time(
                   judgeResult
           );

   Long memoryUsed =
           judge0ResultMapper.memory(
                   judgeResult
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
                   .actualOutput(
                           normalizeOutput(
                                   judgeResult.stdout()
                           )
                   )
                   .expectedOutput(
                           testCase.getExpectedOutput()
                   )
                   .standardError(
                           normalizeOutput(
                                   judgeResult.stderr()
                           )
                   )
                   .compilerOutput(
                           normalizeOutput(
                                   judgeResult.compileOutput()
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
                    judgeResult.compileOutput()
            )
    );

    break;
   }

   if (
           testStatus
                   == SubmissionStatus.RUNTIME_ERROR
   ) {
    submission.setRuntimeError(
            normalizeOutput(
                    judgeResult.stderr()
            )
    );
   }

   if (
           testStatus
                   == SubmissionStatus.INTERNAL_ERROR
   ) {
    submission.setRuntimeError(
            normalizeOutput(
                    judgeResult.message()
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

  /*
   * Enable this when CodeSubmission contains:
   *
   * private Double scoreAwarded;
   *
   * submission.setScoreAwarded(awardedScore);
   */

  CodeSubmission savedSubmission =
          codeSubmissionRepository.saveAndFlush(
                  submission
          );

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
          codeSubmissionRepository.saveAndFlush(
                  submission
          );

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
  /*
   * An attempt row is unique per student and problem.
   */
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
   problemAttemptRepository.saveAndFlush(
           attempt
   );

  } catch (DataIntegrityViolationException exception) {
   /*
    * Handles two first submissions for the same problem
    * arriving concurrently.
    */
   ProblemAttempt existing =
           problemAttemptRepository
                   .findForUpdate(
                           student.getId(),
                           problem.getId()
                   )
                   .orElseThrow(() -> exception);

   existing.recordQueuedSubmission();

   problemAttemptRepository.save(
           existing
   );
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

  /*
   * Exactly one increment per terminal submission.
   */
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
   /*
    * A failed attempt cannot turn an already solved
    * problem back into ATTEMPTED.
    */
   attempt.preserveSolvedStatusOrMarkAttempted();
  }

  problemAttemptRepository.save(
          attempt
  );
 }

 private Request buildJudgeRequest(
         CodeSubmission submission,
         CodingProblem problem,
         ProblemTestCase testCase
 ) {
  if (
          submission.getProgrammingLanguage() == null
                  || submission
                  .getProgrammingLanguage()
                  .getJudge0LanguageId() == null
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Judge0 is not configured for the submission language."
   );
  }

  if (
          problem.getTimeLimitMilliseconds() == null
                  || problem.getTimeLimitMilliseconds() <= 0
  ) {
   throw new ResponseStatusException(
           HttpStatus.CONFLICT,
           "The problem has an invalid time limit."
   );
  }

  if (
          problem.getMemoryLimitMegabytes() == null
                  || problem.getMemoryLimitMegabytes() <= 0
  ) {
   throw new ResponseStatusException(
           HttpStatus.CONFLICT,
           "The problem has an invalid memory limit."
   );
  }

  double timeLimitSeconds =
          testCase.getCustomTimeLimitSeconds()
                  != null
                  ? testCase
                  .getCustomTimeLimitSeconds()
                  : problem
                  .getTimeLimitMilliseconds()
                  / 1000.0;

  int memoryLimitMegabytes =
          testCase.getCustomMemoryLimitMegabytes()
                  != null
                  ? testCase
                  .getCustomMemoryLimitMegabytes()
                  : problem
                  .getMemoryLimitMegabytes();

  int memoryLimitKilobytes;

  try {
   memoryLimitKilobytes =
           Math.multiplyExact(
                   memoryLimitMegabytes,
                   1024
           );

  } catch (ArithmeticException exception) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Configured memory limit is too large."
   );
  }

  return new Request(
          submission.getSourceCode(),
          submission
                  .getProgrammingLanguage()
                  .getJudge0LanguageId(),
          testCase.getInput(),
          testCase.getExpectedOutput(),
          timeLimitSeconds,
          memoryLimitKilobytes
  );
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

  if (
          request.language()
                  .getJudge0LanguageId() == null
  ) {
   throw new ResponseStatusException(
           HttpStatus.BAD_REQUEST,
           "Judge0 is not configured for the selected language."
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

 private Double resolveScoreWeight(
         ProblemTestCase testCase
 ) {
  if (
          testCase.getScoreWeight() == null
                  || testCase.getScoreWeight() < 0
  ) {
   return 0.0;
  }

  return testCase.getScoreWeight();
 }

 private void updateProblemStatistics(
         CodingProblem problem,
         SubmissionStatus submissionStatus
 ) {
  long totalSubmissions =
          problem.getTotalSubmissions() == null
                  ? 0L
                  : problem.getTotalSubmissions();

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

  codingProblemRepository.save(problem);
 }

 private Integer calculateSubmissionScore(
         CodeSubmission submission,
         double awardedScore
 ) {
  /*
   * Prefer weighted score when test cases have score weights.
   */
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

  int passed =
          submission.getPassedTestCases() == null
                  ? 0
                  : submission.getPassedTestCases();

  double score =
          passed * 100.0
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

  /*
   * Sorting is defined in the repository method:
   * OrderBySubmittedAtDesc.
   */
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

 private Submission toResponse(
         CodeSubmission submission
 ) {
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
          submission.getStatus()
                  == SubmissionStatus.ACCEPTED,
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
          output.stripTrailing();

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
}