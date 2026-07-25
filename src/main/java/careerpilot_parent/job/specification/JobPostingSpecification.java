package careerpilot_parent.job.specification;

import careerpilot_parent.company.enums.ExperienceLevel;
import careerpilot_parent.job.entity.JobPosting;
import careerpilot_parent.job.enums.EmploymentType;
import careerpilot_parent.company.enums.JobStatus;
import careerpilot_parent.company.enums.WorkMode;

import jakarta.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class JobPostingSpecification {

    private JobPostingSpecification() {
    }

    public static Specification<JobPosting> isPubliclyAvailable() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(
                                root.get("status"),
                                JobStatus.PUBLISHED
                        ),
                        criteriaBuilder.isTrue(
                                root.get("company")
                                        .get("active")
                        ),
                        criteriaBuilder.or(
                                criteriaBuilder.isNull(
                                        root.get(
                                                "applicationDeadline"
                                        )
                                ),
                                criteriaBuilder.greaterThanOrEqualTo(
                                        root.get(
                                                "applicationDeadline"
                                        ),
                                        LocalDate.now()
                                )
                        )
                );
    }

    public static Specification<JobPosting> hasKeyword(
            String keyword
    ) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null ||
                    keyword.isBlank()) {

                return criteriaBuilder.conjunction();
            }

            String value =
                    "%" + keyword
                            .trim()
                            .toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("title")
                            ),
                            value
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.get("description")
                            ),
                            value
                    ),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(
                                    root.join(
                                            "company",
                                            JoinType.INNER
                                    ).get("name")
                            ),
                            value
                    )
            );
        };
    }

    public static Specification<JobPosting> hasLocation(
            String location
    ) {

        return (root, query, criteriaBuilder) -> {

            if (location == null ||
                    location.isBlank()) {

                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            root.get("location")
                    ),
                    "%" +
                            location.trim()
                                    .toLowerCase()
                            + "%"
            );
        };
    }

    public static Specification<JobPosting> hasEmploymentType(
            EmploymentType employmentType
    ) {

        return (root, query, criteriaBuilder) ->
                employmentType == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(
                        root.get("employmentType"),
                        employmentType
                );
    }

    public static Specification<JobPosting> hasWorkMode(
            WorkMode workMode
    ) {

        return (root, query, criteriaBuilder) ->
                workMode == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(
                        root.get("workMode"),
                        workMode
                );
    }

    public static Specification<JobPosting> hasExperienceLevel(
            ExperienceLevel experienceLevel
    ) {

        return (root, query, criteriaBuilder) ->
                experienceLevel == null
                        ? criteriaBuilder.conjunction()
                        : criteriaBuilder.equal(
                        root.get("experienceLevel"),
                        experienceLevel
                );
    }
}