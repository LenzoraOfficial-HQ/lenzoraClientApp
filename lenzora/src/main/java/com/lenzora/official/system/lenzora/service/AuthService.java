package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.entity.PasswordResetToken;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import com.lenzora.official.system.lenzora.repository.PasswordResetTokenRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling authentication-related tasks.
 * Manages operations involving the password_reset_tokens table and user credentials.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String mailHost;
    private final String mailFrom;

    public AuthService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            @Value("${spring.mail.host:}") String mailHost,
            @Value("${spring.mail.from:}") String mailFrom) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.mailHost = mailHost;
        this.mailFrom = mailFrom;
    }

    /**
     * Generates a password reset token for a given user email.
     * 
     * @param email The email of the user requesting a password reset.
     */
    @Transactional
    public void createPasswordResetToken(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        String rawToken = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(hashToken(rawToken));
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(1));
        resetToken.setCreatedAt(LocalDateTime.now());

        tokenRepository.save(resetToken);

        // 1. PRINT TOKEN TO TERMINAL FOR TESTING
        System.out.println("\n==============================================");
        System.out.println("=== YOUR RESET TOKEN: " + rawToken);
        System.out.println("==============================================\n");

        // 2. ATTEMPT EMAIL, BUT DON'T CRASH IF IT FAILS
        try {
            if (mailHost != null && !mailHost.isBlank() && mailFrom != null && !mailFrom.isBlank()) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(mailFrom);
                message.setTo(user.getEmail());
                message.setSubject("Lenzora password reset");
                message.setText("Use this token with the password reset form: " + rawToken
                        + "\nThis token expires in one hour.");
                mailSender.send(message);
            }
        } catch (Exception e) {
            System.out.println("Could not send real email, but token was generated successfully.");
        }
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
        if (rawToken == null || rawToken.isBlank()
                || newPassword == null || newPassword.length() < 8) {
            return false;
        }

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenHash(hashToken(rawToken));
        
        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();

        // Check if token has expired or has already been used
        if (token.getExpiresAt().isBefore(LocalDateTime.now()) || token.getUsedAt() != null) {
            return false;
        }

        User user = token.getUser();
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);

        // Mark the token as used
        token.setUsedAt(LocalDateTime.now());
        tokenRepository.save(token);

        return true;
    }

    private String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}