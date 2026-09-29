package com.lenzora.official.system.lenzora.ordering;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/** Order placing & saving. */
@Service
@Transactional
public class OrderService {

    /** Prints the date part of an order number,like 20260918. */
    private static final DateTimeFormatter NUMBER_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    /** Reads and writes saved orders. */
    private final OrderRepository orders;

    /** Saves the lines of an order.*/
    private final OrderItemRepository items;

    /**
     * Both repositories read together in when the application starts.
     * @param orders reads and writes saved orders
     * @param items  saves the lines of an order
     */
    public OrderService(OrderRepository orders, OrderItemRepository items) {
        this.orders = orders;
        this.items = items;
    }

    /**
     * Check inputs in order form and saves it and returns the saved order.
     * @param form everything the user typed on the Place Order screen
     * @return a saved order, or a message saying which step said no
     */
    public OrderResult createOrder(PlaceOrderForm form) {

        if (form.getJobType() == null) {
            return failed("Please choose the job type.");
        }

        Long frameId = null;
        String frameName = null;
        BigDecimal framePrice = null;
        String ownFrameDescription = null;

        if (form.getJobType() == Order.JobType.LENS_REPLACEMENT) {
            ownFrameDescription = form.getOwnFrameDescription();
            if (ownFrameDescription == null || ownFrameDescription.trim().isEmpty()) {
                return failed("Please describe the frame the patient brought in.");
            }
        } else {
            CatalogueFrame frame = findCatalogueFrame(form.getFrameId());

            if (frame == null) {
                return failed("The frame catalogue is not connected yet, so a new spectacle " + "or a frame replacement cannot be saved. Please use a lens " + "replacement in the patient's own frame for now.");
            }

            frameId = frame.getId();
            frameName = frame.getName();
            framePrice = frame.getPrice();
        }

        // create the order and give a number
        Order order = Order.place(form.getPatientReference(), form.getJobType(), LocalDate.now(), nextOrderNumber());
        if (order == null) {
            return failed("Please fill in the patient reference.");
        }

        // copy the patient, hospital and reference details
        order.describePatient(form.getPatientName(), form.getPatientContact());
        order.recordHospital(form.getHospitalName(), form.getHospitalReference());
        order.recordExternalRefs(form.getSerialNo(), form.getJobNo(), form.getFromLocation());
        order.annotate(form.getNotes(), form.getRemarks());

        int quantity = (form.getQuantity() == null || form.getQuantity() < 1) ? 1 : form.getQuantity();

        OrderItem.LensType lensType = (form.getJobType() == Order.JobType.FRAME_REPLACEMENT) ? null: form.getLensType();

        OrderItem item = order.addItem(frameId, frameName, framePrice, ownFrameDescription, quantity, lensType, form.getLensIndex(), form.getCoating(), form.getTint());
        if (item == null) {
            return failed("The order line could not be added.");
        }

        //self reference to indicate this is not a replacement.
        if (form.getReplacesOrderItemId() != null) {
            item.markReplacementOf(form.getReplacesOrderItemId());
        }

        item.describeFrame(form.getFrameASize(), form.getFrameBSize(), form.getFrameDSize(), form.getFrameDiameter(), form.getFrameMaterial(), form.getFrameRimType(), form.getFrameShape());

        if (form.getUnitPrice() != null) {
            item.overrideUnitPrice(form.getUnitPrice());
        }

        //add 2 prescriptions unless for a frame replacement.

        if (form.getJobType() != Order.JobType.FRAME_REPLACEMENT) {
            String problem = addPrescription(item, Prescription.EyeSide.RIGHT, lensType, form.getRight(), form.getPd());
            if (problem.isEmpty()) {
                problem = addPrescription(item, Prescription.EyeSide.LEFT, lensType, form.getLeft(), form.getPd());
            }
            if (!problem.isEmpty()) {
                return failed(problem);
            }
        }

        // check the whole order, then save
        String problem = order.check();
        if (!problem.isEmpty()) {
            return failed(problem);
        }
        order.submit();
        Order saved = orders.save(order);

        for (OrderItem line : order.getItems()) {
            line.assignToOrder(saved.getId());
            items.save(line);
        }

        System.out.println("Order " + saved.getOrderNumber() + " saved.");
        return OrderResult.saved(saved);
    }

    /** Act as catalogue. */
    private CatalogueFrame findCatalogueFrame(Long pFrameId) {
        return null;
    }

    private static final class CatalogueFrame {

        private final Long id;
        private final String name;
        private final BigDecimal price;

        private CatalogueFrame(Long pId, String pName, BigDecimal pPrice) {
            this.id = pId;
            this.name = pName;
            this.price = pPrice;
        }

        private Long getId() { return id; }
        private String getName() { return name; }
        private BigDecimal getPrice() { return price; }
    }

    private OrderResult failed(String message) {
        System.out.println("Order not created: " + message);
        return OrderResult.failed(message);
    }

    private String addPrescription(OrderItem item, Prescription.EyeSide eye, OrderItem.LensType lensType, PlaceOrderForm.EyeForm eyeForm, BigDecimal pd) {

        if (eyeForm == null || eyeForm.isEmpty()) {
            return "";
        }
        if (lensType == null) {
            return "Please choose a lens type before entering eye measurements.";
        }

        Prescription prescription = Prescription.forEye(eye, lensType.prescriptionType());
        if (prescription == null) {
            return "The " + eye.getLabel() + " measurements could not be read.";
        }

        prescription.assignSphericalPower(eyeForm.getSph(), eyeForm.getCyl(), eyeForm.getAxis());
        prescription.assignPupillaryData(pd, eyeForm.getNpd());
        prescription.assignPrismData(eyeForm.getPrism(), eyeForm.getCt(), eyeForm.getEt());
        prescription.assignReadingAddition(eyeForm.getAddValue(), eyeForm.getSegHeight());
        prescription.assignFittingData(eyeForm.getFitHeight(), eyeForm.getDecentration());

        item.attach(prescription);
        return "";
    }

    private String nextOrderNumber() {
        String candidate;
        do {
            candidate = newOrderNumber();
        } while (orders.existsByOrderNumber(candidate));
        return candidate;
    }


    private String newOrderNumber() {
        String date = LocalDate.now().format(NUMBER_DATE);
        String suffix = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 6)
                .toUpperCase();
        return "LZ-" + date + "-" + suffix;
    }
}
