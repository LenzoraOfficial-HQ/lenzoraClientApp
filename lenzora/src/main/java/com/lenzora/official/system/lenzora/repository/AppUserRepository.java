package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.entity.UserRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the AppUser entity.
 * Provides standard database operations (Create, Read, Update, Delete) for the app_user table.
 */
@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Finds a user by their unique email address.
     * 
     * @param email The email address to search for.
     * @return An Optional containing the user if found, or empty if not.
     */
    Optional<AppUser> findByEmail(String email);
    
    /**
     * Checks if a user exists with the given email address.
     *
     * @param email The email address to check.
     * @return True if a user exists with this email, false otherwise.
     */
    boolean existsByEmail(String email);
    
    /**
     * Finds users based on their assigned role.
     * 
     * @param role The role to search for (e.g., ORTHOPTIST, ADMIN).
     * @return An iterable of users with the specified role.
     */
    Iterable<AppUser> findByUserRole(UserRole role);
}