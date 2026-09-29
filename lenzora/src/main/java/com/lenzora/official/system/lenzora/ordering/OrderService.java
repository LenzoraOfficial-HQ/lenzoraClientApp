package com.lenzora.official.system.lenzora.ordering;

import com.lenzora.official.system.lenzora.catalogue.domain.Frame;
import com.lenzora.official.system.lenzora.catalogue.domain.LensOption;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.FrameRepository;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.LensOptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Order placing and saving. */
@Service
@Transactional
public class OrderService {

    /** Prints the date part of an order number,like 20260918. */
    private static final DateTimeFormatter NUMBER_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    /** The materials order_item.frame_material accepts, it is a MySQL ENUM. */
    private static final List<String> ORDER_MATERIALS = Arrays.asList("METAL", "PLASTIC", "WOOD");

    /** Reads and writes saved orders. */
    private final OrderRepository orders;

    /** Saves the lines of an order.*/
    private final OrderItemRepository items;

    /** Reads the frames of the catalogue. */
    private final FrameRepository frames;

    /** Reads the lens options of the catalogue. */
    private final LensOptionRepository lensOptions;

    /**
     * The four repositories, injected when the application starts.
     * @param orders reads and writes saved orders
     * @param items  saves the lines of an order
     * @param frames reads the frames of the catalogue
     * @param lensOptions reads the lens options of the catalogue
     */
    public OrderService(OrderRepository orders, OrderItemRepository items, FrameRepository frames, LensOptionRepository lensOptions) {
        this.orders = orders;
        this.items = items;
        this.frames = frames;
        this.lensOptions = lensOptions;
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
        String frameMaterial = null;
        String ownFrameDescription = null;

        if (form.getJobType() == Order.JobType.LENS_REPLACEMENT) {
            ownFrameDescription = form.getOwnFrameDescription();
            if (ownFrameDescription == null || ownFrameDescription.trim().isEmpty()) {
                return failed("Please describe the frame the patient brought in.");
            }
        } else {
            CatalogueFrame frame = findCatalogueFrame(form.getFrameId());

            if (frame == null) {
                return failed("Please choose an available frame from the catalogue.");
            }

            frameId = frame.getId();
            frameName = frame.getName();
            framePrice = frame.getPrice();
            frameMaterial = frame.getMaterial();
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

        // The material radios are empty, so the catalogue frame supplies it.
        String material = form.getFrameMaterial();
        if (material == null || material.trim().isEmpty()) {
            material = frameMaterial;
        }
        item.describeFrame(form.getFrameASize(), form.getFrameBSize(), form.getFrameDSize(), form.getFrameDiameter(), orderMaterial(material), form.getFrameRimType(), form.getFrameShape());

        if (form.getUnitPrice() != null) {
            item.overrideUnitPrice(form.getUnitPrice());
        }

        String optionsProblem = addLensOptions(item, form.getLensOptionCodes());
        if (!optionsProblem.isEmpty()) {
            return failed(optionsProblem);
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

    /**
     * Reads the chosen frame from the catalogue.
     * @param pFrameId the frame the user picked, may be null
     * @return the frame details, or null when it cannot be ordered any more
     */
    private CatalogueFrame findCatalogueFrame(Long pFrameId) {
        if (pFrameId == null) {
            return null;
        }
        Frame frame = frames.findById(pFrameId).orElse(null);
        if (frame == null || !Boolean.TRUE.equals(frame.getActive()) || !Boolean.TRUE.equals(frame.getAvailable())) {
            return null;
        }
        BigDecimal price = (frame.getPrice() == null ? BigDecimal.ZERO : frame.getPrice());
        return new CatalogueFrame(frame.getFrameId(), describeFrame(frame), price, frame.getMaterial());
    }

    /**
     * Builds the wording kept on the order line for a catalogue frame.
     * @param pFrame the catalogue frame
     * @return the brand, model and size, for example Guess GU50051 (51-16)
     */
    private String describeFrame(Frame pFrame) {
        String brand = (pFrame.getBrand() == null ? "" : pFrame.getBrand());
        String model = (pFrame.getModel() == null ? "" : pFrame.getModel());
        String size = (pFrame.getSize() == null ? "" : "(" + pFrame.getSize() + ")");
        return (brand + " " + model + " " + size).trim();
    }

    /**
     * Turns the ticked lens option codes into catalogue rows.
     * @param pItem the order line the options belong to
     * @param pCodes the codes that were ticked, may be null
     * @return an empty text when every option was found, otherwise the first problem
     */
    private String addLensOptions(OrderItem pItem, List<String> pCodes) {
        if (pCodes == null || pCodes.isEmpty()) {
            return "";
        }
        Map<String, Long> offered = new HashMap<String, Long>();
        for (LensOption option : lensOptions.findAll()) {
            if (Boolean.TRUE.equals(option.getActive())) {
                offered.put(option.getLensCode(), option.getLensOptionId());
            }
        }
        List<Long> ids = new ArrayList<Long>();
        for (String code : pCodes) {
            if (code == null || code.trim().isEmpty()) {
                continue;
            }
            Long id = offered.get(code);
            if (id == null) {
                return "The lens option " + code + " is not offered any more.";
            }
            ids.add(id);
        }
        pItem.selectLensOptions(ids);
        return "";
    }

    /**
     * Narrows the free text catalogue material to the values the order line
     * accepts, so an unusual word cannot stop the order from saving.
     * @param pMaterial the material written on the frame, may be null
     * @return the material to store, or null when it does not fit
     */
    private String orderMaterial(String pMaterial) {
        if (pMaterial == null) {
            return null;
        }
        String wanted = pMaterial.trim().toUpperCase();
        for (String allowed : ORDER_MATERIALS) {
            if (allowed.equals(wanted)) {
                return allowed;
            }
        }
        System.out.println("The catalogue material " + pMaterial + " is not one of " + ORDER_MATERIALS + ", so the order line keeps it empty.");
        return null;
    }

    private static final class CatalogueFrame {

        private final Long id;
        private final String name;
        private final BigDecimal price;
        private final String material;

        private CatalogueFrame(Long pId, String pName, BigDecimal pPrice, String pMaterial) {
            this.id = pId;
            this.name = pName;
            this.price = pPrice;
            this.material = pMaterial;
        }

        private Long getId() { return id; }
        private String getName() { return name; }
        private BigDecimal getPrice() { return price; }
        private String getMaterial() { return material; }
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
