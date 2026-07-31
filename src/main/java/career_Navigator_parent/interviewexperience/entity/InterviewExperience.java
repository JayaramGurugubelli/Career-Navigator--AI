package career_Navigator_parent.interviewexperience.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.company.entity.Company;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceStatus;
import career_Navigator_parent.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "interview_experiences",
        indexes = {
                @Index(
                        name = "idx_interview_experience_company",
                        columnList = "company_id"
                ),
                @Index(
                        name = "idx_interview_experience_user",
                        columnList = "submitted_by_user_id"
                ),
                @Index(
                        name = "idx_interview_experience_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_interview_experience_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperience extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "submitted_by_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_interview_experience_user"
            )
    )
    private User submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "company_id",
            foreignKey = @ForeignKey(
                    name = "fk_interview_experience_company"
            )
    )
    private Company company;

    /*
     * Used when a company does not yet exist in the Company table.
     */
    @Column(
            name = "company_name",
            nullable = false,
            length = 150
    )
    private String companyName;

    @Column(
            name = "job_role",
            nullable = false,
            length = 150
    )
    private String jobRole;

    /*
     * Keep this as String initially because the contributor may write:
     * Fresher, Internship, 1-2 Years, Experienced, Campus Placement, etc.
     */
    @Column(
            name = "experience_level",
            length = 100
    )
    private String experienceLevel;

    @Column(
            name = "location",
            length = 150
    )
    private String location;

    @Column(
            name = "preparation_tips",
            columnDefinition = "TEXT"
    )
    private String preparationTips;

    @Column(
            name = "anonymous",
            nullable = false
    )
    @Builder.Default
    private Boolean anonymous = true;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private InterviewExperienceStatus status =
            InterviewExperienceStatus.PENDING_REVIEW;

    @Column(
            name = "verified",
            nullable = false
    )
    @Builder.Default
    private Boolean verified = false;

    @Column(
            name = "like_count",
            nullable = false
    )
    @Builder.Default
    private Integer likeCount = 0;

    @Column(
            name = "comment_count",
            nullable = false
    )
    @Builder.Default
    private Integer commentCount = 0;

    @Column(
            name = "report_count",
            nullable = false
    )
    @Builder.Default
    private Integer reportCount = 0;

    @BatchSize(size = 50)
    @OneToMany(
            mappedBy = "interviewExperience",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<InterviewExperienceRound> rounds =
            new ArrayList<>();

    public void addRound(
            InterviewExperienceRound round
    ) {

        rounds.add(round);
        round.setInterviewExperience(this);
    }

    public void removeRound(
            InterviewExperienceRound round
    ) {

        rounds.remove(round);
        round.setInterviewExperience(null);
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {

        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void decrementCommentCount() {

        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void incrementReportCount() {
        this.reportCount++;
    }

}