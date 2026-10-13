package com.lenzora.official.system.lenzora.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "audit_action")
    private String auditAction;

    private String entity;

    @Column(name = "entity_id")
    private Long entityId;

    private String details;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}