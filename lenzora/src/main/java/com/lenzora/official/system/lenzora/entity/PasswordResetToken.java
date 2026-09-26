package com.lenzora.official.system.lenzora.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a password reset token for a user.
 * Maps directly to the "password_reset_tokens" table in the database.
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    /**
     * Unique identifier for the token record. Auto-incremented by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    /**
     * The user associated with this password reset token.
     * Mapped via the user_id foreign key, cascading deletes according to the database constraint.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    /**
     * The hashed version of the token string. Must be unique.
     */
    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    /**
     * The timestamp indicating when the token expires.
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * The timestamp of when the token was successfully used. 
     * Will be null if the token is currently unused.
     */
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /**
     * Timestamp of record creation. Defaults to current timestamp in the database.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // --- Getters and Setters ---

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}