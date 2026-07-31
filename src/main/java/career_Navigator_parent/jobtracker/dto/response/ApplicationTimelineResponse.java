package career_Navigator_parent.jobtracker.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationTimelineResponse {

    private String event;

    private String title;

    private String description;

    private LocalDateTime occurredAt;

    private boolean completed;

    /**
     * APPLICATION, INTERVIEW, OFFER, SYSTEM
     */
    private String sourceType;

    private Long sourceId;
}