package com.lenzora.official.system.lenzora.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * REST Controller for authentication and password management.
 * Interfaces with the password_reset_tokens and app_user tables.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint to request a password reset link. 
     * Generates a token and stores it in the password_reset_tokens table.
     * 
     * @param payload A JSON object containing the user's "email".
     * @return A success message (and the raw token for testing purposes).
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String rawToken = authService.createPasswordResetToken(email);
        
        if (rawToken != null) {
            // In production, you would email this token instead of returning it in the response.
            return ResponseEntity.ok("Password reset email sent. Token: " + rawToken);
        }
        
        // Always return OK even if the email isn't found to prevent email enumeration attacks.
        return ResponseEntity.ok("If that email exists, a reset link has been sent.");
    }

    /**
     * Endpoint to execute a password reset using a valid token.
     * 
     * @param payload A JSON object containing the "token" and "newPassword".
     * @return A success or failure message.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        boolean success = authService.resetPassword(token, newPassword);

        if (success) {
            return ResponseEntity.ok("Password successfully reset.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }
}