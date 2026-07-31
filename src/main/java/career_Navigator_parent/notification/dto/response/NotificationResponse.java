package career_Navigator_parent.notification.dto.response;

import career_Navigator_parent.notification.enums.NotificationReferenceType;
import career_Navigator_parent.notification.enums.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;

    private NotificationType type;

    private String title;

    private String message;

    private NotificationReferenceType referenceType;

    private Long referenceId;

    private String actionUrl;

    private boolean read;

    private LocalDateTime readAt;

    private LocalDateTime createdAt;
}