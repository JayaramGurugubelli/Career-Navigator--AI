package careerpilot_parent.job.entity;

import careerpilot_parent.common.entity.BaseEntity;

import careerpilot_parent.company.entity.Company;
import careerpilot_parent.company.entity.RecruiterProfile;

import careerpilot_parent.company.enums.CurrencyCode;
import careerpilot_parent.company.enums.EmploymentType;
import careerpilot_parent.company.enums.ExperienceLevel;
import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.company.enums.WorkMode;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "job_postings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_job_posting_slug",
                        columnNames = "slug"
                )
        },
        indexes = {
                @Index(
                        name = "idx_job_status",
                        columnList = "status"
                ),
                @Index(
                        name = "idx_job_company",
                        columnList = "company_id"
                ),
                @Index(
                        name = "idx_job_recruiter",
                        columnList = "recruiter_id"
                ),
                @Index(
                        name = "idx_job_location",
                        columnList = "location"
                ),
                @Index(
                        name = "idx_job_published_at",
                        columnList = "published_at"
                ),
                @Index(
                        name = "idx_job_deadline",
                        columnList = "application_deadline"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting extends BaseEntity {

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "company_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_job_posting_company"
            )
    )
    private Company company;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "recruiter_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_job_posting_recruiter"
            )
    )
    private RecruiterProfile recruiter;

    @Column(
            nullable = false,
            length = 180
    )
    private String title;

    @Column(
            nullable = false,

            length = 250
    )
    private String slug;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String description;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    @Column(columnDefinition = "TEXT")
    private String qualifications;

    @Column(
            name = "preferred_qualifications",
            columnDefinition = "TEXT"
    )
    private String preferredQualifications;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "employment_type",
            nullable = false,
            length = 30
    )
    private EmploymentType employmentType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "work_mode",
            nullable = false,
            length = 20
    )
    private WorkMode workMode;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "experience_level",
            nullable = false,
            length = 30
    )
    private ExperienceLevel experienceLevel;

    @Column(length = 150)
    private String location;

    @Column(name = "minimum_experience")
    private Integer minimumExperience;

    @Column(name = "maximum_experience")
    private Integer maximumExperience;

    @Column(
            name = "minimum_salary",
            precision = 15,
            scale = 2
    )
    private BigDecimal minimumSalary;

    @Column(
            name = "maximum_salary",
            precision = 15,
            scale = 2
    )
    private BigDecimal maximumSalary;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CurrencyCode currency;

    @Column(
            name = "salary_disclosed",
            nullable = false
    )
    @Builder.Default
    private boolean salaryDisclosed = false;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "job_posting_skills",
            joinColumns = @JoinColumn(
                    name = "job_posting_id"
            ),
            foreignKey = @ForeignKey(
                    name = "fk_job_skill_job_posting"
            )
    )
    @Column(
            name = "skill",
            nullable = false,
            length = 100
    )
    @Builder.Default
    private Set<String> requiredSkills =
            new HashSet<>();

    @Column(
            name = "number_of_openings",
            nullable = false
    )
    @Builder.Default
    private Integer numberOfOpenings = 1;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    @Builder.Default
    private JobStatus status =
            JobStatus.DRAFT;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(
            name = "view_count",
            nullable = false
    )
    @Builder.Default
    private Long viewCount = 0L;

    @Column(
            name = "application_count",
            nullable = false
    )
    @Builder.Default
    private Long applicationCount = 0L;

    @Version
    private Long version;
}