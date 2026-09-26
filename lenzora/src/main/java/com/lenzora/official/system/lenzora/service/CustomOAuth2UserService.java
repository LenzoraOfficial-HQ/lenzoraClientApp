package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {

        // Let Spring load the user's information from Google
        OidcUser oidcUser = super.loadUser(userRequest);

        // Get email from Google
        String email = oidcUser.getEmail();

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    "Google account does not provide an email address"
            );
        }

        // Find the existing Lenzora account
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new OAuth2AuthenticationException(
                                "No Lenzora account is registered with this Google email"
                        )
                );

        // Keep Google's existing authorities and add the Lenzora role
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        oidcUser.getAuthorities().forEach(authority ->
                authorities.add(
                        new SimpleGrantedAuthority(authority.getAuthority())
                )
        );

        authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole()
                )
        );

        // Return the authenticated Google user with the Lenzora role
        return new org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "email"
        );
    }
}