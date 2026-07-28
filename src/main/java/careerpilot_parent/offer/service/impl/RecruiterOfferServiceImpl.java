package careerpilot_parent.offer.service.impl;

import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.job.entity.ApplicationStatusHistory;
import careerpilot_parent.job.entity.JobApplication;
import careerpilot_parent.job.repository.ApplicationStatusHistoryRepository;
import careerpilot_parent.job.repository.JobApplicationRepository;
import careerpilot_parent.offer.dto.request.CreateJobOfferRequest;
import careerpilot_parent.offer.dto.request.UpdateJobOfferRequest;
import careerpilot_parent.offer.dto.request.WithdrawOfferRequest;
import careerpilot_parent.offer.dto.response.JobOfferResponse;
import careerpilot_parent.offer.entity.JobOffer;
import careerpilot_parent.offer.enums.OfferStatus;
import careerpilot_parent.offer.mapper.JobOfferMapper;
import careerpilot_parent.offer.repository.JobOfferRepository;
import careerpilot_parent.offer.service.RecruiterOfferService;
import careerpilot_parent.company.entity.RecruiterProfile;
import careerpilot_parent.company.repository.RecruiterProfileRepository;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.shared.enums.ApplicationStatus;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RecruiterOfferServiceImpl
        implements RecruiterOfferService {

    private final JobOfferRepository
            jobOfferRepository;

    private final JobApplicationRepository
            jobApplicationRepository;

    private final RecruiterProfileRepository
            recruiterProfileRepository;

    private final ApplicationStatusHistoryRepository
            applicationStatusHistoryRepository;

    private final UserRepository userRepository;

    private final JobOfferMapper jobOfferMapper;

    private final SecurityUtils securityUtils;

    private static final Set<OfferStatus>
            ACTIVE_OFFER_STATUSES =
            EnumSet.of(
                    OfferStatus.DRAFT,
                    OfferStatus.SENT,
                    OfferStatus.VIEWED
            );

    @Override
    public JobOfferResponse createOffer(
            Long applicationId,
            CreateJobOfferRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobApplication application =
                getRecruiterApplication(
                        applicationId,
                        recruiter.getId()
                );

        validateApplicationForOffer(application);

        validateOfferDates(
                request.getJoiningDate(),
                request.getOfferExpiryDate()
        );

        validateSalaryBreakup(
                request.getAnnualCtc(),
                request.getBaseSalary(),
                request.getBonus()
        );

        boolean activeOfferExists =
                jobOfferRepository
                        .existsByJobApplicationIdAndStatusIn(
                                applicationId,
                                ACTIVE_OFFER_STATUSES
                        );

        if (activeOfferExists) {

            throw new IllegalStateException(
                    "An active offer already exists for this application."
            );
        }

        JobOffer offer =
                jobOfferMapper.toEntity(request);

        offer.setJobApplication(application);
        offer.setRecruiter(recruiter);
        offer.setStatus(OfferStatus.DRAFT);

        JobOffer savedOffer =
                jobOfferRepository.save(offer);

        return jobOfferMapper.toResponse(
                savedOffer
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobOfferResponse> getMyOffers(
            OfferStatus status,
            Pageable pageable
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        Page<JobOffer> offers;

        if (status == null) {

            offers =
                    jobOfferRepository
                            .findByRecruiterId(
                                    recruiter.getId(),
                                    pageable
                            );

        } else {

            offers =
                    jobOfferRepository
                            .findByRecruiterIdAndStatus(
                                    recruiter.getId(),
                                    status,
                                    pageable
                            );
        }

        return offers.map(
                jobOfferMapper::toResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobOfferResponse>
    getApplicationOffers(
            Long applicationId,
            Pageable pageable
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        getRecruiterApplication(
                applicationId,
                recruiter.getId()
        );

        return jobOfferRepository
                .findByJobApplicationIdAndRecruiterId(
                        applicationId,
                        recruiter.getId(),
                        pageable
                )
                .map(jobOfferMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public JobOfferResponse getOfferById(
            Long offerId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobOffer offer =
                getRecruiterOffer(
                        offerId,
                        recruiter.getId()
                );

        return jobOfferMapper.toResponse(offer);
    }

    @Override
    public JobOfferResponse updateOffer(
            Long offerId,
            UpdateJobOfferRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobOffer offer =
                getRecruiterOffer(
                        offerId,
                        recruiter.getId()
                );

        if (offer.getStatus() !=
                OfferStatus.DRAFT) {

            throw new IllegalStateException(
                    "Only a DRAFT offer can be updated."
            );
        }

        validateOfferDates(
                request.getJoiningDate(),
                request.getOfferExpiryDate()
        );

        validateSalaryBreakup(
                request.getAnnualCtc(),
                request.getBaseSalary(),
                request.getBonus()
        );

        jobOfferMapper.updateEntity(
                offer,
                request
        );

        return jobOfferMapper.toResponse(
                jobOfferRepository.save(offer)
        );
    }

    @Override
    public JobOfferResponse sendOffer(
            Long offerId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        User currentUser =
                getCurrentUser();

        JobOffer offer =
                getRecruiterOffer(
                        offerId,
                        recruiter.getId()
                );

        if (offer.getStatus() !=
                OfferStatus.DRAFT) {

            throw new IllegalStateException(
                    "Only a DRAFT offer can be sent."
            );
        }

        validateOfferDates(
                offer.getJoiningDate(),
                offer.getOfferExpiryDate()
        );

        JobApplication application =
                offer.getJobApplication();

        if (application.getStatus() !=
                ApplicationStatus.INTERVIEW_COMPLETED) {

            throw new IllegalStateException(
                    "Application must be INTERVIEW_COMPLETED before sending an offer."
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        offer.setStatus(OfferStatus.SENT);
        offer.setSentAt(now);

        JobOffer savedOffer =
                jobOfferRepository.save(offer);

        ApplicationStatus previousStatus =
                application.getStatus();

        application.setStatus(
                ApplicationStatus.OFFERED
        );

        application.setLastStatusChangedAt(now);

        jobApplicationRepository.save(application);

        saveHistory(
                application,
                previousStatus,
                ApplicationStatus.OFFERED,
                currentUser,
                "Offer sent: "
                        + offer.getOfferTitle()
        );

        return jobOfferMapper.toResponse(
                savedOffer
        );
    }

    @Override
    public JobOfferResponse withdrawOffer(
            Long offerId,
            WithdrawOfferRequest request
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        User currentUser =
                getCurrentUser();

        JobOffer offer =
                getRecruiterOffer(
                        offerId,
                        recruiter.getId()
                );

        if (offer.getStatus() !=
                OfferStatus.SENT &&
                offer.getStatus() !=
                        OfferStatus.VIEWED) {

            throw new IllegalStateException(
                    "Only a SENT or VIEWED offer can be withdrawn."
            );
        }

        LocalDateTime now =
                LocalDateTime.now();

        offer.setStatus(
                OfferStatus.WITHDRAWN
        );

        offer.setWithdrawnAt(now);

        offer.setWithdrawalReason(
                normalize(request.getReason())
        );

        JobOffer savedOffer =
                jobOfferRepository.save(offer);

        JobApplication application =
                offer.getJobApplication();

        if (application.getStatus() ==
                ApplicationStatus.OFFERED) {

            ApplicationStatus previousStatus =
                    application.getStatus();

            application.setStatus(
                    ApplicationStatus.INTERVIEW_COMPLETED
            );

            application.setLastStatusChangedAt(now);

            jobApplicationRepository.save(application);

            saveHistory(
                    application,
                    previousStatus,
                    ApplicationStatus.INTERVIEW_COMPLETED,
                    currentUser,
                    "Offer withdrawn: "
                            + request.getReason()
            );
        }

        return jobOfferMapper.toResponse(
                savedOffer
        );
    }

    @Override
    public void deleteOffer(
            Long offerId
    ) {

        RecruiterProfile recruiter =
                getCurrentRecruiter();

        JobOffer offer =
                getRecruiterOffer(
                        offerId,
                        recruiter.getId()
                );

        if (offer.getStatus() !=
                OfferStatus.DRAFT) {

            throw new IllegalStateException(
                    "Only a DRAFT offer can be deleted."
            );
        }

        jobOfferRepository.delete(offer);
    }

    private RecruiterProfile getCurrentRecruiter() {

        Long userId =
                securityUtils.getCurrentUserId();

        return recruiterProfileRepository
                .findByUserIdAndActiveTrue(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Active recruiter profile not found."
                        )
                );
    }

    private User getCurrentUser() {

        Long userId =
                securityUtils.getCurrentUserId();

        return userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Current user not found."
                        )
                );
    }

    private JobApplication
    getRecruiterApplication(
            Long applicationId,
            Long recruiterId
    ) {

        return jobApplicationRepository
                .findByIdAndJobPostingRecruiterId(
                        applicationId,
                        recruiterId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Application not found or does not belong to the current recruiter."
                        )
                );
    }

    private JobOffer getRecruiterOffer(
            Long offerId,
            Long recruiterId
    ) {

        return jobOfferRepository
                .findByIdAndRecruiterId(
                        offerId,
                        recruiterId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Offer not found or does not belong to the current recruiter."
                        )
                );
    }

    private void validateApplicationForOffer(
            JobApplication application
    ) {

        if (application.getStatus() !=
                ApplicationStatus.INTERVIEW_COMPLETED) {

            throw new IllegalStateException(
                    "Offer can only be created for an INTERVIEW_COMPLETED application."
            );
        }
    }

    private void validateOfferDates(
            LocalDate joiningDate,
            LocalDate offerExpiryDate
    ) {

        LocalDate today =
                LocalDate.now();

        if (joiningDate == null ||
                offerExpiryDate == null) {

            throw new IllegalArgumentException(
                    "Joining date and offer expiry date are required."
            );
        }

        if (!joiningDate.isAfter(today)) {

            throw new IllegalStateException(
                    "Joining date must be in the future."
            );
        }

        if (offerExpiryDate.isBefore(today)) {

            throw new IllegalStateException(
                    "Offer expiry date cannot be in the past."
            );
        }

        if (!offerExpiryDate.isBefore(
                joiningDate
        )) {

            throw new IllegalStateException(
                    "Offer expiry date must be before the joining date."
            );
        }
    }

    private void validateSalaryBreakup(
            BigDecimal annualCtc,
            BigDecimal baseSalary,
            BigDecimal bonus
    ) {

        if (annualCtc == null ||
                annualCtc.signum() <= 0) {

            throw new IllegalStateException(
                    "Annual CTC must be greater than zero."
            );
        }

        if (baseSalary != null &&
                baseSalary.signum() < 0) {

            throw new IllegalStateException(
                    "Base salary cannot be negative."
            );
        }

        if (bonus != null &&
                bonus.signum() < 0) {

            throw new IllegalStateException(
                    "Bonus cannot be negative."
            );
        }

        BigDecimal totalBreakup =
                BigDecimal.ZERO;

        if (baseSalary != null) {
            totalBreakup =
                    totalBreakup.add(baseSalary);
        }

        if (bonus != null) {
            totalBreakup =
                    totalBreakup.add(bonus);
        }

        if (totalBreakup.compareTo(
                annualCtc
        ) > 0) {

            throw new IllegalStateException(
                    "Base salary and bonus total cannot exceed annual CTC."
            );
        }
    }

    private void saveHistory(
            JobApplication application,
            ApplicationStatus previousStatus,
            ApplicationStatus newStatus,
            User changedBy,
            String comment
    ) {

        ApplicationStatusHistory history =
                ApplicationStatusHistory.builder()
                        .application(application)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .changedBy(changedBy)
                        .comment(normalize(comment))
                        .build();

        applicationStatusHistoryRepository.save(
                history
        );
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
}