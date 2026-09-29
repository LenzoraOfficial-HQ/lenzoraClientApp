package com.lenzora.official.system.lenzora.catalogue.infrastructure;

import com.lenzora.official.system.lenzora.catalogue.domain.LensOption;
import org.springframework.data.jpa.repository.JpaRepository;

/** Reads and writes the lens options of the catalogue. */
public interface LensOptionRepository extends JpaRepository<LensOption, Long> {
}

