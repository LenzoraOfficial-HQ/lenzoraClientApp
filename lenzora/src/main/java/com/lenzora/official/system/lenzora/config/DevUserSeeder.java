package com.lenzora.official.system.lenzora.config;

import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Keeps one administrator account ready when the application starts, so there
 * is always an account that can open the admin portal. The address and the
 * password come from ADMIN_EMAIL and ADMIN_PASSWORD in .env, which is never
 * committed, so no credentials live in this file.
 */
@Component
public class DevUserSeeder implements ApplicationRunner {

    /** The address typed into the "Username" box of the login page. */
    @Value("${ADMIN_EMAIL:admin@lenzora.lk}")
    private String adminEmail;

    /** The password of that account. Empty when .env does not set it. */
    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

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

        if (adminPassword == null || adminPassword.isBlank()) {
            System.out.println("ADMIN_PASSWORD is not set in .env, so the administrator account was left alone.");
            return;
        }

        User admin = userRepository.findByEmail(adminEmail).orElse(null);

        if (admin != null) {
            repairAdmin(admin);
            return;
        }

        if (userRepository.countByRole("ADMIN") > 0) {
            System.out.println("An administrator already exists, so none was seeded.");
            return;
        }

        createAdmin();
    }

    /**
     * Makes sure the administrator account can actually be used with the
     * password from .env. The seeded sample data ships a hand-typed hash,
     * which no password matches.
     *
     * @param pAdmin the account read from the table
     */
    private void repairAdmin(User pAdmin) {

        boolean changed = false;

        if (!"ADMIN".equals(pAdmin.getRole())) {
            pAdmin.setRole("ADMIN");
            changed = true;
        }

        if (!"ACTIVE".equals(pAdmin.getStatus())) {
            pAdmin.setStatus("ACTIVE");
            changed = true;
        }

        if (!passwordMatches(pAdmin.getPasswordHash())) {
            pAdmin.setPasswordHash(passwordEncoder.encode(adminPassword));
            changed = true;
        }

        if (!changed) {
            System.out.println("Administrator " + adminEmail + " is ready.");
            return;
        }

        pAdmin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(pAdmin);

        System.out.println("Repaired the administrator account. Sign in as " + adminEmail
                + " with the ADMIN_PASSWORD from .env.");
    }

    /**
     * Tells whether the stored hash belongs to the password in .env. A BCrypt
     * hash is exactly 60 characters long and starts with the version tag, so
     * the placeholder text in the sample data is recognised straight away.
     *
     * @param pHash the text stored in the password column
     * @return true when the password in .env opens this account
     */
    private boolean passwordMatches(String pHash) {

        if (pHash == null || pHash.length() != 60) {
            return false;
        }

        if (!pHash.startsWith("$2a$") && !pHash.startsWith("$2b$") && !pHash.startsWith("$2y$")) {
            return false;
        }

        return passwordEncoder.matches(adminPassword, pHash);
    }

    /** Adds the administrator account to a database that has none. */
    private void createAdmin() {

        User admin = new User();
        admin.setFirstName("Lenzora");
        admin.setLastName("Admin");
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMIN");
        admin.setStatus("ACTIVE");

        LocalDateTime now = LocalDateTime.now();
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        userRepository.save(admin);

        System.out.println("Seeded the default administrator. Sign in as " + adminEmail
                + " with the ADMIN_PASSWORD from .env.");
    }
}
