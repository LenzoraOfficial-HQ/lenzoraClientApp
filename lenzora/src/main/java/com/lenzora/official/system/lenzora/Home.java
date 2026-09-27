
package com.lenzora.official.system.lenzora;

import com.lenzora.official.system.lenzora.ordering.PlaceOrderForm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class Home {

    /** Google client id, empty when Google sign-in is not configured. */
    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    // Dashboard
    @GetMapping("/")
    public String showHome(Model model) {

        model.addAttribute("Name", "Lenzora");
        model.addAttribute("welcome", "Welcome to platform");

        return "index";
    }

    // Login page
    @GetMapping("/login")
    public String showLogin(Model model) {

        model.addAttribute("googleLoginEnabled",
                googleClientId != null && !googleClientId.isBlank());

        return "login";
    }

    // Place Order page. The drop-down and radio lists arrive with the order module.
    @GetMapping("/place-order")
    public String showPlaceOrder(Model model) {

        model.addAttribute("orderForm", new PlaceOrderForm());

        model.addAttribute("frames", List.of());
        model.addAttribute("lensOptions", List.of());
        model.addAttribute("jobTypes", List.of());
        model.addAttribute("lensIndexes", List.of());
        model.addAttribute("lensTypes", List.of());
        model.addAttribute("coatings", List.of());
        model.addAttribute("tints", List.of());
        model.addAttribute("materials", List.of());
        model.addAttribute("rimTypes", List.of());
        model.addAttribute("shapes", List.of());

        return "place-order";
    }

    @GetMapping("/users")
    public String showUsers(){
        return "users";
    }

    @GetMapping("/reports")
    public String showReports(){
        return "reports";
    }

    @GetMapping("/settings")
    public String showSettings(){
        return "settings";
    }
}