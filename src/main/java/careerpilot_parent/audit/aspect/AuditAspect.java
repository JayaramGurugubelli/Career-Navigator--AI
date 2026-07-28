package careerpilot_parent.audit.aspect;

import careerpilot_parent.audit.annotation.Auditable;
import careerpilot_parent.audit.entity.AuditLog;
import careerpilot_parent.audit.service.AuditLogService;
import careerpilot_parent.security.util.SecurityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private static final int MAX_JSON_LENGTH =
            10000;

    private static final int MAX_FAILURE_LENGTH =
            2000;

    private static final Set<String> SENSITIVE_FIELD_NAMES =
            Set.of(
                    "password",
                    "currentpassword",
                    "newpassword",
                    "confirmpassword",
                    "token",
                    "accesstoken",
                    "refreshtoken",
                    "verificationtoken",
                    "resettoken",
                    "secret",
                    "authorization"
            );

    private final AuditLogService auditLogService;

    private final ObjectMapper objectMapper;

    private final SecurityUtils securityUtils;

    @Around("@annotation(auditable)")
    public Object auditMethod(
            ProceedingJoinPoint joinPoint,
            Auditable auditable
    ) throws Throwable {

        log.info(
                "AUDIT ASPECT TRIGGERED: {}.{}",
                joinPoint.getTarget()
                        .getClass()
                        .getSimpleName(),
                joinPoint.getSignature()
                        .getName()
        );

        long startTime =
                System.currentTimeMillis();

        MethodSignature methodSignature =
                (MethodSignature) joinPoint.getSignature();

        Method method =
                methodSignature.getMethod();

        Object[] arguments =
                joinPoint.getArgs();

        Long entityId =
                resolveEntityId(
                        method,
                        arguments,
                        auditable.entityIdParameter()
                );

        AuditUserContext userContext =
                resolveCurrentUser();

        RequestContext requestContext =
                resolveRequestContext();

        String sanitizedArguments =
                auditable.captureArguments()
                        ? serializeAndSanitize(
                        buildArgumentMap(
                                method,
                                arguments
                        )
                )
                        : null;

        try {

            Object result =
                    joinPoint.proceed();

            long executionTime =
                    System.currentTimeMillis()
                            - startTime;

            AuditUserContext successfulUserContext =
                    resolveCurrentUser();

            AuditLog auditLog =
                    buildBaseAuditLog(
                            joinPoint,
                            auditable,
                            successfulUserContext,
                            requestContext,
                            entityId,
                            executionTime
                    );

            auditLog.setSuccess(true);

            auditLog.setOldValue(
                    sanitizedArguments
            );

            auditLog.setNewValue(
                    auditable.captureResponse()
                            ? serializeAndSanitize(result)
                            : null
            );

            saveAuditSafely(
                    auditLog
            );

            return result;

        } catch (Throwable throwable) {

            long executionTime =
                    System.currentTimeMillis()
                            - startTime;

            AuditLog auditLog =
                    buildBaseAuditLog(
                            joinPoint,
                            auditable,
                            userContext,
                            requestContext,
                            entityId,
                            executionTime
                    );

            auditLog.setSuccess(false);

            auditLog.setOldValue(
                    sanitizedArguments
            );

            auditLog.setFailureReason(
                    limitText(
                            throwable.getClass()
                                    .getSimpleName()
                                    + ": "
                                    + throwable.getMessage(),
                            MAX_FAILURE_LENGTH
                    )
            );

            saveAuditSafely(
                    auditLog
            );

            throw throwable;
        }
    }

    private AuditLog buildBaseAuditLog(
            ProceedingJoinPoint joinPoint,
            Auditable auditable,
            AuditUserContext userContext,
            RequestContext requestContext,
            Long entityId,
            long executionTime
    ) {

        String description =
                auditable.description();

        if (description == null
                || description.isBlank()) {

            description =
                    auditable.action().name()
                            + " operation on "
                            + auditable.entityType().name();
        }

        return AuditLog.builder()
                .userId(
                        userContext.userId()
                )
                .username(
                        userContext.username()
                )
                .userRole(
                        userContext.roles()
                )
                .action(
                        auditable.action()
                )
                .entityType(
                        auditable.entityType()
                )
                .entityId(
                        entityId
                )
                .description(
                        description
                )
                .ipAddress(
                        requestContext.ipAddress()
                )
                .userAgent(
                        requestContext.userAgent()
                )
                .requestMethod(
                        requestContext.requestMethod()
                )
                .requestPath(
                        requestContext.requestPath()
                )
                .className(
                        joinPoint.getTarget()
                                .getClass()
                                .getName()
                )
                .methodName(
                        joinPoint.getSignature()
                                .getName()
                )
                .executionTimeMs(
                        executionTime
                )
                .build();
    }

    private Map<String, Object> buildArgumentMap(
            Method method,
            Object[] arguments
    ) {

        Map<String, Object> argumentMap =
                new LinkedHashMap<>();

        Parameter[] parameters =
                method.getParameters();

        for (
                int index = 0;
                index < parameters.length;
                index++
        ) {

            String parameterName =
                    parameters[index]
                            .getName();

            Object argument =
                    index < arguments.length
                            ? arguments[index]
                            : null;

            if (isIgnoredArgument(argument)) {
                continue;
            }

            if (isSensitiveName(parameterName)) {

                argumentMap.put(
                        parameterName,
                        "[MASKED]"
                );

            } else {

                argumentMap.put(
                        parameterName,
                        argument
                );
            }
        }

        return argumentMap;
    }

    private boolean isIgnoredArgument(
            Object argument
    ) {

        if (argument == null) {
            return false;
        }

        String className =
                argument.getClass()
                        .getName();

        return className.startsWith(
                "jakarta.servlet"
        )
                || className.startsWith(
                "org.springframework.web.multipart"
        )
                || className.startsWith(
                "org.springframework.security"
        );
    }

    private Long resolveEntityId(
            Method method,
            Object[] arguments,
            String entityIdParameter
    ) {

        if (entityIdParameter == null
                || entityIdParameter.isBlank()) {

            return null;
        }

        Parameter[] parameters =
                method.getParameters();

        for (
                int index = 0;
                index < parameters.length;
                index++
        ) {

            if (!parameters[index]
                    .getName()
                    .equals(entityIdParameter)) {

                continue;
            }

            Object value =
                    arguments[index];

            if (value instanceof Number number) {

                return number.longValue();
            }

            if (value instanceof String stringValue) {

                try {

                    return Long.valueOf(
                            stringValue
                    );

                } catch (NumberFormatException ignored) {

                    return null;
                }
            }
        }

        return null;
    }

    private AuditUserContext resolveCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(
                authentication.getPrincipal()
        )) {

            return new AuditUserContext(
                    null,
                    "ANONYMOUS",
                    "ANONYMOUS"
            );
        }

        Long userId = null;

        try {

            userId =
                    securityUtils
                            .getCurrentUserId();

        } catch (RuntimeException ignored) {

            log.debug(
                    "Current user ID could not be resolved for audit logging."
            );
        }

        String username =
                authentication.getName();

        String roles =
                authentication
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
                        .collect(
                                Collectors.joining(",")
                        );

        if (roles == null
                || roles.isBlank()) {

            roles =
                    "AUTHENTICATED";
        }

        return new AuditUserContext(
                userId,
                username,
                roles
        );
    }

    private RequestContext resolveRequestContext() {

        if (!(RequestContextHolder
                .getRequestAttributes()
                instanceof ServletRequestAttributes servletAttributes)) {

            return new RequestContext(
                    null,
                    null,
                    null,
                    null
            );
        }

        HttpServletRequest request =
                servletAttributes.getRequest();

        return new RequestContext(
                resolveClientIp(request),
                request.getHeader(
                        "User-Agent"
                ),
                request.getMethod(),
                request.getRequestURI()
        );
    }

    private String resolveClientIp(
            HttpServletRequest request
    ) {

        String forwardedFor =
                request.getHeader(
                        "X-Forwarded-For"
                );

        if (forwardedFor != null
                && !forwardedFor.isBlank()) {

            return forwardedFor
                    .split(",")[0]
                    .trim();
        }

        String realIp =
                request.getHeader(
                        "X-Real-IP"
                );

        if (realIp != null
                && !realIp.isBlank()) {

            return realIp.trim();
        }

        return request.getRemoteAddr();
    }

    private String serializeAndSanitize(
            Object value
    ) {

        if (value == null) {
            return null;
        }

        try {

            String json =
                    objectMapper
                            .writeValueAsString(
                                    value
                            );

            json =
                    maskSensitiveJsonValues(
                            json
                    );

            return limitText(
                    json,
                    MAX_JSON_LENGTH
            );

        } catch (
                JsonProcessingException
                | RuntimeException exception
        ) {

            log.debug(
                    "Audit value could not be serialized as JSON.",
                    exception
            );

            return limitText(
                    String.valueOf(value),
                    MAX_JSON_LENGTH
            );
        }
    }

    private String maskSensitiveJsonValues(
            String json
    ) {

        if (json == null
                || json.isBlank()) {

            return json;
        }

        String sanitized =
                json;

        for (
                String field :
                SENSITIVE_FIELD_NAMES
        ) {

            sanitized =
                    sanitized.replaceAll(
                            "(?i)(\""
                                    + field
                                    + "\"\\s*:\\s*\")"
                                    + "[^\"]*"
                                    + "(\"?)",
                            "$1[MASKED]$2"
                    );
        }

        return sanitized;
    }

    private boolean isSensitiveName(
            String value
    ) {

        if (value == null) {
            return false;
        }

        String normalized =
                value.toLowerCase(
                        Locale.ROOT
                );

        return SENSITIVE_FIELD_NAMES
                .contains(normalized);
    }

    private String limitText(
            String value,
            int maximumLength
    ) {

        if (value == null) {
            return null;
        }

        if (value.length()
                <= maximumLength) {

            return value;
        }

        return value.substring(
                0,
                maximumLength
        );
    }

    private void saveAuditSafely(
            AuditLog auditLog
    ) {

        try {

            auditLogService.saveAuditLog(
                    auditLog
            );

        } catch (RuntimeException exception) {

            log.error(
                    "Failed to save audit log. Action: {}, entity type: {}, method: {}",
                    auditLog.getAction(),
                    auditLog.getEntityType(),
                    auditLog.getMethodName(),
                    exception
            );
        }
    }

    private record AuditUserContext(
            Long userId,
            String username,
            String roles
    ) {
    }

    private record RequestContext(
            String ipAddress,
            String userAgent,
            String requestMethod,
            String requestPath
    ) {
    }
}