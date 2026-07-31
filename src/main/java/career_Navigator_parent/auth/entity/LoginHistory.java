package career_Navigator_parent.auth.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "login_history",
        indexes = {
                @Index(name = "idx_login_user", columnList = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_login_history_user")
    )
    private User user;

    @Column(length = 50)
    private String ipAddress;

    @Column(length = 255)
    private String deviceName;

    @Column(length = 255)
    private String browser;

    @Column(length = 255)
    private String operatingSystem;

    @Builder.Default
    @Column(nullable = false)
    private Boolean success = true;
}