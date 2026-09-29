```java
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

/**
 * Represents a frame in the LOOMS catalogue.
 *
 * <p>This entity is mapped to the {@code frame} table
 * in the database. It stores information about optical
 * frames, including their supplier, brand, model, colour,
 * size, material, price, image, availability, and active status.</p>
 *
 * <p>The {@code createdAt} and {@code updatedAt} fields are
 * inherited from {@link AuditableEntity}.</p>
 */
@Entity
@Table(name = "frame")
public class Frame extends AuditableEntity {

    /**
     * Unique identifier for the frame.
     *
     * <p>This is the primary key of the frame table.
     * MySQL automatically generates the value using AUTO_INCREMENT.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "frame_id")
    private Long frameId;

    /**
     * Supplier that provides this frame.
     *
     * <p>Each frame must belong to a supplier.
     * The supplier_id column acts as a foreign key
     * referencing supplier.supplier_id.</p>
     */
    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    /**
     * Brand name of the frame.
     *
     * <p>This field is required.</p>
     */
    @Column(name = "brand", nullable = false, length = 50)
    private String brand;

    /**
     * Model name or model number of the frame.
     *
     * <p>This field is required.</p>
     */
    @Column(name = "model", nullable = false, length = 50)
    private String model;

    /**
     * Colour of the frame.
     *
     * <p>This field is required.</p>
     */
    @Column(name = "colour", nullable = false, length = 30)
    private String colour;

    /**
     * Size of the frame.
     *
     * <p>This field is optional.</p>
     */
    @Column(name = "size", length = 20)
    private String size;

    /**
     * Material used to make the frame.
     *
     * <p>This field is optional.</p>
     */
    @Column(name = "material", length = 30)
    private String material;

    /**
     * Price of the frame.
     *
     * <p>The database does not allow a negative price.</p>
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal price;

    /**
     * URL or path of the frame image.
     *
     * <p>This field is optional.</p>
     */
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    /**
     * Indicates whether the frame is currently available.
     *
     * <p>This field maps to the is_available column
     * in the database.</p>
     */
    @Column(name = "is_available", nullable = false)
    private Boolean available = true;

    /**
     * Indicates whether the frame is active in the catalogue.
     *
     * <p>This field maps to the is_active column
     * in the database.</p>
     */
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    /**
     * No-argument constructor required by JPA.
     *
     * <p>JPA/Hibernate uses this constructor when creating
     * Frame objects from database records.</p>
     */
    protected Frame() {
    }

    /**
     * Returns the frame ID.
     *
     * @return the frame ID
     */
    public Long getFrameId() {
        return frameId;
    }

    /**
     * Sets the frame ID.
     *
     * @param frameId the frame ID
     */
    public void setFrameId(Long frameId) {
        this.frameId = frameId;
    }

    /**
     * Returns the supplier of this frame.
     *
     * @return the supplier
     */
    public Supplier getSupplier() {
        return supplier;
    }

    /**
     * Sets the supplier of this frame.
     *
     * @param supplier the supplier
     */
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns the frame brand.
     *
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the frame brand.
     *
     * @param brand the brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Returns the frame model.
     *
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the frame model.
     *
     * @param model the model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the frame colour.
     *
     * @return the colour
     */
    public String getColour() {
        return colour;
    }

    /**
     * Sets the frame colour.
     *
     * @param colour the colour
     */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Returns the frame size.
     *
     * @return the size
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the frame size.
     *
     * @param size the size
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Returns the frame material.
     *
     * @return the material
     */
    public String getMaterial() {
        return material;
    }

    /**
     * Sets the frame material.
     *
     * @param material the material
     */
    public void setMaterial(String material) {
        this.material = material;
    }

    /**
     * Returns the frame price.
     *
     * @return the price
     */
    public java.math.BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the frame price.
     *
     * @param price the price
     */
    public void setPrice(java.math.BigDecimal price) {
        this.price = price;
    }

    /**
     * Returns the frame image URL.
     *
     * @return the image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the frame image URL.
     *
     * @param imageUrl the image URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Returns whether the frame is available.
     *
     * @return true if the frame is available, otherwise false
     */
    public Boolean getAvailable() {
        return available;
    }

    /**
     * Sets whether the frame is available.
     *
     * @param available the frame availability status
     */
    public void setAvailable(Boolean available) {
        this.available = available;
    }

    /**
     * Returns whether the frame is active.
     *
     * @return true if the frame is active, otherwise false
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * Sets whether the frame is active.
     *
     * @param active the frame active status
     */
    public void setActive(Boolean active) {
        this.active = active;
    }
}
```
