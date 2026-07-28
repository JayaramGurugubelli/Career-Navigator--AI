package careerpilot_parent.interviewexperience.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewContributorResponse {

    private String displayName;

    private String profilePictureUrl;
}