package com.lenzora.official.system.lenzora.catalogue.infrastructure;

import com.lenzora.official.system.lenzora.catalogue.domain.Frame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Reads and writes the frames of the catalogue. */
public interface FrameRepository extends JpaRepository<Frame, Long> {
    /** Finds frames that can be shown to clients for ordering. */
    List<Frame> findByAvailableTrueAndActiveTrue();
}
