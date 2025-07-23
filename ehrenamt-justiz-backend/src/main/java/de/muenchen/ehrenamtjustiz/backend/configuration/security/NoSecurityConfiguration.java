package de.muenchen.ehrenamtjustiz.backend.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures the security context to not require any authorization for incoming requests.
 */
@Configuration
@Profile("no-security")
@EnableWebSecurity
public class NoSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .headers(customizer -> customizer
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .authorizeHttpRequests(requests -> requests
                        .anyRequest()
                        .permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        // @formatter:on
        return http.build();
    }

}
