package careerpilot_parent.notification.repository;

import careerpilot_parent.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    Page<Notification>
    findByRecipientUserId(
            Long recipientUserId,
            Pageable pageable
    );

    Page<Notification>
    findByRecipientUserIdAndRead(
            Long recipientUserId,
            boolean read,
            Pageable pageable
    );

    Optional<Notification>
    findByIdAndRecipientUserId(
            Long notificationId,
            Long recipientUserId
    );

    long countByRecipientUserIdAndReadFalse(
            Long recipientUserId
    );

    long deleteByRecipientUserIdAndReadTrue(
            Long recipientUserId
    );

    long deleteByRecipientUserId(
            Long recipientUserId
    );
}