package careerpilot_parent.admin.controller;

import careerpilot_parent.admin.dto.request.UpdateUserStatusRequest;
import careerpilot_parent.admin.dto.response.AdminUserResponse;
import careerpilot_parent.admin.service.AdminUserService;
import careerpilot_parent.shared.enums.AccountStatus;
import careerpilot_parent.shared.enums.RoleName;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> getUsers(
            @RequestParam(required = false) RoleName role,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                adminUserService.getUsers(
                        role,
                        status,
                        keyword,
                        pageable
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserResponse> getUserById(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                adminUserService.getUserById(userId)
        );
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<AdminUserResponse> updateStatus(
            @PathVariable Long userId,
            @Valid
            @RequestBody UpdateUserStatusRequest request
    ) {

        return ResponseEntity.ok(
                adminUserService.updateUserStatus(
                        userId,
                        request
                )
        );
    }
}
