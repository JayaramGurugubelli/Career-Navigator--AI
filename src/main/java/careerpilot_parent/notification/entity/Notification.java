package careerpilot_parent.notification.entity;

import careerpilot_parent.notification.enums.NotificationReferenceType;
import careerpilot_parent.notification.enums.NotificationType;
import careerpilot_parent.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "notifications",
        indexes = {
                @Index(
                        name = "idx_notification_recipient",
                        columnList = "recipient_user_id"
                ),
                @Index(
                        name = "idx_notification_recipient_read",
                        columnList = "recipient_user_id, is_read"
                ),
                @Index(
                        name = "idx_notification_type",
                        columnList = "notification_type"
                ),
                @Index(
                        name = "idx_notification_reference",
                        columnList = "reference_type, reference_id"
                ),
                @Index(
                        name = "idx_notification_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "recipient_user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_notification_recipient_user"
            )
    )
    private User recipientUser;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "notification_type",
            nullable = false,
            length = 60
    )
    private NotificationType type;

    @Column(
            nullable = false,
            length = 200
    )
    private String title;

    @Column(
            nullable = false,
            length = 2000
    )
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "reference_type",
            length = 40
    )
    private NotificationReferenceType referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(
            name = "action_url",
            length = 1000
    )
    private String actionUrl;

    @Column(
            name = "is_read",
            nullable = false
    )
    private boolean read;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
        read = false;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}