package career_Navigator_parent.interviewexperience.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "interview_experience_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_interview_experience_like",
                        columnNames = {
                                "interview_experience_id",
                                "user_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_experience_like_experience",
                        columnList = "interview_experience_id"
                ),
                @Index(
                        name = "idx_experience_like_user",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperienceLike extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "interview_experience_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_experience_like_experience"
            )
    )
    private InterviewExperience interviewExperience;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_experience_like_user"
            )
    )
    private User user;
}