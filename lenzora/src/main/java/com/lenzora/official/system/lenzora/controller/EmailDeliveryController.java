package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.EmailDelivery;
import com.lenzora.official.system.lenzora.service.EmailDeliveryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/email-delivery")
public class EmailDeliveryController {

    private final EmailDeliveryService emailDeliveryService;

    public EmailDeliveryController(
            EmailDeliveryService emailDeliveryService) {
        this.emailDeliveryService = emailDeliveryService;
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
}
