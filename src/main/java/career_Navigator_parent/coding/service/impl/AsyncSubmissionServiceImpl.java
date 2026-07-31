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
                        .sourceCode(
                                request.sourceCode().trim()
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
                        .build();

        CodeSubmission savedSubmission =
                codeSubmissionRepository.save(
                        submission
                );

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

    private void validateRequest(
            SubmitCodeRequest request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Submission request is required."
            );
        }

        if (request.problemId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Problem ID is required."
            );
        }

        if (request.programmingLanguage() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Programming language is required."
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

    private int countActiveTestCases(
            CodingProblem problem
    ) {
        if (problem.getTestCases() == null) {
            return 0;
        }

        return (int) problem
                .getTestCases()
                .stream()
                .filter(testCase ->
                        Boolean.TRUE.equals(
                                testCase.getActive()
                        )
                )
                .count();
    }
}