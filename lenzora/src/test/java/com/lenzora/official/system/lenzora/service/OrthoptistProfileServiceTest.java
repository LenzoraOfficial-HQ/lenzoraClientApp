package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrthoptistProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrthoptistProfileService profileService;

    @Test
    void savesAllDetailsAndMarksProfileComplete() {
        User user = pendingOrthoptist();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        profileService.completeProfile(user.getEmail(), " ORTH-123 ", " 0771234567 ", " Colombo ");

        assertEquals("ORTH-123", user.getLicenseNumber());
        assertEquals("0771234567", user.getPhone());
        assertEquals("Colombo", user.getBranch());
        assertEquals(true, user.isProfileCompleted());
        verify(userRepository).save(user);
    }

    @Test
    void profileCanOnlyBeCompletedOnce() {
        User user = pendingOrthoptist();
        user.setLicenseNumber("ORTH-123");
        user.setPhone("0771234567");
        user.setBranch("Colombo");
        user.setProfileCompleted(true);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
            () -> profileService.completeProfile(user.getEmail(), "ORTH-456", "0779876543", "Kandy"));
    }

    private User pendingOrthoptist() {
        User user = new User();
        user.setEmail("doctor@example.com");
        user.setRole("ORTHOPTIST");
        user.setAccountStatus(AccountStatus.PENDING);
        return user;
    }
}