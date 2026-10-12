package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AuditLog;
import com.lenzora.official.system.lenzora.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditLogController {

    private static final int PAGE_SIZE = 20;

    private final AuditLogRepository auditLogRepository;

    public AdminAuditLogController(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/admin/audit-logs")
    public String auditLogs(@RequestParam(defaultValue = "0") int page, Model model) {
        int pageNumber = Math.max(page, 0);
        Page<AuditLog> auditLogs = auditLogRepository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(pageNumber, PAGE_SIZE));

        model.addAttribute("auditLogs", auditLogs);
        return "admin-audit-logs";
    }
}
