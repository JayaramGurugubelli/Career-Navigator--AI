package careerpilot_parent.jobrecommendation.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationReasonResponse {

    private String category;

    private String message;

    private Double scoreContribution;
}