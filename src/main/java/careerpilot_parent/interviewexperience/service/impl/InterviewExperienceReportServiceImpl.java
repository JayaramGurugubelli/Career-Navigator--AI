package careerpilot_parent.interviewexperience.service.impl;

import careerpilot_parent.common.exception.BadRequestException;
import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.common.mapper.PageResponseMapper;
import careerpilot_parent.interviewexperience.dto.request.CreateInterviewExperienceReportRequest;
import careerpilot_parent.interviewexperience.dto.request.ReviewInterviewExperienceReportRequest;
import careerpilot_parent.interviewexperience.dto.response.InterviewExperienceReportResponse;
import careerpilot_parent.interviewexperience.dto.response.PageResponse;
import careerpilot_parent.interviewexperience.entity.InterviewExperience;
import careerpilot_parent.interviewexperience.entity.InterviewExperienceReport;
import careerpilot_parent.interviewexperience.enums.InterviewExperienceReportReason;
import careerpilot_parent.interviewexperience.enums.InterviewExperienceStatus;
import careerpilot_parent.interviewexperience.repository.InterviewExperienceReportRepository;
import careerpilot_parent.interviewexperience.repository.InterviewExperienceRepository;
import careerpilot_parent.interviewexperience.service.InterviewExperienceReportService;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewExperienceReportServiceImpl
        implements InterviewExperienceReportService {

    private final InterviewExperienceRepository experienceRepository;
    private final InterviewExperienceReportRepository reportRepository;
    private final UserRepository userRepository;

    private final PageResponseMapper pageResponseMapper;
    private final SecurityUtils securityUtils;

    @Override
    public InterviewExperienceReportResponse reportExperience(
            Long experienceId,
            CreateInterviewExperienceReportRequest request
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        InterviewExperience experience =
                experienceRepository.findById(experienceId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview experience not found."
                                )
                        );

        if (experience.getStatus()
                != InterviewExperienceStatus.APPROVED) {

            throw new BadRequestException(
                    "Only approved interview experiences can be reported."
            );
        }

        if (experience.getSubmittedBy() != null
                && Objects.equals(
                experience.getSubmittedBy().getId(),
                currentUserId
        )) {

            throw new BadRequestException(
                    "You cannot report your own interview experience."
            );
        }

        if (reportRepository
                .existsByInterviewExperience_IdAndReportedBy_Id(
                        experienceId,
                        currentUserId
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already reported this interview experience."
            );
        }

        User currentUser =
                getUser(currentUserId);

        InterviewExperienceReport report =
                InterviewExperienceReport.builder()
                        .interviewExperience(experience)
                        .reportedBy(currentUser)
                        .reason(request.getReason())
                        .additionalDetails(
                                normalizeNullable(
                                        request.getAdditionalDetails()
                                )
                        )
                        .reviewed(false)
                        .resolved(false)
                        .build();

        InterviewExperienceReport savedReport;

        try {
            savedReport =
                    reportRepository.saveAndFlush(report);
        } catch (DataIntegrityViolationException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already reported this interview experience."
            );
        }

        experienceRepository.incrementReportCount(
                experienceId
        );

        /*
         * Auto-hide only after a configured threshold if required.
         * Do not automatically hide after one report.
         *
         * Example:
         *
         * if (count >= 10) {
         *     experience.setStatus(HIDDEN);
         * }
         */

        return toResponse(
                savedReport,
                false
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterviewExperienceReportResponse> getReports(
            Boolean reviewed,
            Boolean resolved,
            InterviewExperienceReportReason reason,
            Pageable pageable
    ) {

        assertAdmin();

        Specification<InterviewExperienceReport> specification =
                reportSpecification(
                        reviewed,
                        resolved,
                        reason
                );

        Page<InterviewExperienceReport> page =
                reportRepository.findAll(
                        specification,
                        pageable
                );

        return pageResponseMapper.toResponse(
                page,
                report ->
                        toResponse(
                                report,
                                true
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewExperienceReportResponse getReportById(
            Long reportId
    ) {

        assertAdmin();

        InterviewExperienceReport report =
                reportRepository.findDetailedById(
                                reportId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview experience report not found."
                                )
                        );

        return toResponse(
                report,
                true
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterviewExperienceReportResponse>
    getReportsForExperience(
            Long experienceId,
            Pageable pageable
    ) {

        assertAdmin();

        if (!experienceRepository.existsById(
                experienceId
        )) {
            throw new ResourceNotFoundException(
                    "Interview experience not found."
            );
        }

        Page<InterviewExperienceReport> page =
                reportRepository
                        .findByInterviewExperience_IdOrderByCreatedAtDesc(
                                experienceId,
                                pageable
                        );

        return pageResponseMapper.toResponse(
                page,
                report ->
                        toResponse(
                                report,
                                true
                        )
        );
    }

    @Override
    public InterviewExperienceReportResponse reviewReport(
            Long reportId,
            ReviewInterviewExperienceReportRequest request
    ) {

        assertAdmin();

        Long adminUserId =
                securityUtils.getCurrentUserId();

        User admin =
                getUser(adminUserId);

        InterviewExperienceReport report =
                reportRepository.findDetailedById(
                                reportId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview experience report not found."
                                )
                        );

        if (Boolean.TRUE.equals(
                report.getReviewed()
        )) {
            throw new BadRequestException(
                    "This report has already been reviewed."
            );
        }

        report.markReviewed(
                admin,
                normalizeNullable(
                        request.getAdminNotes()
                ),
                Boolean.TRUE.equals(
                        request.getResolved()
                )
        );

        InterviewExperienceReport savedReport =
                reportRepository.save(report);

        return toResponse(
                savedReport,
                true
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long getPendingReportCount() {

        assertAdmin();

        return reportRepository
                .countByReviewedFalseAndResolvedFalse();
    }

    @Override
    public long recalculateExperienceReportCount(
            Long experienceId
    ) {

        assertAdmin();

        if (!experienceRepository.existsById(
                experienceId
        )) {
            throw new ResourceNotFoundException(
                    "Interview experience not found."
            );
        }

        long count =
                reportRepository
                        .countByInterviewExperience_Id(
                                experienceId
                        );

        int safeCount =
                count > Integer.MAX_VALUE
                        ? Integer.MAX_VALUE
                        : (int) count;

        experienceRepository.updateReportCount(
                experienceId,
                safeCount
        );

        return count;
    }

    private Specification<InterviewExperienceReport>
    reportSpecification(
            Boolean reviewed,
            Boolean resolved,
            InterviewExperienceReportReason reason
    ) {

        return (root, query, criteriaBuilder) -> {

            var predicate =
                    criteriaBuilder.conjunction();

            if (reviewed != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("reviewed"),
                                reviewed
                        )
                );
            }

            if (resolved != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("resolved"),
                                resolved
                        )
                );
            }

            if (reason != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(
                                root.get("reason"),
                                reason
                        )
                );
            }

            return predicate;
        };
    }

    private InterviewExperienceReportResponse toResponse(
            InterviewExperienceReport report,
            boolean includeAdminFields
    ) {

        return InterviewExperienceReportResponse.builder()
                .id(report.getId())
                .interviewExperienceId(
                        report.getInterviewExperience() == null
                                ? null
                                : report.getInterviewExperience().getId()
                )
                .companyName(
                        report.getInterviewExperience() == null
                                ? null
                                : report.getInterviewExperience()
                                .getCompanyName()
                )
                .reason(
                        report.getReason()
                )
                .additionalDetails(
                        report.getAdditionalDetails()
                )
                .reviewed(
                        Boolean.TRUE.equals(
                                report.getReviewed()
                        )
                )
                .resolved(
                        Boolean.TRUE.equals(
                                report.getResolved()
                        )
                )
                .reportedByDisplayName(
                        includeAdminFields
                                ? resolveDisplayName(
                                report.getReportedBy()
                        )
                                : null
                )
                .reviewedByDisplayName(
                        includeAdminFields
                                ? resolveDisplayName(
                                report.getReviewedBy()
                        )
                                : null
                )
                .adminNotes(
                        includeAdminFields
                                ? report.getAdminNotes()
                                : null
                )
                .createdAt(
                        report.getCreatedAt()
                )
                .reviewedAt(
                        report.getReviewedAt()
                )
                .build();
    }

    private User getUser(
            Long userId
    ) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        )
                );
    }

    private void assertAdmin() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        boolean admin =
                authentication != null
                        && authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                "ROLE_ADMIN".equals(
                                        authority.getAuthority()
                                )
                        );

        if (!admin) {
            throw new AccessDeniedException(
                    "Administrator access is required."
            );
        }
    }

    private String resolveDisplayName(
            User user
    ) {

        if (user == null) {
            return null;
        }

        String firstName =
                normalizeNullable(
                        user.getFirstName()
                );

        String lastName =
                normalizeNullable(
                        user.getLastName()
                );

        String value =
                String.join(
                        " ",
                        firstName == null
                                ? ""
                                : firstName,
                        lastName == null
                                ? ""
                                : lastName
                ).trim();

        return value.isBlank()
                ? "CareerPilot User"
                : value;
    }

    private String normalizeNullable(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value
                .strip()
                .replaceAll(
                        "[\\p{Z}\\s]+",
                        " "
                );
    }
}