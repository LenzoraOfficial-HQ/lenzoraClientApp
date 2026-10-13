package com.lenzora.official.system.lenzora.config;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.repository.UserRepository;
import com.lenzora.official.system.lenzora.service.CustomOAuth2UserService;
import com.lenzora.official.system.lenzora.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserRepository userRepository;

    /** Google client id, empty when Google sign-in is not configured. */
    @Value("${spring.security.oauth2.client.registration.google.client-id:}")
    private String googleClientId;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, UserRepository userRepository) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration pConfiguration)
            throws Exception {
        return pConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/api/users/register",
                                "/registration/pending",
                                "/registration/rejected",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/css/**",
                                "/oauth2/**",
                                "/logout",
                                "/error"
                        ).permitAll()
                        .anyRequest().access((authentication, context) -> {
                            var currentUser = authentication.get();
                            if (currentUser == null || !currentUser.isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            if (currentUser.getAuthorities().stream()
                                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()))) {
                                return new AuthorizationDecision(true);
                            }
                            boolean isOrthoptist = currentUser.getAuthorities().stream()
                                    .anyMatch(authority -> "ROLE_ORTHOPTIST".equals(authority.getAuthority()));
                            if (!isOrthoptist) {
                                return new AuthorizationDecision(false);
                            }
                            var user = userRepository.findByEmail(currentUser.getName()).orElse(null);
                            if (user == null || user.getAccountStatus() == AccountStatus.REJECTED) {
                                return new AuthorizationDecision(false);
                            }
                            if (!user.hasProfessionalProfile()) {
                                return new AuthorizationDecision(
                                        "/orthoptist/profile".equals(context.getRequest().getRequestURI()));
                            }
                            return new AuthorizationDecision(
                                    user.getAccountStatus() == AccountStatus.APPROVED);
                        })
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(this::onAccessDenied))
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password"))

                // Manual email + password login
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(this::onAuthenticationSuccess)
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
                    .successHandler(this::onAuthenticationSuccess)
                    .failureHandler(this::onOAuthFailure)
            );
        }

        return http.build();
    }

    private void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request,
                                         jakarta.servlet.http.HttpServletResponse response,
                                         org.springframework.security.core.Authentication authentication)
            throws java.io.IOException {
        var user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            response.sendRedirect("/login?error");
        } else if ("ORTHOPTIST".equals(user.getRole())
                && user.getAccountStatus() == AccountStatus.REJECTED) {
            response.sendRedirect("/registration/rejected");
        } else if ("ORTHOPTIST".equals(user.getRole()) && !user.hasProfessionalProfile()) {
            response.sendRedirect("/orthoptist/profile");
        } else if ("ORTHOPTIST".equals(user.getRole())
                && user.getAccountStatus() == AccountStatus.PENDING) {
            response.sendRedirect("/registration/pending");
        } else {
            response.sendRedirect("/");
        }
    }

    private void onOAuthFailure(jakarta.servlet.http.HttpServletRequest request,
                                jakarta.servlet.http.HttpServletResponse response,
                                org.springframework.security.core.AuthenticationException exception)
            throws java.io.IOException {
        System.out.println("Google sign-in failed: " + exception.getMessage());
        response.sendRedirect("/login?oauthError");
    }

    private void onAccessDenied(jakarta.servlet.http.HttpServletRequest request,
                                jakarta.servlet.http.HttpServletResponse response,
                                AccessDeniedException exception) throws java.io.IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            var user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (user != null && "ORTHOPTIST".equals(user.getRole())) {
                if (user.getAccountStatus() == AccountStatus.REJECTED) {
                    response.sendRedirect("/registration/rejected");
                    return;
                }
                if (!user.hasProfessionalProfile()) {
                    response.sendRedirect("/orthoptist/profile");
                    return;
                }
                if (user.getAccountStatus() == AccountStatus.PENDING) {
                    response.sendRedirect("/registration/pending");
                    return;
                }
            }
        }
        response.sendError(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN);
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