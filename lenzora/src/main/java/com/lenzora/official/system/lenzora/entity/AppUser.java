package com.lenzora.official.system.lenzora.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing an application user in the system.
 */
@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole = UserRole.ORTHOPTIST;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.PENDING;

    @Column(name = "registration_no", length = 30)
    private String registrationNo;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "branch", length = 50)
    private String branch;

    // Self-referencing link to the admin who approved this account
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private AppUser approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Automatically sets created and updated timestamps on initial insert.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Automatically updates the timestamp when the record is modified.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public AppUser() {}

    // Getters and Setters
    public Long getUserId() {
         return userId; 
    }
    public void setUserId(Long userId) {
         this.userId = userId; 
    }

    public String getFirstName() {
         return firstName; 
    }
    public void setFirstName(String firstName) {
         this.firstName = firstName; 
    }

    public String getLastName() {
         return lastName; 
    }
    public void setLastName(String lastName) {
         this.lastName = lastName; 
    }

    public String getEmail() {
         return email; 
    }
    public void setEmail(String email) {
         this.email = email; 
    }

    public String getPasswordHash() {
         return passwordHash; 
    }
    public void setPasswordHash(String passwordHash) {
         this.passwordHash = passwordHash; 
    }

    public UserRole getUserRole() {
         return userRole; 
    }
    public void setUserRole(UserRole userRole) {
         this.userRole = userRole;
    }

    public UserStatus getUserStatus() {
         return userStatus; 
    }
    public void setUserStatus(UserStatus userStatus) {
         this.userStatus = userStatus; 
    }

    public String getRegistrationNo() {
         return registrationNo; 
    }
    public void setRegistrationNo(String registrationNo) {
         this.registrationNo = registrationNo; 
    }

    public String getPhone() {
         return phone; 
    }
    public void setPhone(String phone) {
         this.phone = phone; 
    }

    public String getBranch() {
         return branch; 
    }
    public void setBranch(String branch) {
         this.branch = branch; }

    public AppUser getApprovedBy() {
         return approvedBy; 
    }
    public void setApprovedBy(AppUser approvedBy) {
         this.approvedBy = approvedBy; 
    }

    public LocalDateTime getApprovedAt() {
         return approvedAt; 
    }
    public void setApprovedAt(LocalDateTime approvedAt) {
         this.approvedAt = approvedAt; 
    }

    public LocalDateTime getCreatedAt() {
         return createdAt; 
    }
    public void setCreatedAt(LocalDateTime createdAt) {
         this.createdAt = createdAt; 
    }

    public LocalDateTime getUpdatedAt() {
         return updatedAt; 
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
         this.updatedAt = updatedAt; }
}