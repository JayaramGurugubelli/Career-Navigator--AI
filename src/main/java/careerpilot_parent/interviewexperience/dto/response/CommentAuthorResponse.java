package careerpilot_parent.interviewexperience.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentAuthorResponse {

    private String displayName;

    private String profilePictureUrl;

    /*
     * Indicates that the commenter created the interview experience.
     */
    private Boolean postAuthor;

    /*
     * Allows Angular to show edit/delete buttons.
     */
    private Boolean currentUser;
}