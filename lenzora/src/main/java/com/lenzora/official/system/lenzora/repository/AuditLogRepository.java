package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Handles database operations for audit logs.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<AuditLog> findByUser_UserId(Long userId);

    // Find all logs by entity name (e.g., "app_user")
    List<AuditLog> findByEntityName(String entityName);

    // Find all logs by action type (e.g., "CREATE", "UPDATE")
    List<AuditLog> findByAuditAction(String auditAction);

    List<AuditLog> findTop4ByOrderByCreatedAtDesc();
}