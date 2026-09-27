package com.lenzora.official.system.lenzora.ordering;

import com.lenzora.official.system.lenzora.shared.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * One eye's measurements for one order line.
 */
@Entity
@Table(name = "prescription")
public class Prescription extends AuditableEntity {

    private static final BigDecimal SPH_MIN = new BigDecimal("-20.00");
    private static final BigDecimal SPH_MAX = new BigDecimal("20.00");
    private static final BigDecimal CYL_MIN = new BigDecimal("-20.00");
    private static final BigDecimal CYL_MAX = new BigDecimal("20.00");
    private static final BigDecimal AXIS_MIN = new BigDecimal("1");
    private static final BigDecimal AXIS_MAX = new BigDecimal("180");
    private static final BigDecimal PD_MIN = new BigDecimal("40.00");
    private static final BigDecimal PD_MAX = new BigDecimal("80.00");
    private static final BigDecimal ADD_MIN = new BigDecimal("0.50");
    private static final BigDecimal ADD_MAX = new BigDecimal("4.00");

    /**
     * Prescription type stored in the "rx_type" column.
     */
    public enum PrescriptionType {
        SINGLE_VISION,
        BIFOCAL,
        PROGRESSIVE
    }

    /**
     * Which eye the measurements belong to.
     */
    public enum EyeSide {

        RIGHT("Right (R)"),
        LEFT("Left (L)");

        private final String label;

        /**
         * Stores which side eye is it.
         * @param pLabel the text shown to the user
         */
        EyeSide(String pLabel) {
            this.label = pLabel;
        }

        /**
         *  Shown eye side Left or Right.
         * @return the label of this eye
         */
        public String getLabel() {
            return label;
        }
    }

    /** The database id, filled in by MySQL on insert. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private Long id;

    /**
     * The order line these measurements belong to.
     *
     * <p>For now this is the bare id, not a link to {@code OrderItem}, so
     * this file needs no class that has not reached main yet. Replace it
     * with a {@code @ManyToOne OrderItem} once that entity is here.</p>
     */
    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    /** Which kind of lens this eye was measured for. */
    @Enumerated(EnumType.STRING)
    @Column(name = "rx_type", nullable = false, length = 30)
    private PrescriptionType type;

    /** Which eye these measurements belong to. */
    @Enumerated(EnumType.STRING)
    @Column(name = "eye", nullable = false, length = 10)
    private EyeSide eye;

    @Column(name = "sph", precision = 6, scale = 2)
    private BigDecimal sph;
    @Column(name = "cyl", precision = 6, scale = 2)
    private BigDecimal cyl;
    @Column(name = "axis", precision = 6, scale = 2)
    private BigDecimal axis;
    @Column(name = "pd", precision = 6, scale = 2)
    private BigDecimal pd;
    @Column(name = "prism", precision = 6, scale = 2)
    private BigDecimal prism;
    @Column(name = "n_pd", precision = 6, scale = 2)
    private BigDecimal nearPd;
    @Column(name = "ct", precision = 6, scale = 2)
    private BigDecimal ct;
    @Column(name = "et", precision = 6, scale = 2)
    private BigDecimal et;
    @Column(name = "add_value", precision = 6, scale = 2)
    private BigDecimal addValue;
    @Column(name = "seg_height", precision = 6, scale = 2)
    private BigDecimal segHeight;
    @Column(name = "fit_height", precision = 6, scale = 2)
    private BigDecimal fitHeight;
    @Column(name = "decentration", precision = 6, scale = 2)
    private BigDecimal decentration;

    protected Prescription() {}

    // _____________________________________________________________________________________
    //  Prescription
    // ______________________________________________________________________________________

    /**
     * Makes sure prescription always knows its eye and its type.
     * Returns null when not known.
     * @param pEye which eye the measurements belong to
     * @param pType the lens type
     * @return a blank prescription for that eye, or null when no value
     */
    public static Prescription forEye(EyeSide pEye, PrescriptionType pType) {
        if (pEye == null || pType == null) {
            System.out.println("Prescription skipped: a measurement for an eye side and a type are both required.");
            return null;
        }

        Prescription prescription = new Prescription();
        prescription.eye = pEye;
        prescription.type = pType;
        return prescription;
    }

    // _____________________________________________________________________________________
    //  Filling measurements
    // ______________________________________________________________________________________

    /**
     * Stores the main power of lens.
     * @param pSph the sphere value
     * @param pCyl the cylinder value, or null when empty
     * @param pAxis the axis angle, or null when empty
     */
    public void assignSphericalPower(BigDecimal pSph, BigDecimal pCyl, BigDecimal pAxis) {
        this.sph = pSph;
        this.cyl = pCyl;
        this.axis = pAxis;
    }

    /**
     * Stores how far apart the pupils are.
     * @param pPd the distance used for distance vision
     * @param pNearPd the distance used for reading, or null when empty
     */
    public void assignPupillaryData(BigDecimal pPd, BigDecimal pNearPd) {
        this.pd = pPd;
        this.nearPd = pNearPd;
    }

    /**
     * Stores the prism used to correct a squint.
     * @param pPrism the prism value, or null when empty
     * @param pCt the centre thickness, or null when empty
     * @param pEt the edge thickness, or null when empty
     */
    public void assignPrismData(BigDecimal pPrism, BigDecimal pCt, BigDecimal pEt) {
        this.prism = pPrism;
        this.ct = pCt;
        this.et = pEt;
    }

    /**
     * Only a bifocal or progressive lens uses these two values.
     * @param pAddValue the reading addition, or null
     * @param pSegHeight the segment height, or null
     */
    public void assignReadingAddition(BigDecimal pAddValue, BigDecimal pSegHeight) {
        this.addValue = pAddValue;
        this.segHeight = pSegHeight;
    }

    /**
     * Only a progressive lens uses these two values.
     * @param pFitHeight the fitting height, or null
     * @param pDecentration how far the optical centre is moved, or null
     */
    public void assignFittingData(BigDecimal pFitHeight, BigDecimal pDecentration) {
        this.fitHeight = pFitHeight;
        this.decentration = pDecentration;
    }

    /**
     * Called by OrderItem.attach(...) once OrderItem is on main.
     * @param pOrderItemId the id of the line these measurements belong to
     */
    void assignTo(Long pOrderItemId) {
        this.orderItemId = pOrderItemId;
    }

    // _____________________________________________________________________________________
    // Rules
    // ______________________________________________________________________________________

    /**
     * Eye measurements validation.
     * @return an empty text when the eye is ready, or an error msg.
     */
    public String check() {
        if (sph == null) {
            return describe() + ": Sphere (SPH) is required.";
        }
        if (sph.compareTo(SPH_MIN) < 0 || sph.compareTo(SPH_MAX) > 0) {
            return describe() + ": Sphere (SPH) must be between " + SPH_MIN + " and " + SPH_MAX + ".";
        }
        if (cyl != null && (cyl.compareTo(CYL_MIN) < 0 || cyl.compareTo(CYL_MAX) > 0)) {
            return describe() + ": Cylinder (CYL) must be between " + CYL_MIN + " and " + CYL_MAX + ".";
        }
        if (cyl != null && axis == null) {
            return describe() + ": Axis is required when Cylinder is set.";
        }
        if (axis != null && (axis.compareTo(AXIS_MIN) < 0 || axis.compareTo(AXIS_MAX) > 0)) {
            return describe() + ": Axis must be between " + AXIS_MIN + " and " + AXIS_MAX + ".";
        }
        if (pd != null && (pd.compareTo(PD_MIN) < 0 || pd.compareTo(PD_MAX) > 0)) {
            return describe() + ": PD must be between " + PD_MIN + " and " + PD_MAX + ".";
        }

        if (type == PrescriptionType.BIFOCAL || type == PrescriptionType.PROGRESSIVE) {
            if (addValue == null) {
                return describe() + ": Reading addition (ADD) is required.";
            }
            if (addValue.compareTo(ADD_MIN) < 0 || addValue.compareTo(ADD_MAX) > 0) {
                return describe() + ": Reading addition (ADD) must be between " + ADD_MIN + " and " + ADD_MAX + ".";
            }
            if (segHeight == null) {
                return describe() + ": Segment height is required.";
            }
        }

        if (type == PrescriptionType.PROGRESSIVE && fitHeight == null) {
            return describe() + ": Fitting height is required.";
        }
        return "";
    }

    /**
     * Checks if reading addition applies.
     * @return true when a reading addition was given
     */
    public boolean hasReadingAddition() {
        return addValue != null;
    }

    /**
     * Checks if segment height applies.
     * @return true when a segment height was given
     */
    public boolean hasSegmentHeight() {
        return segHeight != null;
    }

    /**
     * Displays the eye and lens type in an error. 
     * @return like "Right (R) PROGRESSIVE"
     */
    private String describe() {
        String side = (eye == null) ? "Eye" : eye.getLabel();
        String kind = (type == null) ? "" : " " + type;
        return side + kind;
    }

    // _____________________________________________________________________________________
    // Reading
    // ______________________________________________________________________________________

    /**
     * Gives the database id.
     * @return the id, or null before the row is saved
     */
    public Long getId() { return id; }

    /**
     * Get the order line id
     * @return the owning order line id
     */
    public Long getOrderItemId() { return orderItemId; }

    /**
     * Get lens type
     * @return the prescription type
     */
    public PrescriptionType getType() { return type; }

    /**
     * Get eye side
     * @return the eye side
     */
    public EyeSide getEye() { return eye; }

    public BigDecimal getSph() { return sph; }
    public BigDecimal getCyl() { return cyl; }
    public BigDecimal getAxis() { return axis; }
    public BigDecimal getPd() { return pd; }
    public BigDecimal getPrism() { return prism; }
    public BigDecimal getNearPd() { return nearPd; }
    public BigDecimal getCt() { return ct; }
    public BigDecimal getEt() { return et; }
    public BigDecimal getAddValue() { return addValue; }
    public BigDecimal getSegHeight() { return segHeight; }
    public BigDecimal getFitHeight() { return fitHeight; }
    public BigDecimal getDecentration() { return decentration; }
}
