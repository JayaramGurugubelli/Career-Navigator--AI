package career_Navigator_parent.auth.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "device_sessions",
        indexes = {
                @Index(name = "idx_device_user", columnList = "user_id"),
                @Index(name = "idx_device_refresh_token", columnList = "refreshToken")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceSession extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_device_session_user")
    )
    private User user;

    @Column(length = 255)
    private String deviceName;

    @Column(length = 255)
    private String browser;

    @Column(length = 50)
    private String ipAddress;

    @Column(nullable = false, unique = true, length = 500)
    private String refreshToken;

    private LocalDateTime lastActive;

    @Builder.Default
    @Column(nullable = false)
    private Boolean expired = false;
}