package com.lenzora.official.system.lenzora.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistrationStatusController {

    @GetMapping("/registration/pending")
    public String pending() {
        return "registration-pending";
    }

    @GetMapping("/registration/rejected")
    public String rejected() {
        return "registration-rejected";
    }
}