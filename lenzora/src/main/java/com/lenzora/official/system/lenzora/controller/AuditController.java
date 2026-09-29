package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AuditLog;
import com.lenzora.official.system.lenzora.repository.AuditLogRepository;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for viewing system audit logs.
 * Exposes data from the audit_logs table.
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
     * Endpoint to retrieve all audit logs associated with a specific user.
     * 
     * @param userId The ID of the user.
     * @return A list of audit logs.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogResponse>> getUserAuditLogs(@PathVariable Long userId) {
        List<AuditLog> logs = auditLogRepository.findByUser_UserId(userId);
        return ResponseEntity.ok(logs.stream().map(AuditLogResponse::from).toList());
    }
    
    /**
     * Endpoint to retrieve all audit logs in the system.
     * 
     * @return A list of all audit logs.
     */
    @GetMapping("/all")
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
        List<AuditLog> logs = auditLogRepository.findAll();
        return ResponseEntity.ok(logs.stream().map(AuditLogResponse::from).toList());
    }

    public record AuditLogResponse(
            Long logId,
            Long userId,
            String auditAction,
            String entityName,
            String entityId,
            String details,
            String ipAddress,
            LocalDateTime createdAt) {

        private static AuditLogResponse from(AuditLog log) {
            Long userId = log.getUser() == null ? null : log.getUser().getUserId();
            return new AuditLogResponse(
                    log.getLogId(), userId, log.getAuditAction(), log.getEntityName(),
                    log.getEntityId(), log.getDetails(), log.getIpAddress(), log.getCreatedAt());
        }
    }
}