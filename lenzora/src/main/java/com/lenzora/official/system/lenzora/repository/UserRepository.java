package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository interface for managing User database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Automatically finds a user by their email address
    Optional<User> findByEmail(String email);

    List<User> findByRoleAndAccountStatusAndProfileCompletedTrueOrderByCreatedAtAsc(
            String role, AccountStatus accountStatus);
}
