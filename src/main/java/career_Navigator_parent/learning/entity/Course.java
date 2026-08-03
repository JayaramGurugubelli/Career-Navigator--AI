package career_Navigator_parent.learning.entity;

import career_Navigator_parent.common.entity.BaseEntity;
import career_Navigator_parent.learning.enums.ContentStatus;
import career_Navigator_parent.learning.enums.CourseType;
import career_Navigator_parent.learning.enums.LearningLevel;
import career_Navigator_parent.learning.enums.ProviderType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(
        name = "learning_courses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_learning_course_slug_version",
                        columnNames = {
                                "slug",
                                "course_version"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_learning_course_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_learning_course_type",
                        columnList = "course_type"
                ),
                @Index(
                        name = "idx_learning_course_provider",
                        columnList = "provider_type"
                ),
                @Index(
                        name = "idx_learning_course_level",
                        columnList = "level"
                ),
                @Index(
                        name = "idx_learning_course_active",
                        columnList = "active"
                ),
                @Index(
                        name = "idx_learning_course_featured",
                        columnList = "featured"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseEntity {

    @Column(
            nullable = false,
            length = 220
    )
    private String title;

    @Column(
            nullable = false,
            length = 240
    )
    private String slug;

    @Lob
    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;

    @Lob
    @Column(
            name = "learning_outcomes",
            columnDefinition = "TEXT"
    )
    private String learningOutcomes;

    @Lob
    @Column(
            name = "prerequisite_description",
            columnDefinition = "TEXT"
    )
    private String prerequisiteDescription;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "course_type",
            nullable = false,
            length = 40
    )
    private CourseType courseType;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private LearningLevel level;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "provider_type",
            nullable = false,
            length = 50
    )
    private ProviderType providerType;

    @Column(
            name = "provider_name",
            length = 180
    )
    private String providerName;

    @Column(
            name = "instructor_name",
            length = 180
    )
    private String instructorName;

    @Column(
            name = "external_course_url",
            length = 1500
    )
    private String externalCourseUrl;

    @Column(
            name = "thumbnail_url",
            length = 1500
    )
    private String thumbnailUrl;

    @Column(
            name = "language",
            nullable = false,
            length = 60
    )
    @Builder.Default
    private String language = "English";

    @Column(
            name = "estimated_duration_hours",
            nullable = false
    )
    private Integer estimatedDurationHours;

    @Column(
            name = "certificate_enabled",
            nullable = false
    )
    @Builder.Default
    private Boolean certificateEnabled = false;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean free = true;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean featured = false;

    @Column(
            nullable = false
    )
    @Builder.Default
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private ContentStatus status = ContentStatus.DRAFT;

    @Column(
            name = "course_version",
            nullable = false
    )
    @Builder.Default
    private Integer courseVersion = 1;

    @Column(
            name = "average_rating",
            nullable = false
    )
    @Builder.Default
    private Double averageRating = 0.0;

    @Column(
            name = "rating_count",
            nullable = false
    )
    @Builder.Default
    private Long ratingCount = 0L;

    @Column(
            name = "enrollment_count",
            nullable = false
    )
    @Builder.Default
    private Long enrollmentCount = 0L;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "learning_course_disciplines",
            joinColumns = @JoinColumn(
                    name = "course_id",
                    foreignKey = @ForeignKey(
                            name = "fk_course_discipline_course"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "discipline_id",
                    foreignKey = @ForeignKey(
                            name = "fk_course_discipline_discipline"
                    )
            ),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_course_discipline",
                            columnNames = {
                                    "course_id",
                                    "discipline_id"
                            }
                    )
            }
    )
    @Builder.Default
    private Set<AcademicDiscipline> disciplines =
            new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "learning_course_prerequisites",
            joinColumns = @JoinColumn(
                    name = "course_id",
                    foreignKey = @ForeignKey(
                            name = "fk_course_prerequisite_course"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "prerequisite_course_id",
                    foreignKey = @ForeignKey(
                            name = "fk_course_prerequisite_required"
                    )
            ),
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_course_prerequisite",
                            columnNames = {
                                    "course_id",
                                    "prerequisite_course_id"
                            }
                    )
            }
    )
    @Builder.Default
    private Set<Course> prerequisites =
            new LinkedHashSet<>();

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("sequenceNumber ASC")
    @Builder.Default
    private List<CourseModule> modules =
            new ArrayList<>();

    @OneToMany(
            mappedBy = "course",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<LearningResource> resources =
            new ArrayList<>();

    @Version
    @Column(nullable = false)
    private Long version;

    public void addModule(
            CourseModule module
    ) {
        if (module == null) {
            return;
        }

        module.setCourse(this);
        modules.add(module);
    }

    public void removeModule(
            CourseModule module
    ) {
        if (module == null) {
            return;
        }

        modules.remove(module);
        module.setCourse(null);
    }

    public void addResource(
            LearningResource resource
    ) {
        if (resource == null) {
            return;
        }

        resource.setCourse(this);
        resources.add(resource);
    }

    public void addDiscipline(
            AcademicDiscipline discipline
    ) {
        if (discipline != null) {
            disciplines.add(discipline);
        }
    }

    public void addPrerequisite(
            Course prerequisite
    ) {
        if (
                prerequisite != null
                        && prerequisite != this
        ) {
            prerequisites.add(prerequisite);
        }
    }

    @PrePersist
    @PreUpdate
    private void normalize() {

        if (title != null) {
            title = title.strip();
        }

        if (slug != null) {
            slug = slug
                    .strip()
                    .toLowerCase();
        }

        if (language == null || language.isBlank()) {
            language = "English";
        }

        if (
                estimatedDurationHours == null
                        || estimatedDurationHours < 1
        ) {
            estimatedDurationHours = 1;
        }

        if (
                courseVersion == null
                        || courseVersion < 1
        ) {
            courseVersion = 1;
        }

        if (status == null) {
            status = ContentStatus.DRAFT;
        }

        if (free == null) {
            free = true;
        }

        if (featured == null) {
            featured = false;
        }

        if (active == null) {
            active = true;
        }

        if (certificateEnabled == null) {
            certificateEnabled = false;
        }

        if (averageRating == null || averageRating < 0) {
            averageRating = 0.0;
        }

        if (ratingCount == null || ratingCount < 0) {
            ratingCount = 0L;
        }

        if (enrollmentCount == null || enrollmentCount < 0) {
            enrollmentCount = 0L;
        }
    }
}