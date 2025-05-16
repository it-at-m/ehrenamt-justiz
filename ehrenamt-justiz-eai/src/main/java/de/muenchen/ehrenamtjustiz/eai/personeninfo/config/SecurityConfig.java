package de.muenchen.ehrenamtjustiz.eai.personeninfo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * WebSec-configuration
 *
 */
@Component
public class SecurityConfig {

    @Value("${management.context-path}")
    private String managementContextPath;

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http, final HandlerMappingIntrospector introspector) throws Exception {
        // @formatter:off
        final MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http.authorizeHttpRequests(
                authorize -> {
                    authorize.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,Konstanten.PERSONENINFO_SUB_PATH_EWO_SUCHE_MIT_OM+"/**")).hasAuthority("getPersoneninfo");
                    authorize.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.POST,Konstanten.PERSONENINFO_SUB_PATH_EWO_SUCHE+"/**")).hasAuthority("getPersoneninfo");
                    authorize.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET,  Konstanten.API_DOC_SUB_PATH)).permitAll();
                    authorize.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, managementContextPath+"/info")).permitAll();
                    authorize.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, managementContextPath+"/health")).permitAll();
                    authorize.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, managementContextPath+"/health/readiness")).permitAll();
                    authorize.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, managementContextPath+"/health/liveness")).permitAll();
                    authorize.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, managementContextPath+"/sbom")).permitAll();
                    authorize.requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, managementContextPath+"/sbom/application")).permitAll();});

        http.csrf(AbstractHttpConfigurer::disable);
        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.httpBasic(Customizer.withDefaults());

        return http.build();
        // @formatter:on
    }
}
