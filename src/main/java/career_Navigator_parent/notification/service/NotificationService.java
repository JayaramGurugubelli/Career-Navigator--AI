package career_Navigator_parent.notification.service;

import career_Navigator_parent.notification.dto.response.NotificationResponse;
import career_Navigator_parent.notification.dto.response.UnreadNotificationCountResponse;
import career_Navigator_parent.notification.enums.NotificationReferenceType;
import career_Navigator_parent.notification.enums.NotificationType;
import career_Navigator_parent.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationResponse createNotification(
            User recipient,
            NotificationType type,
            String title,
            String message,
            NotificationReferenceType referenceType,
            Long referenceId,
            String actionUrl
    );

    NotificationResponse createNotification(
            Long recipientUserId,
            NotificationType type,
            String title,
            String message,
            NotificationReferenceType referenceType,
            Long referenceId,
            String actionUrl
    );

    Page<NotificationResponse> getMyNotifications(
            Boolean read,
            Pageable pageable
    );

    NotificationResponse getNotificationById(
            Long notificationId
    );

    UnreadNotificationCountResponse
    getUnreadNotificationCount();

    NotificationResponse markAsRead(
            Long notificationId
    );

    void markAllAsRead();

    void deleteNotification(
            Long notificationId
    );

    long clearReadNotifications();

    long clearAllNotifications();
}