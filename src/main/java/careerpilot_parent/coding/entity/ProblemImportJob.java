package careerpilot_parent.coding.entity;

import careerpilot_parent.coding.enums.ProblemImportStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "coding_problem_import_jobs",
        indexes = {
                @Index(
                        name = "idx_problem_import_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_problem_import_created_at",
                        columnList = "created_at"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_problem_import_reference",
                        columnNames = "import_reference"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemImportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "import_reference",
            nullable = false,
            length = 150
    )
    private String importReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ProblemImportStatus status;

    @Column(name = "total_problems", nullable = false)
    private Integer totalProblems;

    @Column(name = "processed_problems", nullable = false)
    private Integer processedProblems;

    @Column(name = "successful_problems", nullable = false)
    private Integer successfulProblems;

    @Column(name = "failed_problems", nullable = false)
    private Integer failedProblems;

    @Column(name = "skipped_problems", nullable = false)
    private Integer skippedProblems;

    @Column(name = "continue_on_error", nullable = false)
    private Boolean continueOnError;

    @Column(name = "current_problem", length = 250)
    private String currentProblem;

    @Lob
    @Column(
            name = "request_payload",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String requestPayload;

    @Lob
    @Column(
            name = "failure_message",
            columnDefinition = "TEXT"
    )
    private String failureMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        if (status == null) {
            status = ProblemImportStatus.QUEUED;
        }

        if (processedProblems == null) {
            processedProblems = 0;
        }

        if (successfulProblems == null) {
            successfulProblems = 0;
        }

        if (failedProblems == null) {
            failedProblems = 0;
        }

        if (skippedProblems == null) {
            skippedProblems = 0;
        }

        if (continueOnError == null) {
            continueOnError = Boolean.FALSE;
        }

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}