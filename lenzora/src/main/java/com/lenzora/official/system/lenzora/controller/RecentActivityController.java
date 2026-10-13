package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AuditLog;
import com.lenzora.official.system.lenzora.repository.AuditLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RecentActivityController {

    private final AuditLogRepository auditLogRepository;

    public RecentActivityController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/recent-activities")
    public ResponseEntity<List<AuditLog>> getRecentActivities() {
        List<AuditLog> activities = auditLogRepository.findTop4ByOrderByCreatedAtDesc();
        return ResponseEntity.ok(activities);
    }
}