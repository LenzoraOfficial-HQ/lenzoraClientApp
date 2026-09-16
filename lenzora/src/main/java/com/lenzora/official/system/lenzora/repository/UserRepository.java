package com.lenzora.official.system.lenzora.repository;

import com.lenzora.official.system.lenzora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
