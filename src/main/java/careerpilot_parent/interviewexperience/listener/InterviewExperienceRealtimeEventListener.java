package careerpilot_parent.interviewexperience.listener;

import careerpilot_parent.interviewexperience.event.InterviewCommentCreatedEvent;
import careerpilot_parent.interviewexperience.event.InterviewExperienceLikedEvent;
import careerpilot_parent.interviewexperience.event.InterviewExperienceModeratedEvent;
import careerpilot_parent.interviewexperience.event.InterviewExperienceUnlikedEvent;
import careerpilot_parent.interviewexperience.event.InterviewReplyCreatedEvent;
import careerpilot_parent.interviewexperience.realtime.CommentRealtimeResponse;
import careerpilot_parent.interviewexperience.realtime.LikeRealtimeResponse;
import careerpilot_parent.interviewexperience.realtime.ModerationRealtimeResponse;
import careerpilot_parent.interviewexperience.realtime.ReplyRealtimeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewExperienceRealtimeEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleExperienceLiked(
            InterviewExperienceLikedEvent event
    ) {

        LikeRealtimeResponse response =
                new LikeRealtimeResponse(
                        event.experienceId(),
                        event.likeCount(),
                        true,
                        event.actorUserId(),
                        LocalDateTime.now()
                );

        messagingTemplate.convertAndSend(
                experienceLikeTopic(
                        event.experienceId()
                ),
                response
        );

        /*
         * Avoid notifying the user when they like their own post.
         */
        if (event.experienceOwnerUserId() != null
                && !event.experienceOwnerUserId()
                .equals(event.actorUserId())) {

            messagingTemplate.convertAndSendToUser(
                    event.experienceOwnerUserId().toString(),
                    "/queue/notifications",
                    response
            );
        }

        log.debug(
                "Published interview experience like event. " +
                        "experienceId={}, likeCount={}",
                event.experienceId(),
                event.likeCount()
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleExperienceUnliked(
            InterviewExperienceUnlikedEvent event
    ) {

        LikeRealtimeResponse response =
                new LikeRealtimeResponse(
                        event.experienceId(),
                        event.likeCount(),
                        false,
                        event.actorUserId(),
                        LocalDateTime.now()
                );

        messagingTemplate.convertAndSend(
                experienceLikeTopic(
                        event.experienceId()
                ),
                response
        );

        log.debug(
                "Published interview experience unlike event. " +
                        "experienceId={}, likeCount={}",
                event.experienceId(),
                event.likeCount()
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleCommentCreated(
            InterviewCommentCreatedEvent event
    ) {

        CommentRealtimeResponse response =
                new CommentRealtimeResponse(
                        event.experienceId(),
                        event.commentId(),
                        event.commentCount(),
                        "COMMENT_CREATED",
                        LocalDateTime.now()
                );

        messagingTemplate.convertAndSend(
                experienceCommentTopic(
                        event.experienceId()
                ),
                response
        );

        if (event.experienceOwnerUserId() != null
                && !event.experienceOwnerUserId()
                .equals(event.actorUserId())) {

            messagingTemplate.convertAndSendToUser(
                    event.experienceOwnerUserId().toString(),
                    "/queue/notifications",
                    response
            );
        }

        log.debug(
                "Published comment event. experienceId={}, commentId={}",
                event.experienceId(),
                event.commentId()
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleReplyCreated(
            InterviewReplyCreatedEvent event
    ) {

        ReplyRealtimeResponse response =
                new ReplyRealtimeResponse(
                        event.experienceId(),
                        event.parentCommentId(),
                        event.replyId(),
                        event.replyCount(),
                        "REPLY_CREATED",
                        LocalDateTime.now()
                );

        messagingTemplate.convertAndSend(
                commentReplyTopic(
                        event.experienceId(),
                        event.parentCommentId()
                ),
                response
        );

        /*
         * Also publish to the main comment topic so Angular can update
         * the top-level comment's replyCount.
         */
        messagingTemplate.convertAndSend(
                experienceCommentTopic(
                        event.experienceId()
                ),
                response
        );

        if (event.parentCommentOwnerUserId() != null
                && !event.parentCommentOwnerUserId()
                .equals(event.actorUserId())) {

            messagingTemplate.convertAndSendToUser(
                    event.parentCommentOwnerUserId().toString(),
                    "/queue/notifications",
                    response
            );
        }

        log.debug(
                "Published reply event. experienceId={}, " +
                        "parentCommentId={}, replyId={}",
                event.experienceId(),
                event.parentCommentId(),
                event.replyId()
        );
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleExperienceModerated(
            InterviewExperienceModeratedEvent event
    ) {

        ModerationRealtimeResponse response =
                new ModerationRealtimeResponse(
                        event.experienceId(),
                        event.status(),
                        LocalDateTime.now()
                );

        messagingTemplate.convertAndSend(
                "/topic/interview-experiences/moderation",
                response
        );

        if (event.ownerUserId() != null) {

            messagingTemplate.convertAndSendToUser(
                    event.ownerUserId().toString(),
                    "/queue/notifications",
                    response
            );
        }

        log.debug(
                "Published moderation event. experienceId={}, status={}",
                event.experienceId(),
                event.status()
        );
    }

    private String experienceLikeTopic(
            Long experienceId
    ) {

        return "/topic/interview-experiences/"
                + experienceId
                + "/likes";
    }

    private String experienceCommentTopic(
            Long experienceId
    ) {

        return "/topic/interview-experiences/"
                + experienceId
                + "/comments";
    }

    private String commentReplyTopic(
            Long experienceId,
            Long parentCommentId
    ) {

        return "/topic/interview-experiences/"
                + experienceId
                + "/comments/"
                + parentCommentId
                + "/replies";
    }
}