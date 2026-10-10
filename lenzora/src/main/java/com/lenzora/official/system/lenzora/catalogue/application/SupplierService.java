package com.lenzora.official.system.lenzora.catalogue.application;

import com.lenzora.official.system.lenzora.catalogue.domain.Supplier;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Creates and updates suppliers in the catalogue. */
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    /** Saves a newly registered supplier. */
    @Transactional
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.saveAndFlush(supplier);
    }

    /**
     * Copies the new values onto the stored supplier.
     * @param supplierId the supplier to update
     * @param updatedSupplier the values to copy onto it
     * @return the saved supplier
     * @throws RuntimeException when there is no supplier with that id
     */
    @Transactional
    public Supplier updateSupplier(Long supplierId, Supplier updatedSupplier) {

        Supplier existingSupplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        existingSupplier.setSupplierName(updatedSupplier.getSupplierName());
        existingSupplier.setContactPerson(updatedSupplier.getContactPerson());
        existingSupplier.setSupplierEmail(updatedSupplier.getSupplierEmail());
        existingSupplier.setPhone(updatedSupplier.getPhone());
        existingSupplier.setStreetNo(updatedSupplier.getStreetNo());
        existingSupplier.setStreetName(updatedSupplier.getStreetName());
        existingSupplier.setCity(updatedSupplier.getCity());
        existingSupplier.setPostalCode(updatedSupplier.getPostalCode());
        existingSupplier.setActive(updatedSupplier.getActive());

        return supplierRepository.save(existingSupplier);
    }
}