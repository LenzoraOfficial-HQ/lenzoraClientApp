package com.lenzora.official.system.lenzora.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

/**
 * REST Controller for managing user operations.
 * Exposes endpoints to interact with the app_user table from the frontend[cite: 3].
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to register a new user. The user will be saved with a PENDING status[cite: 3].
     * 
     * @param user The user details sent from the frontend as JSON.
     * @return The saved user data or a bad request if the email exists.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AppUser user) {
        try {
            AppUser savedUser = userService.registerUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint for an admin to approve a pending user account[cite: 3].
     * 
     * @param targetUserId The ID of the user to be approved.
     * @param adminId The ID of the admin performing the action.
     * @return The updated user record.
     */
    @PutMapping("/{targetUserId}/approve")
    public ResponseEntity<?> approveUser(
            @PathVariable Long targetUserId, 
            @RequestParam Long adminId) {
        try {
            AppUser approvedUser = userService.approveUser(targetUserId, adminId);
            return ResponseEntity.ok(approvedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint to fetch a specific user by their ID[cite: 3].
     * 
     * @param userId The ID of the user.
     * @return The user data if found, or a 404 Not Found status.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<AppUser> getUser(@PathVariable Long userId) {
        Optional<AppUser> userOpt = userService.getUserById(userId);
        return userOpt.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
}