package careerpilot_parent.job.dto.response;

import careerpilot_parent.shared.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusHistoryResponse {

    private Long id;
    private Long applicationId;
    private ApplicationStatus previousStatus;
    private ApplicationStatus newStatus;
    private Long changedByUserId;
    private String changedByName;
    private String comment;
    private LocalDateTime changedAt;
}
