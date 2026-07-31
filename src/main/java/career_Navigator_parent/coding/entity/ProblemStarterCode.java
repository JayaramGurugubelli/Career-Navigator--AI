package career_Navigator_parent.coding.entity;

import career_Navigator_parent.coding.enums.ProgrammingLanguage;
import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "coding_problem_starter_codes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_problem_starter_language",
                        columnNames = {
                                "problem_id",
                                "programming_language"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_starter_code_problem",
                        columnList = "problem_id"
                ),
                @Index(
                        name = "idx_starter_code_problem_active",
                        columnList = "problem_id, active"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemStarterCode extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "problem_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_starter_code_problem"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CodingProblem problem;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "programming_language",
            nullable = false,
            length = 30
    )
    private ProgrammingLanguage programmingLanguage;

    @Lob
    @Column(
            name = "starter_code",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String starterCode;

    @Lob
    @Column(
            name = "driver_code",
            columnDefinition = "LONGTEXT"
    )
    private String driverCode;

    @Column(
            name = "method_signature",
            length = 500
    )
    private String methodSignature;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}