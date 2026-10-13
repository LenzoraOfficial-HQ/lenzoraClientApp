package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.entity.UserRole;
import com.lenzora.official.system.lenzora.entity.UserStatus;
import com.lenzora.official.system.lenzora.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    /**
     * Registers a new user with default PENDING status and hashed password.
     */
    @Transactional
    public AppUser registerUser(AppUser user) {
        if (user.getPasswordHash() == null || user.getPasswordHash().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        user.setUserStatus(UserStatus.PENDING);
        user.setUserRole(UserRole.ORTHOPTIST);

        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);

        AppUser savedUser = userRepository.save(user);
        auditService.logAction(
                "REGISTRATION",
                savedUser.getEmail(),
                "app_user",
                "New orthoptist account registered: " + savedUser.getEmail());
        return savedUser;
    }

    /**
     * Approves a pending user and marks them as ACTIVE.
     */
    @Transactional
    public AppUser approveUser(Long targetUserId, String adminEmail) {
        AppUser targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + targetUserId));

        AppUser adminUser = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("Admin account not found."));

        if (adminUser.getUserRole() != UserRole.ADMIN) {
            throw new IllegalArgumentException("Access denied: Only administrators can approve users.");
        }

        if (targetUser.getUserStatus() != UserStatus.PENDING) {
            throw new IllegalArgumentException("Only pending users can be approved.");
        }

        targetUser.setUserStatus(UserStatus.ACTIVE);
        targetUser.setApprovedBy(adminUser);
        targetUser.setApprovedAt(LocalDateTime.now());

        AppUser savedTarget = userRepository.save(targetUser);
        auditService.logAction(
                "APPROVAL",
                adminEmail,
                "app_user",
                "User " + savedTarget.getUserId() + " (" + savedTarget.getEmail() + ") approved");
        return savedTarget;
    }

    /**
     * Finds a user by ID.
     */
    public Optional<AppUser> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public List<AppUser> getAllUsers() {
        return userRepository.findAll();
    }
}