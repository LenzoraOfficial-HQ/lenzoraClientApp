package com.lenzora.official.system.lenzora.catalogue.domain;

import com.lenzora.official.system.lenzora.shared.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** A frame in the LOOMS catalogue. Rows live in the frame table. */
@Entity
@Table(name = "frame")
public class Frame extends AuditableEntity {

    /** Unique identifier for the frame. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "frame_id")
    private Long frameId;

    /** Supplier that provides this frame. */
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    /** Brand name of the frame. */
    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    /** Model name or model number of the frame. */
    @Column(name = "model", nullable = false, length = 50)
    private String model;

    /** Colour of the frame. */
    @Column(name = "colour", nullable = false, length = 30)
    private String colour;

    /** Size of the frame. */
    @Column(name = "size", length = 20)
    private String size;

    /** Material used to make the frame. */
    @Column(name = "material", length = 30)
    private String material;

    /** Price of the frame. */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal price;

    /** URL or path of the frame image. */
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    /** Indicates whether the frame is currently available. */
    @Column(name = "is_available", nullable = false)
    private Boolean available = true;

    /** Indicates whether the frame is active in the catalogue. */
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    /** No-argument constructor required by JPA. */
    public Frame() {
    }

    public Long getFrameId() {
        return frameId;
    }

    public void setFrameId(Long frameId) {
        this.frameId = frameId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public java.math.BigDecimal getPrice() {
        return price;
    }

    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}

