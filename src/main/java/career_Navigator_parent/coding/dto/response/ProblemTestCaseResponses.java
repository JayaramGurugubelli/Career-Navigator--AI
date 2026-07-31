package career_Navigator_parent.coding.dto.response;

import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import career_Navigator_parent.coding.enums.TestCaseGenerationStatus;
import career_Navigator_parent.coding.enums.TestCaseGeneratorType;
import career_Navigator_parent.coding.enums.TestCaseVisibility;

import java.time.LocalDateTime;
import java.util.List;

public final class ProblemTestCaseResponses {

    private ProblemTestCaseResponses() {
    }

    public record AdminTestCase(

            Long id,

            Long problemId,

            String input,

            String expectedOutput,

            TestCaseVisibility visibility,

            Integer displayOrder,

            Double scoreWeight,

            Double customTimeLimitSeconds,

            Integer customMemoryLimitMegabytes,

            Boolean generatedCase,

            Long generatorSeed,

            Boolean active,

            LocalDateTime createdAt,

            LocalDateTime updatedAt

    ) {
    }

    public record BulkError(

            Integer requestIndex,

            Integer displayOrder,

            String message

    ) {
    }

    public record BatchResult(

            Long problemId,

            String batchReference,

            Integer requestedCount,

            Integer createdCount,

            Integer failedCount,

            Integer sampleCount,

            Integer hiddenCount,

            Integer totalProblemTestCaseCount,

            List<BulkError> errors

    ) {

        public BatchResult {

            errors = errors == null
                    ? List.of()
                    : List.copyOf(errors);
        }
    }

    /*
     * Kept temporarily for compatibility with older service or
     * controller implementations that still return BulkResult.
     */
    @Deprecated
    public record BulkResult(

            Long problemId,

            String batchReference,

            Integer requestedCount,

            Integer createdCount,

            Integer failedCount,

            Integer sampleCount,

            Integer hiddenCount,

            Integer totalProblemTestCaseCount,

            List<BulkError> errors

    ) {

        public BulkResult {

            errors = errors == null
                    ? List.of()
                    : List.copyOf(errors);
        }

        public BatchResult toBatchResult() {

            return new BatchResult(
                    problemId,
                    batchReference,
                    requestedCount,
                    createdCount,
                    failedCount,
                    sampleCount,
                    hiddenCount,
                    totalProblemTestCaseCount,
                    errors
            );
        }
    }

    public record ImportResult(

            Long problemId,

            String importReference,

            Integer requestedCount,

            Integer importedCount,

            Integer failedCount,

            Integer startingDisplayOrder,

            Integer endingDisplayOrder,

            Integer totalProblemTestCaseCount,

            List<BulkError> errors

    ) {

        public ImportResult {

            errors = errors == null
                    ? List.of()
                    : List.copyOf(errors);
        }
    }

    /*
     * Shared response for both:
     *
     * DELETE /test-cases
     * POST   /test-cases/batch-delete
     */
    public record DeleteResult(

            Long problemId,

            Integer requestedCount,

            Integer deletedCount

    ) {
    }

    public record Summary(

            Long problemId,

            Integer total,

            Integer active,

            Integer inactive,

            Integer sample,

            Integer hidden,

            Double totalScoreWeight

    ) {
    }

    public record GenerationAccepted(

            Long jobId,

            Long problemId,

            TestCaseGenerationStatus status,

            Integer requestedCases,

            Integer generatedCases,

            Integer failedCases,

            String statusEndpoint,

            String message,

            LocalDateTime acceptedAt

    ) {
    }

    public record GenerationJob(

            Long jobId,

            Long problemId,

            TestCaseGeneratorType generatorType,

            TestCaseGenerationStatus status,

            Integer requestedCases,

            Integer processedCases,

            Integer generatedCases,

            Integer failedCases,

            Integer progressPercentage,

            Long randomSeed,

            Integer batchSize,

            TestCaseVisibility visibility,

            ProgrammingLanguage referenceLanguage,

            Long referenceSolutionId,

            String inputTemplate,

            String message,

            String failureReason,

            LocalDateTime queuedAt,

            LocalDateTime startedAt,

            LocalDateTime completedAt,

            LocalDateTime createdAt,

            LocalDateTime updatedAt

    ) {
    }
}