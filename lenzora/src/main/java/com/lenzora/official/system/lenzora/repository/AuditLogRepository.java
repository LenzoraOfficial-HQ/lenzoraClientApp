package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.entity.AuditLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the AuditLog entity.
 * Provides standard database operations for the audit_logs table.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Retrieves all audit logs associated with a specific user ID.
     * 
     * @param userId The ID of the user (mapped via the user_id foreign key).
     * @return A list of audit logs performed by or related to that user.
     */
    List<AuditLog> findByUser_UserId(Long userId);

    /**
     * Retrieves all audit logs related to a specific database entity or table.
     * 
     * @param entityName The name of the entity (e.g., 'app_user').
     * @return A list of audit logs affecting that specific entity.
     */
    List<AuditLog> findByEntityName(String entityName);
    
    /**
     * Retrieves all audit logs for a specific action type.
     * 
     * @param auditAction The action performed (e.g., 'CREATE', 'UPDATE').
     * @return A list of audit logs matching the action.
     */
    List<AuditLog> findByAuditAction(String auditAction);
}