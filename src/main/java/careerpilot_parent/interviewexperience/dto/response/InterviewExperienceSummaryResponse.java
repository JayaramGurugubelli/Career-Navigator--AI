package careerpilot_parent.interviewexperience.dto.response;

import careerpilot_parent.interviewexperience.enums.InterviewExperienceStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperienceSummaryResponse {

    private Long id;

    private Long companyId;

    private String companyName;

    private String jobRole;

    private String experienceLevel;

    private String location;

    private Boolean anonymous;

    private Boolean verified;

    private InterviewContributorResponse contributor;

    private Integer roundCount;

    private Integer questionCount;

    private Integer likeCount;

    private Integer commentCount;

    private Boolean likedByCurrentUser;

    /*
     * Useful for the owner/admin APIs.
     * It may be null in the public API.
     */
    private InterviewExperienceStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}