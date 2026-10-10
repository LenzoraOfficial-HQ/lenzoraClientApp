package com.lenzora.official.system.lenzora.config;

import com.lenzora.official.system.lenzora.service.CustomOAuth2UserService;
import com.lenzora.official.system.lenzora.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfException;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    /** Google client id, empty when Google sign-in is not configured. */
    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        // Administrators land on the admin portal, everyone else on the client dashboard.
        RoleLoginSuccessHandler successHandler = new RoleLoginSuccessHandler();

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/api/users/register",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/css/**",
                                "/assets/**",
                                "/oauth2/**"
                        ).permitAll()
                        // The admin portal and the management screens are admin only.
                        .requestMatchers(
                                "/admin",
                                "/admin/**",
                                "/users",
                                "/catalogue",
                                "/catalogue/**",
                                "/reports",
                                "/settings"
                        ).hasRole("ADMIN")
                        // Placing an order is the client app's job, not the admin's.
                        .requestMatchers(
                                "/place-order",
                                "/orders"
                        ).hasRole("ORTHOPTIST")
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password"))

                // A signed-in account without the admin role goes back to the client dashboard.
                // A missing or stale CSRF token keeps its own error page, so a form post is
                // never silently repeated as a plain page load.
                .exceptionHandling(handling -> handling
                        .accessDeniedHandler((deniedRequest, deniedResponse, denied) -> {
                            if (denied instanceof CsrfException) {
                                deniedResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                            } else {
                                deniedResponse.sendRedirect("/");
                            }
                        }))

                // Manual email + password login
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        // Google sign-in only when its credentials are configured.
        if (googleClientId != null && !googleClientId.isBlank()) {

            http.oauth2Login(oauth -> oauth
                    .loginPage("/login")
                    .userInfoEndpoint(userInfo -> userInfo
                            .oidcUserService(customOAuth2UserService)
                    )
                    .successHandler(successHandler)
            );
        }

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }
}
