package careerpilot_parent.admin.service;

import careerpilot_parent.admin.dto.request.RecruiterVerificationRequest;
import careerpilot_parent.admin.dto.request.RejectRecruiterRequest;
import careerpilot_parent.admin.dto.response.AdminRecruiterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminRecruiterService {

    Page<AdminRecruiterResponse> getRecruiters(
            Boolean verified,
            Boolean active,
            Pageable pageable
    );

    AdminRecruiterResponse getRecruiterById(Long recruiterId);

    AdminRecruiterResponse verifyRecruiter(
            Long recruiterId,
            RecruiterVerificationRequest request
    );

    AdminRecruiterResponse rejectRecruiter(
            Long recruiterId,
            RejectRecruiterRequest request
    );

    AdminRecruiterResponse updateRecruiterActiveStatus(
            Long recruiterId,
            boolean active
    );
}
