package com.lenzora.official.system.lenzora.ordering;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Everything the Place Order page sends back.
 */
@Getter
@Setter
public class PlaceOrderForm {

    @NotNull(message = "Please choose the job type")
    private Order.JobType jobType;
    @NotBlank(message = "Patient reference is required")
    private String patientReference;
    @NotBlank(message = "Patient name is required")
    private String patientName;
    @NotBlank(message = "Patient contact number is required")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Use 9 to 15 digits, with an optional leading +") 
    private String patientContact;
    @NotBlank(message = "Hospital or clinic name is required")
    private String hospitalName;
    private String hospitalReference;
    private String notes;
    private String remarks;
    private String serialNo;

    private String jobNo;
    @NotBlank(message = "The branch the job is sent from is required")
    private String fromLocation;
    private Long replacesOrderItemId;
    @DecimalMin(value = "40.00", message = "PD must be between 40 and 80 mm")
    @DecimalMax(value = "80.00", message = "PD must be between 40 and 80 mm")
    private BigDecimal pd;
   
    @Valid
    private EyeForm right = new EyeForm();
    @Valid
    private EyeForm left = new EyeForm();

    private String lensIndex;
    private OrderItem.LensType lensType;
    private List<String> lensOptionCodes = new ArrayList<String>();
    private OrderItem.CoatingType coating;
    private OrderItem.TintType tint;


    private Long frameId;
    private String ownFrameDescription;
    private BigDecimal frameASize;
    private BigDecimal frameBSize;
    private BigDecimal frameDSize;
    private BigDecimal frameDiameter;
    private String frameMaterial;
    private String frameRimType;
    private String frameShape;
    private BigDecimal unitPrice;
    private Integer quantity;

    @Getter
    @Setter
    public static class EyeForm {

        @DecimalMin(value = "-20.00", message = "must be between -20.00 and +20.00")
        @DecimalMax(value = "20.00", message = "must be between -20.00 and +20.00")
        private BigDecimal sph;
        @DecimalMin(value = "-20.00", message = "must be between -20.00 and +20.00")
        @DecimalMax(value = "20.00", message = "must be between -20.00 and +20.00")
        private BigDecimal cyl;
        @DecimalMin(value = "1.00", message = "must be between 1 and 180")
        @DecimalMax(value = "180.00", message = "must be between 1 and 180")
        private BigDecimal axis;
        @DecimalMin(value = "0.50", message = "must be between 0.50 and 4.00")
        @DecimalMax(value = "4.00", message = "must be between 0.50 and 4.00")
     
        private BigDecimal addValue;
        private BigDecimal fitHeight;
        private BigDecimal segHeight;
        private BigDecimal prism;
        private BigDecimal npd;
        private BigDecimal ct;
        private BigDecimal et;
        private BigDecimal decentration;

        public boolean isEmpty() {
            return sph == null && cyl == null && axis == null && addValue == null && fitHeight == null && segHeight == null && prism == null && npd == null && ct == null && et == null && decentration == null;
        }
    }
}
