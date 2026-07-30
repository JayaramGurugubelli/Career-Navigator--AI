package careerpilot_parent.coding.service;

import careerpilot_parent.coding.dto.request.ProblemRequests.Activation;
import careerpilot_parent.coding.dto.request.ProblemRequests.Create;
import careerpilot_parent.coding.dto.request.ProblemRequests.Status;
import careerpilot_parent.coding.dto.request.ProblemRequests.Update;
import careerpilot_parent.coding.dto.response.CodingResponses.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemManagementService {

    Admin create(Create request);

    Admin update(
            Long problemId,
            Update request
    );

    Admin status(
            Long problemId,
            Status request
    );

    Admin updateActivation(
            Long problemId,
            Activation request
    );

    Admin get(Long problemId);

    Page<Admin> list(Pageable pageable);

    void delete(Long problemId);
}