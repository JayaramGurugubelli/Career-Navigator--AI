package careerpilot_parent.interviewexperience.mapper;

import careerpilot_parent.interviewexperience.dto.response.CommentAuthorResponse;
import careerpilot_parent.interviewexperience.dto.response.InterviewCommentResponse;
import careerpilot_parent.interviewexperience.entity.InterviewExperience;
import careerpilot_parent.interviewexperience.entity.InterviewExperienceComment;
import careerpilot_parent.interviewexperience.enums.CommentStatus;
import careerpilot_parent.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InterviewExperienceCommentMapper {

    public InterviewExperienceComment toEntity(
            InterviewExperience experience,
            User user,
            String validatedContent
    ) {

        return InterviewExperienceComment.builder()
                .interviewExperience(experience)
                .user(user)
                .parentComment(null)
                .content(validatedContent)
                .status(CommentStatus.VISIBLE)
                .edited(false)
                .deleted(false)
                .replyCount(0)
                .reportCount(0)
                .build();
    }

    public InterviewExperienceComment toReplyEntity(
            InterviewExperience experience,
            InterviewExperienceComment parentComment,
            User user,
            String validatedContent
    ) {

        return InterviewExperienceComment.builder()
                .interviewExperience(experience)
                .user(user)
                .parentComment(parentComment)
                .content(validatedContent)
                .status(CommentStatus.VISIBLE)
                .edited(false)
                .deleted(false)
                .replyCount(0)
                .reportCount(0)
                .build();
    }

    public void updateEntity(
            InterviewExperienceComment comment,
            String validatedContent
    ) {

        comment.setContent(validatedContent);
        comment.setEdited(true);
    }

    public InterviewCommentResponse toResponse(
            InterviewExperienceComment comment,
            Long currentUserId
    ) {

        InterviewExperience experience =
                comment.getInterviewExperience();

        Long postOwnerId =
                experience != null
                        && experience.getSubmittedBy() != null
                        ? experience.getSubmittedBy().getId()
                        : null;

        Long commentOwnerId =
                comment.getUser() == null
                        ? null
                        : comment.getUser().getId();

        boolean ownedByCurrentUser =
                currentUserId != null
                        && Objects.equals(
                        commentOwnerId,
                        currentUserId
                );

        boolean postAuthor =
                postOwnerId != null
                        && Objects.equals(
                        postOwnerId,
                        commentOwnerId
                );

        String content =
                Boolean.TRUE.equals(comment.getDeleted())
                        || comment.getStatus()
                        == CommentStatus.REMOVED
                        ? "This comment was removed."
                        : comment.getContent();

        return InterviewCommentResponse.builder()
                .id(comment.getId())
                .interviewExperienceId(
                        experience == null
                                ? null
                                : experience.getId()
                )
                .parentCommentId(
                        comment.getParentComment() == null
                                ? null
                                : comment.getParentComment().getId()
                )
                .content(content)
                .author(
                        toAuthorResponse(
                                comment.getUser(),
                                postAuthor,
                                ownedByCurrentUser,
                                experience
                        )
                )
                .status(
                        comment.getStatus()
                )
                .edited(
                        Boolean.TRUE.equals(
                                comment.getEdited()
                        )
                )
                .deleted(
                        Boolean.TRUE.equals(
                                comment.getDeleted()
                        )
                )
                .replyCount(
                        comment.getReplyCount() == null
                                ? 0
                                : comment.getReplyCount()
                )
                .ownedByCurrentUser(
                        ownedByCurrentUser
                )
                .createdAt(
                        comment.getCreatedAt()
                )
                .updatedAt(
                        comment.getUpdatedAt()
                )
                .build();
    }

    private CommentAuthorResponse toAuthorResponse(
            User user,
            boolean postAuthor,
            boolean currentUser,
            InterviewExperience experience
    ) {

        /*
         * When the post owner created the experience anonymously,
         * keep that identity anonymous while responding in the thread.
         */
        if (postAuthor
                && experience != null
                && Boolean.TRUE.equals(
                experience.getAnonymous()
        )) {

            return CommentAuthorResponse.builder()
                    .displayName(
                            "Anonymous Contributor"
                    )
                    .profilePictureUrl(null)
                    .postAuthor(true)
                    .currentUser(currentUser)
                    .build();
        }

        return CommentAuthorResponse.builder()
                .displayName(
                        resolveDisplayName(user)
                )
                /*
                 * Connect this later to your profile picture endpoint.
                 */
                .profilePictureUrl(null)
                .postAuthor(postAuthor)
                .currentUser(currentUser)
                .build();
    }

    private String resolveDisplayName(
            User user
    ) {

        if (user == null) {
            return "CareerPilot User";
        }

        String firstName =
                normalizeNullable(
                        user.getFirstName()
                );

        String lastName =
                normalizeNullable(
                        user.getLastName()
                );

        String displayName =
                String.join(
                        " ",
                        firstName == null
                                ? ""
                                : firstName,
                        lastName == null
                                ? ""
                                : lastName
                ).trim();

        return displayName.isBlank()
                ? "CareerPilot User"
                : displayName;
    }

    private String normalizeNullable(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value
                .strip()
                .replaceAll(
                        "[\\p{Z}\\s]+",
                        " "
                );
    }
}