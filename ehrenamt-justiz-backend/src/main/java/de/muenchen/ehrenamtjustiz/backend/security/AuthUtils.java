package de.muenchen.ehrenamtjustiz.backend.security;

import de.muenchen.ehrenamtjustiz.backend.configuration.security.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.stereotype.Component;

/**
 * Utility bean for authentication related data.
 */
@Component
@Profile("!no-security")
@RequiredArgsConstructor
public class AuthUtils {

    public static final String NAME_UNAUTHENTICATED_USER = "unauthenticated";

    private final SecurityProperties securityProperties;

    /**
     * Extracts the user name from the existing Spring Security Context via
     * {@link SecurityContextHolder}.
     *
     * @return the username or an "unauthenticated" if no {@link Authentication} exists
     */
    public String getUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return (String) jwtAuth.getTokenAttributes().get(securityProperties.getUsernameClaim());
        } else if (authentication instanceof UsernamePasswordAuthenticationToken usernameAuth) {
            return usernameAuth.getName();
        } else {
            return NAME_UNAUTHENTICATED_USER;
        }
    }

}
