package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.PasswordResetToken;
import com.lenzora.official.system.lenzora.repository.PasswordResetTokenRepository;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import com.lenzora.official.system.lenzora.model.User; 
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository, 
                                PasswordResetTokenRepository tokenRepository, 
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            
            PasswordResetToken resetToken = new PasswordResetToken();
            resetToken.setUser(user);
            resetToken.setTokenHash(token);
            resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            
            tokenRepository.save(resetToken);
            
            System.out.println("Password reset token for " + email + ": " + token);
        });
    }

    @Transactional
    public boolean validateTokenAndResetPassword(String token, String newPassword) {
        return tokenRepository.findByTokenHash(token)
                .filter(resetToken -> resetToken.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(resetToken -> resetToken.getUsedAt() == null)
                .map(resetToken -> {
                    var user = resetToken.getUser();
                    user.setPasswordHash(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    
                    // Mark token as used instead of deleting it
                    resetToken.setUsedAt(LocalDateTime.now());
                    tokenRepository.save(resetToken);
                    
                    return true;
                }).orElse(false);
    }
}