package career_Navigator_parent.coding.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "coding_problem_import_items",
        indexes = {
                @Index(
                        name = "idx_import_item_job_id",
                        columnList = "import_job_id"
                ),
                @Index(
                        name = "idx_import_item_problem_id",
                        columnList = "problem_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemImportedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "import_job_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_import_item_job"
            )
    )
    private ProblemImportJob importJob;

    @Column(name = "problem_index", nullable = false)
    private Integer problemIndex;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 220)
    private String slug;

    @Column(name = "imported_at", nullable = false)
    private LocalDateTime importedAt;

    @PrePersist
    public void prePersist() {
        if (importedAt == null) {
            importedAt = LocalDateTime.now();
        }
    }
}