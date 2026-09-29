package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for login, password reset requests, and password updates.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Sends a password reset link to the given email.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            authService.createPasswordResetToken(request.email());
        } catch (MailException | IllegalStateException e) {
            // Silently ignore errors to prevent revealing registered emails
        }

        return ResponseEntity.ok("If that email exists, a reset link has been sent.");
    }

    /**
     * Resets user password using the provided token.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean updated = authService.resetPassword(request.token(), request.newPassword());

        if (updated) {
            return ResponseEntity.ok("Password successfully reset.");
        }

        return ResponseEntity.badRequest().body("Invalid or expired token.");
    }

    // Request DTOs
    public record ForgotPasswordRequest(String email) {}

    public record ResetPasswordRequest(String token, String newPassword) {}
}