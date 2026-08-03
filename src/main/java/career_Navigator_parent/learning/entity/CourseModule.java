package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "learning_course_modules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_learning_course_module_sequence",
                        columnNames = {
                                "course_id",
                                "sequence_number"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_learning_module_course",
                        columnList = "course_id"
                ),
                @Index(
                        name = "idx_learning_module_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_learning_module_mandatory",
                        columnList = "course_id, mandatory"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseModule extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "course_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_learning_module_course"
            )
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Course course;

    @Column(
            nullable = false,
            length = 220
    )
    private String title;

    @Lob
    @Column(
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(
            name = "sequence_number",
            nullable = false
    )
    private Integer sequenceNumber;

    @Column(
            name = "estimated_minutes",
            nullable = false
    )
    @Builder.Default
    private Integer estimatedMinutes = 1;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean mandatory = true;

    @Column(
            name = "preview_enabled",
            nullable = false
    )
    @Builder.Default
    private Boolean previewEnabled = false;

    @Column(
            name = "completion_percentage_required",
            nullable = false
    )
    @Builder.Default
    private Integer completionPercentageRequired = 100;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @OneToMany(
            mappedBy = "module",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sequenceNumber ASC")
    @Builder.Default
    private List<Lesson> lessons =
            new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version;

    public void addLesson(
            Lesson lesson
    ) {
        if (lesson == null) {
            return;
        }

        lesson.setModule(this);
        lessons.add(lesson);
    }

    public void removeLesson(
            Lesson lesson
    ) {
        if (lesson == null) {
            return;
        }

        lessons.remove(lesson);
        lesson.setModule(null);
    }

    public int activeLessonCount() {
        return Math.toIntExact(
                lessons.stream()
                        .filter(lesson ->
                                Boolean.TRUE.equals(
                                        lesson.getActive()
                                )
                        )
                        .count()
        );
    }

    @PrePersist
    @PreUpdate
    private void normalizeAndValidate() {

        if (title != null) {
            title = title.strip();
        }

        if (
                sequenceNumber == null
                        || sequenceNumber < 1
        ) {
            sequenceNumber = 1;
        }

        if (
                estimatedMinutes == null
                        || estimatedMinutes < 1
        ) {
            estimatedMinutes = 1;
        }

        if (
                completionPercentageRequired == null
                        || completionPercentageRequired < 1
                        || completionPercentageRequired > 100
        ) {
            completionPercentageRequired = 100;
        }

        if (mandatory == null) {
            mandatory = true;
        }

        if (previewEnabled == null) {
            previewEnabled = false;
        }

        if (active == null) {
            active = true;
        }
    }
}