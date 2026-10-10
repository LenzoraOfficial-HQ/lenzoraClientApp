package com.lenzora.official.system.lenzora.catalogue.domain;

import com.lenzora.official.system.lenzora.shared.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** A supplier that frames are bought from. Rows live in the supplier table. */
@Entity
@Table(name = "supplier")
public class Supplier extends AuditableEntity {

    /** Unique identifier for the supplier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;

    /** Name of the supplier. */
    @NotBlank(message = "Supplier name is required.")
    @Size(max = 100, message = "Supplier name must be at most 100 characters.")
    @Column(name = "name", nullable = false, length = 100)
    private String supplierName;

    /** Keeps the original required database column populated during schema transition. */
    @Column(name = "supplier_name", nullable = false, length = 100)
    private String schemaSupplierName;

    /** Name of the supplier's contact person. */
    @Size(max = 100, message = "Contact person must be at most 100 characters.")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    /** Email address of the supplier. */
    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 100, message = "Email must be at most 100 characters.")
    @Column(name = "email", nullable = false, length = 100)
    private String supplierEmail;

    /** Phone number of the supplier. */
    @Size(max = 20, message = "Phone must be at most 20 characters.")
    @Column(name = "phone", length = 20)
    private String phone;

    /** Street number of the supplier's address. */
    @Size(max = 10, message = "Street number must be at most 10 characters.")
    @Column(name = "street_no", length = 10)
    private String streetNo;

    /** Street name of the supplier's address. */
    @Size(max = 100, message = "Street name must be at most 100 characters.")
    @Column(name = "street_name", length = 100)
    private String streetName;

    /** City where the supplier is located. */
    @Size(max = 50, message = "City must be at most 50 characters.")
    @Column(name = "city", length = 50)
    private String city;

    /** Postal code of the supplier's address. */
    @Size(max = 10, message = "Postal code must be at most 10 characters.")
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /** Indicates whether the supplier is currently active. */
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    /** No-argument constructor required by JPA. */
    public Supplier() {
    }

    @PrePersist
    @PreUpdate
    private void synchronizeSchemaSupplierName() {
        this.schemaSupplierName = this.supplierName;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getSupplierEmail() {
        return supplierEmail;
    }

    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreetNo() {
        return streetNo;
    }

    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
