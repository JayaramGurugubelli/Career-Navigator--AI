package careerpilot_parent.audit.service.impl;

import careerpilot_parent.audit.dto.response.AuditLogResponse;
import careerpilot_parent.audit.entity.AuditLog;
import careerpilot_parent.audit.enums.AuditAction;
import careerpilot_parent.audit.enums.AuditEntityType;
import careerpilot_parent.audit.mapper.AuditLogMapper;
import careerpilot_parent.audit.repository.AuditLogRepository;
import careerpilot_parent.audit.service.AuditLogService;

import careerpilot_parent.common.exception.ResourceNotFoundException;

import jakarta.persistence.criteria.Predicate;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl
        implements AuditLogService {

    private final AuditLogRepository
            auditLogRepository;

    private final AuditLogMapper
            auditLogMapper;

    /*
     * REQUIRES_NEW is important.
     *
     * If the main business transaction fails and rolls back,
     * the audit failure record will still be committed.
     */
    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void saveAuditLog(
            AuditLog auditLog
    ) {

        if (auditLog == null) {
            return;
        }

        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(
            Long auditLogId
    ) {

        validatePositiveId(
                auditLogId,
                "Audit log ID"
        );

        AuditLog auditLog =
                auditLogRepository
                        .findById(auditLogId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Audit log not found."
                                        )
                        );

        return auditLogMapper.toResponse(
                auditLog
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(
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
    ) {

        validateDateRange(from, to);

        return auditLogRepository
                .findAll(
                        (root, query, criteriaBuilder) -> {

                            List<Predicate> predicates =
                                    new ArrayList<>();

                            if (userId != null) {

                                predicates.add(
                                        criteriaBuilder.equal(
                                                root.get("userId"),
                                                userId
                                        )
                                );
                            }

                            if (username != null
                                    && !username.isBlank()) {

                                predicates.add(
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get("username")
                                                ),
                                                "%"
                                                        + username
                                                        .trim()
                                                        .toLowerCase()
                                                        + "%"
                                        )
                                );
                            }

                            if (action != null) {

                                predicates.add(
                                        criteriaBuilder.equal(
                                                root.get("action"),
                                                action
                                        )
                                );
                            }

                            if (entityType != null) {

                                predicates.add(
                                        criteriaBuilder.equal(
                                                root.get("entityType"),
                                                entityType
                                        )
                                );
                            }

                            if (entityId != null) {

                                predicates.add(
                                        criteriaBuilder.equal(
                                                root.get("entityId"),
                                                entityId
                                        )
                                );
                            }

                            if (success != null) {

                                predicates.add(
                                        criteriaBuilder.equal(
                                                root.get("success"),
                                                success
                                        )
                                );
                            }

                            if (from != null) {

                                predicates.add(
                                        criteriaBuilder
                                                .greaterThanOrEqualTo(
                                                        root.get(
                                                                "createdAt"
                                                        ),
                                                        from
                                                )
                                );
                            }

                            if (to != null) {

                                predicates.add(
                                        criteriaBuilder
                                                .lessThanOrEqualTo(
                                                        root.get(
                                                                "createdAt"
                                                        ),
                                                        to
                                                )
                                );
                            }

                            if (search != null
                                    && !search.isBlank()) {

                                String searchPattern =
                                        "%"
                                                + search
                                                .trim()
                                                .toLowerCase()
                                                + "%";

                                Predicate descriptionMatch =
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get(
                                                                "description"
                                                        )
                                                ),
                                                searchPattern
                                        );

                                Predicate pathMatch =
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get(
                                                                "requestPath"
                                                        )
                                                ),
                                                searchPattern
                                        );

                                Predicate failureMatch =
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get(
                                                                "failureReason"
                                                        )
                                                ),
                                                searchPattern
                                        );

                                Predicate methodMatch =
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get(
                                                                "methodName"
                                                        )
                                                ),
                                                searchPattern
                                        );

                                predicates.add(
                                        criteriaBuilder.or(
                                                descriptionMatch,
                                                pathMatch,
                                                failureMatch,
                                                methodMatch
                                        )
                                );
                            }

                            return criteriaBuilder.and(
                                    predicates.toArray(
                                            new Predicate[0]
                                    )
                            );
                        },
                        pageable
                )
                .map(auditLogMapper::toResponse);
    }

    @Override
    @Transactional
    public long deleteAuditLogsOlderThan(
            LocalDateTime cutoffDate
    ) {

        if (cutoffDate == null) {

            throw new IllegalArgumentException(
                    "Cutoff date is required."
            );
        }

        List<AuditLog> logs =
                auditLogRepository.findAll(
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.lessThan(
                                        root.get("createdAt"),
                                        cutoffDate
                                )
                );

        long deletedCount =
                logs.size();

        if (!logs.isEmpty()) {

            auditLogRepository.deleteAllInBatch(
                    logs
            );
        }

        return deletedCount;
    }

    private void validateDateRange(
            LocalDateTime from,
            LocalDateTime to
    ) {

        if (from != null
                && to != null
                && from.isAfter(to)) {

            throw new IllegalArgumentException(
                    "'from' date must be before or equal to 'to' date."
            );
        }
    }

    private void validatePositiveId(
            Long id,
            String fieldName
    ) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    fieldName + " must be positive."
            );
        }
    }
}