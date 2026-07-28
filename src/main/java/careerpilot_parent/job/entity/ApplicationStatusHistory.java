package careerpilot_parent.job.entity;

import careerpilot_parent.common.entity.BaseEntity;
import careerpilot_parent.shared.enums.ApplicationStatus;
import careerpilot_parent.user.entity.User;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "application_status_history",
        indexes = {
                @Index(
                        name = "idx_status_history_application",
                        columnList = "application_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusHistory extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "application_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_status_history_application"
            )
    )
    private JobApplication application;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "previous_status",
            length = 40
    )
    private ApplicationStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "new_status",
            nullable = false,
            length = 40
    )
    private ApplicationStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "changed_by_user_id",
            foreignKey = @ForeignKey(
                    name = "fk_status_history_changed_by"
            )
    )
    private User changedBy;

    @Column(
            columnDefinition = "TEXT"
    )
    private String comment;
}