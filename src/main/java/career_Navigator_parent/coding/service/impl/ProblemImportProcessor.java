package career_Navigator_parent.coding.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import career_Navigator_parent.coding.dto.request.ProblemImportRequests;
import career_Navigator_parent.coding.dto.request.ProblemRequests;
import career_Navigator_parent.coding.dto.response.CodingResponses;
import career_Navigator_parent.coding.dto.response.ProblemImportResponses;
import career_Navigator_parent.coding.entity.ProblemImportFailure;
import career_Navigator_parent.coding.entity.ProblemImportJob;
import career_Navigator_parent.coding.entity.ProblemImportedItem;
import career_Navigator_parent.coding.enums.ProblemImportIssueSeverity;
import career_Navigator_parent.coding.enums.ProblemImportStatus;
import career_Navigator_parent.coding.repository.ProblemImportFailureRepository;
import career_Navigator_parent.coding.repository.ProblemImportJobRepository;
import career_Navigator_parent.coding.repository.ProblemImportedItemRepository;
import career_Navigator_parent.coding.service.ProblemManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemImportProcessor {

    private final ProblemImportJobRepository importJobRepository;
    private final ProblemImportFailureRepository failureRepository;
    private final ProblemImportedItemRepository importedItemRepository;

    private final ProblemManagementService problemManagementService;
    private final ProblemImportValidator importValidator;
    private final ObjectMapper objectMapper;

    public void process(Long importId) {

        ProblemImportJob job = importJobRepository.findById(importId)
                .orElse(null);

        if (job == null) {
            return;
        }

        if (job.getStatus() != ProblemImportStatus.QUEUED) {
            return;
        }

        try {

            markProcessing(job);

            ProblemImportRequests.ImportProblems request =
                    objectMapper.readValue(
                            job.getRequestPayload(),
                            ProblemImportRequests.ImportProblems.class
                    );

            processProblems(job, request);

            completeJob(job);

        } catch (Exception exception) {

            markFailed(job, exception);
        }
    }

    private void processProblems(
            ProblemImportJob job,
            ProblemImportRequests.ImportProblems request
    ) {

        List<ProblemImportRequests.ImportProblem> problems =
                request.getProblems();

        if (problems == null || problems.isEmpty()) {

            job.setFailureMessage(
                    "Import request does not contain any coding problems."
            );

            return;
        }

        for (int index = 0; index < problems.size(); index++) {

            ProblemImportRequests.ImportProblem problem =
                    problems.get(index);

            if (problem == null) {

                saveNullProblemFailure(job, index);
                incrementFailed(job);

                if (Boolean.FALSE.equals(job.getContinueOnError())) {
                    stopRemainingProblems(job, problems.size(), index);
                    break;
                }

                continue;
            }

            String slug = importValidator.resolveSlug(problem);

            job.setCurrentProblem(problem.getTitle());
            importJobRepository.save(job);

            List<ProblemImportResponses.ValidationIssue> issues =
                    importValidator.validateProblem(
                            index,
                            problem,
                            slug
                    );

            boolean invalid = issues.stream()
                    .anyMatch(issue ->
                            issue.getSeverity()
                                    == ProblemImportIssueSeverity.ERROR
                    );

            if (invalid) {

                saveValidationFailure(
                        job,
                        index,
                        problem,
                        slug,
                        issues
                );

                incrementFailed(job);

                if (Boolean.FALSE.equals(job.getContinueOnError())) {

                    stopRemainingProblems(
                            job,
                            problems.size(),
                            index
                    );

                    job.setFailureMessage(
                            "Import stopped because continueOnError is false."
                    );

                    importJobRepository.save(job);

                    break;
                }

                continue;
            }

            try {

                ProblemRequests.Create createRequest =
                        toCreateRequest(problem);

                CodingResponses.Admin imported =
                        problemManagementService.create(createRequest);

                saveImportedProblem(
                        job,
                        index,
                        imported,
                        problem,
                        slug
                );

                incrementSuccess(job);

            } catch (Exception exception) {

                saveProcessingFailure(
                        job,
                        index,
                        problem,
                        slug,
                        exception
                );

                incrementFailed(job);

                if (Boolean.FALSE.equals(job.getContinueOnError())) {

                    stopRemainingProblems(
                            job,
                            problems.size(),
                            index
                    );

                    job.setFailureMessage(
                            "Import stopped after a problem failed."
                    );

                    importJobRepository.save(job);

                    break;
                }
            }
        }
    }

    private ProblemRequests.Create toCreateRequest(
            ProblemImportRequests.ImportProblem problem
    ) {

        List<ProblemRequests.TestCase> testCases =
                problem.getTestCases() == null
                        ? List.of()
                        : problem.getTestCases()
                        .stream()
                        .map(testCase ->
                                new ProblemRequests.TestCase(
                                        testCase.getInput(),
                                        testCase.getExpectedOutput(),
                                        testCase.getVisibility(),
                                        testCase.getDisplayOrder(),
                                        testCase.getScoreWeight(),
                                        testCase.getCustomTimeLimitSeconds(),
                                        testCase.getCustomMemoryLimitMegabytes()
                                )
                        )
                        .toList();

        List<ProblemRequests.Starter> starterCodes =
                problem.getStarterCodes() == null
                        ? List.of()
                        : problem.getStarterCodes()
                        .stream()
                        .map(starterCode ->
                                new ProblemRequests.Starter(
                                        starterCode.getLanguage(),
                                        starterCode.getStarterCode(),
                                        starterCode.getDriverCode(),
                                        starterCode.getMethodSignature()
                                )
                        )
                        .toList();

        Set<Long> tagIds =
                problem.getTagIds() == null
                        ? Set.of()
                        : problem.getTagIds()
                        .stream()
                        .collect(Collectors.toSet());

        return new ProblemRequests.Create(
                problem.getTitle(),
                problem.getDescription(),
                problem.getInputFormat(),
                problem.getOutputFormat(),
                problem.getConstraints(),
                problem.getExplanation(),
                problem.getDifficulty(),
                problem.getTimeLimitMilliseconds(),
                problem.getMemoryLimitMegabytes(),
                Boolean.TRUE.equals(problem.getFunctionBased()),
                problem.getFunctionName(),
                problem.getExpectedComplexity(),
                Boolean.TRUE.equals(problem.getPremium()),
                tagIds,
                testCases,
                starterCodes
        );
    }

    private void markProcessing(ProblemImportJob job) {

        job.setStatus(ProblemImportStatus.PROCESSING);
        job.setStartedAt(LocalDateTime.now());
        job.setCurrentProblem(null);
        job.setFailureMessage(null);

        importJobRepository.save(job);
    }

    private void incrementSuccess(ProblemImportJob job) {

        job.setSuccessfulProblems(
                safeInteger(job.getSuccessfulProblems()) + 1
        );

        job.setProcessedProblems(
                safeInteger(job.getProcessedProblems()) + 1
        );

        importJobRepository.save(job);
    }

    private void incrementFailed(ProblemImportJob job) {

        job.setFailedProblems(
                safeInteger(job.getFailedProblems()) + 1
        );

        job.setProcessedProblems(
                safeInteger(job.getProcessedProblems()) + 1
        );

        importJobRepository.save(job);
    }

    private void stopRemainingProblems(
            ProblemImportJob job,
            int totalProblems,
            int currentIndex
    ) {

        int remainingProblems =
                Math.max(totalProblems - currentIndex - 1, 0);

        job.setSkippedProblems(
                safeInteger(job.getSkippedProblems())
                        + remainingProblems
        );
    }

    private void completeJob(ProblemImportJob job) {

        int totalProblems =
                safeInteger(job.getTotalProblems());

        int successfulProblems =
                safeInteger(job.getSuccessfulProblems());

        int failedProblems =
                safeInteger(job.getFailedProblems());

        int skippedProblems =
                safeInteger(job.getSkippedProblems());

        if (failedProblems == 0
                && skippedProblems == 0
                && successfulProblems == totalProblems) {

            job.setStatus(ProblemImportStatus.COMPLETED);

        } else if (successfulProblems > 0) {

            job.setStatus(
                    ProblemImportStatus.PARTIALLY_COMPLETED
            );

        } else {

            job.setStatus(ProblemImportStatus.FAILED);
        }

        job.setCurrentProblem(null);
        job.setCompletedAt(LocalDateTime.now());

        importJobRepository.save(job);
    }

    private void markFailed(
            ProblemImportJob job,
            Exception exception
    ) {

        job.setStatus(ProblemImportStatus.FAILED);
        job.setCurrentProblem(null);
        job.setFailureMessage(
                limit(resolveMessage(exception), 2000)
        );
        job.setCompletedAt(LocalDateTime.now());

        importJobRepository.save(job);
    }

    private void saveValidationFailure(
            ProblemImportJob job,
            int index,
            ProblemImportRequests.ImportProblem problem,
            String slug,
            List<ProblemImportResponses.ValidationIssue> issues
    ) {

        String message = issues.stream()
                .map(ProblemImportResponses.ValidationIssue::getMessage)
                .filter(value ->
                        value != null && !value.isBlank()
                )
                .distinct()
                .reduce(
                        (first, second) ->
                                first + "; " + second
                )
                .orElse("Problem validation failed.");

        ProblemImportFailure failure =
                ProblemImportFailure.builder()
                        .importJob(job)
                        .problemIndex(index)
                        .problemTitle(problem.getTitle())
                        .generatedSlug(slug)
                        .errorCode("VALIDATION_FAILED")
                        .message(limit(message, 2000))
                        .failedAt(LocalDateTime.now())
                        .build();

        failureRepository.save(failure);
    }

    private void saveProcessingFailure(
            ProblemImportJob job,
            int index,
            ProblemImportRequests.ImportProblem problem,
            String slug,
            Exception exception
    ) {

        ProblemImportFailure failure =
                ProblemImportFailure.builder()
                        .importJob(job)
                        .problemIndex(index)
                        .problemTitle(problem.getTitle())
                        .generatedSlug(slug)
                        .errorCode(
                                exception
                                        .getClass()
                                        .getSimpleName()
                        )
                        .message(
                                limit(
                                        resolveMessage(exception),
                                        2000
                                )
                        )
                        .failedAt(LocalDateTime.now())
                        .build();

        failureRepository.save(failure);
    }

    private void saveNullProblemFailure(
            ProblemImportJob job,
            int index
    ) {

        ProblemImportFailure failure =
                ProblemImportFailure.builder()
                        .importJob(job)
                        .problemIndex(index)
                        .problemTitle(null)
                        .generatedSlug(null)
                        .errorCode("NULL_PROBLEM")
                        .message(
                                "Problem entry cannot be null."
                        )
                        .failedAt(LocalDateTime.now())
                        .build();

        failureRepository.save(failure);
    }

    private void saveImportedProblem(
            ProblemImportJob job,
            int index,
            CodingResponses.Admin imported,
            ProblemImportRequests.ImportProblem request,
            String generatedSlug
    ) {

        if (imported == null) {

            throw new IllegalStateException(
                    "Problem creation returned an empty response."
            );
        }

        /*
         * CodingResponses.Admin is a Java record.
         *
         * Therefore use:
         * imported.id()
         * imported.title()
         * imported.slug()
         *
         * Do not use getId(), getTitle(), or getSlug().
         */

        Long importedProblemId = imported.id();

        if (importedProblemId == null) {

            throw new IllegalStateException(
                    "Imported problem response does not contain an ID."
            );
        }

        String importedTitle =
                imported.title() != null
                        && !imported.title().isBlank()
                        ? imported.title()
                        : request.getTitle();

        String importedSlug =
                imported.slug() != null
                        && !imported.slug().isBlank()
                        ? imported.slug()
                        : generatedSlug;

        ProblemImportedItem item =
                ProblemImportedItem.builder()
                        .importJob(job)
                        .problemIndex(index)
                        .problemId(importedProblemId)
                        .title(importedTitle)
                        .slug(importedSlug)
                        .importedAt(LocalDateTime.now())
                        .build();

        importedItemRepository.save(item);
    }

    private String resolveMessage(Exception exception) {

        if (exception == null) {
            return "Unexpected error while importing coding problems.";
        }

        Throwable current = exception;

        while (current != null) {

            if (current.getMessage() != null
                    && !current.getMessage().isBlank()) {

                return current.getMessage();
            }

            current = current.getCause();
        }

        return "Unexpected error while importing coding problems.";
    }

    private int safeInteger(Integer value) {
        return value == null ? 0 : value;
    }

    private String limit(
            String value,
            int maxLength
    ) {

        if (value == null) {
            return null;
        }

        if (value.length() <= maxLength) {
            return value;
        }

        return value.substring(0, maxLength);
    }
}