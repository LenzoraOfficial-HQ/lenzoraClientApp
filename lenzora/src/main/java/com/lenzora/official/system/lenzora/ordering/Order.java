package com.lenzora.official.system.lenzora.ordering;

import com.lenzora.official.system.lenzora.shared.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * One customer order, built from the Place Order screen.
 */
@Entity
@Table(name = "app_order")
public class Order extends AuditableEntity {

    public enum JobType {

        NEW_SPECTACLE("New Spectacle"),
        FRAME_REPLACEMENT("Frame Replacement"),
        LENS_REPLACEMENT("Lens Replacement");

        private final String label;

        JobType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum OrderStatus {

        SENT_FOR_REVIEW("SENT FOR REVIEW"),
        REVIEWED("REVIEWED"),
        PLACED("PLACED"),
        DELIVERED("DELIVERED"),
        COMPLETED("COMPLETED"),
        CANCELLED("CANCELLED");

        private final String code;

        OrderStatus(String code) {
            this.code = code;
        }
        public String getLabel() {
            return code;
        }
        public String getCode() {
            return code;
        }

        public boolean isEditable() {
            return this == SENT_FOR_REVIEW || this == REVIEWED;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;
    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false, length = 20)
    private JobType jobType = JobType.NEW_SPECTACLE;
    @Convert(converter = OrderStatusConverter.class)
    @Column(name = "order_status", nullable = false, length = 20)
    private OrderStatus status;
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;
    @Column(name = "patient_reference", nullable = false, length = 50)
    private String patientReference;
    @Column(name = "patient_name", nullable = false, length = 100)
    private String patientName;
    @Column(name = "patient_contact", nullable = false, length = 20)
    private String patientContact;
    @Column(name = "hospital_name", nullable = false, length = 100)
    private String hospitalName;
    @Column(name = "hospital_reference")
    private String hospitalReference;
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;
    @Column(name = "serial_no")
    private String serialNo;
    @Column(name = "job_no")
    private String jobNo;
    @Column(name = "from_location", nullable = false, length = 50)
    private String fromLocation;
    @Column(name = "orthoptist_id")
    private Long orthoptistId;
    @Transient
    private List<OrderItem> items = new ArrayList<OrderItem>();

    protected Order() {}

    /**
     * Creates an order.
     *
     * @param patientReference the clinic's reference for the patient
     * @param jobType what the patient asked for
     * @param orderDate the day the order is being placed
     * @param orderNumber the number to print on the job sheet
     * @return a fresh order in SENT_FOR_REVIEW state
     */
    public static Order place(String patientReference, JobType jobType, LocalDate orderDate, String orderNumber) {
        if (patientReference == null || patientReference.trim().isEmpty()) {
            System.out.println("Order not created: Patient reference is required.");
            return null;
        }
        if (jobType == null) {
            System.out.println("Order not created: The job type is required.");
            return null;
        }
        if (orderDate == null) {
            System.out.println("Order not created: Order date is required.");
            return null;
        }
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            System.out.println("Order not created: Order number is required.");
            return null;
        }

        Order order = new Order();
        order.patientReference = patientReference.trim();
        order.jobType = jobType;
        order.orderDate = orderDate;
        order.orderNumber = orderNumber;
        order.status = OrderStatus.SENT_FOR_REVIEW;
        order.total = BigDecimal.ZERO;
        return order;
    }

    public OrderItem addItem(Long pFrameId, String pFrameName, BigDecimal pFramePrice, String pOwnFrameDescription, int pQuantity, OrderItem.LensType pLensType, String pLensIndex, OrderItem.CoatingType pCoating, OrderItem.TintType pTint) {
        if (!canChange()) {
            return null;
        }
        boolean described = pOwnFrameDescription != null && !pOwnFrameDescription.trim().isEmpty();
        if (pFrameId == null && !described) {
            System.out.println("Order line not added: either choose a catalogue frame " + "or describe the frame the patient brought in.");
            return null;
        }
        OrderItem item = new OrderItem(this.getId(), pFrameId, pFrameName, pFramePrice, pOwnFrameDescription, pQuantity, pLensType, pLensIndex, pCoating, pTint);
        items.add(item);
        recalculateTotal();
        return item;
    }

    /**Patient description.
     * @param patientName patient's name
     * @param patientContact patient's contact number
     */
    public void describePatient(String patientName, String patientContact) {
        if (!canChange()) {
            return;
        }
        this.patientName = trimToNull(patientName);
        this.patientContact = trimToNull(patientContact);
    }

    /** Stores where the order came from.
     * @param hospitalName hospital or clinic that sent the order
     * @param hospitalReference that hospital's own file number
     */
    public void recordHospital(String hospitalName, String hospitalReference) {
        if (!canChange()) {
            return;
        }
        this.hospitalName = trimToNull(hospitalName);
        this.hospitalReference = trimToNull(hospitalReference);
    }

    /**
     * Stores reference numbers.
     *
     * @param serialNo the clinic's serial number
     * @param jobNo the workshop's job number
     * @param fromLocation the branch the order came from
     */
    public void recordExternalRefs(String serialNo, String jobNo, String fromLocation) {
        if (!canChange()) {
            return;
        }
        this.serialNo = trimToNull(serialNo);
        this.jobNo = trimToNull(jobNo);
        this.fromLocation = trimToNull(fromLocation);
    }

    /**
     * Stores the two free text boxes.
     * @param notes notes
     * @param remarks remarks 
     */
    public void annotate(String notes, String remarks) {
        if (!canChange()) {
            return;
        }
        this.notes = trimToNull(notes);
        this.remarks = trimToNull(remarks);
    }

    /**
     * Checks the whole order
     * @return an empty text when the order is ready or error msg.
     */
    public String check() {
        if (patientName == null) {
            return "Patient name is required.";
        }
        if (patientContact == null) {
            return "Patient contact number is required.";
        }
        if (hospitalName == null) {
            return "Hospital or clinic name is required.";
        }
        if (fromLocation == null) {
            return "The branch the job is sent from is required.";
        }
        if (items.isEmpty()) {
            return "An order must contain at least one frame/lens line.";
        }
        for (OrderItem item : items) {
            String problem = item.check(jobType == null ? null : jobType.name());
            if (!problem.isEmpty()) {
                return problem;
            }
        }
        return "";
    }

    /** Makes the total and the status official, once check() has passed. */
    public void submit() {
        recalculateTotal();
        this.status = OrderStatus.SENT_FOR_REVIEW;
    }

    /**
     * The total from sum of lines.
     * @return the new total
     */
    public BigDecimal recalculateTotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (OrderItem item : items) {
            BigDecimal line = item.getLineTotal();
            if (line != null) {
                sum = sum.add(line);
            }
        }
        this.total = sum;
        return this.total;
    }

    /**
     * Tells whether the order may still be changed or not.
     * @return true when the status still allows changes
     */
    private boolean canChange() {
        if (status == null || status.isEditable()) {
            return true;
        }
        System.out.println("Order " + orderNumber + " is " + status.getLabel()
                + " and can no longer be changed.");
        return false;
    }

    /**
     * Trims the text so column is empty rather than holding a space.
     * @param text the text typed by the user
     * @return the trimmed text, or null 
     */
    private static String trimToNull(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        return text.trim();
    }


    public Long getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public JobType getJobType() { return jobType; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotal() { return total; }
    public LocalDate getOrderDate() { return orderDate; }
    public String getPatientReference() { return patientReference; }
    public String getPatientName() { return patientName; }
    public String getPatientContact() { return patientContact; }
    public String getHospitalName() { return hospitalName; }
    public String getHospitalReference() { return hospitalReference; }
    public String getNotes() { return notes; }
    public String getRemarks() { return remarks; }
    public String getSerialNo() { return serialNo; }
    public String getJobNo() { return jobNo; }
    public String getFromLocation() { return fromLocation; }
    public Long getOrthoptistId() { return orthoptistId; }
    public List<OrderItem> getItems() {return Collections.unmodifiableList(items);}
}
