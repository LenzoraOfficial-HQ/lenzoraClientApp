package com.lenzora.official.system.lenzora.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing an audit log entry in the system.
 */
@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    // User who performed the action (optional/nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    // The audit_log relation has no actor column - the name is read from user_id.
    @Transient
    private String username;

    @Column(name = "audit_action", nullable = false, length = 50)
    private String auditAction;

    @Column(name = "entity", nullable = false, length = 50)
    private String entityName;

    @Column(name = "entity_id", length = 40)
    private String entityId;

    @Column(name = "details", length = 255)
    private String details;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Automatically assigns creation timestamp before saving. */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public AuditLog() {}

    public Long getLogId() {
         return logId; 
    }

    public Long getId() {
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

    /** Returns the ID of the associated user, or null if unassigned. */
    public Long getUserId() {
        if (user != null) {
            return user.getUserId();
        }
        return null;
    }

    public String getUsername() {
        if (user != null) {
            return user.getEmail();
        }
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuditAction() {
         return auditAction; 
    }

    public String getActionType() {
        return auditAction;
    }

    public void setAuditAction(String auditAction) {
         this.auditAction = auditAction; 
    }

    public String getEntityName() {
         return entityName; 
    }

    public String getTargetEntity() {
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

    public LocalDateTime getTimestamp() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
         this.createdAt = createdAt; 
    }
}