package com.lenzora.official.system.lenzora;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Home {
    
    @GetMapping("/")
    public String showHome(Model model) {
        model.addAttribute("appName", "Lenzora");
        model.addAttribute("welcome", "Welcome to platform");
        return "index";
    }
}