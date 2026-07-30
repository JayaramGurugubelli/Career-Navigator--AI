package careerpilot_parent.coding.service;

import careerpilot_parent.coding.dto.request.SubmitCodeRequest;
import careerpilot_parent.coding.dto.response.SubmissionAcceptedResponse;

public interface AsyncSubmissionService {

    SubmissionAcceptedResponse enqueue(
            SubmitCodeRequest request
    );
}