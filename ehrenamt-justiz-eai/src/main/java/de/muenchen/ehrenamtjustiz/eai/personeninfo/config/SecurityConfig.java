package de.muenchen.ehrenamtjustiz.eai.personeninfo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;

/**
 * WebSec-configuration
 *
 */
@Component
public class SecurityConfig {

    @Value(Configuration.BASEPATH_VALUE)
    private String basePath;

    @Value("${management.context-path}")
    private String managementContextPath;

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) {
        // @formatter:off
        final PathPatternRequestMatcher.Builder requestMatcherBuilder = PathPatternRequestMatcher.withDefaults();

        http
                .authorizeHttpRequests((requests) -> requests.requestMatchers(
                    requestMatcherBuilder.matcher(HttpMethod.GET,basePath + Konstanten.PERSONENINFO_SUB_PATH_EWO_SUCHE_MIT_OM+"/**"),
                    requestMatcherBuilder.matcher(HttpMethod.POST,basePath + Konstanten.PERSONENINFO_SUB_PATH_EWO_SUCHE+"/**")).hasAuthority("getPersoneninfo"))
                .authorizeHttpRequests((requests) -> requests.requestMatchers(
                    requestMatcherBuilder.matcher(HttpMethod.GET,  basePath + Konstanten.API_DOC_SUB_PATH),
                    requestMatcherBuilder.matcher(HttpMethod.GET, managementContextPath+"/info"),
                    requestMatcherBuilder.matcher(HttpMethod.GET, managementContextPath+"/health"),
                    requestMatcherBuilder.matcher(HttpMethod.GET, managementContextPath+"/health/readiness"),
                    requestMatcherBuilder.matcher(HttpMethod.GET, managementContextPath+"/health/liveness"),
                    requestMatcherBuilder.matcher(HttpMethod.GET, managementContextPath+"/sbom"),
                    requestMatcherBuilder.matcher(HttpMethod.GET, managementContextPath+"/sbom/application")).permitAll());

        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(Customizer.withDefaults());

        return http.build();
        // @formatter:on
    }
}
