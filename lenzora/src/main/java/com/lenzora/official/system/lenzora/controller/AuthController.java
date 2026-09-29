package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.service.AuthService;
import org.springframework.mail.MailException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param request A JSON object containing the user's email.
     * @return A generic response that does not disclose whether the email exists.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.createPasswordResetToken(request.email());
        } catch (MailException | IllegalStateException exception) {
            // Keep the response identical for unknown accounts and delivery failures.
        }
        return ResponseEntity.ok("If that email exists, a reset link has been sent.");
    }

    /**
     * Endpoint to execute a password reset using a valid token.
     * 
    * @param request A JSON object containing the token and new password.
     * @return A success or failure message.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean success = authService.resetPassword(request.token(), request.newPassword());

        if (success) {
            return ResponseEntity.ok("Password successfully reset.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }

    public record ForgotPasswordRequest(String email) {
    }

    public record ResetPasswordRequest(String token, String newPassword) {
    }
}