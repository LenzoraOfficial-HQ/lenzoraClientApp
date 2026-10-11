package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrthoptistApprovalServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private OrthoptistApprovalService approvalService;

    @Test
    void registrationCreatesPendingAccountWithoutProfessionalDetails() {
        UserRegistrationService registrationService =
                new UserRegistrationService(userRepository, passwordEncoder, auditService);
        when(userRepository.findByEmailIgnoreCase("doctor@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("securepass")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String error = registrationService.register(
            "Ada", "Lovelace", "doctor@example.com", "securepass", "securepass");

        assertEquals("", error);
        ArgumentCaptor<User> savedUser = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUser.capture());
        assertEquals(null, savedUser.getValue().getLicenseNumber());
        assertEquals(AccountStatus.PENDING, savedUser.getValue().getAccountStatus());
        assertEquals(false, savedUser.getValue().isProfileCompleted());
        verify(auditService).logAction(
                "REGISTRATION",
                "doctor@example.com",
                "app_user",
                "New orthoptist account registered: doctor@example.com");
    }

    @Test
    void adminCanApprovePendingOrthoptist() {
        User admin = user("admin@example.com", "ADMIN", AccountStatus.ACTIVE);
        admin.setId(7L);
        User orthoptist = user("doctor@example.com", "ORTHOPTIST", AccountStatus.PENDING);
        orthoptist.setId(12L);
        orthoptist.setLicenseNumber("ORTH-123");
        orthoptist.setRegistrationNo("REG-123");
        orthoptist.setPhone("0771234567");
        orthoptist.setBranch("Colombo");
        orthoptist.setProfileCompleted(true);
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(userRepository.findById(orthoptist.getId())).thenReturn(Optional.of(orthoptist));
        when(userRepository.save(orthoptist)).thenReturn(orthoptist);

        User updated = approvalService.approve(12L, admin.getEmail());

        assertEquals(AccountStatus.APPROVED, updated.getAccountStatus());
        assertEquals(admin.getId(), updated.getApprovedBy());
        verify(auditService).logAction(
                "APPROVAL",
                admin.getEmail(),
                "app_user",
                "Orthoptist 12 (doctor@example.com) approved");
    }

    @Test
    void adminCanRejectPendingOrthoptist() {
        User admin = user("admin@example.com", "ADMIN", AccountStatus.ACTIVE);
        admin.setId(7L);
        User orthoptist = user("doctor@example.com", "ORTHOPTIST", AccountStatus.PENDING);
        orthoptist.setId(12L);
        orthoptist.setLicenseNumber("ORTH-123");
        orthoptist.setRegistrationNo("REG-123");
        orthoptist.setPhone("0771234567");
        orthoptist.setBranch("Colombo");
        orthoptist.setProfileCompleted(true);
        when(userRepository.findByEmail(admin.getEmail())).thenReturn(Optional.of(admin));
        when(userRepository.findById(orthoptist.getId())).thenReturn(Optional.of(orthoptist));
        when(userRepository.save(orthoptist)).thenReturn(orthoptist);

        User updated = approvalService.reject(12L, admin.getEmail());

        assertEquals(AccountStatus.REJECTED, updated.getAccountStatus());
        assertEquals(null, updated.getLicenseNumber());
        assertEquals(null, updated.getRegistrationNo());
        assertEquals(null, updated.getPhone());
        assertEquals(null, updated.getBranch());
        assertEquals(false, updated.isProfileCompleted());
        verify(auditService).logAction(
                "REJECTION",
                admin.getEmail(),
                "app_user",
                "Orthoptist 12 (doctor@example.com) rejected");
    }

    @Test
    void nonAdminCannotApproveAccounts() {
        User orthoptistAdmin = user("user@example.com", "ORTHOPTIST", AccountStatus.APPROVED);
        when(userRepository.findByEmail(orthoptistAdmin.getEmail())).thenReturn(Optional.of(orthoptistAdmin));

        assertThrows(IllegalArgumentException.class,
                () -> approvalService.approve(12L, orthoptistAdmin.getEmail()));
    }

    private User user(String email, String role, AccountStatus accountStatus) {
        User user = new User();
        user.setEmail(email);
        user.setRole(role);
        user.setAccountStatus(accountStatus);
        return user;
    }
}