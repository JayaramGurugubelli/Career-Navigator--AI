package career_Navigator_parent.analytics.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationFunnelResponse {

    private long totalApplications;
    private long applied;
    private long underReview;
    private long shortlisted;
    private long interviewScheduled;
    private long interviewed;
    private long offered;
    private long hired;
    private long rejected;
    private long withdrawn;
}