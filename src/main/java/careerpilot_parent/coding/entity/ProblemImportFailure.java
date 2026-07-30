package careerpilot_parent.coding.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "coding_problem_import_failures",
        indexes = {
                @Index(
                        name = "idx_import_failure_job_id",
                        columnList = "import_job_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemImportFailure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "import_job_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_import_failure_job"
            )
    )
    private ProblemImportJob importJob;

    @Column(name = "problem_index", nullable = false)
    private Integer problemIndex;

    @Column(name = "problem_title", length = 200)
    private String problemTitle;

    @Column(name = "generated_slug", length = 220)
    private String generatedSlug;

    @Column(name = "error_code", nullable = false, length = 100)
    private String errorCode;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(name = "failed_at", nullable = false)
    private LocalDateTime failedAt;

    @PrePersist
    public void prePersist() {
        if (failedAt == null) {
            failedAt = LocalDateTime.now();
        }
    }
}