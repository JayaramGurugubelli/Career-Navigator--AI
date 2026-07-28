package careerpilot_parent.interviewexperience.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperienceLikeResponse {

    private Long interviewExperienceId;

    private Boolean liked;

    private Integer likeCount;
}