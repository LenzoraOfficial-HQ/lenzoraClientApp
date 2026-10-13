package com.lenzora.official.system.lenzora;

import com.lenzora.official.system.lenzora.service.UserRegistrationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Serves the sign-up page and saves the account it submits. The new account is
 * signed in straight away so it goes on to the license verification step.
 */
@Controller
public class RegisterController {

    /** Checks the submitted details and writes the account. */
    private final UserRegistrationService userRegistrationService;

    /** Signs the freshly created account in. */
    private final AuthenticationManager authenticationManager;

    /** Google client id, empty when Google sign-in is not configured. */
    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    public RegisterController(UserRegistrationService pUserRegistrationService,
                              AuthenticationManager pAuthenticationManager) {
        this.userRegistrationService = pUserRegistrationService;
        this.authenticationManager = pAuthenticationManager;
    }

    /** Shows the empty sign-up form. */
    @GetMapping("/register")
    public String showRegister(Model pModel) {
        addGoogleLoginFlag(pModel);
        return "register";
    }

    /**
     * Creates the account, or shows the form again with the reason it could not
     * be created.
     */
    @PostMapping("/register")
    public String createAccount(
            @RequestParam(name = "firstName", required = false) String pFirstName,
            @RequestParam(name = "lastName", required = false) String pLastName,
            @RequestParam(name = "email", required = false) String pEmail,
            @RequestParam(name = "password", required = false) String pPassword,
            @RequestParam(name = "confirmPassword", required = false) String pConfirmedPassword,
            HttpServletRequest pRequest, HttpServletResponse pResponse, Model pModel) {

        String problem = userRegistrationService.register(
                pFirstName, pLastName, pEmail, pPassword, pConfirmedPassword);

        if (!problem.isEmpty()) {
            pModel.addAttribute("errorMessage", problem);
            addGoogleLoginFlag(pModel);
            return "register";
        }

        signIn(pEmail, pPassword, pRequest, pResponse);
        return "redirect:/orthoptist/profile";
    }

    /** Signs the new account in so the license step is shown right away. */
    private void signIn(String pEmail, String pPassword, HttpServletRequest pRequest,
                        HttpServletResponse pResponse) {

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(pEmail, pPassword));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        new HttpSessionSecurityContextRepository().saveContext(context, pRequest, pResponse);
    }

    /** Tells the form whether the "Sign up with Google" button applies. */
    private void addGoogleLoginFlag(Model pModel) {
        pModel.addAttribute("googleLoginEnabled",
                googleClientId != null && !googleClientId.isBlank());
    }
}
