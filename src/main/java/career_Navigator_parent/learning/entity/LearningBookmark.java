package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.BookmarkTargetType;
import career_Navigator_parent.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "learning_bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_learning_bookmark_target",
                        columnNames = {
                                "student_id",
                                "target_type",
                                "target_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_learning_bookmark_student",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_learning_bookmark_target",
                        columnList = "target_type, target_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningBookmark extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "target_type",
            nullable = false,
            length = 40
    )
    private BookmarkTargetType targetType;

    @Column(
            name = "target_id",
            nullable = false
    )
    private Long targetId;

    @Column(
            name = "target_title",
            nullable = false,
            length = 250
    )
    private String targetTitle;

    @Column(
            name = "target_slug",
            length = 250
    )
    private String targetSlug;
}