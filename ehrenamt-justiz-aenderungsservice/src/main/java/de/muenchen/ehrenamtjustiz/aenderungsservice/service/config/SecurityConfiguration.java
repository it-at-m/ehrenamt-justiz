package de.muenchen.ehrenamtjustiz.aenderungsservice.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

/**
 * The central class for configuration of all security aspects.
 * Configures all endpoints to require authentication via access token.
 * (except the Spring Boot Actuator endpoints)
 */
@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests.requestMatchers(
                        // allow access to /actuator/info
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/info"),
                        // allow access to /actuator/health for OpenShift Health Check
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/health"),
                        // allow access to /actuator/health/liveness for OpenShift Liveness Check
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/health/liveness"),
                        // allow access to /actuator/health/readiness for OpenShift Readiness Check
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/health/readiness"),
                        // allow access to SBOM overview
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/sbom"),
                        // allow access to SBOM application data
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/sbom/application"),
                        // allow access to /actuator/metrics for Prometheus monitoring in OpenShift
                        PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.GET, "/actuator/metrics"))
                        .permitAll())
                .authorizeHttpRequests((requests) -> requests.requestMatchers("/**")
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

}
