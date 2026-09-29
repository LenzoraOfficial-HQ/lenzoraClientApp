package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.service.UserRegistrationService;
import com.lenzora.official.system.lenzora.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Controller handling user registration, approval, and profile retrieval.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRegistrationService registrationService;

    public UserController(UserService userService, UserRegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
    }

    /**
     * Registers a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        String error = registrationService.register(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password(),
                request.confirmPassword()
        );

        // If there's an error message, return it with 400 Bad Request
        if (!error.isEmpty()) {
            return ResponseEntity.badRequest().body(error);
        }

        return ResponseEntity.status(201).body("Registration successful.");
    }

    /**
     * Approves a pending user (Admin only).
     */
    @PutMapping("/{targetUserId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveUser(@PathVariable Long targetUserId, Authentication authentication) {
        try {
            String adminEmail = authentication.getName();
            AppUser approvedUser = userService.approveUser(targetUserId, adminEmail);
            return ResponseEntity.ok(UserResponse.from(approvedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Gets user details by ID (Admin only).
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return userService.getUserById(userId)
                .map(user -> ResponseEntity.ok(UserResponse.from(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DTO for incoming registration requests
    public record UserRegistrationRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            String confirmPassword
    ) {}

    // DTO for outgoing user data (hides sensitive fields like passwordHash)
    public record UserResponse(
            Long userId,
            String firstName,
            String lastName,
            String email,
            String role,
            String status,
            String registrationNo,
            String phone,
            String branch,
            LocalDateTime approvedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static UserResponse from(AppUser user) {
            return new UserResponse(
                    user.getUserId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getUserRole().name(),
                    user.getUserStatus().name(),
                    user.getRegistrationNo(),
                    user.getPhone(),
                    user.getBranch(),
                    user.getApprovedAt(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
        }
    }
}