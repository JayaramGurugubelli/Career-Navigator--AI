package career_Navigator_parent.interviewexperience.service;

import career_Navigator_parent.interviewexperience.dto.request.CreateInterviewExperienceRequest;
import career_Navigator_parent.interviewexperience.dto.request.ModerateInterviewExperienceRequest;
import career_Navigator_parent.interviewexperience.dto.request.UpdateInterviewExperienceRequest;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceDetailResponse;
import career_Navigator_parent.interviewexperience.dto.response.InterviewExperienceSummaryResponse;
import career_Navigator_parent.interviewexperience.dto.response.PageResponse;
import career_Navigator_parent.interviewexperience.enums.InterviewExperienceStatus;
import career_Navigator_parent.interviewexperience.enums.InterviewQuestionCategory;
import org.springframework.data.domain.Pageable;

public interface InterviewExperienceService {

    /*
     * Any authenticated user can submit an experience.
     * Initial status: PENDING_REVIEW.
     */
    InterviewExperienceDetailResponse createExperience(
            CreateInterviewExperienceRequest request
    );

    /*
     * Public endpoint.
     * Returns only APPROVED experiences.
     */
    PageResponse<InterviewExperienceSummaryResponse>
    getPublicExperiences(
            Long companyId,
            String companyName,
            String jobRole,
            String experienceLevel,
            String location,
            InterviewQuestionCategory category,
            String topic,
            Pageable pageable
    );

    /*
     * Public users can access only APPROVED experiences.
     * Owner/admin can access non-public experiences.
     */
    InterviewExperienceDetailResponse getExperienceById(
            Long experienceId
    );

    /*
     * Returns all experiences submitted by current user,
     * including pending, approved, rejected and hidden.
     */
    PageResponse<InterviewExperienceSummaryResponse>
    getMyExperiences(
            InterviewExperienceStatus status,
            Pageable pageable
    );

    /*
     * Only the experience owner may update it.
     * Updating sends the post back to PENDING_REVIEW.
     */
    InterviewExperienceDetailResponse updateExperience(
            Long experienceId,
            UpdateInterviewExperienceRequest request
    );

    /*
     * Owner delete.
     * Prefer soft deletion or HIDDEN status in production.
     */
    void deleteExperience(
            Long experienceId
    );

    /*
     * Admin list used for moderation.
     */
    PageResponse<InterviewExperienceSummaryResponse>
    getExperiencesForAdmin(
            InterviewExperienceStatus status,
            String keyword,
            Pageable pageable
    );

    /*
     * Admin-only moderation operation.
     */
    InterviewExperienceDetailResponse moderateExperience(
            Long experienceId,
            ModerateInterviewExperienceRequest request
    );

    /*
     * Used when a hidden/rejected experience is corrected and resubmitted.
     */
    InterviewExperienceDetailResponse resubmitExperience(
            Long experienceId
    );
}