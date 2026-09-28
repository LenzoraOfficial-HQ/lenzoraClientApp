package com.lenzora.official.system.lenzora.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for viewing system audit logs.
 * Exposes data from the audit_logs table.
 */
@RestController
@RequestMapping("/api/audit")
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
    public ResponseEntity<List<AuditLog>> getUserAuditLogs(@PathVariable Long userId) {
        List<AuditLog> logs = auditLogRepository.findByUser_UserId(userId);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * Endpoint to retrieve all audit logs in the system.
     * 
     * @return A list of all audit logs.
     */
    @GetMapping("/all")
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        List<AuditLog> logs = auditLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }
}