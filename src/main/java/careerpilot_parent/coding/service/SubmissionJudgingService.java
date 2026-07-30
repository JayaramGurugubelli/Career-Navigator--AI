package careerpilot_parent.coding.service;

import careerpilot_parent.coding.dto.request.ExecutionRequests;
import careerpilot_parent.coding.dto.response.CodingResponses.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubmissionJudgingService {

    Submission get(
            Long submissionId
    );

    Page<Submission> history(
            Long problemId,
            Pageable pageable
    );

    void judge(
            Long submissionId
    );

    void markAsFailed(
            Long submissionId,
            String errorMessage
    );
    public Submission submit(ExecutionRequests.Submit request);
}