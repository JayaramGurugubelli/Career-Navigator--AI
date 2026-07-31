package career_Navigator_parent.interview.service;

import career_Navigator_parent.interview.dto.request.CreateInterviewRequest;
import career_Navigator_parent.interview.dto.request.UpdateInterviewFeedbackRequest;
import career_Navigator_parent.interview.dto.request.UpdateInterviewRequest;
import career_Navigator_parent.interview.dto.request.UpdateInterviewStatusRequest;
import career_Navigator_parent.interview.dto.response.InterviewResponse;
import career_Navigator_parent.interview.enums.InterviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruiterInterviewService {

    InterviewResponse createInterview(
            Long applicationId,
            CreateInterviewRequest request
    );

    Page<InterviewResponse> getMyInterviews(
            InterviewStatus status,
            Pageable pageable
    );

    Page<InterviewResponse> getApplicationInterviews(
            Long applicationId,
            Pageable pageable
    );

    InterviewResponse getInterviewById(
            Long interviewId
    );

    InterviewResponse updateInterview(
            Long interviewId,
            UpdateInterviewRequest request
    );

    InterviewResponse updateInterviewStatus(
            Long interviewId,
            UpdateInterviewStatusRequest request
    );

    InterviewResponse updateInterviewFeedback(
            Long interviewId,
            UpdateInterviewFeedbackRequest request
    );

    void deleteInterview(
            Long interviewId
    );
}