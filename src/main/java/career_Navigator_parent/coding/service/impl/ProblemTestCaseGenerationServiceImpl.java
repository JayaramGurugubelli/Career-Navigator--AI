package career_Navigator_parent.coding.service.impl;

import career_Navigator_parent.coding.dto.request.ProblemTestCaseRequests.Generate;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.GenerationAccepted;
import career_Navigator_parent.coding.dto.response.ProblemTestCaseResponses.GenerationJob;
import career_Navigator_parent.coding.entity.CodingProblem;
import career_Navigator_parent.coding.entity.ProblemSolution;
import career_Navigator_parent.coding.entity.ProblemTestCaseGenerationJob;
import career_Navigator_parent.coding.enums.TestCaseGenerationStatus;
import career_Navigator_parent.coding.enums.TestCaseGeneratorType;
import career_Navigator_parent.coding.repository.CodingProblemRepository;
import career_Navigator_parent.coding.repository.ProblemSolutionRepository;
import career_Navigator_parent.coding.repository.ProblemTestCaseGenerationJobRepository;
import career_Navigator_parent.coding.service.ProblemTestCaseGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class ProblemTestCaseGenerationServiceImpl
        implements ProblemTestCaseGenerationService {

    private static final List<TestCaseGenerationStatus>
            ACTIVE_JOB_STATUSES =
            List.of(
                    TestCaseGenerationStatus.QUEUED,
                    TestCaseGenerationStatus.PROCESSING
            );

    private final CodingProblemRepository problems;

    private final ProblemSolutionRepository solutions;

    private final ProblemTestCaseGenerationJobRepository
            generationJobs;

    @Override
    public GenerationAccepted startGeneration(
            Long problemId,
            Generate request
    ) {

        CodingProblem problem =
                getRequiredProblem(problemId);

        validateRequest(request);

        ProblemSolution referenceSolution =
                solutions
                        .findByIdAndProblemIdAndActiveTrue(
                                request.referenceSolutionId(),
                                problemId
                        )
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Active reference solution not found "
                                                + "for this problem."
                                )
                        );

        if (
                referenceSolution.getProgrammingLanguage()
                        != request.referenceLanguage()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Reference language does not match the "
                            + "reference solution language."
            );
        }

        if (
                generationJobs.existsByProblemIdAndStatusIn(
                        problemId,
                        ACTIVE_JOB_STATUSES
                )
        ) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A test-case generation job is already active "
                            + "for this problem."
            );
        }

        Long resolvedSeed =
                request.randomSeed() == null
                        ? ThreadLocalRandom
                        .current()
                        .nextLong(
                                1L,
                                Long.MAX_VALUE
                        )
                        : request.randomSeed();

        ProblemTestCaseGenerationJob job =
                ProblemTestCaseGenerationJob.builder()
                        .problem(problem)
                        .generatorType(
                                request.generatorType()
                        )
                        .status(
                                TestCaseGenerationStatus.QUEUED
                        )
                        .requestedCases(
                                request.totalCases()
                        )
                        .processedCases(0)
                        .generatedCases(0)
                        .failedCases(0)
                        .randomSeed(resolvedSeed)
                        .minimumArrayLength(
                                request.minArrayLength()
                        )
                        .maximumArrayLength(
                                request.maxArrayLength()
                        )
                        .minimumValue(
                                request.minValue()
                        )
                        .maximumValue(
                                request.maxValue()
                        )
                        .includeEdgeCases(
                                Boolean.TRUE.equals(
                                        request.includeEdgeCases()
                                )
                        )
                        .includePerformanceCases(
                                Boolean.TRUE.equals(
                                        request.includePerformanceCases()
                                )
                        )
                        .visibility(
                                request.visibility()
                        )
                        .batchSize(
                                request.batchSize()
                        )
                        .referenceLanguage(
                                request.referenceLanguage()
                        )
                        .referenceSolutionId(
                                request.referenceSolutionId()
                        )
                        .inputTemplate(
                                request.inputTemplate().strip()
                        )
                        .minimumTargetValue(
                                request.minTargetValue()
                        )
                        .maximumTargetValue(
                                request.maxTargetValue()
                        )
                        .scoreWeight(
                                request.scoreWeight() == null
                                        ? 1.0
                                        : request.scoreWeight()
                        )
                        .customTimeLimitSeconds(
                                request.customTimeLimitSeconds()
                        )
                        .customMemoryLimitMegabytes(
                                request.customMemoryLimitMegabytes()
                        )
                        .message(
                                "Test-case generation job accepted "
                                        + "and queued for execution."
                        )
                        .queuedAt(
                                LocalDateTime.now()
                        )
                        .build();

        ProblemTestCaseGenerationJob saved =
                generationJobs.saveAndFlush(job);

        return new GenerationAccepted(
                saved.getId(),
                problemId,
                saved.getStatus(),
                saved.getRequestedCases(),
                saved.getGeneratedCases(),
                saved.getFailedCases(),
                "/api/admin/coding/problems/"
                        + problemId
                        + "/test-cases/generation-jobs/"
                        + saved.getId(),
                saved.getMessage(),
                saved.getQueuedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public GenerationJob getJob(
            Long problemId,
            Long jobId
    ) {

        getRequiredProblem(problemId);

        if (jobId == null || jobId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid generation-job ID is required."
            );
        }

        ProblemTestCaseGenerationJob job =
                generationJobs
                        .findByIdAndProblemId(
                                jobId,
                                problemId
                        )
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Test-case generation job not found."
                                )
                        );

        return toResponse(job);
    }

    private GenerationJob toResponse(
            ProblemTestCaseGenerationJob job
    ) {

        int progressPercentage =
                calculateProgressPercentage(
                        job.getProcessedCases(),
                        job.getRequestedCases()
                );

        return new GenerationJob(
                job.getId(),
                job.getProblem().getId(),
                job.getGeneratorType(),
                job.getStatus(),
                job.getRequestedCases(),
                job.getProcessedCases(),
                job.getGeneratedCases(),
                job.getFailedCases(),
                progressPercentage,
                job.getRandomSeed(),
                job.getBatchSize(),
                job.getVisibility(),
                job.getReferenceLanguage(),
                job.getReferenceSolutionId(),
                job.getInputTemplate(),
                job.getMessage(),
                job.getFailureReason(),
                job.getQueuedAt(),
                job.getStartedAt(),
                job.getCompletedAt(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }

    private int calculateProgressPercentage(
            Integer processedCases,
            Integer requestedCases
    ) {

        if (
                requestedCases == null
                        || requestedCases <= 0
        ) {
            return 0;
        }

        int processed =
                processedCases == null
                        ? 0
                        : processedCases;

        return Math.min(
                100,
                (int) Math.floor(
                        processed * 100.0
                                / requestedCases
                )
        );
    }

    private CodingProblem getRequiredProblem(
            Long problemId
    ) {

        if (problemId == null || problemId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid coding problem ID is required."
            );
        }

        return problems.findById(problemId)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Coding problem not found."
                        )
                );
    }

    private void validateRequest(
            Generate request
    ) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Generation request is required."
            );
        }

        if (
                request.minArrayLength()
                        > request.maxArrayLength()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum array length cannot exceed "
                            + "maximum array length."
            );
        }

        if (
                request.minValue()
                        > request.maxValue()
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum value cannot exceed maximum value."
            );
        }

        if (
                request.generatorType()
                        == TestCaseGeneratorType.INTEGER_ARRAY_WITH_TARGET
        ) {

            if (
                    request.minTargetValue() == null
                            || request.maxTargetValue() == null
            ) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Minimum and maximum target values are required "
                                + "for INTEGER_ARRAY_WITH_TARGET."
                );
            }

            if (
                    request.minTargetValue()
                            > request.maxTargetValue()
            ) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Minimum target value cannot exceed "
                                + "maximum target value."
                );
            }

            if (
                    !request.inputTemplate()
                            .contains("{target}")
            ) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Input template must contain {target} for "
                                + "INTEGER_ARRAY_WITH_TARGET."
                );
            }
        }

        if (
                request.generatorType()
                        == TestCaseGeneratorType.INTEGER_ARRAY
                        || request.generatorType()
                        == TestCaseGeneratorType.INTEGER_ARRAY_WITH_TARGET
                        || request.generatorType()
                        == TestCaseGeneratorType.SORTED_INTEGER_ARRAY
        ) {

            if (
                    !request.inputTemplate()
                            .contains("{array}")
            ) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Input template must contain {array} "
                                + "for array generators."
                );
            }
        }

        if (
                request.referenceLanguage()
                        .getJudge0LanguageId() == null
        ) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Judge0 is not configured for the reference language."
            );
        }
    }
}