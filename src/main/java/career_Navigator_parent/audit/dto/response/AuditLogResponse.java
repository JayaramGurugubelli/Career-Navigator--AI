package career_Navigator_parent.audit.dto.response;

import career_Navigator_parent.audit.enums.AuditAction;
import career_Navigator_parent.audit.enums.AuditEntityType;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;

    private Long userId;

    private String username;

    private String userRole;

    private AuditAction action;

    private AuditEntityType entityType;

    private Long entityId;

    private String description;

    private String oldValue;

    private String newValue;

    private String ipAddress;

    private String userAgent;

    private String requestMethod;

    private String requestPath;

    private String className;

    private String methodName;

    private Boolean success;

    private String failureReason;

    private Long executionTimeMs;

    @JsonFormat(
            pattern = "dd-MM-yyyy hh:mm:ss a"
    )
    private LocalDateTime createdAt;
}