package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AuditLog;
import com.lenzora.official.system.lenzora.repository.AppUserRepository;
import com.lenzora.official.system.lenzora.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * Service class for managing system audit logs.
 * Handles recording user and system actions into the audit_logs table.
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AppUserRepository appUserRepository;

    public AuditService(AuditLogRepository auditLogRepository, AppUserRepository appUserRepository) {
        this.auditLogRepository = auditLogRepository;
        this.appUserRepository = appUserRepository;
    }

    /**
     * Records a new audit log entry in the database[cite: 1].
     *
     * @param userId     The ID of the user performing the action (can be null for unauthenticated or system actions).
     * @param action     The specific action performed (e.g., 'CREATE', 'UPDATE', 'LOGIN').
     * @param entityName The name of the table or entity affected (e.g., 'app_user').
     * @param entityId   The ID of the specific record affected.
     * @param details    Additional context, descriptions, or payload information.
     * @param ipAddress  The IP address of the user initiating the action.
     */
    @Transactional
    public void logAction(Long userId, String action, String entityName, String entityId, String details, String ipAddress) {
        AuditLog auditLog = new AuditLog();
        
        // Link the user if a valid user ID is provided
        if (userId != null) {
            appUserRepository.findById(userId).ifPresent(auditLog::setUser);
        }
        
        auditLog.setAuditAction(action);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLog.setIpAddress(ipAddress);
        auditLog.setCreatedAt(LocalDateTime.now());
        
        auditLogRepository.save(auditLog);
    }
}