package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Creates a new account from the sign-up page. The password is hashed before it
 * is stored, and every problem is reported by returning a message.
 *
 * @author Lenzora
 * @version 1.0
 */
@Service
public class UserRegistrationService {

    /** The shortest password the sign-up page accepts. */
    private static final int MINIMUM_PASSWORD_LENGTH = 8;

    /** Reads and writes the login accounts. */
    private final UserRepository userRepository;

    /** Turns the plain password into the BCrypt hash the table stores. */
    private final PasswordEncoder passwordEncoder;

    /**
     * @param pUserRepository  the login account table
     * @param pPasswordEncoder the encoder used by the login form
     */
    public UserRegistrationService(UserRepository pUserRepository,
                                   PasswordEncoder pPasswordEncoder) {
        this.userRepository = pUserRepository;
        this.passwordEncoder = pPasswordEncoder;
    }

    /**
     * Checks the submitted details and, when they are all acceptable, saves the
     * new account.
     *
     * @param pFirstName         the first name typed on the form
     * @param pLastName          the last name typed on the form
     * @param pEmail             the email address, which is also the login name
     * @param pPassword          the chosen password, in plain text
     * @param pConfirmedPassword the same password typed a second time
     * @return an empty text when the account was created, otherwise the message
     *         to show on the form
     */
    public String register(String pFirstName,
                           String pLastName,
                           String pEmail,
                           String pPassword,
                           String pConfirmedPassword) {

        String firstName = text(pFirstName);
        String lastName = text(pLastName);
        String email = text(pEmail);
        String password = text(pPassword);

        if (firstName.isEmpty() || lastName.isEmpty()) {
            return "First name and last name are required.";
        }
        if (email.isEmpty()) {
            return "Email is required.";
        }
        if (!email.contains("@") || email.startsWith("@") || email.endsWith("@")) {
            return "Email does not look like a valid address.";
        }
        if (password.length() < MINIMUM_PASSWORD_LENGTH) {
            return "Password must be at least " + MINIMUM_PASSWORD_LENGTH + " characters.";
        }
        if (!password.equals(text(pConfirmedPassword))) {
            return "The two passwords do not match.";
        }

        User existing = userRepository.findByEmail(email).orElse(null);
        if (existing != null) {
            return "An account already exists for " + email + ". Please log in instead.";
        }

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

        return "";
    }

    /**
     * Treats a missing value as an empty one, and removes spaces around it.
     *
     * @param pValue the text typed on the form, possibly null
     * @return the trimmed text, never null
     */
    private String text(String pValue) {
        if (pValue == null) {
            return "";
        }
        return pValue.trim();
    }
}
