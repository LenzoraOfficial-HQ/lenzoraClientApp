package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Validates input details and registers a new user.
     * Returns an error message if validation fails, or an empty string on success.
     */
    public String register(String firstName, String lastName, String email, String password, String confirmPassword) {
        // Clean input values
        firstName = cleanText(firstName);
        lastName = cleanText(lastName);
        email = cleanText(email);
        password = cleanText(password);
        confirmPassword = cleanText(confirmPassword);

        // Basic validations
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

        // Check if email is already registered
        if (userRepository.findByEmail(email).isPresent()) {
            return "An account already exists for " + email + ". Please log in instead.";
        }

        // Build and save the new user
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole("ORTHOPTIST");
        user.setStatus("ACTIVE");

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);

        return ""; // Success
    }

    /**
     * Trims surrounding whitespace and converts null to an empty string.
     */
    private String cleanText(String value) {
        return (value == null) ? "" : value.trim();
    }
}