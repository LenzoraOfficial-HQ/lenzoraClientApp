package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import com.lenzora.official.system.lenzora.service.OrthoptistProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrthoptistProfileController {

    private final UserRepository userRepository;
    private final OrthoptistProfileService profileService;

    public OrthoptistProfileController(UserRepository userRepository,
                                       OrthoptistProfileService profileService) {
        this.userRepository = userRepository;
        this.profileService = profileService;
    }

    @GetMapping("/orthoptist/profile")
    public String showProfileForm(Authentication authentication, Model model) {
        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null || !"ORTHOPTIST".equals(user.getRole())) {
            return "redirect:/";
        }
        if (user.getAccountStatus() == AccountStatus.REJECTED) {
            return "redirect:/registration/rejected";
        }
        if (user.hasProfessionalProfile()) {
            return user.getAccountStatus() == AccountStatus.PENDING
                    ? "redirect:/registration/pending" : "redirect:/";
        }

        model.addAttribute("licenseNumber", user.getLicenseNumber());
        model.addAttribute("phone", user.getPhone());
        model.addAttribute("branch", user.getBranch());
        return "orthoptist-profile";
    }

    @PostMapping("/orthoptist/profile")
    public String saveProfile(@RequestParam(required = false) String licenseNumber,
                              @RequestParam(required = false) String phone,
                              @RequestParam(required = false) String branch,
                              Authentication authentication, Model model) {
        try {
            profileService.completeProfile(authentication.getName(), licenseNumber, phone, branch);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("licenseNumber", licenseNumber);
            model.addAttribute("phone", phone);
            model.addAttribute("branch", branch);
            return "orthoptist-profile";
        }

        User saved = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (saved != null && saved.getAccountStatus() != AccountStatus.PENDING) {
            return "redirect:/";
        }
        return "redirect:/registration/pending";
    }
}