package careerpilot_parent.interviewexperience.entity;

import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.interviewexperience.enums.CommentStatus;
import careerpilot_parent.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "interview_experience_comments",
        indexes = {
                @Index(
                        name = "idx_experience_comment_experience",
                        columnList = "interview_experience_id"
                ),
                @Index(
                        name = "idx_experience_comment_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_experience_comment_parent",
                        columnList = "parent_comment_id"
                ),
                @Index(
                        name = "idx_experience_comment_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_experience_comment_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperienceComment extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "interview_experience_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_experience_comment_experience"
            )
    )
    private InterviewExperience interviewExperience;

    /*
     * Stored internally to identify the comment owner.
     * Never expose email or phone number in the response DTO.
     */
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_experience_comment_user"
            )
    )
    private User user;

    /*
     * Null means this is a top-level comment.
     * Non-null means this is a reply.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "parent_comment_id",
            foreignKey = @ForeignKey(
                    name = "fk_experience_comment_parent"
            )
    )
    private InterviewExperienceComment parentComment;

    @OneToMany(
            mappedBy = "parentComment",
            cascade = CascadeType.ALL
    )
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<InterviewExperienceComment> replies =
            new ArrayList<>();

    @Column(
            name = "content",
            nullable = false,
            length = 1000
    )
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private CommentStatus status =
            CommentStatus.VISIBLE;

    @Column(
            name = "edited",
            nullable = false
    )
    @Builder.Default
    private Boolean edited = false;

    /*
     * Soft deletion preserves replies and discussion structure.
     */
    @Column(
            name = "deleted",
            nullable = false
    )
    @Builder.Default
    private Boolean deleted = false;

    @Column(
            name = "reply_count",
            nullable = false
    )
    @Builder.Default
    private Integer replyCount = 0;

    @Column(
            name = "report_count",
            nullable = false
    )
    @Builder.Default
    private Integer reportCount = 0;

    public boolean isReply() {
        return parentComment != null;
    }

    public void addReply(
            InterviewExperienceComment reply
    ) {

        replies.add(reply);
        reply.setParentComment(this);
        reply.setInterviewExperience(
                this.interviewExperience
        );

        replyCount++;
    }

    public void removeReply(
            InterviewExperienceComment reply
    ) {

        replies.remove(reply);
        reply.setParentComment(null);

        if (replyCount > 0) {
            replyCount--;
        }
    }

    public void markEdited() {
        this.edited = true;
    }

    public void softDelete() {

        this.deleted = true;
        this.status = CommentStatus.REMOVED;
        this.content = null;
    }

    public void incrementReportCount() {
        this.reportCount++;
    }
}