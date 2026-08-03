package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.LessonType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "learning_lessons",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_module_lesson_sequence",
                        columnNames = {
                                "module_id",
                                "sequence_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_lesson_module",
                        columnList = "module_id"
                ),
                @Index(
                        name = "idx_lesson_type",
                        columnList = "lesson_type"
                ),
                @Index(
                        name = "idx_lesson_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_lesson_preview",
                        columnList = "preview"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "module_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_lesson_module"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseModule module;

    @Column(
            nullable = false,
            length = 250
    )
    private String title;

    @Lob
    @Column(
            name = "summary",
            columnDefinition = "TEXT"
    )
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "lesson_type",
            nullable = false,
            length = 40
    )
    private LessonType lessonType;

    @Lob
    @Column(
            name = "content",
            columnDefinition = "LONGTEXT"
    )
    private String content;

    @Column(
            name = "video_url",
            length = 1500
    )
    private String videoUrl;

    @Column(
            name = "external_url",
            length = 1500
    )
    private String externalUrl;

    @Column(
            name = "file_url",
            length = 1500
    )
    private String fileUrl;

    @Column(
            name = "duration_minutes",
            nullable = false
    )
    @Builder.Default
    private Integer durationMinutes = 1;

    @Column(
            name = "sequence_number",
            nullable = false
    )
    private Integer sequenceNumber;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean preview = false;

    @Column(
            name = "completion_required",
            nullable = false
    )
    @Builder.Default
    private Boolean completionRequired = true;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @OneToMany(
            mappedBy = "lesson",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<LearningResource> resources =
            new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version;

    public void addResource(
            LearningResource resource
    ) {
        if (resource == null) {
            return;
        }

        resource.setLesson(this);
        resources.add(resource);
    }

    @PrePersist
    @PreUpdate
    private void normalize() {

        if (title != null) {
            title = title.strip();
        }

        if (
                durationMinutes == null
                        || durationMinutes < 1
        ) {
            durationMinutes = 1;
        }

        if (
                sequenceNumber == null
                        || sequenceNumber < 1
        ) {
            sequenceNumber = 1;
        }

        if (preview == null) {
            preview = false;
        }

        if (completionRequired == null) {
            completionRequired = true;
        }

        if (active == null) {
            active = true;
        }
    }
}