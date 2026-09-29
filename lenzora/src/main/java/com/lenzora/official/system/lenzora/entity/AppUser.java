package com.lenzora.official.system.lenzora.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents an application user. 
 * Maps directly to the "app_user" table in the database.
 */
@Entity
@Table(name = "app_user")
public class AppUser {

    /**
     * Unique identifier for the user. Auto-incremented.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /**
     * The user's first name.
     */
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    /**
     * The user's last name.
     */
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /**
     * The user's email address. Must be unique.
     */
    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    /**
     * The hashed password for authentication.
     */
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    /**
     * The role assigned to the user. Defaults to ORTHOPTIST.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole = UserRole.ORTHOPTIST;

    /**
     * The current status of the user account. Defaults to PENDING.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus userStatus = UserStatus.PENDING;

    /**
     * Optional medical registration number[.
     */
    @Column(name = "registration_no", length = 30)
    private String registrationNo;

    /**
     * Contact phone number.
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * The branch the user belongs to.
     */
    @Column(name = "branch", length = 50)
    private String branch;

    /**
     * A self-referencing relationship linking to the admin who approved this user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private AppUser approvedBy;

    /**
     * Timestamp of when the user was approved.
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * Timestamp of record creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last record update.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- Getters and Setters ---

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
        this.branch = branch;
    }

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
        this.updatedAt = updatedAt;
    }
}