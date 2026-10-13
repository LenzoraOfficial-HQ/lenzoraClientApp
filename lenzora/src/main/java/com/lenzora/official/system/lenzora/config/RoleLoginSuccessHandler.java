package com.lenzora.official.system.lenzora.config;

import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 * Sends an administrator to the admin portal, handles orthoptist verification flows, 
 * and routes other accounts to their destination.
 */
@Component
public class RoleLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    /** Landing page of the admin portal. */
    private static final String ADMIN_HOME = "/admin";
    private static final String PROFILE_SETUP = "/orthoptist/profile";
    private static final String REGISTRATION_PENDING = "/registration-pending";

    private final UserRepository userRepository;

    public RoleLoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest pRequest, HttpServletResponse pResponse, Authentication pAuthentication) {
        if (pAuthentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) pAuthentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            if (email != null) {
                Optional<User> userOpt = userRepository.findByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if ("ORTHOPTIST".equals(user.getRole())) {
                        if (user.getLicenseNumber() == null || user.getLicenseNumber().isBlank()) {
                            return PROFILE_SETUP;
                        }
                    }
                } else {
                    return PROFILE_SETUP;
                }
            }
        }

        String target = super.determineTargetUrl(pRequest, pResponse, pAuthentication);

        // A page the user asked for before signing in is never skipped for admins.
        if ("/".equals(target) && RoleChecks.isAdmin(pAuthentication)) {
            return ADMIN_HOME;
        }

        return target;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}