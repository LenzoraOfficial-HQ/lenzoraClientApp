package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AuditLog;
import com.lenzora.official.system.lenzora.repository.AuditLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for viewing system audit logs (Admin only).
 */
@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    public AuditController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Gets all audit logs for a specific user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getUserAuditLogs(@PathVariable Long userId) {
        List<AuditLogResponse> response = auditLogRepository.findByUser_UserId(userId)
                .stream()
                .map(AuditLogResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    /**
     * Gets all audit logs in the system.
     */
    @GetMapping("/all")
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
        List<AuditLogResponse> response = auditLogRepository.findAll()
                .stream()
                .map(AuditLogResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    // DTO for returning audit log data
    public record AuditLogResponse(
            Long logId,
            Long userId,
            String auditAction,
            String entityName,
            String entityId,
            String details,
            String ipAddress,
            LocalDateTime createdAt
    ) {
        public static AuditLogResponse from(AuditLog log) {
            Long userId = (log.getUser() != null) ? log.getUser().getUserId() : null;

            return new AuditLogResponse(
                    log.getLogId(),
                    userId,
                    log.getAuditAction(),
                    log.getEntityName(),
                    log.getEntityId(),
                    log.getDetails(),
                    log.getIpAddress(),
                    log.getCreatedAt()
            );
        }
    }
}