package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.service.OrthoptistApprovalService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminApprovalController {

    private final OrthoptistApprovalService approvalService;

    public AdminApprovalController(OrthoptistApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping("/admin/orthoptists")
    public String pendingOrthoptists(Model model) {
        model.addAttribute("orthoptists", approvalService.findPendingOrthoptists());
        return "admin-orthoptists";
    }

    @PostMapping("/admin/orthoptists/{userId}/approve")
    public String approve(@PathVariable Long userId, Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        return update(userId, authentication, redirectAttributes, true);
    }

    @PostMapping("/admin/orthoptists/{userId}/reject")
    public String reject(@PathVariable Long userId, Authentication authentication,
                         RedirectAttributes redirectAttributes) {
        return update(userId, authentication, redirectAttributes, false);
    }

    private String update(Long userId, Authentication authentication,
                          RedirectAttributes redirectAttributes, boolean approve) {
        try {
            if (approve) {
                approvalService.approve(userId, authentication.getName());
                redirectAttributes.addFlashAttribute("notice", "Orthoptist approved.");
            } else {
                approvalService.reject(userId, authentication.getName());
                redirectAttributes.addFlashAttribute("notice", "Orthoptist rejected.");
            }
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/orthoptists";
    }
}