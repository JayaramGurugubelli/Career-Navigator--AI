package career_Navigator_parent.interview.service;

import career_Navigator_parent.interview.dto.request.StudentInterviewResponseRequest;
import career_Navigator_parent.interview.dto.response.InterviewResponse;
import career_Navigator_parent.interview.enums.InterviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentInterviewService {

    Page<InterviewResponse> getMyInterviews(
            InterviewStatus status,
            Pageable pageable
    );

    InterviewResponse getInterviewById(
            Long interviewId
    );

    InterviewResponse confirmInterview(
            Long interviewId,
            StudentInterviewResponseRequest request
    );

    InterviewResponse declineInterview(
            Long interviewId,
            StudentInterviewResponseRequest request
    );
}