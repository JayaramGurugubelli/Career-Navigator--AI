package careerpilot_parent.admin.service;

import careerpilot_parent.admin.dto.request.UpdateUserStatusRequest;
import careerpilot_parent.admin.dto.response.AdminUserResponse;
import careerpilot_parent.shared.enums.AccountStatus;
import careerpilot_parent.shared.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {

    Page<AdminUserResponse> getUsers(
            RoleName role,
            AccountStatus status,
            String keyword,
            Pageable pageable
    );

    AdminUserResponse getUserById(Long userId);

    AdminUserResponse updateUserStatus(
            Long userId,
            UpdateUserStatusRequest request
    );
}
