package com.lenzora.official.system.lenzora.catalogue.infrastructure;

import com.lenzora.official.system.lenzora.catalogue.domain.LensOption;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for performing database operations on LensOption entities.
 *
 * <p>JpaRepository provides common operations such as
 * saving, finding, updating, and deleting LensOption records.</p>
 */
public interface LensOptionRepository extends JpaRepository<LensOption, Long> {
}

