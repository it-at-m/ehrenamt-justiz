package de.muenchen.ehrenamtjustiz.eai.personeninfo.config;

import de.muenchen.ehrenamtjustiz.eai.personeninfo.filter.RequestResponseLoggingFilter;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/// Config class that configures all additional beans that are not available as components
@org.springframework.context.annotation.Configuration
public class Configuration {

    public static final String BASEPATH_VALUE = "${service.rest.basepath}";

    @Value("${api.producer.user}")
    private String ewoeaiuser;

    @PostConstruct
    public void validate() {
        if (StringUtils.isEmpty(ewoeaiuser)) {
            throw new IllegalStateException("Missing required property in application[profile].yml: api.producer.user");
        }
    }

    @Bean
    @ConditionalOnProperty(
            name = { "api.auth.enabled" },
            matchIfMissing = true
    )
    public ConfiguredWebSecProperties webSecProperties() {
        return new ConfiguredWebSecProperties();
    }

    @Bean
    @ConditionalOnBean({ ConfiguredWebSecProperties.class })
    public UserDetailsService userDetailsService(final WebSecProperties webSecProperties) {
        final UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl();
        userDetailsService.setWebSecProperties(webSecProperties);
        return userDetailsService;
    }

    @Bean
    @ConditionalOnMissingBean({ PasswordEncoder.class })
    public PasswordEncoder passwordEncoder(@Value("${api.auth.bcrypt.strength:10}") final Integer strength) {
        return new BCryptPasswordEncoder(strength);
    }

    @Bean
    @ConditionalOnBean({ UserDetailsService.class })
    public AuthenticationProvider authenticationProvider(final UserDetailsService userDetailsService,
            final PasswordEncoder passwordEncoder) {
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    @ConditionalOnBean({ AuthenticationProvider.class })
    public AuthenticationManager authenticationManager(@Qualifier("authenticationProvider") final AuthenticationProvider authProvider) {
        final List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        authenticationProviders.add(authProvider);
        return new ProviderManager(authenticationProviders);
    }

    @Bean
    @ConditionalOnProperty({ "api.loggingFilter.urlPattern" })
    @Order()
    public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter(@Value("${api.loggingFilter.urlPattern}") final String urlPattern,
            @Value("${api.loggingFilter.charsetName:UTF-8}") final String charsetName, @Value("${api.loggingFilter.buffersize:1024}") final int buffersize,
            @Value("${api.loggingFilter.loggerName:}") final String filterLoggerName) {
        final FilterRegistrationBean<RequestResponseLoggingFilter> bean = new FilterRegistrationBean<>();
        final RequestResponseLoggingFilter loggingFilter;
        if (StringUtils.isEmpty(filterLoggerName)) {
            loggingFilter = new RequestResponseLoggingFilter();
        } else {
            loggingFilter = new RequestResponseLoggingFilter(filterLoggerName);
        }

        loggingFilter.setBufferSize(buffersize);
        loggingFilter.setCharsetName(charsetName);
        bean.setFilter(loggingFilter);
        bean.addUrlPatterns(urlPattern);
        return bean;
    }

}
