package career_Navigator_parent.coding.service.impl;

import career_Navigator_parent.coding.dto.request.SubmitCodeRequest;
import career_Navigator_parent.coding.dto.response.SubmissionAcceptedResponse;
import career_Navigator_parent.coding.entity.CodeSubmission;
import career_Navigator_parent.coding.entity.CodingProblem;
import career_Navigator_parent.coding.enums.ProblemStatus;
import career_Navigator_parent.coding.enums.SubmissionStatus;
import career_Navigator_parent.coding.event.SubmissionQueuedEvent;
import career_Navigator_parent.coding.repository.CodeSubmissionRepository;
import career_Navigator_parent.coding.repository.CodingProblemRepository;
import career_Navigator_parent.coding.service.AsyncSubmissionService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class AsyncSubmissionServiceImpl
        implements AsyncSubmissionService {

    private static final int MAX_SOURCE_CODE_LENGTH =
            200_000;

    private final CodingProblemRepository codingProblemRepository;
    private final CodeSubmissionRepository codeSubmissionRepository;
    private final StudentRepository studentRepository;
    private final SecurityUtils securityUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public SubmissionAcceptedResponse enqueue(
            SubmitCodeRequest request
    ) {
        validateRequest(request);

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

        int totalTestCases =
                countActiveTestCases(problem);

        if (totalTestCases == 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No active test cases are configured for this problem."
            );
        }

        CodeSubmission submission =
                CodeSubmission.builder()
                        .problem(problem)
                        .student(student)
                        .programmingLanguage(
                                request.programmingLanguage()
                        )
                        /*
                         * Do not trim source code because indentation
                         * is meaningful in languages such as Python.
                         */
                        .sourceCode(
                                request.sourceCode()
                        )
                        .status(
                                SubmissionStatus.QUEUED
                        )
                        .passedTestCases(0)
                        .totalTestCases(totalTestCases)
                        .executionTimeSeconds(null)
                        .memoryUsedKilobytes(null)
                        .compilerOutput(null)
                        .runtimeError(null)
                        .standardOutput(null)
                        .submittedAt(
                                java.time.LocalDateTime.now()
                        )
                        .build();

        CodeSubmission savedSubmission =
                codeSubmissionRepository
                        .saveAndFlush(submission);

        /*
         * The event is published while the current transaction is active.
         * TransactionalEventListener will execute it only after commit.
         */
        eventPublisher.publishEvent(
                new SubmissionQueuedEvent(
                        savedSubmission.getId()
                )
        );

        return new SubmissionAcceptedResponse(
                savedSubmission.getId(),
                savedSubmission.getStatus(),
                "Submission accepted and queued for evaluation.",
                savedSubmission.getSubmittedAt()
        );
    }

    private void validateRequest(
            SubmitCodeRequest request
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

        if (request.programmingLanguage() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Programming language is required."
            );
        }

        if (
                !request.programmingLanguage()
                        .isPistonConfigured()
        ) {
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

    private int countActiveTestCases(
            CodingProblem problem
    ) {
        if (
                problem.getTestCases() == null
                        || problem.getTestCases().isEmpty()
        ) {
            return 0;
        }

        return Math.toIntExact(
                problem
                        .getTestCases()
                        .stream()
                        .filter(testCase ->
                                Boolean.TRUE.equals(
                                        testCase.getActive()
                                )
                        )
                        .count()
        );
    }
}