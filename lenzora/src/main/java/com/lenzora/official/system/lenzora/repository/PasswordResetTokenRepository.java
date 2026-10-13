package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Handles database operations for password reset tokens.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void deleteByExpiresAtBefore(LocalDateTime now);
}