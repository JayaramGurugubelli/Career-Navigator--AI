package careerpilot_parent.audit.mapper;

import careerpilot_parent.audit.dto.response.AuditLogResponse;
import careerpilot_parent.audit.entity.AuditLog;

import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogResponse toResponse(
            AuditLog auditLog
    ) {

        if (auditLog == null) {
            return null;
        }

        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .username(auditLog.getUsername())
                .userRole(auditLog.getUserRole())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .description(auditLog.getDescription())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .requestMethod(auditLog.getRequestMethod())
                .requestPath(auditLog.getRequestPath())
                .className(auditLog.getClassName())
                .methodName(auditLog.getMethodName())
                .success(auditLog.getSuccess())
                .failureReason(auditLog.getFailureReason())
                .executionTimeMs(auditLog.getExecutionTimeMs())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}