package com.lenzora.official.system.lenzora.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for handling authentication-related tasks.
 * Manages operations involving the password_reset_tokens table and user credentials.
 */
@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;

    public AuthService(AppUserRepository userRepository, PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Generates a password reset token for a given user email.
     * 
     * @param email The email of the user requesting a password reset.
     * @return The raw token string to be sent to the user via email, or null if user not found.
     */
    @Transactional
    public String createPasswordResetToken(String email) {
        Optional<AppUser> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return null; // In production, avoid revealing if an email exists for security reasons
        }

        AppUser user = userOpt.get();
        String rawToken = UUID.randomUUID().toString();
        
        // In a real application, you MUST hash the token before saving it to the database
        // String hashedToken = passwordEncoder.encode(rawToken);
        String hashedToken = rawToken; // Placeholder for actual hashing logic

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(hashedToken);
        // Set expiration to 24 hours from now
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(24)); 
        resetToken.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(resetToken);
        
        return rawToken; 
    }

    /**
     * Validates the token and updates the user's password if valid.
     * 
     * @param rawToken The raw token provided by the user.
     * @param newPassword The user's new password.
     * @return True if the password was successfully reset, false otherwise.
     */
    @Transactional
    public boolean resetPassword(String rawToken, String newPassword) {
        // In a real application, hash the incoming rawToken to look it up in the database
        // String hashedToken = hashMethod(rawToken);
        String hashedToken = rawToken; // Placeholder

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenHash(hashedToken);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();

        // Check if token has expired or has already been used
        if (token.getExpiresAt().isBefore(LocalDateTime.now()) || token.getUsedAt() != null) {
            return false;
        }

        AppUser user = token.getUser();
        
        // Hash the new password before saving
        // user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordHash(newPassword); // Placeholder for actual password hashing
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);

        // Mark the token as used
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);

        return true;
    }
}