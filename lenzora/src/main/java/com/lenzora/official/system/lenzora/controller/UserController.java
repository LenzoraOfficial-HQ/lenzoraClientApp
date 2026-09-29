package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.service.UserRegistrationService;
import com.lenzora.official.system.lenzora.service.UserService;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/**
 * REST Controller for managing user operations.
 * Exposes endpoints to interact with the app_user table from the frontend.
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
     * Endpoint to register a new user. The user will be saved with a PENDING status.
     * 
     * @param user The user details sent from the frontend as JSON.
     * @return The saved user data or a bad request if the email exists.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        String problem = registrationService.register(
                request.firstName(), request.lastName(), request.email(),
                request.password(), request.confirmPassword());
        if (!problem.isEmpty()) {
            return ResponseEntity.badRequest().body(problem);
        }
        return ResponseEntity.status(201).body("Registration successful.");
    }

    /**
     * Endpoint for an admin to approve a pending user account.
     * 
     * @param targetUserId The ID of the user to be approved.
     * @param adminId The ID of the admin performing the action.
     * @return The updated user record.
     */
    @PutMapping("/{targetUserId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveUser(
            @PathVariable Long targetUserId,
            Authentication authentication) {
        try {
            AppUser approvedUser = userService.approveUser(targetUserId, authentication.getName());
            return ResponseEntity.ok(UserResponse.from(approvedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint to fetch a specific user by their ID.
     * 
     * @param userId The ID of the user.
     * @return The user data if found, or a 404 Not Found status.
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        Optional<AppUser> userOpt = userService.getUserById(userId);
        return userOpt.map(UserResponse::from).map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record UserRegistrationRequest(
            String firstName,
            String lastName,
            String email,
            String password,
            String confirmPassword) {
    }

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
            LocalDateTime updatedAt) {

        private static UserResponse from(AppUser user) {
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
                    user.getUpdatedAt());
        }
    }
}
