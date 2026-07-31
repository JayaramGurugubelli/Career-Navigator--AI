package career_Navigator_parent.interviewexperience.service;

import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceLikeResponse;

public interface InterviewExperienceLikeService {

    /*
     * Creates one like for the current user.
     * Duplicate like must return HTTP 409.
     */
    InterviewExperienceLikeResponse likeExperience(
            Long experienceId
    );

    /*
     * Removes current user's like.
     */
    InterviewExperienceLikeResponse unlikeExperience(
            Long experienceId
    );

    /*
     * Useful for experience-detail refresh and WebSocket events.
     */
    InterviewExperienceLikeResponse getLikeState(
            Long experienceId
    );

    boolean isLikedByCurrentUser(
            Long experienceId
    );

    long getLikeCount(
            Long experienceId
    );
}