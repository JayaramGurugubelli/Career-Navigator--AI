package career_Navigator_parent.interviewexperience.service.impl;

import career_Navigator_parent.common.exception.BadRequestException;
import career_Navigator_parent.common.exception.ResourceNotFoundException;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceLikeResponse;
import career_Navigator_parent.interviewexperience.entity.InterviewExperience;
import career_Navigator_parent.interviewexperience.entity.InterviewExperienceLike;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceStatus;
import career_Navigator_parent.interviewexperience.event.InterviewExperienceLikedEvent;
import career_Navigator_parent.interviewexperience.event.InterviewExperienceUnlikedEvent;
import career_Navigator_parent.interviewexperience.repository.InterviewExperienceLikeRepository;
import career_Navigator_parent.interviewexperience.repository.InterviewExperienceRepository;
import career_Navigator_parent.interviewexperience.service.InterviewExperienceLikeService;
import career_Navigator_parent.security.util.SecurityUtils;
import career_Navigator_parent.user.entity.User;
import career_Navigator_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewExperienceLikeServiceImpl implements InterviewExperienceLikeService {

    private final InterviewExperienceRepository experienceRepository;
    private final InterviewExperienceLikeRepository likeRepository;
    private final UserRepository userRepository;

    private final SecurityUtils securityUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public InterviewExperienceLikeResponse likeExperience(
            Long experienceId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        InterviewExperience experience =
                getApprovedExperience(experienceId);

        if (likeRepository
                .existsByInterviewExperience_IdAndUser_Id(
                        experienceId,
                        currentUserId
                )) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already liked this interview experience."
            );
        }

        User currentUser =
                userRepository.findById(currentUserId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found."
                                )
                        );

        InterviewExperienceLike like =
                InterviewExperienceLike.builder()
                        .interviewExperience(experience)
                        .user(currentUser)
                        .build();

        try {
            likeRepository.saveAndFlush(like);
        } catch (DataIntegrityViolationException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You have already liked this interview experience."
            );
        }

        experienceRepository.incrementLikeCount(
                experienceId
        );

        int updatedLikeCount =
                experienceRepository.findLikeCount(
                                experienceId
                        )
                        .orElse(0);

        eventPublisher.publishEvent(
                new InterviewExperienceLikedEvent(
                        experienceId,
                        currentUserId,
                        experience.getSubmittedBy().getId(),
                        updatedLikeCount
                )
        );

        return InterviewExperienceLikeResponse.builder()
                .interviewExperienceId(experienceId)
                .liked(true)
                .likeCount(updatedLikeCount)
                .build();
    }

    @Override
    public InterviewExperienceLikeResponse unlikeExperience(
            Long experienceId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        getApprovedExperience(experienceId);

        InterviewExperienceLike like =
                likeRepository
                        .findByInterviewExperience_IdAndUser_Id(
                                experienceId,
                                currentUserId
                        )
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "You have not liked this interview experience."
                                )
                        );

        likeRepository.delete(like);
        likeRepository.flush();

        experienceRepository.decrementLikeCount(
                experienceId
        );

        int updatedLikeCount =
                experienceRepository.findLikeCount(
                                experienceId
                        )
                        .orElse(0);

        eventPublisher.publishEvent(
                new InterviewExperienceUnlikedEvent(
                        experienceId,
                        currentUserId,
                        updatedLikeCount
                )
        );

        return InterviewExperienceLikeResponse.builder()
                .interviewExperienceId(experienceId)
                .liked(false)
                .likeCount(updatedLikeCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public InterviewExperienceLikeResponse getLikeState(
            Long experienceId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        getApprovedExperience(experienceId);

        boolean liked =
                likeRepository
                        .existsByInterviewExperience_IdAndUser_Id(
                                experienceId,
                                currentUserId
                        );

        int likeCount =
                experienceRepository.findLikeCount(
                                experienceId
                        )
                        .orElse(0);

        return InterviewExperienceLikeResponse.builder()
                .interviewExperienceId(experienceId)
                .liked(liked)
                .likeCount(likeCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLikedByCurrentUser(
            Long experienceId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        return likeRepository
                .existsByInterviewExperience_IdAndUser_Id(
                        experienceId,
                        currentUserId
                );
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(
            Long experienceId
    ) {

        if (!experienceRepository.existsById(experienceId)) {
            throw new ResourceNotFoundException(
                    "Interview experience not found."
            );
        }

        return likeRepository
                .countByInterviewExperience_Id(
                        experienceId
                );
    }

    private InterviewExperience getApprovedExperience(
            Long experienceId
    ) {

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
                    "This interview experience is not available for likes."
            );
        }

        return experience;
    }
}