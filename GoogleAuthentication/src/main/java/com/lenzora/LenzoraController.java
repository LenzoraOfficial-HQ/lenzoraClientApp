package com.lenzora;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LenzoraController {

    @GetMapping("/frames")
    public String viewFrames(@AuthenticationPrincipal OAuth2User principal) {
        return "Welcome to Lenzora, " + principal.getAttribute("name") + "! You are successfully logged in.";
    }
}