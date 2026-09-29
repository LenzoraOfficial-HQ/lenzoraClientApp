package com.lenzora.official.system.lenzora.catalogue.infrastructure;

import com.lenzora.official.system.lenzora.catalogue.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

/** Reads and writes the suppliers of the catalogue. */
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}

