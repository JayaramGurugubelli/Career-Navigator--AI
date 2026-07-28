package careerpilot_parent.admin.service.impl;

import careerpilot_parent.admin.dto.request.UpdateUserStatusRequest;
import careerpilot_parent.admin.dto.response.AdminUserResponse;
import careerpilot_parent.admin.mapper.AdminMapper;
import careerpilot_parent.admin.service.AdminUserService;
import careerpilot_parent.common.exception.ResourceNotFoundException;
import careerpilot_parent.security.util.SecurityUtils;
import careerpilot_parent.shared.enums.AccountStatus;
import careerpilot_parent.shared.enums.RoleName;
import careerpilot_parent.user.entity.User;
import careerpilot_parent.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final AdminMapper adminMapper;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponse> getUsers(
            RoleName role,
            AccountStatus status,
            String keyword,
            Pageable pageable
    ) {

        List<User> filteredUsers = userRepository.findAll()
                .stream()
                .filter(user -> status == null ||
                        user.getAccountStatus() == status)
                .filter(user -> role == null ||
                        user.getRoles()
                                .stream()
                                .anyMatch(userRole ->
                                        userRole.getRole().getName() == role))
                .filter(user -> matchesKeyword(user, keyword))
                .toList();

        int start = Math.min(
                (int) pageable.getOffset(),
                filteredUsers.size()
        );

        int end = Math.min(
                start + pageable.getPageSize(),
                filteredUsers.size()
        );

        List<AdminUserResponse> content =
                filteredUsers.subList(start, end)
                        .stream()
                        .map(adminMapper::toUserResponse)
                        .toList();

        return new PageImpl<>(
                content,
                pageable,
                filteredUsers.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserResponse getUserById(
            Long userId
    ) {

        return adminMapper.toUserResponse(
                getUser(userId)
        );
    }

    @Override
    public AdminUserResponse updateUserStatus(
            Long userId,
            UpdateUserStatusRequest request
    ) {

        Long currentAdminId =
                securityUtils.getCurrentUserId();

        if (currentAdminId.equals(userId)) {
            throw new IllegalStateException(
                    "Admin cannot change the status of their own account."
            );
        }

        User user = getUser(userId);

        if (user.getAccountStatus() ==
                request.getStatus()) {

            throw new IllegalStateException(
                    "User account is already in "
                            + request.getStatus()
                            + " status."
            );
        }

        user.setAccountStatus(
                request.getStatus()
        );

        return adminMapper.toUserResponse(
                userRepository.save(user)
        );
    }

    private User getUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "User not found."
                        )
                );
    }

    private boolean matchesKeyword(
            User user,
            String keyword
    ) {

        if (keyword == null ||
                keyword.isBlank()) {
            return true;
        }

        String value = keyword.trim().toLowerCase();

        return contains(user.getFirstName(), value)
                || contains(user.getLastName(), value)
                || contains(user.getUsername(), value)
                || contains(user.getEmail(), value);
    }

    private boolean contains(
            String source,
            String value
    ) {

        return source != null &&
                source.toLowerCase().contains(value);
    }
}
