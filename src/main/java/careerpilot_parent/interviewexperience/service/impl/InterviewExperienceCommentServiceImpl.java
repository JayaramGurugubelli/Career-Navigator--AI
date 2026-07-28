package careerpilot_parent.interviewexperience.service.impl;

import careerpilot_parent.common.exception.BadRequestException;
import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.common.mapper.PageResponseMapper;
import careerpilot_parent.interviewexperience.dto.request.CreateInterviewCommentRequest;
import careerpilot_parent.interviewexperience.dto.request.CreateInterviewReplyRequest;
import careerpilot_parent.interviewexperience.dto.request.UpdateInterviewCommentRequest;
import careerpilot_parent.interviewexperience.dto.response.InterviewCommentResponse;
import careerpilot_parent.interviewexperience.dto.response.PageResponse;
import careerpilot_parent.interviewexperience.entity.InterviewExperience;
import careerpilot_parent.interviewexperience.entity.InterviewExperienceComment;
import careerpilot_parent.interviewexperience.enums.CommentStatus;
import careerpilot_parent.interviewexperience.enums.InterviewExperienceStatus;
import careerpilot_parent.interviewexperience.event.InterviewCommentCreatedEvent;
import careerpilot_parent.interviewexperience.event.InterviewReplyCreatedEvent;
import careerpilot_parent.interviewexperience.mapper.InterviewExperienceCommentMapper;
import careerpilot_parent.interviewexperience.repository.InterviewExperienceCommentRepository;
import careerpilot_parent.interviewexperience.repository.InterviewExperienceRepository;
import careerpilot_parent.interviewexperience.service.InterviewExperienceCommentService;
import careerpilot_parent.interviewexperience.validation.CommentContentValidator;
import careerpilot_parent.security.model.CustomUserDetails;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewExperienceCommentServiceImpl
        implements InterviewExperienceCommentService {

    private final InterviewExperienceRepository experienceRepository;
    private final InterviewExperienceCommentRepository commentRepository;
    private final UserRepository userRepository;

    private final InterviewExperienceCommentMapper commentMapper;
    private final CommentContentValidator contentValidator;
    private final PageResponseMapper pageResponseMapper;

    private final SecurityUtils securityUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public InterviewCommentResponse createComment(
            Long experienceId,
            CreateInterviewCommentRequest request
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        InterviewExperience experience =
                getApprovedExperience(experienceId);

        User currentUser =
                getUser(currentUserId);

        String content =
                contentValidator.validateAndNormalize(
                        request.getContent()
                );

        InterviewExperienceComment comment =
                commentMapper.toEntity(
                        experience,
                        currentUser,
                        content
                );

        InterviewExperienceComment savedComment =
                commentRepository.save(comment);

        experienceRepository.incrementCommentCount(
                experienceId
        );

        int updatedCommentCount =
                experienceRepository.findCommentCount(
                                experienceId
                        )
                        .orElse(0);

        eventPublisher.publishEvent(
                new InterviewCommentCreatedEvent(
                        experienceId,
                        savedComment.getId(),
                        currentUserId,
                        experience.getSubmittedBy().getId(),
                        updatedCommentCount
                )
        );

        return commentMapper.toResponse(
                savedComment,
                currentUserId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterviewCommentResponse> getComments(
            Long experienceId,
            Pageable pageable
    ) {

        getApprovedExperience(experienceId);

        Long currentUserId =
                getOptionalCurrentUserId();

        Page<InterviewExperienceComment> page =
                commentRepository
                        .findByInterviewExperience_IdAndParentCommentIsNullAndStatusAndDeletedFalseOrderByCreatedAtAsc(
                                experienceId,
                                CommentStatus.VISIBLE,
                                pageable
                        );

        return pageResponseMapper.toResponse(
                page,
                comment ->
                        commentMapper.toResponse(
                                comment,
                                currentUserId
                        )
        );
    }

    @Override
    public InterviewCommentResponse createReply(
            Long experienceId,
            Long parentCommentId,
            CreateInterviewReplyRequest request
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        InterviewExperience experience =
                getApprovedExperience(experienceId);

        InterviewExperienceComment parentComment =
                commentRepository
                        .findByIdAndInterviewExperience_IdAndStatusAndDeletedFalse(
                                parentCommentId,
                                experienceId,
                                CommentStatus.VISIBLE
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Parent comment not found."
                                )
                        );

        /*
         * Only one reply level:
         *
         * Comment
         * └── Reply
         */
        if (parentComment.getParentComment() != null) {
            throw new BadRequestException(
                    "Replies can only be added to top-level comments."
            );
        }

        User currentUser =
                getUser(currentUserId);

        String content =
                contentValidator.validateAndNormalize(
                        request.getContent()
                );

        InterviewExperienceComment reply =
                commentMapper.toReplyEntity(
                        experience,
                        parentComment,
                        currentUser,
                        content
                );

        InterviewExperienceComment savedReply =
                commentRepository.save(reply);

        commentRepository.incrementReplyCount(
                parentCommentId
        );

        experienceRepository.incrementCommentCount(
                experienceId
        );

        int updatedReplyCount =
                commentRepository.findReplyCount(
                                parentCommentId
                        )
                        .orElse(0);

        eventPublisher.publishEvent(
                new InterviewReplyCreatedEvent(
                        experienceId,
                        parentCommentId,
                        savedReply.getId(),
                        currentUserId,
                        parentComment.getUser().getId(),
                        updatedReplyCount
                )
        );

        return commentMapper.toResponse(
                savedReply,
                currentUserId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InterviewCommentResponse> getReplies(
            Long experienceId,
            Long parentCommentId,
            Pageable pageable
    ) {

        getApprovedExperience(experienceId);

        InterviewExperienceComment parentComment =
                commentRepository
                        .findByIdAndInterviewExperience_Id(
                                parentCommentId,
                                experienceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Parent comment not found."
                                )
                        );

        if (parentComment.getParentComment() != null) {
            throw new BadRequestException(
                    "Replies can only be retrieved for a top-level comment."
            );
        }

        Long currentUserId =
                getOptionalCurrentUserId();

        Page<InterviewExperienceComment> page =
                commentRepository
                        .findByParentComment_IdAndStatusAndDeletedFalseOrderByCreatedAtAsc(
                                parentCommentId,
                                CommentStatus.VISIBLE,
                                pageable
                        );

        return pageResponseMapper.toResponse(
                page,
                reply ->
                        commentMapper.toResponse(
                                reply,
                                currentUserId
                        )
        );
    }

    @Override
    public InterviewCommentResponse updateComment(
            Long experienceId,
            Long commentId,
            UpdateInterviewCommentRequest request
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        getApprovedExperience(experienceId);

        InterviewExperienceComment comment =
                commentRepository
                        .findByIdAndInterviewExperience_Id(
                                commentId,
                                experienceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Comment not found."
                                )
                        );

        assertCommentOwner(
                comment,
                currentUserId
        );

        if (Boolean.TRUE.equals(
                comment.getDeleted()
        )) {
            throw new BadRequestException(
                    "A deleted comment cannot be updated."
            );
        }

        if (comment.getStatus()
                != CommentStatus.VISIBLE) {

            throw new BadRequestException(
                    "Only visible comments can be updated."
            );
        }

        String content =
                contentValidator.validateAndNormalize(
                        request.getContent()
                );

        commentMapper.updateEntity(
                comment,
                content
        );

        InterviewExperienceComment savedComment =
                commentRepository.save(comment);

        return commentMapper.toResponse(
                savedComment,
                currentUserId
        );
    }

    @Override
    public void deleteComment(
            Long experienceId,
            Long commentId
    ) {

        Long currentUserId =
                securityUtils.getCurrentUserId();

        getApprovedExperience(experienceId);

        InterviewExperienceComment comment =
                commentRepository
                        .findByIdAndInterviewExperience_Id(
                                commentId,
                                experienceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Comment not found."
                                )
                        );

        assertCommentOwner(
                comment,
                currentUserId
        );

        if (Boolean.TRUE.equals(
                comment.getDeleted()
        )) {
            return;
        }

        boolean reply =
                comment.getParentComment() != null;

        /*
         * Top-level comments with replies remain as placeholders.
         */
        if (!reply
                && comment.getReplyCount() != null
                && comment.getReplyCount() > 0) {

            comment.setDeleted(true);
            comment.setContent(null);

            /*
             * Keep it visible so the thread structure remains accessible.
             * The mapper displays "This comment was removed."
             */
            comment.setStatus(CommentStatus.VISIBLE);

            commentRepository.save(comment);

        } else {

            comment.setDeleted(true);
            comment.setContent(null);
            comment.setStatus(CommentStatus.REMOVED);

            commentRepository.save(comment);

            if (reply) {
                commentRepository.decrementReplyCount(
                        comment.getParentComment().getId()
                );
            }
        }

        experienceRepository.decrementCommentCount(
                experienceId
        );
    }

    @Override
    public InterviewCommentResponse hideComment(
            Long experienceId,
            Long commentId
    ) {

        assertAdmin();

        InterviewExperienceComment comment =
                commentRepository
                        .findByIdAndInterviewExperience_Id(
                                commentId,
                                experienceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Comment not found."
                                )
                        );

        if (Boolean.TRUE.equals(
                comment.getDeleted()
        )) {
            throw new BadRequestException(
                    "A deleted comment cannot be hidden."
            );
        }

        if (comment.getStatus()
                == CommentStatus.HIDDEN) {

            return commentMapper.toResponse(
                    comment,
                    securityUtils.getCurrentUserId()
            );
        }

        comment.setStatus(CommentStatus.HIDDEN);

        InterviewExperienceComment saved =
                commentRepository.save(comment);

        experienceRepository.decrementCommentCount(
                experienceId
        );

        if (comment.getParentComment() != null) {
            commentRepository.decrementReplyCount(
                    comment.getParentComment().getId()
            );
        }

        return commentMapper.toResponse(
                saved,
                securityUtils.getCurrentUserId()
        );
    }

    @Override
    public InterviewCommentResponse restoreComment(
            Long experienceId,
            Long commentId
    ) {

        assertAdmin();

        InterviewExperienceComment comment =
                commentRepository
                        .findByIdAndInterviewExperience_Id(
                                commentId,
                                experienceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Comment not found."
                                )
                        );

        if (Boolean.TRUE.equals(
                comment.getDeleted()
        )) {
            throw new BadRequestException(
                    "A deleted comment cannot be restored."
            );
        }

        if (comment.getStatus()
                == CommentStatus.VISIBLE) {

            return commentMapper.toResponse(
                    comment,
                    securityUtils.getCurrentUserId()
            );
        }

        comment.setStatus(CommentStatus.VISIBLE);

        InterviewExperienceComment saved =
                commentRepository.save(comment);

        experienceRepository.incrementCommentCount(
                experienceId
        );

        if (comment.getParentComment() != null) {
            commentRepository.incrementReplyCount(
                    comment.getParentComment().getId()
            );
        }

        return commentMapper.toResponse(
                saved,
                securityUtils.getCurrentUserId()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long getVisibleCommentCount(
            Long experienceId
    ) {

        return commentRepository
                .countByInterviewExperience_IdAndStatusAndDeletedFalse(
                        experienceId,
                        CommentStatus.VISIBLE
                );
    }

    @Override
    @Transactional(readOnly = true)
    public long getVisibleReplyCount(
            Long parentCommentId
    ) {

        return commentRepository
                .countByParentComment_IdAndStatusAndDeletedFalse(
                        parentCommentId,
                        CommentStatus.VISIBLE
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

            throw new ResourceNotFoundException(
                    "Interview experience not found."
            );
        }

        return experience;
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

    private void assertCommentOwner(
            InterviewExperienceComment comment,
            Long currentUserId
    ) {

        if (comment.getUser() == null
                || !Objects.equals(
                comment.getUser().getId(),
                currentUserId
        )) {

            throw new AccessDeniedException(
                    "You can modify only your own comments."
            );
        }
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
}