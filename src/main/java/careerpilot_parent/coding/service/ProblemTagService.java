package careerpilot_parent.coding.service;

import careerpilot_parent.coding.dto.request.ProblemTagRequests;
import careerpilot_parent.coding.dto.response.ProblemTagResponse;

import java.util.List;

public interface ProblemTagService {

    ProblemTagResponse create(
            ProblemTagRequests.Create request
    );

    ProblemTagResponse update(
            Long tagId,
            ProblemTagRequests.Update request
    );

    ProblemTagResponse get(Long tagId);

    List<ProblemTagResponse> list(
            Boolean includeInactive
    );

    void delete(Long tagId);
}