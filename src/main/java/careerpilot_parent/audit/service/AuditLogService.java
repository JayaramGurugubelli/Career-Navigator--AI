package careerpilot_parent.audit.service;

import careerpilot_parent.audit.dto.response.AuditLogResponse;
import careerpilot_parent.audit.entity.AuditLog;
import careerpilot_parent.audit.enums.AuditAction;
import careerpilot_parent.audit.enums.AuditEntityType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditLogService {

    void saveAuditLog(
            AuditLog auditLog
    );

    AuditLogResponse getAuditLogById(
            Long auditLogId
    );

    Page<AuditLogResponse> getAuditLogs(
            Long userId,
            String username,
            AuditAction action,
            AuditEntityType entityType,
            Long entityId,
            Boolean success,
            LocalDateTime from,
            LocalDateTime to,
            String search,
            Pageable pageable
    );

    long deleteAuditLogsOlderThan(
            LocalDateTime cutoffDate
    );
}