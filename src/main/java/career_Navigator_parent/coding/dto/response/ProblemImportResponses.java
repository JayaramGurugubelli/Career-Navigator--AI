package career_Navigator_parent.coding.dto.response;

import career_Navigator_parent.coding.enums.ProblemImportIssueSeverity;
import career_Navigator_parent.coding.enums.ProblemImportStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ProblemImportResponses {

    private ProblemImportResponses() {
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationIssue {

        private Integer problemIndex;

        private String problemTitle;

        private String field;

        private String code;

        private String message;

        private ProblemImportIssueSeverity severity;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblemValidation {

        private Integer problemIndex;

        private String title;

        private String generatedSlug;

        private Boolean valid;

        @Builder.Default
        private List<ValidationIssue> issues = new ArrayList<>();
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationResult {

        private String importReference;

        private Boolean valid;

        private Integer totalProblems;

        private Integer validProblems;

        private Integer invalidProblems;

        private Integer duplicateProblems;

        @Builder.Default
        private List<ProblemValidation> problems = new ArrayList<>();

        @Builder.Default
        private List<ValidationIssue> issues = new ArrayList<>();

        private LocalDateTime validatedAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportResult {

        private Long importId;

        private String importReference;

        private ProblemImportStatus status;

        private Integer totalProblems;

        private Integer acceptedProblems;

        private Boolean continueOnError;

        private String message;

        private String statusUrl;

        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportFailure {

        private Integer problemIndex;

        private String problemTitle;

        private String generatedSlug;

        private String errorCode;

        private String message;

        private LocalDateTime failedAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportedProblem {

        private Integer problemIndex;

        private Long problemId;

        private String title;

        private String slug;

        private LocalDateTime importedAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportStatus {

        private Long importId;

        private String importReference;

        private ProblemImportStatus status;

        private Integer totalProblems;

        private Integer processedProblems;

        private Integer successfulProblems;

        private Integer failedProblems;

        private Integer skippedProblems;

        private Double progressPercentage;

        private Boolean continueOnError;

        private String currentProblem;

        private String failureMessage;

        @Builder.Default
        private List<ImportFailure> failures = new ArrayList<>();

        @Builder.Default
        private List<ImportedProblem> importedProblems = new ArrayList<>();

        private LocalDateTime createdAt;

        private LocalDateTime startedAt;

        private LocalDateTime completedAt;

        private LocalDateTime updatedAt;
    }
}