package career_Navigator_parent.coding.service;

import career_Navigator_parent.coding.dto.request.ProblemRequests.Activation;
import career_Navigator_parent.coding.dto.request.ProblemRequests.Create;
import career_Navigator_parent.coding.dto.request.ProblemRequests.Status;
import career_Navigator_parent.coding.dto.request.ProblemRequests.Update;
import career_Navigator_parent.coding.dto.response.CodingResponses.Admin;
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