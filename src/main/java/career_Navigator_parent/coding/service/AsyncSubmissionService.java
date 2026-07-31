package career_Navigator_parent.coding.service;

import career_Navigator_parent.coding.dto.request.SubmitCodeRequest;
import career_Navigator_parent.coding.dto.response.SubmissionAcceptedResponse;

public interface AsyncSubmissionService {

    SubmissionAcceptedResponse enqueue(
            SubmitCodeRequest request
    );
}