package career_Navigator_parent.interviewexperience.service.impl;

import career_Navigator_parent.common.exception.BadRequestException;
import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.common.mapper.PageResponseMapper;
import career_Navigator_parent.company.entity.Company;
import career_Navigator_parent.company.repository.CompanyRepository;
import career_Navigator_parent.interviewexperience.dto.request.CreateInterviewExperienceRequest;
import career_Navigator_parent.interviewexperience.dto.request.ModerateInterviewExperienceRequest;
import career_Navigator_parent.interviewexperience.dto.request.UpdateInterviewExperienceRequest;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceDetailResponse;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceSummaryResponse;
import career_Navigator_parent.interviewexperience.dto.response.PageResponse;
import career_Navigator_parent.interviewexperience.entity.InterviewExperience;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceStatus;
import career_Navigator_parent.interviewexperience.enums.InterviewQuestionCategory;
import career_Navigator_parent.interviewexperience.event.InterviewExperienceModeratedEvent;
import career_Navigator_parent.interviewexperience.mapper.InterviewExperienceMapper;
import career_Navigator_parent.interviewexperience.repository.InterviewExperienceLikeRepository;
import career_Navigator_parent.interviewexperience.repository.InterviewExperienceRepository;
import career_Navigator_parent.interviewexperience.service.InterviewExperienceService;
import career_Navigator_parent.security.model.CustomUserDetails;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.user.entity.User;
import career_Navigator_parent.user.repository.UserRepository;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewExperienceServiceImpl
        implements InterviewExperienceService {

    private final InterviewExperienceRepository experienceRepository;
    private final InterviewExperienceLikeRepository likeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    private final InterviewExperienceMapper experienceMapper;
    private final PageResponseMapper pageResponseMapper;

    private final SecurityUtils securityUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public InterviewExperienceDetailResponse createExperience(
            CreateInterviewExperienceRequest request
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        User currentUser =
                getUser(currentUserId);

        Company company =
                resolveCompany(request.getCompanyId());

        validateCreateRequest(request);

        InterviewExperience experience =
                experienceMapper.toEntity(
                        request,
                        currentUser,
                        company
                );

        InterviewExperience savedExperience =
                experienceRepository.save(experience);

        return experienceMapper.toDetailResponse(
                savedExperience,
                currentUserId,
                false,
                true
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterviewExperienceSummaryResponse>
    getPublicExperiences(
            Long companyId,
            String companyName,
            String jobRole,
            String experienceLevel,
            String location,
            InterviewQuestionCategory category,
            String topic,
            Pageable pageable
    ) {

        Long currentUserId =
                getOptionalCurrentUserId();

        Specification<InterviewExperience> specification =
                publicSpecification(
                        companyId,
                        companyName,
                        jobRole,
                        experienceLevel,
                        location,
                        category,
                        topic
                );

        Page<InterviewExperience> page =
                experienceRepository.findAll(
                        specification,
                        pageable
                );

        return pageResponseMapper.toResponse(
                page,
                experience -> {

                    boolean liked =
                            currentUserId != null
                                    && likeRepository
                                    .existsByInterviewExperience_IdAndUser_Id(
                                            experience.getId(),
                                            currentUserId
                                    );

                    return experienceMapper.toSummaryResponse(
                            experience,
                            currentUserId,
                            liked,
                            false
                    );
                }
        );
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewExperienceDetailResponse getExperienceById(
            Long experienceId
    ) {

        InterviewExperience experience =
                experienceRepository.findDetailedById(
                                experienceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview experience not found."
                                )
                        );

        Long currentUserId =
                getOptionalCurrentUserId();

        boolean owner =
                currentUserId != null
                        && experience.getSubmittedBy() != null
                        && Objects.equals(
                        experience.getSubmittedBy().getId(),
                        currentUserId
                );

        boolean admin =
                hasRole("ROLE_ADMIN");

        if (experience.getStatus()
                != InterviewExperienceStatus.APPROVED
                && !owner
                && !admin) {

            throw new ResourceNotFoundException(
                    "Interview experience not found."
            );
        }

        boolean liked =
                currentUserId != null
                        && likeRepository
                        .existsByInterviewExperience_IdAndUser_Id(
                                experienceId,
                                currentUserId
                        );

        return experienceMapper.toDetailResponse(
                experience,
                currentUserId,
                liked,
                owner || admin
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterviewExperienceSummaryResponse>
    getMyExperiences(
            InterviewExperienceStatus status,
            Pageable pageable
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        Page<InterviewExperience> page =
                status == null
                        ? experienceRepository
                        .findBySubmittedBy_IdOrderByCreatedAtDesc(
                                currentUserId,
                                pageable
                        )
                        : experienceRepository
                        .findBySubmittedBy_IdAndStatusOrderByCreatedAtDesc(
                                currentUserId,
                                status,
                                pageable
                        );

        return pageResponseMapper.toResponse(
                page,
                experience -> {

                    boolean liked =
                            likeRepository
                                    .existsByInterviewExperience_IdAndUser_Id(
                                            experience.getId(),
                                            currentUserId
                                    );

                    return experienceMapper.toSummaryResponse(
                            experience,
                            currentUserId,
                            liked,
                            true
                    );
                }
        );
    }

    @Override
    public InterviewExperienceDetailResponse updateExperience(
            Long experienceId,
            UpdateInterviewExperienceRequest request
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        InterviewExperience experience =
                getDetailedExperience(experienceId);

        assertOwner(experience, currentUserId);

        if (experience.getStatus()
                == InterviewExperienceStatus.HIDDEN
                && hasRole("ROLE_ADMIN")) {

            throw new BadRequestException(
                    "A hidden experience must be restored before editing."
            );
        }

        validateUpdateRequest(request);

        Company company =
                resolveCompany(request.getCompanyId());

        experienceMapper.updateEntity(
                experience,
                request,
                company
        );

        InterviewExperience savedExperience =
                experienceRepository.save(experience);

        boolean liked =
                likeRepository
                        .existsByInterviewExperience_IdAndUser_Id(
                                experienceId,
                                currentUserId
                        );

        return experienceMapper.toDetailResponse(
                savedExperience,
                currentUserId,
                liked,
                true
        );
    }

    @Override
    public void deleteExperience(
            Long experienceId
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

        assertOwner(experience, currentUserId);

        experienceRepository.delete(experience);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterviewExperienceSummaryResponse>
    getExperiencesForAdmin(
            InterviewExperienceStatus status,
            String keyword,
            Pageable pageable
    ) {

        assertAdmin();

        Specification<InterviewExperience> specification =
                adminSpecification(
                        status,
                        keyword
                );

        Page<InterviewExperience> page =
                experienceRepository.findAll(
                        specification,
                        pageable
                );

        return pageResponseMapper.toResponse(
                page,
                experience ->
                        experienceMapper.toSummaryResponse(
                                experience,
                                securityUtils.getCurrentUserId(),
                                false,
                                true
                        )
        );
    }

    @Override
    public InterviewExperienceDetailResponse moderateExperience(
            Long experienceId,
            ModerateInterviewExperienceRequest request
    ) {

        assertAdmin();

        Long adminUserId =
                securityUtils.getCurrentUserId();

        InterviewExperience experience =
                getDetailedExperience(experienceId);

        validateModerationTransition(
                experience.getStatus(),
                request.getStatus()
        );

        experience.setStatus(request.getStatus());

        /*
         * verified means CareerPilot moderation has approved the post.
         */
        experience.setVerified(
                request.getStatus()
                        == InterviewExperienceStatus.APPROVED
        );

        InterviewExperience savedExperience =
                experienceRepository.save(experience);

        eventPublisher.publishEvent(
                new InterviewExperienceModeratedEvent(
                        savedExperience.getId(),
                        savedExperience.getSubmittedBy().getId(),
                        savedExperience.getStatus()
                )
        );

        return experienceMapper.toDetailResponse(
                savedExperience,
                adminUserId,
                false,
                true
        );
    }

    @Override
    public InterviewExperienceDetailResponse resubmitExperience(
            Long experienceId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        InterviewExperience experience =
                getDetailedExperience(experienceId);

        assertOwner(experience, currentUserId);

        if (experience.getStatus()
                != InterviewExperienceStatus.REJECTED
                && experience.getStatus()
                != InterviewExperienceStatus.DRAFT) {

            throw new BadRequestException(
                    "Only rejected or draft experiences can be resubmitted."
            );
        }

        experience.setStatus(
                InterviewExperienceStatus.PENDING_REVIEW
        );
        experience.setVerified(false);

        InterviewExperience savedExperience =
                experienceRepository.save(experience);

        return experienceMapper.toDetailResponse(
                savedExperience,
                currentUserId,
                false,
                true
        );
    }

    private Specification<InterviewExperience>
    publicSpecification(
            Long companyId,
            String companyName,
            String jobRole,
            String experienceLevel,
            String location,
            InterviewQuestionCategory category,
            String topic
    ) {

        return (root, query, criteriaBuilder) -> {

            query.distinct(true);

            var predicates =
                    criteriaBuilder.conjunction();

            predicates = criteriaBuilder.and(
                    predicates,
                    criteriaBuilder.equal(
                            root.get("status"),
                            InterviewExperienceStatus.APPROVED
                    )
            );

            if (companyId != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                root.get("company").get("id"),
                                companyId
                        )
                );
            }

            if (hasText(companyName)) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("companyName")
                                ),
                                contains(companyName)
                        )
                );
            }

            if (hasText(jobRole)) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("jobRole")
                                ),
                                contains(jobRole)
                        )
                );
            }

            if (hasText(experienceLevel)) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                criteriaBuilder.lower(
                                        root.get("experienceLevel")
                                ),
                                experienceLevel
                                        .strip()
                                        .toLowerCase()
                        )
                );
            }

            if (hasText(location)) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("location")
                                ),
                                contains(location)
                        )
                );
            }

            if (category != null || hasText(topic)) {

                var rounds =
                        root.join(
                                "rounds",
                                JoinType.INNER
                        );

                var questions =
                        rounds.join(
                                "questions",
                                JoinType.INNER
                        );

                if (category != null) {
                    predicates = criteriaBuilder.and(
                            predicates,
                            criteriaBuilder.equal(
                                    questions.get("category"),
                                    category
                            )
                    );
                }

                if (hasText(topic)) {
                    predicates = criteriaBuilder.and(
                            predicates,
                            criteriaBuilder.like(
                                    criteriaBuilder.lower(
                                            questions.get("topic")
                                    ),
                                    contains(topic)
                            )
                    );
                }
            }

            return predicates;
        };
    }

    private Specification<InterviewExperience>
    adminSpecification(
            InterviewExperienceStatus status,
            String keyword
    ) {

        return (root, query, criteriaBuilder) -> {

            var predicates =
                    criteriaBuilder.conjunction();

            if (status != null) {
                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.equal(
                                root.get("status"),
                                status
                        )
                );
            }

            if (hasText(keyword)) {

                String value =
                        contains(keyword);

                predicates = criteriaBuilder.and(
                        predicates,
                        criteriaBuilder.or(
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("companyName")
                                        ),
                                        value
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("jobRole")
                                        ),
                                        value
                                ),
                                criteriaBuilder.like(
                                        criteriaBuilder.lower(
                                                root.get("location")
                                        ),
                                        value
                                )
                        )
                );
            }

            return predicates;
        };
    }

    private void validateCreateRequest(
            CreateInterviewExperienceRequest request
    ) {

        Set<Integer> roundNumbers =
                new HashSet<>();

        Set<Integer> displayOrders =
                new HashSet<>();

        for (var round : request.getRounds()) {

            if (!roundNumbers.add(
                    round.getRoundNumber()
            )) {
                throw new BadRequestException(
                        "Duplicate round number: "
                                + round.getRoundNumber()
                );
            }

            if (!displayOrders.add(
                    round.getDisplayOrder()
            )) {
                throw new BadRequestException(
                        "Duplicate round display order: "
                                + round.getDisplayOrder()
                );
            }

            validateQuestionOrders(
                    round.getQuestions()
            );
        }
    }

    private void validateUpdateRequest(
            UpdateInterviewExperienceRequest request
    ) {

        Set<Integer> roundNumbers =
                new HashSet<>();

        Set<Integer> displayOrders =
                new HashSet<>();

        for (var round : request.getRounds()) {

            if (!roundNumbers.add(
                    round.getRoundNumber()
            )) {
                throw new BadRequestException(
                        "Duplicate round number: "
                                + round.getRoundNumber()
                );
            }

            if (!displayOrders.add(
                    round.getDisplayOrder()
            )) {
                throw new BadRequestException(
                        "Duplicate round display order: "
                                + round.getDisplayOrder()
                );
            }

            Set<Integer> questionOrders =
                    new HashSet<>();

            for (var question : round.getQuestions()) {

                if (!questionOrders.add(
                        question.getDisplayOrder()
                )) {
                    throw new BadRequestException(
                            "Duplicate question display order "
                                    + question.getDisplayOrder()
                                    + " in round "
                                    + round.getRoundNumber()
                    );
                }
            }
        }
    }

    private void validateQuestionOrders(
            Iterable<? extends Object> questions
    ) {

        Set<Integer> orders =
                new HashSet<>();

        for (Object object : questions) {

            var question =
                    (career_Navigator_parent.interviewexperience.dto.request.CreateInterviewQuestionRequest)
                            object;

            if (!orders.add(
                    question.getDisplayOrder()
            )) {
                throw new BadRequestException(
                        "Duplicate question display order: "
                                + question.getDisplayOrder()
                );
            }
        }
    }

    private void validateModerationTransition(
            InterviewExperienceStatus currentStatus,
            InterviewExperienceStatus requestedStatus
    ) {

        if (currentStatus == requestedStatus) {
            return;
        }

        boolean valid =
                switch (currentStatus) {

                    case DRAFT ->
                            requestedStatus
                                    == InterviewExperienceStatus.PENDING_REVIEW;

                    case PENDING_REVIEW ->
                            requestedStatus
                                    == InterviewExperienceStatus.APPROVED
                                    || requestedStatus
                                    == InterviewExperienceStatus.REJECTED;

                    case APPROVED ->
                            requestedStatus
                                    == InterviewExperienceStatus.HIDDEN;

                    case REJECTED ->
                            requestedStatus
                                    == InterviewExperienceStatus.PENDING_REVIEW;

                    case HIDDEN ->
                            requestedStatus
                                    == InterviewExperienceStatus.APPROVED
                                    || requestedStatus
                                    == InterviewExperienceStatus.REJECTED;
                };

        if (!valid) {
            throw new BadRequestException(
                    "Invalid moderation transition from "
                            + currentStatus
                            + " to "
                            + requestedStatus
                            + "."
            );
        }
    }

    private InterviewExperience getDetailedExperience(
            Long experienceId
    ) {

        return experienceRepository.findDetailedById(
                        experienceId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Interview experience not found."
                        )
                );
    }

    private Company resolveCompany(
            Long companyId
    ) {

        if (companyId == null) {
            return null;
        }

        return companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Company not found."
                        )
                );
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

    private void assertOwner(
            InterviewExperience experience,
            Long currentUserId
    ) {

        if (experience.getSubmittedBy() == null
                || !Objects.equals(
                experience.getSubmittedBy().getId(),
                currentUserId
        )) {

            throw new AccessDeniedException(
                    "You are not allowed to modify this interview experience."
            );
        }
    }

    private void assertAdmin() {

        if (!hasRole("ROLE_ADMIN")) {
            throw new AccessDeniedException(
                    "Administrator access is required."
            );
        }
    }

    private boolean hasRole(
            String authority
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority ->
                        authority.equals(
                                grantedAuthority.getAuthority()
                        )
                );
    }

    private Long getOptionalCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getPrincipal()
        )) {
            return null;
        }

        if (authentication.getPrincipal()
                instanceof CustomUserDetails userDetails) {

            return userDetails.getUser().getId();
        }

        return null;
    }

    private boolean hasText(
            String value
    ) {

        return value != null
                && !value.isBlank();
    }

    private String contains(
            String value
    ) {

        return "%"
                + value.strip().toLowerCase()
                + "%";
    }
}