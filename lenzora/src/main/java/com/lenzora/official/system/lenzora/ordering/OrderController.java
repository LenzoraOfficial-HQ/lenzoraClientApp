package com.lenzora.official.system.lenzora.ordering;

import com.lenzora.official.system.lenzora.catalogue.domain.Frame;
import com.lenzora.official.system.lenzora.catalogue.domain.LensOption;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.FrameRepository;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.LensOptionRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** The web layer for placing an order. */
@Controller
public class OrderController {

    private final OrderService orderService;

    /** Reads the frames the customer can choose from. */
    private final FrameRepository frameRepository;

    /** Reads the lens options shown as checkboxes. */
    private final LensOptionRepository lensOptionRepository;

    public OrderController(OrderService pOrderService, FrameRepository pFrameRepository, LensOptionRepository pLensOptionRepository) {
        this.orderService = pOrderService;
        this.frameRepository = pFrameRepository;
        this.lensOptionRepository = pLensOptionRepository;
    }

    /**
     * Shows the empty Place Order page.
     * @param model the data handed to place-order.html
     * @return the name of the page to show
     */
    @GetMapping("/place-order")
    public String showForm(Model model) {
        if (!model.containsAttribute("orderForm")) {
            model.addAttribute("orderForm", new PlaceOrderForm());
        }
        addDropDowns(model);
        return "place-order";
    }

    /**
     * Handles the "Save Order" button.
     *
     * @param form everything the user typed
     * @param errors the quick browser-side checks, filled in by Spring
     * @param model the data handed back to the page
     * @param redirect what to show after a successful save
     * @return a redirect after a save, or the page again when something is wrong
     */
    @PostMapping("/orders")
    public String create(@Valid @ModelAttribute("orderForm") PlaceOrderForm form, BindingResult errors, Model model, RedirectAttributes redirect) {

        if (errors.hasErrors()) {
            addDropDowns(model);
            return "place-order";
        }

        OrderResult result = orderService.createOrder(form);

        if (result.isSaved()) {
            String message = "Order " + result.getOrder().getOrderNumber() + " created successfully.";
            System.out.println(message);
            redirect.addFlashAttribute("successMessage", message);
            return "redirect:/place-order";
        }

        // A rule said not to keep what the user typed and show the message.
        System.out.println("Place order failed: " + result.getMessage());
        model.addAttribute("errorMessage", result.getMessage());
        addDropDowns(model);
        return "place-order";
    }

    /**
     * Sends every list the page needs, so the template never hard-codes one.
     * @param model the data handed to place-order.html
     */
    private void addDropDowns(Model model) {
        model.addAttribute("frames", orderableFrames());
        model.addAttribute("lensOptions", activeLensOptions());
        model.addAttribute("jobTypes", Order.JobType.values());
        model.addAttribute("lensIndexes", Arrays.asList("1.5", "1.59Poly", "1.6", "1.67", "1.74"));
        model.addAttribute("lensTypes", OrderItem.LensType.values());
        model.addAttribute("coatings", OrderItem.CoatingType.values());
        model.addAttribute("tints", OrderItem.TintType.values());
        model.addAttribute("materials", OrderItem.FrameMaterial.values());
        model.addAttribute("rimTypes", OrderItem.FrameRimType.values());
        model.addAttribute("shapes", OrderItem.FrameShape.values());
    }

    /**
     * Gives the frames the catalogue still offers and the workshop can supply.
     * @return the frames that may be ordered, sorted by brand and model
     */
    private List<Frame> orderableFrames() {
        List<Frame> wanted = new ArrayList<Frame>();
        for (Frame frame : frameRepository.findAll()) {
            if (Boolean.TRUE.equals(frame.getActive()) && Boolean.TRUE.equals(frame.getAvailable())) {
                wanted.add(frame);
            }
        }
        Collections.sort(wanted, new Comparator<Frame>() {
            @Override
            public int compare(Frame pLeft, Frame pRight) {
                int byBrand = pLeft.getBrand().compareToIgnoreCase(pRight.getBrand());
                if (byBrand != 0) {
                    return byBrand;
                }
                return pLeft.getModel().compareToIgnoreCase(pRight.getModel());
            }
        });
        return wanted;
    }

    /**
     * Gives the lens options that are still offered.
     * @return the active lens options, sorted by label
     */
    private List<LensOption> activeLensOptions() {
        List<LensOption> wanted = new ArrayList<LensOption>();
        for (LensOption option : lensOptionRepository.findAll()) {
            if (Boolean.TRUE.equals(option.getActive())) {
                wanted.add(option);
            }
        }
        Collections.sort(wanted, new Comparator<LensOption>() {
            @Override
            public int compare(LensOption pLeft, LensOption pRight) {
                return pLeft.getLabel().compareToIgnoreCase(pRight.getLabel());
            }
        });
        return wanted;
    }
}
