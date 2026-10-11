package com.lenzora.official.system.lenzora.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JpaAuditingConfigTest {

    private final JpaAuditingConfig config = new JpaAuditingConfig();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void auditorAwareReturnsAuthenticatedUsername() {
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("admin@example.com", "N/A", java.util.List.of()));

        assertEquals("admin@example.com", config.auditorAware().getCurrentAuditor().orElseThrow());
    }

    @Test
    void auditorAwareIsEmptyWhenNoUserIsAuthenticated() {
        SecurityContextHolder.clearContext();

        assertEquals(true, config.auditorAware().getCurrentAuditor().isEmpty());
    }
}
