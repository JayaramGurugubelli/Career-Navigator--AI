package careerpilot_parent.audit.entity;

import careerpilot_parent.audit.enums.AuditAction;
import careerpilot_parent.audit.enums.AuditEntityType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(
                        name = "idx_audit_user_id",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_audit_action",
                        columnList = "action"
                ),
                @Index(
                        name = "idx_audit_entity_type",
                        columnList = "entity_type"
                ),
                @Index(
                        name = "idx_audit_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    private static final ZoneId APPLICATION_ZONE =
            ZoneId.of("Asia/Kolkata");

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(
            name = "username",
            length = 150
    )
    private String username;

    @Column(
            name = "user_role",
            length = 100
    )
    private String userRole;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "action",
            nullable = false,
            length = 80
    )
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "entity_type",
            nullable = false,
            length = 80
    )
    private AuditEntityType entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(
            name = "description",
            length = 1000
    )
    private String description;

    @Lob
    @Column(
            name = "old_value",
            columnDefinition = "LONGTEXT"
    )
    private String oldValue;

    @Lob
    @Column(
            name = "new_value",
            columnDefinition = "LONGTEXT"
    )
    private String newValue;

    @Column(
            name = "ip_address",
            length = 100
    )
    private String ipAddress;

    @Column(
            name = "user_agent",
            length = 1000
    )
    private String userAgent;

    @Column(
            name = "request_method",
            length = 20
    )
    private String requestMethod;

    @Column(
            name = "request_path",
            length = 1000
    )
    private String requestPath;

    @Column(
            name = "class_name",
            length = 255
    )
    private String className;

    @Column(
            name = "method_name",
            length = 255
    )
    private String methodName;

    @Column(
            name = "success",
            nullable = false
    )
    private Boolean success;

    @Column(
            name = "failure_reason",
            length = 2000
    )
    private String failureReason;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {

            createdAt =
                    LocalDateTime.now(
                            APPLICATION_ZONE
                    );
        }

        if (success == null) {
            success = true;
        }
    }
}