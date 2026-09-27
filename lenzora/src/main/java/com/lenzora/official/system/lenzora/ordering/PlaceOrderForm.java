package com.lenzora.official.system.lenzora.ordering;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the values bound to the place-order form.
 *
 * <p>Every drop-down and radio on the page is filled from lists the controller supplies,
 * so this class only carries what the user typed.</p>
 *
 * @author Lenzora
 * @version 1.0
 */
@Getter
@Setter
public class PlaceOrderForm {

    // Section 1 - job type
    private String jobType;

    // Section 2 - patient and hospital reference
    private String patientReference;
    private String patientName;
    private String patientContact;
    private String hospitalName;
    private String hospitalReference;
    private String fromLocation;
    private String serialNo;
    private String jobNo;

    // Section 3 - frame, either from the catalogue or the customer's own
    private Long frameId;
    private String ownFrameDescription;
    private String frameMaterial;
    private String frameRimType;
    private String frameShape;
    private BigDecimal frameASize;
    private BigDecimal frameBSize;
    private BigDecimal frameDSize;
    private BigDecimal frameDiameter;

    // Section 4 - one prescription block per eye
    private EyeForm right = new EyeForm();
    private EyeForm left = new EyeForm();

    // Section 5 - lens choices
    private String lensType;
    private String lensIndex;
    private String coating;
    private String tint;
    private List<String> lensOptionCodes = new ArrayList<String>();
    private BigDecimal pd;

    // Section 6 - pricing and notes
    private Integer quantity;
    private BigDecimal unitPrice;
    private String notes;
    private String remarks;

    /**
     * The prescription values for one eye.
     *
     * @author Lenzora
     * @version 1.0
     */
    @Getter
    @Setter
    public static class EyeForm {

        private BigDecimal sph;
        private BigDecimal cyl;
        private BigDecimal axis;
        private BigDecimal prism;
        private BigDecimal addValue;
        private BigDecimal npd;
        private BigDecimal segHeight;
        private BigDecimal fitHeight;
        private BigDecimal et;
        private BigDecimal ct;
        private BigDecimal decentration;
    }
}
