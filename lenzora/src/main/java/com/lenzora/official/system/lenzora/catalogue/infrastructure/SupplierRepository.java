package com.lenzora.official.system.lenzora.catalogue.infrastructure;

import com.lenzora.official.system.lenzora.catalogue.domain.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for performing database operations on Supplier entities.
 *
 * <p>JpaRepository provides common operations such as
 * saving, finding, updating, and deleting Supplier records.</p>
 */
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}

