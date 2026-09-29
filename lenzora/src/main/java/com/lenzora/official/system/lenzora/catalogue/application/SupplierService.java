package com.lenzora.official.system.lenzora.catalogue.application;

import com.lenzora.official.system.lenzora.catalogue.domain.Supplier;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling business operations related to Supplier entities.
 *
 * <p>This class manages the update operation for suppliers
 * and communicates with the SupplierRepository to access the database.</p>
 */
@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    /**
     * Creates a SupplierService with the required repository.
     *
     * @param supplierRepository repository used to perform database operations
     */
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    /**
     * Updates an existing supplier with the provided information.
     *
     * @param supplierId ID of the supplier to be updated
     * @param updatedSupplier supplier object containing the new information
     * @return the updated Supplier entity
     * @throws RuntimeException if the supplier does not exist
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