package careerpilot_parent.notification.mapper;

import careerpilot_parent.notification.dto.response.NotificationResponse;
import careerpilot_parent.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(
            Notification notification
    ) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceType(
                        notification.getReferenceType()
                )
                .referenceId(
                        notification.getReferenceId()
                )
                .actionUrl(
                        notification.getActionUrl()
                )
                .read(notification.isRead())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}