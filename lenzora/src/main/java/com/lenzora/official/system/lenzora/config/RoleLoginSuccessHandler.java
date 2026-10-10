package com.lenzora.official.system.lenzora.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Sends an administrator to the admin portal and every other account to the client dashboard.
 */
public class RoleLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    /** Landing page of the admin portal. */
    private static final String ADMIN_HOME = "/admin";

    @Override
    protected String determineTargetUrl(HttpServletRequest pRequest, HttpServletResponse pResponse, Authentication pAuthentication) {

        String target = super.determineTargetUrl(pRequest, pResponse, pAuthentication);

        // A page the user asked for before signing in is never skipped.
        if ("/".equals(target) && RoleChecks.isAdmin(pAuthentication)) {
            return ADMIN_HOME;
        }

        return target;
    }
}
