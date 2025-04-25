package de.muenchen.ehrenamtjustiz.backend.configuration;

import de.muenchen.ehrenamtjustiz.backend.rest.BasicAuthRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
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
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new BasicAuthRestTemplate(userEwoEai, pwEwoEai);
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

}
