package com.lenzora.official.system.lenzora.ordering;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;

/** The web layer for placing an order. */
@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService pOrderService) {
        this.orderService = pOrderService;
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
        model.addAttribute("frames", Collections.emptyList());
        model.addAttribute("lensOptions", Collections.emptyList());
        model.addAttribute("jobTypes", Order.JobType.values());
        model.addAttribute("lensIndexes", Arrays.asList("1.5", "1.59Poly", "1.6", "1.67", "1.74"));
        model.addAttribute("lensTypes", OrderItem.LensType.values());
        model.addAttribute("coatings", OrderItem.CoatingType.values());
        model.addAttribute("tints", OrderItem.TintType.values());
        model.addAttribute("materials", Collections.emptyList());
        model.addAttribute("rimTypes", Collections.emptyList());
        model.addAttribute("shapes", Collections.emptyList());
    }
}
