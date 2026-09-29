
package com.lenzora.official.system.lenzora;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    // NOTE: /place-order now lives in OrderController (it needs the lookup data).
    // Keeping it here as well would cause "Ambiguous mapping" at startup.

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