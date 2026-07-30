package careerpilot_parent.coding.service.impl;

import careerpilot_parent.coding.dto.request.ExecutionRequests.Submit;
import careerpilot_parent.coding.dto.response.CodingResponses.Submission;
import careerpilot_parent.coding.dto.response.CodingResponses.TestResult;
import careerpilot_parent.coding.entity.CodeSubmission;
import careerpilot_parent.coding.entity.CodingProblem;
import careerpilot_parent.coding.entity.ProblemAttempt;
import careerpilot_parent.coding.entity.ProblemTestCase;
import careerpilot_parent.coding.entity.SubmissionTestCaseResult;
import careerpilot_parent.coding.enums.ProblemAttemptStatus;
import careerpilot_parent.coding.enums.ProblemStatus;
import careerpilot_parent.coding.enums.SubmissionStatus;
import careerpilot_parent.coding.enums.TestCaseVisibility;
import careerpilot_parent.coding.event.SubmissionQueuedEvent;
import careerpilot_parent.coding.execution.client.Judge0Client;
import careerpilot_parent.coding.execution.dto.Judge0Models.Request;
import careerpilot_parent.coding.execution.dto.Judge0Models.Result;
import careerpilot_parent.coding.execution.mapper.Judge0ResultMapper;
import careerpilot_parent.coding.repository.CodeSubmissionRepository;
import careerpilot_parent.coding.repository.CodingProblemRepository;
import careerpilot_parent.coding.repository.ProblemAttemptRepository;
import careerpilot_parent.coding.service.SubmissionJudgingService;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.student.entity.Student;
import careerpilot_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionJudgingServiceImpl
        implements SubmissionJudgingService {

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
                  .orElseThrow(
                          () -> new ResponseStatusException(
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
                  .submittedAt(
                          LocalDateTime.now()
                  )
                  .build();

  CodeSubmission savedSubmission =
          codeSubmissionRepository.saveAndFlush(
                  submission
          );

  eventPublisher.publishEvent(
          new SubmissionQueuedEvent(
                  savedSubmission.getId()
          )
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
                  .orElseThrow(
                          () -> new ResponseStatusException(
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

  Page<CodeSubmission> submissions;

  if (problemId == null) {

   submissions =
           codeSubmissionRepository
                   .findByStudentIdOrderBySubmittedAtDesc(
                           student.getId(),
                           pageable
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
                           pageable
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

  CodeSubmission submission =
          codeSubmissionRepository
                  .findById(submissionId)
                  .orElseThrow(
                          () -> new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Submission not found."
                          )
                  );

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

   markAsFailed(
           submissionId,
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

    markAsFailed(
            submissionId,
            exception.getMessage()
    );

    return;
   }

   SubmissionStatus testStatus =
           judge0ResultMapper.status(
                   judgeResult
           );

   boolean passed =
           testStatus
                   == SubmissionStatus.ACCEPTED;

   Double testCaseScore =
           resolveScoreWeight(testCase);

   Double scoreAwarded =
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

   if (executionTime != null) {

    maximumExecutionTime =
            Math.max(
                    maximumExecutionTime,
                    executionTime
            );
   }

   if (memoryUsed != null) {

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
                  : finalStatus;

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
   * When CodeSubmission contains a Double scoreAwarded field,
   * this value can be persisted directly:
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
          savedSubmission
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
                  .findById(submissionId)
                  .orElseThrow(
                          () -> new ResponseStatusException(
                                  HttpStatus.NOT_FOUND,
                                  "Submission not found."
                          )
                  );

  submission.setStatus(
          SubmissionStatus.INTERNAL_ERROR
  );

  submission.setRuntimeError(
          normalizeErrorMessage(
                  errorMessage
          )
  );

  submission.setJudgedAt(
          LocalDateTime.now()
  );

  codeSubmissionRepository.saveAndFlush(
          submission
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

  double timeLimitSeconds =
          testCase.getCustomTimeLimitSeconds()
                  != null
                  ? testCase
                  .getCustomTimeLimitSeconds()
                  : problem
                  .getTimeLimitMilliseconds()
                  / 1000.0;

  int memoryLimitMegabytes =
          testCase
                  .getCustomMemoryLimitMegabytes()
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
          .filter(
                  testCase ->
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
          totalSubmissions + 1
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
           acceptedSubmissions + 1
   );
  }

  codingProblemRepository.save(
          problem
  );
 }

 private void updateAttempt(
         Student student,
         CodingProblem problem,
         CodeSubmission submission
 ) {

  LocalDateTime now =
          LocalDateTime.now();

  ProblemAttempt attempt =
          problemAttemptRepository
                  .findByStudentIdAndProblemId(
                          student.getId(),
                          problem.getId()
                  )
                  .orElseGet(
                          () -> ProblemAttempt.builder()
                                  .student(student)
                                  .problem(problem)
                                  .status(
                                          ProblemAttemptStatus.ATTEMPTED
                                  )
                                  .totalAttempts(0)
                                  .acceptedAttempts(0)
                                  .firstAttemptedAt(now)
                                  .build()
                  );

  int totalAttempts =
          attempt.getTotalAttempts() == null
                  ? 0
                  : attempt.getTotalAttempts();

  attempt.setTotalAttempts(
          totalAttempts + 1
  );

  attempt.setLastAttemptedAt(now);

  if (
          submission.getStatus()
                  == SubmissionStatus.ACCEPTED
  ) {

   int acceptedAttempts =
           attempt.getAcceptedAttempts() == null
                   ? 0
                   : attempt.getAcceptedAttempts();

   attempt.setAcceptedAttempts(
           acceptedAttempts + 1
   );

   attempt.setStatus(
           ProblemAttemptStatus.SOLVED
   );

   if (attempt.getSolvedAt() == null) {
    attempt.setSolvedAt(now);
   }

   attempt.setBestSubmission(
           submission
   );

  } else if (
          attempt.getStatus()
                  != ProblemAttemptStatus.SOLVED
  ) {

   attempt.setStatus(
           ProblemAttemptStatus.ATTEMPTED
   );
  }

  problemAttemptRepository.save(
          attempt
  );
 }

 private Submission toResponse(
         CodeSubmission submission
 ) {

  List<TestResult> testResults;

  if (
          submission.getTestCaseResults()
                  == null
  ) {

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
                   .map(
                           this::toTestResult
                   )
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
          .orElseThrow(
                  () -> new ResponseStatusException(
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

  return output.stripTrailing();
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

  int maximumLength = 2_000;

  String normalized =
          errorMessage.strip();

  return normalized.length()
          <= maximumLength
          ? normalized
          : normalized.substring(
          0,
          maximumLength
  );
 }
}