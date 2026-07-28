package careerpilot_parent.interviewexperience.dto.response;

import careerpilot_parent.interviewexperience.enums.InterviewExperienceStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewExperienceDetailResponse {

    private Long id;

    private Long companyId;

    private String companyName;

    private String jobRole;

    private String experienceLevel;

    private String location;

    private String preparationTips;

    private Boolean anonymous;

    private Boolean verified;

    private InterviewContributorResponse contributor;

    private Integer likeCount;

    private Integer commentCount;

    private Boolean likedByCurrentUser;

    private Boolean ownedByCurrentUser;

    /*
     * Return this for the owner/admin.
     * It can be omitted or null for normal public users.
     */
    private InterviewExperienceStatus status;

    @Builder.Default
    private List<InterviewRoundResponse> rounds =
            new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}