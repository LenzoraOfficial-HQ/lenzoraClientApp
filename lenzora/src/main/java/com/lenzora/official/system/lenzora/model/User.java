package com.lenzora.official.system.lenzora.model;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "user_role", nullable = false)
    private String role;

    @Column(name = "role", nullable = false)
    private String legacyRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Column(name = "status", nullable = false)
    private String legacyStatus;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(name = "profile_completed", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean profileCompleted;

    @Column(name = "registration_no")
    private String registrationNo;

    @Column(name = "phone")
    private String phone;

    @Column(name = "branch")
    private String branch;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private java.time.LocalDateTime approvedAt;

    @Column(name = "created_at", nullable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;


    public User() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @PrePersist
    @PreUpdate
    protected void synchronizeRoleColumns() {
        this.legacyRole = this.role;
        this.legacyStatus = this.accountStatus.name();
    }


    public AccountStatus getAccountStatus() {
        return accountStatus == AccountStatus.ACTIVE ? AccountStatus.APPROVED : accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public boolean isProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
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


    public Long getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Long approvedBy) {
        this.approvedBy = approvedBy;
    }


    public java.time.LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(java.time.LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }


    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
