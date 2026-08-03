package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.LearningLevel;
import career_Navigator_parent.learning.enums.ProjectSubmissionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "learning_projects",
        indexes = {
                @Index(
                        name = "idx_learning_project_course",
                        columnList = "course_id"
                ),
                @Index(
                        name = "idx_learning_project_milestone",
                        columnList = "milestone_id"
                ),
                @Index(
                        name = "idx_learning_project_active",
                        columnList = "active"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningProject extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "course_id",
            foreignKey = @ForeignKey(
                    name = "fk_learning_project_course"
            )
    )
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "milestone_id",
            foreignKey = @ForeignKey(
                    name = "fk_learning_project_milestone"
            )
    )
    private LearningPathMilestone milestone;

    @Column(
            nullable = false,
            length = 250
    )
    private String title;

    @Lob
    @Column(
            name = "problem_statement",
            nullable = false,
            columnDefinition = "LONGTEXT"
    )
    private String problemStatement;

    @Lob
    @Column(
            name = "objectives",
            columnDefinition = "TEXT"
    )
    private String objectives;

    @Lob
    @Column(
            name = "deliverables",
            columnDefinition = "TEXT"
    )
    private String deliverables;

    @Lob
    @Column(
            name = "evaluation_criteria",
            columnDefinition = "TEXT"
    )
    private String evaluationCriteria;

    @Lob
    @Column(
            name = "starter_instructions",
            columnDefinition = "TEXT"
    )
    private String starterInstructions;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private LearningLevel difficulty;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "submission_type",
            nullable = false,
            length = 50
    )
    private ProjectSubmissionType submissionType;

    @Column(
            name = "estimated_hours",
            nullable = false
    )
    private Integer estimatedHours;

    @Column(
            name = "maximum_score",
            nullable = false
    )
    @Builder.Default
    private Double maximumScore = 100.0;

    @Column(
            name = "passing_score",
            nullable = false
    )
    @Builder.Default
    private Double passingScore = 60.0;

    @Column(
            name = "allow_resubmission",
            nullable = false
    )
    @Builder.Default
    private Boolean allowResubmission = true;

    @Column(
            name = "maximum_attempts"
    )
    private Integer maximumAttempts;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean mandatory = true;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<LearningResource> resources =
            new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    @PreUpdate
    private void validateAndNormalize() {

        if (title != null) {
            title = title.strip();
        }

        if (
                estimatedHours == null
                        || estimatedHours < 1
        ) {
            estimatedHours = 1;
        }

        if (
                maximumScore == null
                        || maximumScore <= 0
        ) {
            maximumScore = 100.0;
        }

        if (
                passingScore == null
                        || passingScore < 0
                        || passingScore > maximumScore
        ) {
            passingScore =
                    Math.min(60.0, maximumScore);
        }

        if (
                maximumAttempts != null
                        && maximumAttempts < 1
        ) {
            maximumAttempts = 1;
        }

        if (allowResubmission == null) {
            allowResubmission = true;
        }

        if (mandatory == null) {
            mandatory = true;
        }

        if (active == null) {
            active = true;
        }

        if (
                course == null
                        && milestone == null
        ) {
            throw new IllegalStateException(
                    "A learning project must belong to a course or milestone."
            );
        }
    }
}