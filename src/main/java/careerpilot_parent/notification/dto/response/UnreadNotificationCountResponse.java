package careerpilot_parent.notification.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnreadNotificationCountResponse {

    private long unreadCount;
}