package careerpilot_parent.jobrecommendation.entity;

import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.job.entity.JobPosting;
import careerpilot_parent.jobrecommendation.enums.RecommendationSource;
import careerpilot_parent.student.entity.Student;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "job_recommendations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_recommendation_student_job",
                        columnNames = {
                                "student_id",
                                "job_posting_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_job_recommendation_student_active",
                        columnList = "student_id, active"
                ),
                @Index(
                        name = "idx_job_recommendation_score",
                        columnList = "match_score"
                ),
                @Index(
                        name = "idx_job_recommendation_generated_at",
                        columnList = "generated_at"
                ),
                @Index(
                        name = "idx_job_recommendation_expires_at",
                        columnList = "expires_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRecommendation extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_job_recommendation_student"
            )
    )
    private Student student;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "job_posting_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_job_recommendation_job_posting"
            )
    )
    private JobPosting jobPosting;

    @Column(
            name = "match_score",
            nullable = false
    )
    private Double matchScore;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "job_recommendation_matched_skills",
            joinColumns = @JoinColumn(
                    name = "recommendation_id"
            ),
            foreignKey = @ForeignKey(
                    name = "fk_recommendation_matched_skills"
            )
    )
    @Column(
            name = "skill_name",
            nullable = false,
            length = 120
    )
    @Builder.Default
    private List<String> matchedSkills =
            new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "job_recommendation_missing_skills",
            joinColumns = @JoinColumn(
                    name = "recommendation_id"
            ),
            foreignKey = @ForeignKey(
                    name = "fk_recommendation_missing_skills"
            )
    )
    @Column(
            name = "skill_name",
            nullable = false,
            length = 120
    )
    @Builder.Default
    private List<String> missingSkills =
            new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "job_recommendation_reasons",
            joinColumns = @JoinColumn(
                    name = "recommendation_id"
            ),
            foreignKey = @ForeignKey(
                    name = "fk_recommendation_reasons"
            )
    )
    @Column(
            name = "reason_text",
            nullable = false,
            length = 500
    )
    @Builder.Default
    private List<String> reasons =
            new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(
            name = "recommendation_source",
            nullable = false,
            length = 50
    )
    private RecommendationSource source;

    @Column(
            name = "generated_at",
            nullable = false
    )
    private LocalDateTime generatedAt;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private LocalDateTime expiresAt;

    @Column(
            nullable = false
    )
    @Builder.Default
    private boolean active = true;

    @Column(
            name = "dismissed_at"
    )
    private LocalDateTime dismissedAt;

    @Version
    private Long version;

    @PrePersist
    public void initializeDefaults() {

        LocalDateTime now =
                LocalDateTime.now();

        if (generatedAt == null) {
            generatedAt = now;
        }

        if (expiresAt == null) {
            expiresAt = now.plusHours(24);
        }

        if (matchScore == null) {
            matchScore = 0.0;
        }
    }
}