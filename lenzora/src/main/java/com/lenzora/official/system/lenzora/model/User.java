package com.lenzora.official.system.lenzora.model;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.shared.BaseAuditableEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class User extends BaseAuditableEntity {

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

    /**
     * Tells whether the account carries a saved, usable professional profile.
     * The stored flag on its own is not enough - an account only counts as
     * complete once the license number, contact number and branch are all there.
     *
     * @return true when the flag is set and every professional detail is present
     */
    public boolean hasProfessionalProfile() {
        return profileCompleted
                && hasText(licenseNumber)
                && hasText(phone)
                && hasText(branch);
    }

    /** @return true when the text is present and not blank */
    private static boolean hasText(String pValue) {
        return pValue != null && !pValue.isBlank();
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
}
