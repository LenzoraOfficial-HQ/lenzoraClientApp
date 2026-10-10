package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class OrthoptistProfileService {

    private final UserRepository userRepository;

    public OrthoptistProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void completeProfile(String email, String licenseNumber, String phone, String branch) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (!"ORTHOPTIST".equals(user.getRole())
                || user.getAccountStatus() == AccountStatus.REJECTED) {
            throw new IllegalArgumentException("This account cannot complete an Orthoptist profile.");
        }
        if (user.isProfileCompleted()) {
            throw new IllegalArgumentException("The Orthoptist profile has already been completed.");
        }

        licenseNumber = clean(licenseNumber);
        phone = clean(phone);
        branch = clean(branch);
        if (licenseNumber.isEmpty() || phone.isEmpty() || branch.isEmpty()) {
            throw new IllegalArgumentException("Complete all required professional details.");
        }
        if (licenseNumber.length() > 50) {
            throw new IllegalArgumentException("License number must be 50 characters or fewer.");
        }

        user.setLicenseNumber(licenseNumber);
        user.setPhone(phone);
        user.setBranch(branch);
        user.setProfileCompleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}