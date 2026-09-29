package com.lenzora.official.system.lenzora.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a password reset token.
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically sets the creation time before saving.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public PasswordResetToken() {}

    // Getters and Setters
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
         this.createdAt = createdAt; }
}