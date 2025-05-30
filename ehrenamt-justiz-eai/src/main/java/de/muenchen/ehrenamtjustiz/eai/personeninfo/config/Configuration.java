package de.muenchen.ehrenamtjustiz.eai.personeninfo.config;

import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.EwoPersonErweitertServiceInterface;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.callbacks.PasswordCallback;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.filter.RequestResponseLoggingFilter;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ConfigurationConstants;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
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

/**
 * Config class that configures all additional beans that are not available as components
 *
 */
@org.springframework.context.annotation.Configuration
public class Configuration {

    public static final String BASEPATH_VALUE = "${service.rest.basepath}";

    @Value("${producer.user}")
    private String ewoeaiuser;

    @Value("${producer.password}")
    private String ewoeaipassword;

    @Value("${ewo.eai.url}")
    private String ewoeaiurl;

    @PostConstruct
    public void validate() {
        if (StringUtils.isEmpty(ewoeaiuser)) {
            throw new IllegalStateException("Missing required property in application[profile].yml: producer.user");
        }
        if (StringUtils.isEmpty(ewoeaipassword)) {
            throw new IllegalStateException("Missing required property in application[profile].yml: producer.password");
        }
        if (StringUtils.isEmpty(ewoeaiurl)) {
            throw new IllegalStateException("Missing required property in application[profile].yml: ewo.eai.url");
        }
    }

    @Bean
    public CxfEndpoint ewoPersonErweitertServiceEndpointLesePersonErweitert() {
        final CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress(ewoeaiurl);
        endpoint.setServiceClass(EwoPersonErweitertServiceInterface.class);
        endpoint.getOutInterceptors().add(wss4JOutInterceptor());

        return endpoint;
    }

    @Bean
    public CxfEndpoint ewoPersonErweitertServiceEndpointSuchePersonErweitert() {
        final CxfEndpoint endpoint = new CxfEndpoint();
        endpoint.setAddress(ewoeaiurl);
        endpoint.setServiceClass(EwoPersonErweitertServiceInterface.class);
        endpoint.getOutInterceptors().add(wss4JOutInterceptor());

        return endpoint;
    }

    @SuppressFBWarnings("HARD_CODE_PASSWORD")
    private WSS4JOutInterceptor wss4JOutInterceptor() {
        final Map<String, Object> args = new HashMap<>();
        args.put(WSHandlerConstants.ACTION, ConfigurationConstants.USERNAME_TOKEN);
        args.put(WSHandlerConstants.USER, ewoeaiuser);
        args.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        final PasswordCallback passwordCallback = new PasswordCallback();
        passwordCallback.setUserName(ewoeaiuser);
        passwordCallback.setUserPassword(ewoeaipassword);
        args.put(WSHandlerConstants.PW_CALLBACK_REF, passwordCallback);
        args.put(WSHandlerConstants.MUST_UNDERSTAND, "0");
        return new WSS4JOutInterceptor(args);
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
        final DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
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
