package careerpilot_parent.interviewexperience.mapper;

import careerpilot_parent.company.entity.Company;
import careerpilot_parent.interviewexperience.dto.request.CreateInterviewExperienceRequest;
import careerpilot_parent.interviewexperience.dto.request.CreateInterviewQuestionRequest;
import careerpilot_parent.interviewexperience.dto.request.CreateInterviewRoundRequest;
import careerpilot_parent.interviewexperience.dto.request.UpdateInterviewExperienceRequest;
import careerpilot_parent.interviewexperience.dto.request.UpdateInterviewQuestionRequest;
import careerpilot_parent.interviewexperience.dto.request.UpdateInterviewRoundRequest;
import careerpilot_parent.interviewexperience.dto.response.InterviewContributorResponse;
import careerpilot_parent.interviewexperience.dto.response.InterviewExperienceDetailResponse;
import careerpilot_parent.interviewexperience.dto.response.InterviewExperienceSummaryResponse;
import careerpilot_parent.interviewexperience.dto.response.InterviewQuestionResponse;
import careerpilot_parent.interviewexperience.dto.response.InterviewRoundResponse;
import careerpilot_parent.interviewexperience.entity.InterviewExperience;
import careerpilot_parent.interviewexperience.entity.InterviewExperienceRound;
import careerpilot_parent.interviewexperience.entity.InterviewQuestion;
import careerpilot_parent.interviewexperience.enums.InterviewExperienceStatus;
import careerpilot_parent.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class InterviewExperienceMapper {

    public InterviewExperience toEntity(
            CreateInterviewExperienceRequest request,
            User submittedBy,
            Company company
    ) {

        InterviewExperience experience =
                InterviewExperience.builder()
                        .submittedBy(submittedBy)
                        .company(company)
                        .companyName(normalize(request.getCompanyName()))
                        .jobRole(normalize(request.getJobRole()))
                        .experienceLevel(
                                normalizeNullable(
                                        request.getExperienceLevel()
                                )
                        )
                        .location(
                                normalizeNullable(
                                        request.getLocation()
                                )
                        )
                        .preparationTips(
                                normalizeNullable(
                                        request.getPreparationTips()
                                )
                        )
                        .anonymous(
                                Boolean.TRUE.equals(
                                        request.getAnonymous()
                                )
                        )
                        .verified(false)
                        .status(
                                InterviewExperienceStatus.PENDING_REVIEW
                        )
                        .likeCount(0)
                        .commentCount(0)
                        .reportCount(0)
                        .rounds(new ArrayList<>())
                        .build();

        if (request.getRounds() != null) {

            request.getRounds()
                    .stream()
                    .sorted(
                            Comparator.comparing(
                                    CreateInterviewRoundRequest::getDisplayOrder
                            )
                    )
                    .map(this::toRoundEntity)
                    .forEach(experience::addRound);
        }

        return experience;
    }

    public void updateEntity(
            InterviewExperience experience,
            UpdateInterviewExperienceRequest request,
            Company company
    ) {

        experience.setCompany(company);
        experience.setCompanyName(
                normalize(request.getCompanyName())
        );
        experience.setJobRole(
                normalize(request.getJobRole())
        );
        experience.setExperienceLevel(
                normalizeNullable(
                        request.getExperienceLevel()
                )
        );
        experience.setLocation(
                normalizeNullable(
                        request.getLocation()
                )
        );
        experience.setPreparationTips(
                normalizeNullable(
                        request.getPreparationTips()
                )
        );
        experience.setAnonymous(
                Boolean.TRUE.equals(
                        request.getAnonymous()
                )
        );

        /*
         * Replace rounds and questions as one aggregate.
         * orphanRemoval=true deletes previous children.
         */
        experience.getRounds().clear();

        if (request.getRounds() != null) {

            request.getRounds()
                    .stream()
                    .sorted(
                            Comparator.comparing(
                                    UpdateInterviewRoundRequest::getDisplayOrder
                            )
                    )
                    .map(this::toRoundEntity)
                    .forEach(experience::addRound);
        }

        /*
         * An edited approved or rejected post should be reviewed again.
         */
        experience.setStatus(
                InterviewExperienceStatus.PENDING_REVIEW
        );
        experience.setVerified(false);
    }

    public InterviewExperienceRound toRoundEntity(
            CreateInterviewRoundRequest request
    ) {

        InterviewExperienceRound round =
                InterviewExperienceRound.builder()
                        .roundNumber(request.getRoundNumber())
                        .roundTitle(
                                normalize(request.getRoundTitle())
                        )
                        .roundType(request.getRoundType())
                        .durationMinutes(
                                request.getDurationMinutes()
                        )
                        .displayOrder(
                                request.getDisplayOrder()
                        )
                        .questions(new ArrayList<>())
                        .build();

        if (request.getQuestions() != null) {

            request.getQuestions()
                    .stream()
                    .sorted(
                            Comparator.comparing(
                                    CreateInterviewQuestionRequest::getDisplayOrder
                            )
                    )
                    .map(this::toQuestionEntity)
                    .forEach(round::addQuestion);
        }

        return round;
    }

    public InterviewExperienceRound toRoundEntity(
            UpdateInterviewRoundRequest request
    ) {

        InterviewExperienceRound round =
                InterviewExperienceRound.builder()
                        .roundNumber(request.getRoundNumber())
                        .roundTitle(
                                normalize(request.getRoundTitle())
                        )
                        .roundType(request.getRoundType())
                        .durationMinutes(
                                request.getDurationMinutes()
                        )
                        .displayOrder(
                                request.getDisplayOrder()
                        )
                        .questions(new ArrayList<>())
                        .build();

        if (request.getQuestions() != null) {

            request.getQuestions()
                    .stream()
                    .sorted(
                            Comparator.comparing(
                                    UpdateInterviewQuestionRequest::getDisplayOrder
                            )
                    )
                    .map(this::toQuestionEntity)
                    .forEach(round::addQuestion);
        }

        return round;
    }

    public InterviewQuestion toQuestionEntity(
            CreateInterviewQuestionRequest request
    ) {

        return InterviewQuestion.builder()
                .question(
                        normalize(request.getQuestion())
                )
                .category(request.getCategory())
                .topic(
                        normalizeNullable(
                                request.getTopic()
                        )
                )
                .additionalDetails(
                        normalizeNullable(
                                request.getAdditionalDetails()
                        )
                )
                .displayOrder(
                        request.getDisplayOrder()
                )
                .build();
    }

    public InterviewQuestion toQuestionEntity(
            UpdateInterviewQuestionRequest request
    ) {

        return InterviewQuestion.builder()
                .question(
                        normalize(request.getQuestion())
                )
                .category(request.getCategory())
                .topic(
                        normalizeNullable(
                                request.getTopic()
                        )
                )
                .additionalDetails(
                        normalizeNullable(
                                request.getAdditionalDetails()
                        )
                )
                .displayOrder(
                        request.getDisplayOrder()
                )
                .build();
    }

    public InterviewExperienceSummaryResponse toSummaryResponse(
            InterviewExperience experience,
            Long currentUserId,
            boolean likedByCurrentUser,
            boolean includeStatus
    ) {

        int roundCount =
                experience.getRounds() == null
                        ? 0
                        : experience.getRounds().size();

        int questionCount =
                experience.getRounds() == null
                        ? 0
                        : experience.getRounds()
                        .stream()
                        .filter(Objects::nonNull)
                        .mapToInt(round ->
                                round.getQuestions() == null
                                        ? 0
                                        : round.getQuestions().size()
                        )
                        .sum();

        return InterviewExperienceSummaryResponse.builder()
                .id(experience.getId())
                .companyId(
                        experience.getCompany() == null
                                ? null
                                : experience.getCompany().getId()
                )
                .companyName(
                        experience.getCompanyName()
                )
                .jobRole(
                        experience.getJobRole()
                )
                .experienceLevel(
                        experience.getExperienceLevel()
                )
                .location(
                        experience.getLocation()
                )
                .anonymous(
                        experience.getAnonymous()
                )
                .verified(
                        experience.getVerified()
                )
                .contributor(
                        toContributorResponse(experience)
                )
                .roundCount(roundCount)
                .questionCount(questionCount)
                .likeCount(
                        safeInteger(
                                experience.getLikeCount()
                        )
                )
                .commentCount(
                        safeInteger(
                                experience.getCommentCount()
                        )
                )
                .likedByCurrentUser(
                        likedByCurrentUser
                )
                .status(
                        includeStatus
                                ? experience.getStatus()
                                : null
                )
                .createdAt(
                        experience.getCreatedAt()
                )
                .updatedAt(
                        experience.getUpdatedAt()
                )
                .build();
    }

    public InterviewExperienceDetailResponse toDetailResponse(
            InterviewExperience experience,
            Long currentUserId,
            boolean likedByCurrentUser,
            boolean includeStatus
    ) {

        boolean ownedByCurrentUser =
                isOwnedByCurrentUser(
                        experience,
                        currentUserId
                );

        List<InterviewRoundResponse> rounds =
                experience.getRounds() == null
                        ? List.of()
                        : experience.getRounds()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        InterviewExperienceRound::getDisplayOrder
                                )
                        )
                        .map(this::toRoundResponse)
                        .toList();

        return InterviewExperienceDetailResponse.builder()
                .id(experience.getId())
                .companyId(
                        experience.getCompany() == null
                                ? null
                                : experience.getCompany().getId()
                )
                .companyName(
                        experience.getCompanyName()
                )
                .jobRole(
                        experience.getJobRole()
                )
                .experienceLevel(
                        experience.getExperienceLevel()
                )
                .location(
                        experience.getLocation()
                )
                .preparationTips(
                        experience.getPreparationTips()
                )
                .anonymous(
                        experience.getAnonymous()
                )
                .verified(
                        experience.getVerified()
                )
                .contributor(
                        toContributorResponse(experience)
                )
                .likeCount(
                        safeInteger(
                                experience.getLikeCount()
                        )
                )
                .commentCount(
                        safeInteger(
                                experience.getCommentCount()
                        )
                )
                .likedByCurrentUser(
                        likedByCurrentUser
                )
                .ownedByCurrentUser(
                        ownedByCurrentUser
                )
                .status(
                        includeStatus
                                ? experience.getStatus()
                                : null
                )
                .rounds(rounds)
                .createdAt(
                        experience.getCreatedAt()
                )
                .updatedAt(
                        experience.getUpdatedAt()
                )
                .build();
    }

    public InterviewRoundResponse toRoundResponse(
            InterviewExperienceRound round
    ) {

        List<InterviewQuestionResponse> questions =
                round.getQuestions() == null
                        ? List.of()
                        : round.getQuestions()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        InterviewQuestion::getDisplayOrder
                                )
                        )
                        .map(this::toQuestionResponse)
                        .toList();

        return InterviewRoundResponse.builder()
                .id(round.getId())
                .roundNumber(
                        round.getRoundNumber()
                )
                .roundTitle(
                        round.getRoundTitle()
                )
                .roundType(
                        round.getRoundType()
                )
                .durationMinutes(
                        round.getDurationMinutes()
                )
                .displayOrder(
                        round.getDisplayOrder()
                )
                .questions(questions)
                .build();
    }

    public InterviewQuestionResponse toQuestionResponse(
            InterviewQuestion question
    ) {

        return InterviewQuestionResponse.builder()
                .id(question.getId())
                .question(
                        question.getQuestion()
                )
                .category(
                        question.getCategory()
                )
                .topic(
                        question.getTopic()
                )
                .additionalDetails(
                        question.getAdditionalDetails()
                )
                .displayOrder(
                        question.getDisplayOrder()
                )
                .build();
    }

    public InterviewContributorResponse toContributorResponse(
            InterviewExperience experience
    ) {

        if (Boolean.TRUE.equals(
                experience.getAnonymous()
        )) {

            return InterviewContributorResponse.builder()
                    .displayName(
                            "Anonymous Contributor"
                    )
                    .profilePictureUrl(null)
                    .build();
        }

        User user = experience.getSubmittedBy();

        return InterviewContributorResponse.builder()
                .displayName(
                        resolveDisplayName(user)
                )
                /*
                 * Replace null after connecting your profile-picture service.
                 * Do not expose storagePath directly.
                 */
                .profilePictureUrl(null)
                .build();
    }

    private boolean isOwnedByCurrentUser(
            InterviewExperience experience,
            Long currentUserId
    ) {

        return currentUserId != null
                && experience.getSubmittedBy() != null
                && Objects.equals(
                experience.getSubmittedBy().getId(),
                currentUserId
        );
    }

    private String resolveDisplayName(
            User user
    ) {

        if (user == null) {
            return "CareerPilot User";
        }

        String firstName =
                normalizeNullable(
                        user.getFirstName()
                );

        String lastName =
                normalizeNullable(
                        user.getLastName()
                );

        String fullName =
                String.join(
                        " ",
                        firstName == null
                                ? ""
                                : firstName,
                        lastName == null
                                ? ""
                                : lastName
                ).trim();

        return fullName.isBlank()
                ? "CareerPilot User"
                : fullName;
    }

    private String normalize(
            String value
    ) {

        return value
                .strip()
                .replaceAll(
                        "[\\p{Z}\\s]+",
                        " "
                );
    }

    private String normalizeNullable(
            String value
    ) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return normalize(value);
    }

    private Integer safeInteger(
            Integer value
    ) {

        return value == null
                ? 0
                : value;
    }
}