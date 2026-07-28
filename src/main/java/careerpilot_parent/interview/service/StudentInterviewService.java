package careerpilot_parent.interview.service;

import careerpilot_parent.interview.dto.request.StudentInterviewResponseRequest;
import careerpilot_parent.interview.dto.response.InterviewResponse;
import careerpilot_parent.interview.enums.InterviewStatus;
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