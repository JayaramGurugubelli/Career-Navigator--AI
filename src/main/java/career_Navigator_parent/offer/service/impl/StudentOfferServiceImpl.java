package career_Navigator_parent.offer.service.impl;

import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.job.entity.ApplicationStatusHistory;
import career_Navigator_parent.job.entity.JobApplication;
import career_Navigator_parent.job.repository.ApplicationStatusHistoryRepository;
import career_Navigator_parent.job.repository.JobApplicationRepository;
import career_Navigator_parent.offer.dto.request.StudentOfferResponseRequest;
import career_Navigator_parent.offer.dto.response.JobOfferResponse;
import career_Navigator_parent.offer.entity.JobOffer;
import career_Navigator_parent.offer.enums.OfferStatus;
import career_Navigator_parent.offer.mapper.JobOfferMapper;
import career_Navigator_parent.offer.repository.JobOfferRepository;
import career_Navigator_parent.offer.service.StudentOfferService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.shared.enums.ApplicationStatus;
import career_Navigator_parent.student.entity.Student;
import career_Navigator_parent.student.repository.StudentRepository;
import career_Navigator_parent.user.entity.User;
import career_Navigator_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentOfferServiceImpl
        implements StudentOfferService {

    private final JobOfferRepository
            jobOfferRepository;

    private final JobApplicationRepository
            jobApplicationRepository;

    private final StudentRepository
            studentRepository;

    private final UserRepository userRepository;

    private final ApplicationStatusHistoryRepository
            applicationStatusHistoryRepository;

    private final JobOfferMapper jobOfferMapper;

    private final SecurityUtils securityUtils;

    @Override
    public Page<JobOfferResponse> getMyOffers(
            OfferStatus status,
            Pageable pageable
    ) {

        Student student =
                getCurrentStudent();

        Page<JobOffer> offers;

        if (status == null) {

            offers =
                    jobOfferRepository
                            .findByJobApplicationStudentId(
                                    student.getId(),
                                    pageable
                            );

        } else {

            offers =
                    jobOfferRepository
                            .findByJobApplicationStudentIdAndStatus(
                                    student.getId(),
                                    status,
                                    pageable
                            );
        }

        offers.forEach(
                this::refreshExpiredOffer
        );

        return offers.map(
                jobOfferMapper::toResponse
        );
    }

    @Override
    public JobOfferResponse getOfferById(
            Long offerId
    ) {

        Student student =
                getCurrentStudent();

        JobOffer offer =
                getStudentOffer(
                        offerId,
                        student.getId()
                );

        refreshExpiredOffer(offer);

        if (offer.getStatus() ==
                OfferStatus.SENT) {

            offer.setStatus(
                    OfferStatus.VIEWED
            );

            offer.setViewedAt(
                    LocalDateTime.now()
            );

            offer =
                    jobOfferRepository.save(offer);
        }

        return jobOfferMapper.toResponse(offer);
    }

    @Override
    public JobOfferResponse acceptOffer(
            Long offerId,
            StudentOfferResponseRequest request
    ) {

        Student student =
                getCurrentStudent();

        User currentUser =
                getCurrentUser();

        JobOffer offer =
                getStudentOffer(
                        offerId,
                        student.getId()
                );

        refreshExpiredOffer(offer);

        validateOfferCanBeRespondedTo(offer);

        LocalDateTime now =
                LocalDateTime.now();

        offer.setStatus(
                OfferStatus.ACCEPTED
        );

        offer.setAcceptedAt(now);

        offer.setStudentResponseNotes(
                normalize(request.getNotes())
        );

        JobOffer savedOffer =
                jobOfferRepository.save(offer);

        JobApplication application =
                offer.getJobApplication();

        if (application.getStatus() !=
                ApplicationStatus.OFFERED) {

            throw new IllegalStateException(
                    "Application must be in OFFERED status before accepting the offer."
            );
        }

        ApplicationStatus previousStatus =
                application.getStatus();

        application.setStatus(
                ApplicationStatus.HIRED
        );

        application.setLastStatusChangedAt(now);

        jobApplicationRepository.save(application);

        saveHistory(
                application,
                previousStatus,
                ApplicationStatus.HIRED,
                currentUser,
                "Student accepted the job offer."
        );

        return jobOfferMapper.toResponse(
                savedOffer
        );
    }

    @Override
    public JobOfferResponse rejectOffer(
            Long offerId,
            StudentOfferResponseRequest request
    ) {

        Student student =
                getCurrentStudent();

        User currentUser =
                getCurrentUser();

        JobOffer offer =
                getStudentOffer(
                        offerId,
                        student.getId()
                );

        refreshExpiredOffer(offer);

        validateOfferCanBeRespondedTo(offer);

        LocalDateTime now =
                LocalDateTime.now();

        offer.setStatus(
                OfferStatus.REJECTED
        );

        offer.setRejectedAt(now);

        offer.setStudentResponseNotes(
                normalize(request.getNotes())
        );

        JobOffer savedOffer =
                jobOfferRepository.save(offer);

        JobApplication application =
                offer.getJobApplication();

        if (application.getStatus() !=
                ApplicationStatus.OFFERED) {

            throw new IllegalStateException(
                    "Application must be in OFFERED status before rejecting the offer."
            );
        }

        ApplicationStatus previousStatus =
                application.getStatus();

        application.setStatus(
                ApplicationStatus.REJECTED
        );

        application.setLastStatusChangedAt(now);

        jobApplicationRepository.save(application);

        saveHistory(
                application,
                previousStatus,
                ApplicationStatus.REJECTED,
                currentUser,
                "Student rejected the job offer."
        );

        return jobOfferMapper.toResponse(
                savedOffer
        );
    }

    private Student getCurrentStudent() {

        Long userId =
                securityUtils.getCurrentUserId();

        return studentRepository
                .findByUserId(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Student profile not found."
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

    private JobOffer getStudentOffer(
            Long offerId,
            Long studentId
    ) {

        return jobOfferRepository
                .findByIdAndJobApplicationStudentId(
                        offerId,
                        studentId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Offer not found or does not belong to the current student."
                        )
                );
    }

    private void validateOfferCanBeRespondedTo(
            JobOffer offer
    ) {

        if (offer.getStatus() ==
                OfferStatus.ACCEPTED) {

            throw new IllegalStateException(
                    "Offer has already been accepted."
            );
        }

        if (offer.getStatus() ==
                OfferStatus.REJECTED) {

            throw new IllegalStateException(
                    "Offer has already been rejected."
            );
        }

        if (offer.getStatus() ==
                OfferStatus.WITHDRAWN) {

            throw new IllegalStateException(
                    "Withdrawn offer cannot be accepted or rejected."
            );
        }

        if (offer.getStatus() ==
                OfferStatus.EXPIRED) {

            throw new IllegalStateException(
                    "Expired offer cannot be accepted or rejected."
            );
        }

        if (offer.getStatus() !=
                OfferStatus.SENT &&
                offer.getStatus() !=
                        OfferStatus.VIEWED) {

            throw new IllegalStateException(
                    "Only a SENT or VIEWED offer can be accepted or rejected."
            );
        }

        if (LocalDate.now().isAfter(
                offer.getOfferExpiryDate()
        )) {

            throw new IllegalStateException(
                    "Offer has expired."
            );
        }
    }

    private void refreshExpiredOffer(
            JobOffer offer
    ) {

        if (offer.getStatus() !=
                OfferStatus.SENT &&
                offer.getStatus() !=
                        OfferStatus.VIEWED) {

            return;
        }

        if (LocalDate.now().isAfter(
                offer.getOfferExpiryDate()
        )) {

            offer.setStatus(
                    OfferStatus.EXPIRED
            );

            offer.setExpiredAt(
                    LocalDateTime.now()
            );

            jobOfferRepository.save(offer);
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
                        .comment(comment)
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