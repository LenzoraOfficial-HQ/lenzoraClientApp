package com.lenzora.official.system.lenzora.config;

import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.repository.UserRepository;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;

/**
 * Creates one administrator account when the application starts against an
 * empty database, so there is always an account to sign in with.
 */
@Component
@Profile("!production")
public class DevUserSeeder implements ApplicationRunner {

    /** Reads and writes the login accounts. */
    private final UserRepository userRepository;

    /** Turns the plain password into the BCrypt hash the table stores. */
    private final PasswordEncoder passwordEncoder;

    private final String adminEmail;
    private final String adminPassword;

    public DevUserSeeder(UserRepository pUserRepository,
                         PasswordEncoder pPasswordEncoder,
                         @Value("${lenzora.admin.email:}") String pAdminEmail,
                         @Value("${lenzora.admin.password:}") String pAdminPassword) {
        this.userRepository = pUserRepository;
        this.passwordEncoder = pPasswordEncoder;
        this.adminEmail = pAdminEmail;
        this.adminPassword = pAdminPassword;
    }

    @Override
    public void run(ApplicationArguments pArgs) {

        if (userRepository.count() > 0) {
            System.out.println("Login accounts already exist, so none were seeded.");
            return;
        }

        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            System.out.println("Admin credentials are not configured, so no account was seeded.");
            return;
        }

        User admin = new User();
        admin.setFirstName("Lenzora");
        admin.setLastName("Admin");
        admin.setEmail(adminEmail);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole("ADMIN");
        admin.setAccountStatus(AccountStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();
        admin.setCreatedAt(now);
        admin.setUpdatedAt(now);

        userRepository.save(admin);

        System.out.println("Seeded the configured development administrator account: "
            + adminEmail + ".");
    }
}
