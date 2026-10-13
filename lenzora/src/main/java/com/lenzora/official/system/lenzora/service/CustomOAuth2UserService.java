package com.lenzora.official.system.lenzora.service;

import com.lenzora.official.system.lenzora.entity.AccountStatus;
import com.lenzora.official.system.lenzora.model.User;
import com.lenzora.official.system.lenzora.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    // Password hash given to accounts created by Google sign-in. It is a hash of
    // a random value nobody knows, so those accounts cannot be used with a
    // password login. Set GOOGLE_ACCOUNT_PASSWORD in .env to pin your own value.
    private final String googleAccountPassword;

    public CustomOAuth2UserService(UserRepository userRepository,
                                   @Value("${GOOGLE_ACCOUNT_PASSWORD:}") String pGoogleAccountPassword) {
        this.userRepository = userRepository;
        this.googleAccountPassword = pGoogleAccountPassword.isBlank()
                ? randomPasswordHash()
                : pGoogleAccountPassword;
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

        // Sign in the existing account, or create one so a first-time Google
        // user can start the license verification step.
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createAccount(oidcUser, email));

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

    /** Creates a pending account for a Google email that is not registered yet. */
    private User createAccount(OidcUser pOidcUser, String pEmail) {

        String fallbackName = nameFromEmail(pEmail);

        User user = new User();
        user.setFirstName(textOr(pOidcUser.getGivenName(), fallbackName));
        user.setLastName(textOr(pOidcUser.getFamilyName(), fallbackName));
        user.setEmail(pEmail);
        user.setPasswordHash(googleAccountPassword);
        user.setRole("ORTHOPTIST");
        user.setAccountStatus(AccountStatus.PENDING);
        user.setProfileCompleted(false);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        System.out.println("Created an account for the Google user " + pEmail + ".");

        return userRepository.save(user);
    }

    private String textOr(String pValue, String pFallback) {
        return (pValue == null || pValue.isBlank()) ? pFallback : pValue;
    }

    private String nameFromEmail(String pEmail) {
        int at = pEmail.indexOf('@');
        return at > 0 ? pEmail.substring(0, at) : pEmail;
    }

    /** A BCrypt hash of a fresh random value, used as an unusable password. */
    private static String randomPasswordHash() {
        return BCrypt.hashpw(UUID.randomUUID().toString(), BCrypt.gensalt());
    }
}