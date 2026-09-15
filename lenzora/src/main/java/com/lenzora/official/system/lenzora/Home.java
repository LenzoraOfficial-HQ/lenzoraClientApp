
package com.lenzora.official.system.lenzora;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {

    // Dashboard
    @GetMapping("/")
    public String showHome(Model model) {

        model.addAttribute("Name", "Lenzora");
        model.addAttribute("welcome", "Welcome to platform");

        return "index";
    }

    // Place Order page
    @GetMapping("/place-order")
    public String showPlaceOrder() {

        return "place-order";
    }
}