package career_Navigator_parent.analytics.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HiringTrendResponse {

    /**
     * Format: yyyy-MM
     */
    private String period;

    private long applications;
    private long interviews;
    private long offers;
    private long hires;
}