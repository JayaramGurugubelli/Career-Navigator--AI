package careerpilot_parent.notification.service.impl;

import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.notification.dto.response.NotificationResponse;
import careerpilot_parent.notification.dto.response.UnreadNotificationCountResponse;
import careerpilot_parent.notification.entity.Notification;
import careerpilot_parent.notification.enums.NotificationReferenceType;
import careerpilot_parent.notification.enums.NotificationType;
import careerpilot_parent.notification.mapper.NotificationMapper;
import careerpilot_parent.notification.repository.NotificationRepository;
import careerpilot_parent.notification.service.NotificationService;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository
            notificationRepository;

    private final UserRepository
            userRepository;

    private final NotificationMapper
            notificationMapper;

    private final SecurityUtils
            securityUtils;

    @Override
    public NotificationResponse createNotification(
            User recipient,
            NotificationType type,
            String title,
            String message,
            NotificationReferenceType referenceType,
            Long referenceId,
            String actionUrl
    ) {

        validateNotificationData(
                recipient,
                type,
                title,
                message,
                referenceType,
                referenceId
        );

        Notification notification =
                Notification.builder()
                        .recipientUser(recipient)
                        .type(type)
                        .title(normalizeRequiredText(
                                title,
                                "Notification title is required."
                        ))
                        .message(normalizeRequiredText(
                                message,
                                "Notification message is required."
                        ))
                        .referenceType(referenceType)
                        .referenceId(referenceId)
                        .actionUrl(normalize(actionUrl))
                        .read(false)
                        .build();

        Notification savedNotification =
                notificationRepository.save(
                        notification
                );

        return notificationMapper.toResponse(
                savedNotification
        );
    }

    @Override
    public NotificationResponse createNotification(
            Long recipientUserId,
            NotificationType type,
            String title,
            String message,
            NotificationReferenceType referenceType,
            Long referenceId,
            String actionUrl
    ) {

        User recipient =
                userRepository.findById(recipientUserId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Notification recipient user not found."
                                )
                        );

        return createNotification(
                recipient,
                type,
                title,
                message,
                referenceType,
                referenceId,
                actionUrl
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse>
    getMyNotifications(
            Boolean read,
            Pageable pageable
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Page<Notification> notifications;

        if (read == null) {

            notifications =
                    notificationRepository
                            .findByRecipientUserId(
                                    currentUserId,
                                    pageable
                            );

        } else {

            notifications =
                    notificationRepository
                            .findByRecipientUserIdAndRead(
                                    currentUserId,
                                    read,
                                    pageable
                            );
        }

        return notifications.map(
                notificationMapper::toResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(
            Long notificationId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Notification notification =
                getUserNotification(
                        notificationId,
                        currentUserId
                );

        return notificationMapper.toResponse(
                notification
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse
    getUnreadNotificationCount() {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        long unreadCount =
                notificationRepository
                        .countByRecipientUserIdAndReadFalse(
                                currentUserId
                        );

        return UnreadNotificationCountResponse
                .builder()
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    public NotificationResponse markAsRead(
            Long notificationId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Notification notification =
                getUserNotification(
                        notificationId,
                        currentUserId
                );

        /*
         * Idempotent operation:
         * marking an already-read notification again
         * does not change readAt.
         */
        if (!notification.isRead()) {

            notification.setRead(true);

            notification.setReadAt(
                    LocalDateTime.now()
            );

            notification =
                    notificationRepository.save(
                            notification
                    );
        }

        return notificationMapper.toResponse(
                notification
        );
    }

    @Override
    public void markAllAsRead() {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Page<Notification> unreadPage =
                notificationRepository
                        .findByRecipientUserIdAndRead(
                                currentUserId,
                                false,
                                Pageable.unpaged()
                        );

        if (unreadPage.isEmpty()) {
            return;
        }

        LocalDateTime now =
                LocalDateTime.now();

        List<Notification> notifications =
                unreadPage.getContent();

        notifications.forEach(notification -> {

            notification.setRead(true);
            notification.setReadAt(now);
        });

        notificationRepository.saveAll(
                notifications
        );
    }

    @Override
    public void deleteNotification(
            Long notificationId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Notification notification =
                getUserNotification(
                        notificationId,
                        currentUserId
                );

        notificationRepository.delete(
                notification
        );
    }

    @Override
    public long clearReadNotifications() {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        return notificationRepository
                .deleteByRecipientUserIdAndReadTrue(
                        currentUserId
                );
    }

    @Override
    public long clearAllNotifications() {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        return notificationRepository
                .deleteByRecipientUserId(
                        currentUserId
                );
    }

    private Notification getUserNotification(
            Long notificationId,
            Long userId
    ) {

        return notificationRepository
                .findByIdAndRecipientUserId(
                        notificationId,
                        userId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Notification not found."
                        )
                );
    }

    private void validateNotificationData(
            User recipient,
            NotificationType type,
            String title,
            String message,
            NotificationReferenceType referenceType,
            Long referenceId
    ) {

        if (recipient == null ||
                recipient.getId() == null) {

            throw new IllegalArgumentException(
                    "Notification recipient is required."
            );
        }

        if (type == null) {

            throw new IllegalArgumentException(
                    "Notification type is required."
            );
        }

        normalizeRequiredText(
                title,
                "Notification title is required."
        );

        normalizeRequiredText(
                message,
                "Notification message is required."
        );

        if (referenceId != null &&
                referenceType == null) {

            throw new IllegalArgumentException(
                    "Reference type is required when reference ID is provided."
            );
        }

        if (referenceType != null &&
                referenceType !=
                        NotificationReferenceType.SYSTEM &&
                referenceId == null) {

            throw new IllegalArgumentException(
                    "Reference ID is required for "
                            + referenceType
                            + " notifications."
            );
        }
    }

    private String normalizeRequiredText(
            String value,
            String errorMessage
    ) {

        String normalized = normalize(value);

        if (normalized == null) {
            throw new IllegalArgumentException(
                    errorMessage
            );
        }

        return normalized;
    }

    private String normalize(
            String value
    ) {

        if (value == null ||
                value.isBlank()) {

            return null;
        }

        return value.trim();
    }
}