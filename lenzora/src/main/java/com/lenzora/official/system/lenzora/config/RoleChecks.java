package com.lenzora.official.system.lenzora.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Reads the role of the signed-in account.
 */
public final class RoleChecks {

    /** The authority Spring Security gives an administrator. */
    public static final String ADMIN = "ROLE_ADMIN";

    private RoleChecks() {
    }

    /**
     * Tells whether the signed-in account is an administrator.
     *
     * @param pAuthentication the signed-in account, may be null
     * @return true when the account carries the admin role
     */
    public static boolean isAdmin(Authentication pAuthentication) {

        if (pAuthentication == null) {
            return false;
        }

        for (GrantedAuthority authority : pAuthentication.getAuthorities()) {
            if (ADMIN.equals(authority.getAuthority())) {
                return true;
            }
        }

        return false;
    }
}
