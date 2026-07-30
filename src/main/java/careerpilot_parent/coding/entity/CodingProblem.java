package careerpilot_parent.coding.entity;

import careerpilot_parent.coding.enums.ProblemDifficulty;
import careerpilot_parent.coding.enums.ProblemStatus;
import careerpilot_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "coding_problems",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_coding_problem_slug",
                        columnNames = "slug"
                )
        },
        indexes = {
                @Index(
                        name = "idx_coding_problem_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_coding_problem_difficulty",
                        columnList = "difficulty"
                ),
                @Index(
                        name = "idx_coding_problem_title",
                        columnList = "title"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingProblem extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 220)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String inputFormat;

    @Column(columnDefinition = "TEXT")
    private String outputFormat;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProblemDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProblemStatus status = ProblemStatus.DRAFT;

    @Column(nullable = false)
    @Builder.Default
    private Integer timeLimitMilliseconds = 2000;

    @Column(nullable = false)
    @Builder.Default
    private Integer memoryLimitMegabytes = 256;

    @Column(nullable = false)
    @Builder.Default
    private Integer maximumOutputCharacters = 10000;

    @Column(nullable = false)
    @Builder.Default
    private Boolean functionBased = false;

    @Column(length = 150)
    private String functionName;

    @Column(columnDefinition = "TEXT")
    private String expectedComplexity;

    @Column(columnDefinition = "TEXT")
    private String editorial;

    @Column(nullable = false)
    @Builder.Default
    private Boolean premium = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(nullable = false)
    @Builder.Default
    private Long totalSubmissions = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long acceptedSubmissions = 0L;

    @ManyToMany
    @JoinTable(
            name = "coding_problem_tag_mappings",
            joinColumns = @JoinColumn(
                    name = "problem_id",
                    foreignKey = @ForeignKey(
                            name = "fk_problem_tag_mapping_problem"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id",
                    foreignKey = @ForeignKey(
                            name = "fk_problem_tag_mapping_tag"
                    )
            ),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_problem_tag_mapping",
                            columnNames = {"problem_id", "tag_id"}
                    )
            }
    )
    @Builder.Default
    private Set<ProblemTag> tags = new HashSet<>();

    @OneToMany(
            mappedBy = "problem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<ProblemTestCase> testCases = new ArrayList<>();

    @OneToMany(
            mappedBy = "problem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ProblemStarterCode> starterCodes = new ArrayList<>();
}