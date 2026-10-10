package com.lenzora.official.system.lenzora.catalogue.domain;

import com.lenzora.official.system.lenzora.shared.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/** A lens option in the LOOMS catalogue, shown as a checkbox on the order form. */
@Entity
@Table(name = "lens_option")
public class LensOption extends AuditableEntity {

    /** Unique identifier for the lens option. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lens_option_id")
    private Long lensOptionId;

    /** Unique code used to identify the lens option. */
    @Column(name = "lens_code", nullable = false, length = 50)
    private String lensCode;

    /** Display name or description of the lens option. */
    @Column(name = "label", nullable = false, length = 100)
    private String label;

    /** Additional charge for selecting this lens option. */
    @Column(
        name = "extra_charge",
        nullable = false,
        precision = 10,
        scale = 2
    )
    private BigDecimal extraCharge;

    /** Indicates whether the lens option is currently active. */
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    /** No-argument constructor required by JPA. */
    public LensOption() {
    }

    public Long getLensOptionId() {
        return lensOptionId;
    }

    public void setLensOptionId(Long lensOptionId) {
        this.lensOptionId = lensOptionId;
    }

    public String getLensCode() {
        return lensCode;
    }

    public void setLensCode(String lensCode) {
        this.lensCode = lensCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getExtraCharge() {
        return extraCharge;
    }

    public void setExtraCharge(BigDecimal extraCharge) {
        this.extraCharge = extraCharge;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

