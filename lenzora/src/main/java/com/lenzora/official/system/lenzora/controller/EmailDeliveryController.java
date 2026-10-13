package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.EmailDelivery;
import com.lenzora.official.system.lenzora.service.EmailDeliveryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.lenzora.official.system.lenzora.ordering.Order;
import com.lenzora.official.system.lenzora.service.EmailDeliveryOrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lenzora.official.system.lenzora.ordering.OrderItem;
import org.springframework.ui.Model;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/email-delivery")
public class EmailDeliveryController {

    private final EmailDeliveryService emailDeliveryService;
    private final EmailDeliveryOrderService emailDeliveryOrderService;

    public EmailDeliveryController(
            EmailDeliveryService emailDeliveryService,
            EmailDeliveryOrderService emailDeliveryOrderService) {

        this.emailDeliveryService = emailDeliveryService;
        this.emailDeliveryOrderService = emailDeliveryOrderService;
    }

    @GetMapping
    public String showEmailDeliveries(Model model) {

        List<EmailDelivery> emailDeliveries = emailDeliveryService.getAllEmailDeliveries();

        model.addAttribute("emailDeliveries", emailDeliveries);

        model.addAttribute("totalCount", emailDeliveries.size());

        model.addAttribute("pendingCount", emailDeliveries.stream().filter(email -> "PENDING".equals(email.getStatus())).count());

        model.addAttribute("sentCount", emailDeliveries.stream().filter(email -> "SENT".equals(email.getStatus())).count());

        model.addAttribute("failedCount", emailDeliveries.stream().filter(email -> "FAILED".equals(email.getStatus())).count());

        return "admin/email-delivery";
    }

    @GetMapping("/create")
    public String showCreateEmailForm(Model model) {
        model.addAttribute("emailDelivery", new EmailDelivery());
        return "admin/email-delivery-form";
    }

    @PostMapping("/create")
    public String createEmailDelivery(
            @ModelAttribute("emailDelivery") EmailDelivery emailDelivery) {

        emailDelivery.setEmailId(null);
        emailDelivery.setStatus("PENDING");
        emailDelivery.setAttemptCount(0);
        emailDelivery.setSentAt(null);
        emailDelivery.setLastError(null);

        if ("SUPPLIER".equals(emailDelivery.getRecipientType())) {
            emailDelivery.setContainsPatientData(false);
        }

        emailDeliveryService.saveEmailDelivery(emailDelivery);

        return "redirect:/admin/email-delivery";
    }

    @PostMapping("/send/{emailId}")
    public String sendEmail(
            @org.springframework.web.bind.annotation.PathVariable Long emailId,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        try {
            boolean sent = emailDeliveryService.sendEmail(emailId);

            if (sent) {
                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Email sent successfully!"
                );
            } else {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Email was not sent. It may already have been sent."
                );
            }

        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Unable to send email. Please check the delivery record."
            );
        }

        return "redirect:/admin/email-delivery";
    }

    @GetMapping("/orders")
    public String showOrderSelection(Model model) {

        List<Order> orders = emailDeliveryOrderService.getAllOrders();

        model.addAttribute("orders", orders);

        return "admin/email-order-selection";
    }

    @GetMapping("/orders/{id}/generate")
    public String generateEmailPreview(
            @PathVariable("id") Long orderId,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Order order = emailDeliveryOrderService.getOrderById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            List<OrderItem> items =
                    emailDeliveryOrderService.getOrderItems(orderId);

            model.addAttribute("order", order);
            model.addAttribute("orderItems", items);
            model.addAttribute("orderId", orderId);

            return "admin/email-order-preview";

        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Could not load the order preview."
            );

            return "redirect:/admin/email-delivery/orders";
        }
    }

    @PostMapping("/orders/{id}/generate-emails")
    public String generateSupplierEmails(
            @PathVariable("id") Long orderId,
            RedirectAttributes redirectAttributes) {

        try {
            int generated =
                    emailDeliveryOrderService.generateSupplierEmails(orderId);

            if (generated > 0) {
                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        generated + " supplier email(s) generated successfully!"
                );
            } else {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "No new emails generated. Check supplier email addresses or existing records."
                );
            }

        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Email generation failed. Please check the application logs."
            );
        }

        return "redirect:/admin/email-delivery";
    }
}

