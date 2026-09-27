package com.lenzora.official.system.lenzora.ordering;

import com.lenzora.official.system.lenzora.shared.AuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * One frame + lens line of an order.
 */
@Entity
@Table(name = "order_item")
public class OrderItem extends AuditableEntity {

    private static final BigDecimal PROGRESSIVE_RATE = new BigDecimal("0.35");
    private static final BigDecimal COATING_RATE = new BigDecimal("0.15");
    private static final BigDecimal SINGLE_VISION_CHARGE = new BigDecimal("4500.00");
    private static final BigDecimal BIFOCAL_CHARGE = new BigDecimal("8500.00");
    private static final BigDecimal PROGRESSIVE_CHARGE = new BigDecimal("12500.00");
    private static final BigDecimal DIGITAL_PROGRESSIVE_CHARGE = new BigDecimal("18500.00");

    /**
     * Where the frame comes from.
     */
    public enum FrameSource {

        CATALOGUE("Catalogue frame"),
        CUSTOMER_OWN("Patient's own frame");

        private final String label;

        /**
         * Stores frame source.
         * @param label frame source label
         */
        FrameSource(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    /**
     * The lens styles
     */
    public enum LensType {

        SINGLE_VISION("Single Vision"),
        BF_FLAT_TOP("BF - Flat Top"),
        BF_ROUND_SEG("BF - Round Seg"),
        NORMAL_PROGRESSIVE("Normal Progressive"),
        DIGITAL_PROGRESSIVE("Digital Progressive");

        private final String label;

        /**
         * Stores lens style
         * @param label lens style
         */
        LensType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        /**
         * Check progressive styles
         * @return true for the two progressive styles
         */
        public boolean isProgressive() {
            return this == NORMAL_PROGRESSIVE || this == DIGITAL_PROGRESSIVE;
        }

        /**
         * Checks if needs reading addition.
         * @return true when BF lens
         */
        public boolean needsReadingAddition() {
            return this != SINGLE_VISION;
        }

        /**
         * A bifocal or progressive lens needs a segment height.
         * @return true when the segment height must be filled in
         */
        public boolean needsSegmentHeight() {
            return this != SINGLE_VISION;
        }

        /**
         * Only a progressive lens needs a fitting height.
         * @return true when the fitting height must be filled in
         */
        public boolean needsFittingHeight() {
            return isProgressive();
        }

        /**
         * Prescription type for this lens.
         * @return BIFOCAL for the two bifocal styles, PROGRESSIVE for two progressive styles, otherwise SINGLE_VISION
         */
        public Prescription.PrescriptionType prescriptionType() {
            if (this == BF_FLAT_TOP || this == BF_ROUND_SEG) {
                return Prescription.PrescriptionType.BIFOCAL;
            }
            if (isProgressive()) {
                return Prescription.PrescriptionType.PROGRESSIVE;
            }
            return Prescription.PrescriptionType.SINGLE_VISION;
        }
    }

    /**
     * The coatings 
     */
    public enum CoatingType {

        CRIZAL_MULTICOATING("Crizal Multicoating"),
        CRIZAL_UV("Crizal UV Multicoating"),
        PREVENCIA("Prevencia Multicoating"),
        NORMAL_MULTICOATING("Normal Multicoating"),
        TITUS_HARD("Titus Hard Coating"),
        NONE("No Coating / Uncoat");

        private final String label;

        CoatingType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        /**
         * Price doesn't add for no coating.
         * @return true for every coating except NONE
         */
        public boolean isChargeable() {
            return this != NONE;
        }
    }

    /**
     * The tints
     */
    public enum TintType {

        NONE("No Tint"),
        FULL("Full"),
        SHADED("Shaded"),
        GRADIENT("Gradient");

        private final String label;

        TintType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    /** The database id, filled in by MySQL on insert. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;
    @Column(name = "frame_id")
    private Long frameId;
    @Transient
    private BigDecimal framePrice;
    @Enumerated(EnumType.STRING)
    @Column(name = "frame_source", nullable = false, length = 20)
    private FrameSource frameSource = FrameSource.CATALOGUE;

    /**
     * The earlier line that a replacement repairs, or null when this line
     * repairs nothing
     */
    @Column(name = "replaces_order_item_id")
    private Long replacesOrderItemId;
    @Column(name = "frame_description", nullable = false, length = 500)
    private String frameDescription;
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /** Unit price x quantity. */
    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal = BigDecimal.ZERO;
    @Column(name = "quantity", nullable = false)
    private int quantity;
    @Enumerated(EnumType.STRING)
    @Column(name = "lens_type", length = 30)
    private LensType lensType;
    @Column(name = "lens_index", length = 20)
    private String lensIndex;
    @Enumerated(EnumType.STRING)
    @Column(name = "coating", length = 30)
    private CoatingType coating;
    @Enumerated(EnumType.STRING)
    @Column(name = "tint", length = 20)
    private TintType tint;
    @Column(name = "a_size", precision = 6, scale = 2)
    private BigDecimal aSize;
    @Column(name = "b_size", precision = 6, scale = 2)
    private BigDecimal bSize;
    @Column(name = "d_size", precision = 6, scale = 2)
    private BigDecimal dSize;
    @Column(name = "diameter", precision = 6, scale = 2)
    private BigDecimal diameter;
    @Column(name = "frame_material", length = 20)
    private String frameMaterial;
    @Column(name = "frame_rim_type", length = 20)
    private String frameRimType;
    @Column(name = "frame_shape", length = 40)
    private String frameShape;

    /** One prescription per eye */
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL,orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions = new ArrayList<Prescription>();

    /**
     * The extra options ticked for this line, like tint or Transitions.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "order_item_lens_option",joinColumns = @JoinColumn(name = "order_item_id"))
    @Column(name = "lens_option_id")
    private Set<Long> lensOptionIds = new LinkedHashSet<Long>();

    protected OrderItem() {}

    OrderItem(Long pOrderId, Long pFrameId, String pFrameName, BigDecimal pFramePrice,String pOwnFrameDescription, int pQuantity,LensType pLensType, String pLensIndex, CoatingType pCoating, TintType pTint) {
        if (pQuantity < 1) {
            pQuantity = 1;
            System.out.println("Quantity must be at least 1.");
        }

        this.orderId = pOrderId;
        this.frameId = pFrameId;
        this.framePrice = pFramePrice;
        this.quantity = pQuantity;
        this.lensType = pLensType;
        this.lensIndex = (pLensIndex == null || pLensIndex.trim().isEmpty()) ? null : pLensIndex.trim();
        this.coating = pCoating;
        this.tint = pTint;

        if (pFrameId != null) {
            this.frameSource = FrameSource.CATALOGUE;
            this.frameDescription = pFrameName;
        } else {
            this.frameSource = FrameSource.CUSTOMER_OWN;
            this.frameDescription = (pOwnFrameDescription == null) ? null : pOwnFrameDescription.trim();
        }
        applyStandardPrice();
    }

    /**
     * Line total
     * @return the new line total
     */
    public BigDecimal applyStandardPrice() {
        BigDecimal price;

        if (frameId != null) {
            price = (framePrice == null) ? BigDecimal.ZERO : framePrice;
            if (lensType != null && lensType.isProgressive()) {
                price = price.add(round(price.multiply(PROGRESSIVE_RATE)));
            }
        } else {
            price = lensOnlyCharge();
        }

        if (coating != null && coating.isChargeable()) {
            price = price.add(round(price.multiply(COATING_RATE)));
        }

        this.unitPrice = round(price);
        this.lineTotal = round(this.unitPrice.multiply(BigDecimal.valueOf(quantity)));
        return this.lineTotal;
    }

    /**
     * Lens only charges
     * @return the lens charge 
     */
    private BigDecimal lensOnlyCharge() {
        if (lensType == null) {
            return BigDecimal.ZERO;
        }
        if (lensType == LensType.SINGLE_VISION) {
            return SINGLE_VISION_CHARGE;
        }
        if (lensType == LensType.BF_FLAT_TOP || lensType == LensType.BF_ROUND_SEG) {
            return BIFOCAL_CHARGE;
        }
        if (lensType == LensType.DIGITAL_PROGRESSIVE) {
            return DIGITAL_PROGRESSIVE_CHARGE;
        }
        return PROGRESSIVE_CHARGE;
    }

    /**
     * A unit price typed
     * @param price the price typed by the user, or null when the box was empty
     */
    public void overrideUnitPrice(BigDecimal price) {
        if (price == null) {
            System.out.println("No price was typed in.");
            return;
        }
        if (price.signum() < 0) {
            System.out.println("Price cannot be negative.");
            return;
        }
        this.unitPrice = round(price);
        this.lineTotal = round(this.unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }

    /**
     * Rounds an amount to rupees and paise.
     * @param value the raw amount
     * @return the same amount with two decimal places, rounded half up
     */
    private static BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * The measurements and the look of the chosen frame.
     *
     * @param pASize the width of the lens
     * @param pBSize the height of the lens
     * @param pDSize the diagonal measurement
     * @param pDiameter the diameter of the blank lens
     * @param pMaterial the material, or null 
     * @param pRimType how much of the lens the rim holds
     * @param pShape the outline shape of the frame
     */
    public void describeFrame(BigDecimal pASize, BigDecimal pBSize, BigDecimal pDSize,BigDecimal pDiameter, String pMaterial,String pRimType, String pShape) {
        this.aSize = pASize;
        this.bSize = pBSize;
        this.dSize = pDSize;
        this.diameter = pDiameter;
        this.frameMaterial = pMaterial;
        this.frameRimType = pRimType;
        this.frameShape = pShape;
    }

    /**
     * Records which earlier line this job repairs.
     * @param originalOrderItemId the id of the line being repaired, or null
     */
    public void markReplacementOf(Long originalOrderItemId) {
        this.replacesOrderItemId = originalOrderItemId;
    }

    /**
     * Replaces the extra lens options 
     * @param pLensOptionIds the ids of the options that were ticked, or null
     */
    public void selectLensOptions(Collection<Long> pLensOptionIds) {
        this.lensOptionIds.clear();
        if (pLensOptionIds != null) {
            this.lensOptionIds.addAll(pLensOptionIds);
        }
    }

    /**
     * Attaches one eye's prescription
     * @param prescription the eye measurements, or null
     */
    public void attach(Prescription prescription) {
        if (prescription == null) {
            System.out.println("No prescription to attach.");
            return;
        }
        prescription.assignTo(this);
        prescriptions.add(prescription);
    }

    /**
     * Remembers which saved order this line belongs to.
     * @param pOrderId the id of the saved order
     */
    public void assignToOrder(Long pOrderId) {
        this.orderId = pOrderId;
    }

    // _________________________________________________________________
    // Rules
    // _______________________________________________________________

    /**
     * check job type.
     * @param pJobType the job type , or null
     * @return an empty text when the line is ready,or an error msg
     */
    public String check(String pJobType) {
        List<String> problems = new ArrayList<String>();

        if (frameSource == FrameSource.CATALOGUE && frameId == null) {
            problems.add("a frame from the catalogue");
        }
        if (frameDescription == null || frameDescription.trim().isEmpty()) {
            problems.add("a description of the frame");
        }

        if ("NEW_SPECTACLE".equals(pJobType) || "LENS_REPLACEMENT".equals(pJobType)) {
            if (lensType == null) {
                problems.add("the lens type");
            }
            if (prescriptions.isEmpty()) {
                problems.add("at least one eye measurement");
            }
        }
        if ("FRAME_REPLACEMENT".equals(pJobType) && frameId == null) {
            problems.add("the replacement frame");
        }

        if (lensType != null && lensType.needsReadingAddition() && !prescriptions.isEmpty()
                && !anyReadingAddition()) {
            problems.add("reading addition (ADD)");
        }
        if (lensType != null && lensType.needsSegmentHeight() && !prescriptions.isEmpty()
                && !anySegmentHeight()) {
            problems.add("segment height");
        }

        if (!problems.isEmpty()) {
            return "Order line for \"" + frameDescription+ "\" is missing: " + String.join(", ", problems) + ".";
        }

        for (Prescription prescription : prescriptions) {
            String problem = prescription.check();
            if (!problem.isEmpty()) {
                return problem;
            }
        }

        return "";
    }

    /**
     * Checks for reading addition.
     * @return true when at least one eye has ADD filled in
     */
    private boolean anyReadingAddition() {
        for (Prescription prescription : prescriptions) {
            if (prescription.hasReadingAddition()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks segment height applies
     * @return true when at least one eye has the segment height filled in
     */
    private boolean anySegmentHeight() {
        for (Prescription prescription : prescriptions) {
            if (prescription.hasSegmentHeight()) {
                return true;
            }
        }
        return false;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getFrameId() { return frameId; }
    public FrameSource getFrameSource() { return frameSource; }
    public boolean isCustomerOwnFrame() { return frameSource == FrameSource.CUSTOMER_OWN; }
    public Long getReplacesOrderItemId() { return replacesOrderItemId; }
    public String getFrameDescription() { return frameDescription; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public int getQuantity() { return quantity; }
    public LensType getLensType() { return lensType; }
    public String getLensIndex() { return lensIndex; }
    public CoatingType getCoating() { return coating; }
    public TintType getTint() { return tint; }
    public BigDecimal getASize() { return aSize; }
    public BigDecimal getBSize() { return bSize; }
    public BigDecimal getDSize() { return dSize; }
    public BigDecimal getDiameter() { return diameter; }
    public String getFrameMaterial() { return frameMaterial; }
    public String getFrameRimType() { return frameRimType; }
    public String getFrameShape() { return frameShape; }
    public List<Prescription> getPrescriptions() {
        return Collections.unmodifiableList(prescriptions);
    }
    public Set<Long> getLensOptionIds() {
        return Collections.unmodifiableSet(lensOptionIds);
    }
}
