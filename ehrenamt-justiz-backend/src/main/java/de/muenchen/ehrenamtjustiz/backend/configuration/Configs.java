package de.muenchen.ehrenamtjustiz.backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Configs {

    @Value("${ewo.eai.connecttimeout}")
    private int connectTimeout;

    @Value("${ewo.eai.readtimeout}")
    private int readTimeout;

    @Value("${ewo.eai.user:ohneuser default}")
    private String userEwoEai;

    @Value("${ewo.eai.password:ohnepassword default}")
    private String pwEwoEai;

    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder builder) {

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        // Erstelle RestTemplate mit Basic Auth
        return builder
                .requestFactory(() -> requestFactory)
                .basicAuthentication(userEwoEai, pwEwoEai)
                .build();
    }

}
