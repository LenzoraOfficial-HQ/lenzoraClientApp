package com.lenzora.official.system.lenzora.catalogue.domain;

import com.lenzora.official.system.lenzora.shared.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a supplier in the LOOMS catalogue.
 *
 * <p>This entity is mapped to the {@code supplier} table
 * in the database. It stores supplier contact information,
 * address details, and the supplier's active status.</p>
 *
 * <p>The {@code createdAt} and {@code updatedAt} fields are
 * inherited from {@link AuditableEntity}.</p>
 */
@Entity
@Table(name = "supplier")
public class Supplier extends AuditableEntity {

    /**
     * Unique identifier for the supplier.
     *
     * <p>This field is the primary key of the supplier table.
     * MySQL automatically generates the value using AUTO_INCREMENT.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;

    /**
     * Name of the supplier.
     *
     * <p>This field is required and cannot be NULL in the database.</p>
     */
    @Column(name = "supplier_name", nullable = false, length = 100)
    private String supplierName;

    /**
     * Name of the supplier's contact person.
     *
     * <p>This field is optional.</p>
     */
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    /**
     * Email address of the supplier.
     *
     * <p>This field is required and maps to the {@code email}
     * column in the database.</p>
     */
    @Column(name = "email", nullable = false, length = 100)
    private String supplierEmail;

    /**
     * Phone number of the supplier.
     *
     * <p>This field is optional.</p>
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Street number of the supplier's address.
     *
     * <p>This field is optional.</p>
     */
    @Column(name = "street_no", length = 10)
    private String streetNo;

    /**
     * Street name of the supplier's address.
     *
     * <p>This field is optional.</p>
     */
    @Column(name = "street_name", length = 100)
    private String streetName;

    /**
     * City where the supplier is located.
     *
     * <p>This field is optional.</p>
     */
    @Column(name = "city", length = 50)
    private String city;

    /**
     * Postal code of the supplier's address.
     *
     * <p>This field is optional.</p>
     */
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    /**
     * Indicates whether the supplier is currently active.
     *
     * <p>The default value is {@code true}, matching the
     * database default.</p>
     */
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    /**
     * No-argument constructor required by JPA.
     *
     * <p>JPA/Hibernate uses this constructor when creating
     * Supplier objects from database records.</p>
     */
    protected Supplier() {
    }

    /**
     * Returns the supplier ID.
     *
     * @return the supplier ID
     */
    public Long getSupplierId() {
        return supplierId;
    }

    /**
     * Sets the supplier ID.
     *
     * @param supplierId the supplier ID
     */
    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * Returns the supplier name.
     *
     * @return the supplier name
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * Sets the supplier name.
     *
     * @param supplierName the supplier name
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * Returns the contact person's name.
     *
     * @return the contact person's name
     */
    public String getContactPerson() {
        return contactPerson;
    }

    /**
     * Sets the contact person's name.
     *
     * @param contactPerson the contact person's name
     */
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    /**
     * Returns the supplier's email address.
     *
     * @return the supplier email
     */
    public String getSupplierEmail() {
        return supplierEmail;
    }

    /**
     * Sets the supplier's email address.
     *
     * @param supplierEmail the supplier email
     */
    public void setSupplierEmail(String supplierEmail) {
        this.supplierEmail = supplierEmail;
    }

    /**
     * Returns the supplier's phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the supplier's phone number.
     *
     * @param phone the phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the supplier's street number.
     *
     * @return the street number
     */
    public String getStreetNo() {
        return streetNo;
    }

    /**
     * Sets the supplier's street number.
     *
     * @param streetNo the street number
     */
    public void setStreetNo(String streetNo) {
        this.streetNo = streetNo;
    }

    /**
     * Returns the supplier's street name.
     *
     * @return the street name
     */
    public String getStreetName() {
        return streetName;
    }

    /**
     * Sets the supplier's street name.
     *
     * @param streetName the street name
     */
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    /**
     * Returns the supplier's city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the supplier's city.
     *
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the supplier's postal code.
     *
     * @return the postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the supplier's postal code.
     *
     * @param postalCode the postal code
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Returns whether the supplier is active.
     *
     * @return true if the supplier is active, otherwise false
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * Sets whether the supplier is active.
     *
     * @param active the supplier's active status
     */
    public void setActive(Boolean active) {
        this.active = active;
    }
}