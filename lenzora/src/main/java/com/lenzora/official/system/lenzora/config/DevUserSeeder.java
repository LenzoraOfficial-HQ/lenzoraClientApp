package com.lenzora.official.system.lenzora.config;

import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Creates one administrator account when the application starts against an
 * empty database, so there is always an account to sign in with.
 */
@Component
public class DevUserSeeder implements ApplicationRunner {

    /** The address typed into the "Username" box of the login page. */
    private static final String ADMIN_EMAIL = "admin@lenzora.lk";

    /** The matching password, hashed with BCrypt before it is stored. */
    private static final String ADMIN_PASSWORD = "admin123";

    /** Reads and writes the login accounts. */
    private final UserRepository userRepository;

    /** Turns the plain password into the BCrypt hash the table stores. */
    private final PasswordEncoder passwordEncoder;

    public DevUserSeeder(UserRepository pUserRepository,
                         PasswordEncoder pPasswordEncoder) {
        this.userRepository = pUserRepository;
        this.passwordEncoder = pPasswordEncoder;
    }

    @Override
    public void run(ApplicationArguments pArgs) {

        if (userRepository.count() > 0) {
            System.out.println("Login accounts already exist, so none were seeded.");
            return;
        }

        User admin = new User();
        admin.setFirstName("Lenzora");
        admin.setLastName("Admin");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");

        LocalDateTime now = LocalDateTime.now();
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        userRepository.save(admin);

        System.out.println("Seeded the default administrator. Sign in with "
                + ADMIN_EMAIL + " / " + ADMIN_PASSWORD
                + " (development account - change it before release).");
    }
}
