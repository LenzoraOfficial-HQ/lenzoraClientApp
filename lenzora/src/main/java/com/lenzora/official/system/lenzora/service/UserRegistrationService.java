package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                   AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    /**
     * Validates input details and registers a new user.
     * Returns an error message if validation fails, or an empty string on success.
     */
    @Transactional
    public String register(String firstName, String lastName, String email,
                           String password, String confirmPassword) {
        firstName = cleanText(firstName);
        lastName = cleanText(lastName);
        email = cleanText(email).toLowerCase(Locale.ROOT);
        password = password == null ? "" : password;
        confirmPassword = confirmPassword == null ? "" : confirmPassword;

        if (firstName.isEmpty() || lastName.isEmpty()) {
            return "First name and last name are required.";
        }

        if (email.isEmpty()) {
            return "Email is required.";
        }

        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            return "Email does not look like a valid address.";
        }

        if (password.length() < 8) {
            return "Password must be at least 8 characters.";
        }

        if (!password.equals(confirmPassword)) {
            return "The two passwords do not match.";
        }

        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            return "An account already exists for " + email + ". Please log in instead.";
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole("ORTHOPTIST");
        // A new account must pass license verification before it can be used.
        user.setAccountStatus(AccountStatus.PENDING);
        user.setProfileCompleted(false);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);
        auditService.logAction(
                "REGISTRATION",
                email,
                "app_user",
                "New orthoptist account registered: " + savedUser.getEmail());

        return ""; // Success
    }

    /**
     * Trims surrounding whitespace and converts null to an empty string.
     */
    private String cleanText(String value) {
        return (value == null) ? "" : value.trim();
    }
}