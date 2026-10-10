package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Automatically finds a user by their email address
    Optional<User> findByEmail(String email);

    // Counts the accounts that carry one role, such as ADMIN
    long countByRole(String role);
}
