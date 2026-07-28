package careerpilot_parent.offer.mapper;

import careerpilot_parent.offer.dto.request.CreateJobOfferRequest;
import careerpilot_parent.offer.dto.request.UpdateJobOfferRequest;
import careerpilot_parent.offer.dto.response.JobOfferResponse;
import careerpilot_parent.offer.entity.JobOffer;
import org.springframework.stereotype.Component;

@Component
public class JobOfferMapper {

    public JobOffer toEntity(
            CreateJobOfferRequest request
    ) {

        return JobOffer.builder()
                .offerTitle(
                        normalize(request.getOfferTitle())
                )
                .annualCtc(request.getAnnualCtc())
                .baseSalary(request.getBaseSalary())
                .bonus(request.getBonus())
                .currency(
                        normalizeUppercase(
                                request.getCurrency()
                        )
                )
                .employmentType(
                        normalize(request.getEmploymentType())
                )
                .workLocation(
                        normalize(request.getWorkLocation())
                )
                .joiningDate(request.getJoiningDate())
                .offerExpiryDate(
                        request.getOfferExpiryDate()
                )
                .probationPeriodMonths(
                        request.getProbationPeriodMonths()
                )
                .noticePeriodDays(
                        request.getNoticePeriodDays()
                )
                .termsAndConditions(
                        normalize(
                                request.getTermsAndConditions()
                        )
                )
                .offerLetterUrl(
                        normalize(
                                request.getOfferLetterUrl()
                        )
                )
                .build();
    }

    public void updateEntity(
            JobOffer offer,
            UpdateJobOfferRequest request
    ) {

        offer.setOfferTitle(
                normalize(request.getOfferTitle())
        );

        offer.setAnnualCtc(
                request.getAnnualCtc()
        );

        offer.setBaseSalary(
                request.getBaseSalary()
        );

        offer.setBonus(
                request.getBonus()
        );

        offer.setCurrency(
                normalizeUppercase(
                        request.getCurrency()
                )
        );

        offer.setEmploymentType(
                normalize(request.getEmploymentType())
        );

        offer.setWorkLocation(
                normalize(request.getWorkLocation())
        );

        offer.setJoiningDate(
                request.getJoiningDate()
        );

        offer.setOfferExpiryDate(
                request.getOfferExpiryDate()
        );

        offer.setProbationPeriodMonths(
                request.getProbationPeriodMonths()
        );

        offer.setNoticePeriodDays(
                request.getNoticePeriodDays()
        );

        offer.setTermsAndConditions(
                normalize(
                        request.getTermsAndConditions()
                )
        );

        offer.setOfferLetterUrl(
                normalize(
                        request.getOfferLetterUrl()
                )
        );
    }

    public JobOfferResponse toResponse(
            JobOffer offer
    ) {

        var application =
                offer.getJobApplication();

        var student =
                application.getStudent();

        var studentUser =
                student.getUser();

        var recruiter =
                offer.getRecruiter();

        var recruiterUser =
                recruiter.getUser();

        var job =
                application.getJobPosting();

        return JobOfferResponse.builder()
                .id(offer.getId())
                .applicationId(
                        application.getId()
                )
                .jobId(job.getId())
                .jobTitle(job.getTitle())
                .studentId(student.getId())
                .studentName(
                        buildFullName(
                                studentUser.getFirstName(),
                                studentUser.getLastName()
                        )
                )
                .recruiterId(recruiter.getId())
                .recruiterName(
                        buildFullName(
                                recruiterUser.getFirstName(),
                                recruiterUser.getLastName()
                        )
                )
                .companyName(
                        recruiter.getCompany().getName()
                )
                .offerTitle(offer.getOfferTitle())
                .annualCtc(offer.getAnnualCtc())
                .baseSalary(offer.getBaseSalary())
                .bonus(offer.getBonus())
                .currency(offer.getCurrency())
                .employmentType(
                        offer.getEmploymentType()
                )
                .workLocation(
                        offer.getWorkLocation()
                )
                .joiningDate(
                        offer.getJoiningDate()
                )
                .offerExpiryDate(
                        offer.getOfferExpiryDate()
                )
                .probationPeriodMonths(
                        offer.getProbationPeriodMonths()
                )
                .noticePeriodDays(
                        offer.getNoticePeriodDays()
                )
                .termsAndConditions(
                        offer.getTermsAndConditions()
                )
                .offerLetterUrl(
                        offer.getOfferLetterUrl()
                )
                .status(offer.getStatus())
                .sentAt(offer.getSentAt())
                .viewedAt(offer.getViewedAt())
                .acceptedAt(offer.getAcceptedAt())
                .rejectedAt(offer.getRejectedAt())
                .withdrawnAt(offer.getWithdrawnAt())
                .expiredAt(offer.getExpiredAt())
                .studentResponseNotes(
                        offer.getStudentResponseNotes()
                )
                .withdrawalReason(
                        offer.getWithdrawalReason()
                )
                .createdAt(offer.getCreatedAt())
                .updatedAt(offer.getUpdatedAt())
                .build();
    }

    private String buildFullName(
            String firstName,
            String lastName
    ) {

        String first =
                firstName == null
                        ? ""
                        : firstName.trim();

        String last =
                lastName == null
                        ? ""
                        : lastName.trim();

        return (first + " " + last).trim();
    }

    private String normalize(
            String value
    ) {

        if (value == null ||
                value.isBlank()) {

            return null;
        }

        return value.trim();
    }

    private String normalizeUppercase(
            String value
    ) {

        String normalized = normalize(value);

        return normalized == null
                ? null
                : normalized.toUpperCase();
    }
}