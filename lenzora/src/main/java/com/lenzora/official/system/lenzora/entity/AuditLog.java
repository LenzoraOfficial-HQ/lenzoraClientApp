package com.lenzora.official.system.lenzora.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents an audit log entry in the system.
 * This entity maps directly to the "audit_logs" table in the database to track user and system actions.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    /**
     * Unique identifier for the audit log. Auto-incremented by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    /**
     * The user who performed the action. 
     * This is mapped as a foreign key to the app_user table. It can be null if the action was system-generated or unauthenticated.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    /**
     * The specific action that was performed (e.g., 'CREATE', 'UPDATE', 'DELETE', 'LOGIN').
     */
    @Column(name = "audit_action", nullable = false, length = 50)
    private String auditAction;

    /**
     * The name of the entity or table that was affected by the action (e.g., 'app_user').
     */
    @Column(name = "entity", nullable = false, length = 50)
    private String entityName;

    /**
     * The ID of the specific record that was affected.
     */
    @Column(name = "entity_id", length = 40)
    private String entityId;

    /**
     * Additional details, descriptions, or payload information regarding the audit event.
     */
    @Column(name = "details", length = 255)
    private String details;

    /**
     * The IP address from which the action was initiated.
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Timestamp of when the audit log was created. Defaults to the current timestamp in the database.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- Getters and Setters ---

    public Long getLogId() { 
        return logId; 
    }
    
    public void setLogId(Long logId) { 
        this.logId = logId; 
    }

    public AppUser getUser() { 
        return user; 
    }
    
    public void setUser(AppUser user) { 
        this.user = user; 
    }

    public String getAuditAction() { 
        return auditAction; 
    }
    
    public void setAuditAction(String auditAction) { 
        this.auditAction = auditAction; 
    }

    public String getEntityName() { 
        return entityName; 
    }
    
    public void setEntityName(String entityName) { 
        this.entityName = entityName; 
    }

    public String getEntityId() { 
        return entityId; 
    }
    
    public void setEntityId(String entityId) { 
        this.entityId = entityId; 
    }

    public String getDetails() { 
        return details; 
    }
    
    public void setDetails(String details) { 
        this.details = details; 
    }

    public String getIpAddress() { 
        return ipAddress; 
    }
    
    public void setIpAddress(String ipAddress) { 
        this.ipAddress = ipAddress; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
}