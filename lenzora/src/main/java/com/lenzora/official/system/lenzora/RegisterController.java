package com.lenzora.official.system.lenzora;

import com.lenzora.official.system.lenzora.service.UserRegistrationService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Serves the sign-up page and saves the account it submits. The new account is
 * not logged in straight away - the login page is shown instead.
 */
@Controller
public class RegisterController {

    /** Checks the submitted details and writes the account. */
    private final UserRegistrationService userRegistrationService;

    public RegisterController(UserRegistrationService pUserRegistrationService) {
        this.userRegistrationService = pUserRegistrationService;
    }

    /** Shows the empty sign-up form. */
    @GetMapping("/register")
    public String showRegister() {
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
            Model pModel) {

        String problem = userRegistrationService.register(
                pFirstName, pLastName, pEmail, pPassword, pConfirmedPassword);

        if (!problem.isEmpty()) {
            pModel.addAttribute("errorMessage", problem);
            return "register";
        }

        return "redirect:/login?registered";
    }
}
