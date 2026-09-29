```java
package com.lenzora.official.system.lenzora.catalogue.domain;

import com.lenzora.official.system.lenzora.shared.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Represents a lens option in the LOOMS catalogue.
 *
 * <p>This entity is mapped to the {@code lens_option} table
 * in the database. It stores optional lens features such as
 * anti-reflective coating, blue light filtering, and
 * photochromic lenses.</p>
 *
 * <p>The {@code createdAt} and {@code updatedAt} fields are
 * inherited from {@link AuditableEntity}.</p>
 */
@Entity
@Table(name = "lens_option")
public class LensOption extends AuditableEntity {

    /**
     * Unique identifier for the lens option.
     *
     * <p>This is the primary key of the lens_option table.
     * MySQL automatically generates the value using AUTO_INCREMENT.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lens_option_id")
    private Long lensOptionId;

    /**
     * Unique code used to identify the lens option.
     *
     * <p>Examples include AR_MULTICOAT and BLUE_LIGHT.
     * This field is required and must be unique.</p>
     */
    @Column(name = "lens_code", nullable = false, length = 50)
    private String lensCode;

    /**
     * Display name or description of the lens option.
     *
     * <p>This is the wording that can be shown to the user.
     * This field is required.</p>
     */
    @Column(name = "label", nullable = false, length = 100)
    private String label;

    /**
     * Additional charge for selecting this lens option.
     *
     * <p>The database does not allow a negative extra charge.</p>
     */
    @Column(
        name = "extra_charge",
        nullable = false,
        precision = 10,
        scale = 2
    )
    private BigDecimal extraCharge;

    /**
     * Indicates whether the lens option is currently active.
     *
     * <p>The default value is true, matching the database default.</p>
     */
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    /**
     * No-argument constructor required by JPA.
     *
     * <p>JPA/Hibernate uses this constructor when creating
     * LensOption objects from database records.</p>
     */
    protected LensOption() {
    }

    /**
     * Returns the lens option ID.
     *
     * @return the lens option ID
     */
    public Long getLensOptionId() {
        return lensOptionId;
    }

    /**
     * Sets the lens option ID.
     *
     * @param lensOptionId the lens option ID
     */
    public void setLensOptionId(Long lensOptionId) {
        this.lensOptionId = lensOptionId;
    }

    /**
     * Returns the lens code.
     *
     * @return the lens code
     */
    public String getLensCode() {
        return lensCode;
    }

    /**
     * Sets the lens code.
     *
     * @param lensCode the lens code
     */
    public void setLensCode(String lensCode) {
        this.lensCode = lensCode;
    }

    /**
     * Returns the lens option label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the lens option label.
     *
     * @param label the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the additional charge for the lens option.
     *
     * @return the extra charge
     */
    public BigDecimal getExtraCharge() {
        return extraCharge;
    }

    /**
     * Sets the additional charge for the lens option.
     *
     * @param extraCharge the additional charge
     */
    public void setExtraCharge(BigDecimal extraCharge) {
        this.extraCharge = extraCharge;
    }

    /**
     * Returns whether the lens option is active.
     *
     * @return true if active, otherwise false
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * Sets whether the lens option is active.
     *
     * @param active the active status
     */
    public void setActive(Boolean active) {
        this.active = active;
    }
}

