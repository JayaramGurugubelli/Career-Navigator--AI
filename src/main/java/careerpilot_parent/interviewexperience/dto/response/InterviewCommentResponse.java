package careerpilot_parent.interviewexperience.dto.response;

import careerpilot_parent.interviewexperience.enums.CommentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewCommentResponse {

    private Long id;

    private Long interviewExperienceId;

    private Long parentCommentId;

    private String content;

    private CommentAuthorResponse author;

    private CommentStatus status;

    private Boolean edited;

    private Boolean deleted;

    private Integer replyCount;

    private Boolean ownedByCurrentUser;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}