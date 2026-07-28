package careerpilot_parent.analytics.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceAnalyticsResponse {

    private String source;
    private long applicationCount;
    private long interviewCount;
    private long offerCount;
    private long hireCount;

    private double interviewConversionRate;
    private double hireConversionRate;
}