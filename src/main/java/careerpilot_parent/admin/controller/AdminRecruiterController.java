package careerpilot_parent.admin.controller;

import careerpilot_parent.admin.dto.request.RecruiterVerificationRequest;
import careerpilot_parent.admin.dto.request.RejectRecruiterRequest;
import careerpilot_parent.admin.dto.response.AdminRecruiterResponse;
import careerpilot_parent.admin.service.AdminRecruiterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/recruiters")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRecruiterController {

    private final AdminRecruiterService
            adminRecruiterService;

    @GetMapping
    public ResponseEntity<Page<AdminRecruiterResponse>>
    getRecruiters(
            @RequestParam(required = false)
            Boolean verified,

            @RequestParam(required = false)
            Boolean active,

            @PageableDefault(size = 20)
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                adminRecruiterService.getRecruiters(
                        verified,
                        active,
                        pageable
                )
        );
    }

    @GetMapping("/{recruiterId}")
    public ResponseEntity<AdminRecruiterResponse>
    getRecruiterById(
            @PathVariable Long recruiterId
    ) {

        return ResponseEntity.ok(
                adminRecruiterService
                        .getRecruiterById(
                                recruiterId
                        )
        );
    }

    @PatchMapping("/{recruiterId}/verify")
    public ResponseEntity<AdminRecruiterResponse>
    verifyRecruiter(
            @PathVariable Long recruiterId,
            @Valid
            @RequestBody
            RecruiterVerificationRequest request
    ) {

        return ResponseEntity.ok(
                adminRecruiterService.verifyRecruiter(
                        recruiterId,
                        request
                )
        );
    }

    @PatchMapping("/{recruiterId}/reject")
    public ResponseEntity<AdminRecruiterResponse>
    rejectRecruiter(
            @PathVariable Long recruiterId,
            @Valid
            @RequestBody
            RejectRecruiterRequest request
    ) {

        return ResponseEntity.ok(
                adminRecruiterService.rejectRecruiter(
                        recruiterId,
                        request
                )
        );
    }

    @PatchMapping("/{recruiterId}/active")
    public ResponseEntity<AdminRecruiterResponse>
    updateActiveStatus(
            @PathVariable Long recruiterId,
            @RequestParam boolean active
    ) {

        return ResponseEntity.ok(
                adminRecruiterService
                        .updateRecruiterActiveStatus(
                                recruiterId,
                                active
                        )
        );
    }
}
