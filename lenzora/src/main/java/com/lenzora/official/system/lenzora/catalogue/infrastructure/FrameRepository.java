package com.lenzora.official.system.lenzora.catalogue.infrastructure;

import com.lenzora.official.system.lenzora.catalogue.domain.Frame;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for performing database operations on Frame entities.
 *
 * <p>JpaRepository provides common operations such as
 * saving, finding, updating, and deleting Frame records.</p>
 */
public interface FrameRepository extends JpaRepository<Frame, Long> {
}

