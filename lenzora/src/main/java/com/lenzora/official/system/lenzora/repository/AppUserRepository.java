package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Handles database operations for AppUser.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // Find a user by email
    Optional<AppUser> findByEmail(String email);

    // Check if an email is already registered
    boolean existsByEmail(String email);

    // Find all users with a specific role
    List<AppUser> findByUserRole(UserRole role);
}