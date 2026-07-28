package careerpilot_parent.audit.controller;

import careerpilot_parent.audit.dto.response.AuditLogResponse;
import careerpilot_parent.audit.enums.AuditAction;
import careerpilot_parent.audit.enums.AuditEntityType;
import careerpilot_parent.audit.mapper.AuditLogMapper;
import careerpilot_parent.audit.service.AuditLogService;
import careerpilot_parent.audit.entity.AuditLog;

import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditLogController {

    private final AuditLogService
            auditLogService;
    private final AuditLogMapper auditLogMapper;

    @GetMapping
    public ResponseEntity<Page<AuditLogResponse>>
    getAuditLogs(
            @RequestParam(required = false)
            Long userId,

            @RequestParam(required = false)
            String username,

            @RequestParam(required = false)
            AuditAction action,

            @RequestParam(required = false)
            AuditEntityType entityType,

            @RequestParam(required = false)
            Long entityId,

            @RequestParam(required = false)
            Boolean success,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime to,

            @RequestParam(required = false)
            String search,

            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction =
                            org.springframework
                                    .data
                                    .domain
                                    .Sort
                                    .Direction
                                    .DESC
            )
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                auditLogService.getAuditLogs(
                        userId,
                        username,
                        action,
                        entityType,
                        entityId,
                        success,
                        from,
                        to,
                        search,
                        pageable
                )
        );
    }

    @GetMapping("/{auditLogId}")
    public ResponseEntity<AuditLogResponse>
    getAuditLogById(
            @PathVariable
            Long auditLogId
    ) {

        return ResponseEntity.ok(
                auditLogService
                        .getAuditLogById(
                                auditLogId
                        )
        );
    }

    @DeleteMapping("/cleanup")
    public ResponseEntity<CleanupResponse>
    deleteOldAuditLogs(
            @RequestParam
            @DateTimeFormat(
                    iso = DateTimeFormat.ISO.DATE_TIME
            )
            LocalDateTime before
    ) {

        long deletedCount =
                auditLogService
                        .deleteAuditLogsOlderThan(
                                before
                        );

        return ResponseEntity.ok(
                new CleanupResponse(
                        "Old audit logs deleted successfully.",
                        deletedCount
                )
        );
    }

    public record CleanupResponse(
            String message,
            long deletedCount
    ) {
    }
    @PostMapping("/test")
    public ResponseEntity<AuditLogResponse> createTestAuditLog() {

        AuditLog auditLog = AuditLog.builder()
                .userId(1L)
                .username("test-admin")
                .userRole("ROLE_ADMIN")
                .action(AuditAction.CREATE)
                .entityType(AuditEntityType.AUDIT_LOG)
                .description("Manual audit log test")
                .requestMethod("POST")
                .requestPath("/api/admin/audit-logs/test")
                .className(AdminAuditLogController.class.getName())
                .methodName("createTestAuditLog")
                .success(true)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogService.saveAuditLog(auditLog);

        return ResponseEntity.ok(
                auditLogMapper.toResponse(auditLog)
        );
    }
}