package de.muenchen.ehrenamtjustiz.eai.personeninfo.config;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Class is used to load user data (user, password, roles). Used for authentication
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(UserDetailsService.class);
    private WebSecProperties webSecProperties = new DefaultWebSecProperties();

    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.AvoidCatchingNPE" })
    @Override
    @SuppressFBWarnings({ "DCN_NULLPOINTER_EXCEPTION", "NM_SAME_SIMPLE_NAME_AS_INTERFACE" })
    public UserDetails loadUserByUsername(final String username) {
        final User user;

        try {
            user = this.webSecProperties.getUsers().stream().filter((u) -> username.equals(u.getUsername())).findFirst()
                    .orElseThrow(NullPointerException::new);
        } catch (NullPointerException npe) {
            LOG.warn("Benutzer {} nicht in Konfiguration vorhanden", username);
            throw new UsernameNotFoundException("Benutzer " + username + " ist unvollständig konfiguriert - Username fehlt", npe);
        }

        if (user.getPassword() == null) {
            LOG.warn("Benutzer {} hat keine Passwort oder fehlt in der Konfiguration", username);
            throw new UsernameNotFoundException("Benutzer " + username + " ist unvollständig konfiguriert - Passwort fehlt");
        } else {
            final List<SimpleGrantedAuthority> auths;

            try {
                auths = user.getRoles().stream().map((role) -> this.webSecProperties.getRoles().get(role)).flatMap(Collection::stream).distinct()
                        .map(authorities -> new SimpleGrantedAuthority(authorities.toString())).collect(Collectors.toList());
            } catch (NullPointerException npe) {
                LOG.warn("Benutzer {} hat keine Rollen", username);
                throw new UsernameNotFoundException("Benutzer " + username + " ist unvollständig konfiguriert - Rollen fehlen", npe);
            }

            if (auths != null && !auths.isEmpty()) {
                LOG.debug("User geladen > {}", user);
                return new org.springframework.security.core.userdetails.User(username, user.getPassword(), auths);
            } else {
                LOG.warn("Benutzer {} hat keine Rollen oder sie fehlen in der Konfiguration", username);
                throw new UsernameNotFoundException("Benutzer " + username + " ist unvollständig konfiguriert - Rollen fehlen");
            }
        }
    }

    public WebSecProperties getWebSecProperties() {
        return this.webSecProperties;
    }

    public void setWebSecProperties(final WebSecProperties webSecProperties) {
        this.webSecProperties = webSecProperties;
    }
}
