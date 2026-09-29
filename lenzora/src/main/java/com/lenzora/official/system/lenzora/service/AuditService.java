package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.entity.AuditLog;
import com.lenzora.official.system.lenzora.repository.AppUserRepository;
import com.lenzora.official.system.lenzora.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AppUserRepository appUserRepository;

    public AuditService(AuditLogRepository auditLogRepository, AppUserRepository appUserRepository) {
        this.auditLogRepository = auditLogRepository;
        this.appUserRepository = appUserRepository;
    }

    /**
     * Saves a new audit log record.
     */
    @Transactional
    public void logAction(Long userId, String action, String entityName, String entityId, String details, String ipAddress) {
        AuditLog auditLog = new AuditLog();

        // If a userId is given, find and attach the user
        if (userId != null) {
            AppUser user = appUserRepository.findById(userId).orElse(null);
            auditLog.setUser(user);
        }

        // Set log details
        auditLog.setAuditAction(action);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLog.setIpAddress(ipAddress);
        auditLog.setCreatedAt(LocalDateTime.now());

        // Save to database
        auditLogRepository.save(auditLog);
    }
}