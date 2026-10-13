package com.lenzora.official.system.lenzora.controller;

import com.lenzora.official.system.lenzora.entity.AppUser;
import com.lenzora.official.system.lenzora.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserWebController {

    private final UserService userService;

    public UserWebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/view")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewUserProfile(@RequestParam Long id, Model model) {
        AppUser user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + id));
        model.addAttribute("user", user);
        return "user-profile";
    }
}