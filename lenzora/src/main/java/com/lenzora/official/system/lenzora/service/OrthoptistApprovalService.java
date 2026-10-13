package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrthoptistApprovalService {

    private final UserRepository userRepository;
    private final AuditService auditService;

    public OrthoptistApprovalService(UserRepository userRepository, AuditService auditService) {
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<User> findPendingOrthoptists() {
        return userRepository.findByRoleAndAccountStatusAndProfileCompletedTrueOrderByCreatedAtAsc(
                "ORTHOPTIST", AccountStatus.PENDING);
    }

    @Transactional
    public User approve(Long userId, String adminEmail) {
        return updatePendingOrthoptist(userId, adminEmail, AccountStatus.ACTIVE);
    }

    @Transactional
    public User reject(Long userId, String adminEmail) {
        return updatePendingOrthoptist(userId, adminEmail, AccountStatus.REJECTED);
    }

    private User updatePendingOrthoptist(Long userId, String adminEmail, AccountStatus newStatus) {
        User admin = userRepository.findByEmail(adminEmail)
                .filter(user -> "ADMIN".equals(user.getRole()))
                .orElseThrow(() -> new IllegalArgumentException("Administrator account not found."));
        User orthoptist = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Orthoptist account not found."));

        if (!"ORTHOPTIST".equals(orthoptist.getRole())
                || orthoptist.getAccountStatus() != AccountStatus.PENDING
                || !orthoptist.hasProfessionalProfile()) {
            throw new IllegalArgumentException(
                    "Only pending Orthoptist accounts with a completed profile can be updated.");
        }

        orthoptist.setAccountStatus(newStatus);
        if (newStatus == AccountStatus.ACTIVE) {
            orthoptist.setApprovedBy(admin.getId());
            orthoptist.setApprovedAt(LocalDateTime.now());
        } else {
            orthoptist.setLicenseNumber(null);
            orthoptist.setRegistrationNo(null);
            orthoptist.setPhone(null);
            orthoptist.setBranch(null);
            orthoptist.setProfileCompleted(false);
        }
        User savedOrthoptist = userRepository.save(orthoptist);
        auditService.logAction(
                newStatus == AccountStatus.ACTIVE ? "APPROVAL" : "REJECTION",
                adminEmail,
                "app_user",
                "Orthoptist " + savedOrthoptist.getId() + " (" + savedOrthoptist.getEmail()
                        + ") " + (newStatus == AccountStatus.ACTIVE ? "approved" : "rejected"));
        return savedOrthoptist;
    }
}