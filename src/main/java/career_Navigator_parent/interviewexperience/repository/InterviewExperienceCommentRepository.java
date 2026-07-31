package career_Navigator_parent.interviewexperience.repository;

import career_Navigator_parent.interviewexperience.entity.InterviewExperienceComment;
import career_Navigator_parent.interviewexperience.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewExperienceCommentRepository
        extends JpaRepository<InterviewExperienceComment, Long> {

    /*
     * Returns public top-level comments.
     *
     * Conditions:
     * - Comment belongs to the requested experience
     * - Comment has no parent, so it is not a reply
     * - Comment status is VISIBLE
     * - Comment has not been deleted
     */
    @EntityGraph(
            attributePaths = {
                    "user"
            }
    )
    Page<InterviewExperienceComment>
    findByInterviewExperience_IdAndParentCommentIsNullAndStatusAndDeletedFalseOrderByCreatedAtAsc(
            Long interviewExperienceId,
            CommentStatus status,
            Pageable pageable
    );

    /*
     * Returns visible replies belonging to a particular parent comment.
     */
    @EntityGraph(
            attributePaths = {
                    "user"
            }
    )
    List<InterviewExperienceComment>
    findByParentComment_IdAndStatusAndDeletedFalseOrderByCreatedAtAsc(
            Long parentCommentId,
            CommentStatus status
    );

    /*
     * Used when the comment owner wants to update or delete their comment.
     */
    Optional<InterviewExperienceComment>
    findByIdAndUser_Id(
            Long commentId,
            Long userId
    );

    /*
     * Ensures that the requested comment belongs to the requested experience.
     */
    Optional<InterviewExperienceComment>
    findByIdAndInterviewExperience_Id(
            Long commentId,
            Long interviewExperienceId
    );

    /*
     * Used when replying to a visible comment.
     */
    Optional<InterviewExperienceComment>
    findByIdAndInterviewExperience_IdAndStatusAndDeletedFalse(
            Long commentId,
            Long interviewExperienceId,
            CommentStatus status
    );

    long countByInterviewExperience_IdAndStatusAndDeletedFalse(
            Long interviewExperienceId,
            CommentStatus status
    );

    long countByParentComment_IdAndStatusAndDeletedFalse(
            Long parentCommentId,
            CommentStatus status
    );

    List<InterviewExperienceComment>
    findByUser_IdOrderByCreatedAtDesc(
            Long userId
    );

    void deleteAllByInterviewExperience_Id(
            Long interviewExperienceId
    );
    @EntityGraph(attributePaths = {"user"})
    Page<InterviewExperienceComment>
    findByParentComment_IdAndStatusAndDeletedFalseOrderByCreatedAtAsc(
            Long parentCommentId,
            CommentStatus status,
            Pageable pageable
    );
    long countByInterviewExperience_IdAndDeletedFalse(
            Long interviewExperienceId
    );

    long countByParentComment_IdAndDeletedFalse(
            Long parentCommentId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update InterviewExperienceComment c
        set c.replyCount = coalesce(c.replyCount, 0) + 1
        where c.id = :commentId
        """)
    int incrementReplyCount(
            @Param("commentId") Long commentId
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update InterviewExperienceComment c
        set c.replyCount =
            case
                when coalesce(c.replyCount, 0) > 0
                then c.replyCount - 1
                else 0
            end
        where c.id = :commentId
        """)
    int decrementReplyCount(
            @Param("commentId") Long commentId
    );

    @Query("""
        select coalesce(c.replyCount, 0)
        from InterviewExperienceComment c
        where c.id = :commentId
        """)
    Optional<Integer> findReplyCount(
            @Param("commentId") Long commentId
    );
}