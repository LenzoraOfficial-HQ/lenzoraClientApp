package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.entity.PasswordResetToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the PasswordResetToken entity.
 * Provides database operations for the password_reset_tokens table.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Finds a password reset token by its hashed value.
     * 
     * @param tokenHash The hashed token string.
     * @return An Optional containing the token record if found.
     */
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /**
     * Deletes all tokens that have expired before a specific date and time.
     * This is useful for creating a scheduled background task to clean up the database.
     * 
     * @param now The current date and time to compare against the expires_at column.
     */
    void deleteByExpiresAtBefore(LocalDateTime now);
}