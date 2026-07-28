package careerpilot_parent.admin.controller;

import careerpilot_parent.admin.dto.response.AdminDashboardResponse;
import careerpilot_parent.admin.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final AdminDashboardService
            adminDashboardService;

    @GetMapping
    public ResponseEntity<AdminDashboardResponse>
    getDashboard() {

        return ResponseEntity.ok(
                adminDashboardService.getDashboard()
        );
    }
}
