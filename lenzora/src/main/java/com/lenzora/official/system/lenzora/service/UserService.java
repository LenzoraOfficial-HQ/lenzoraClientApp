package com.lenzora.official.system.lenzora.service;
import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for managing application users.
 * Handles business logic and coordinates operations for the app_user table.
 */
@Service
public class UserService {

    private final AppUserRepository userRepository;

    public UserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user in the system with a PENDING status.
     *
     * @param user The user entity populated with registration details.
     * @return The saved AppUser entity.
     * @throws IllegalArgumentException if the email is already in use.
     */
    @Transactional
    public AppUser registerUser(AppUser user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        
        // Ensure new users always start as PENDING and ORTHOPTIST as defined in the schema
        user.setUserStatus(UserStatus.PENDING);
        user.setUserRole(UserRole.ORTHOPTIST);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    /**
     * Approves a pending user account, setting their status to ACTIVE.
     *
     * @param targetUserId The ID of the user being approved.
     * @param adminId The ID of the admin performing the approval.
     * @return The updated AppUser entity.
     */
    @Transactional
    public AppUser approveUser(Long targetUserId, Long adminId) {
        AppUser targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
                
        AppUser adminUser = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        targetUser.setUserStatus(UserStatus.ACTIVE);
        targetUser.setApprovedBy(adminUser);
        targetUser.setApprovedAt(LocalDateTime.now());
        targetUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(targetUser);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to find.
     * @return An Optional containing the user if found.
     */
    public Optional<AppUser> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}