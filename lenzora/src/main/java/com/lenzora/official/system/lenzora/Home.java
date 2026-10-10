
package com.lenzora.official.system.lenzora;

import com.lenzora.official.system.lenzora.config.RoleChecks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {

    /** Google client id, empty when Google sign-in is not configured. */
    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    // Dashboard. The client screens belong to the orthoptists, so an
    // administrator is sent straight to the admin portal instead.
    @GetMapping("/")
    public String showHome(Model model, Authentication authentication) {

        if (RoleChecks.isAdmin(authentication)) {
            return "redirect:/admin";
        }

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
    // NOTE: the admin screens (/users, /reports, /settings) moved to AdminController.
}