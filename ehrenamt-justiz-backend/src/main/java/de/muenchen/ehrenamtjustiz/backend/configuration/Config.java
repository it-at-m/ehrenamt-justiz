package de.muenchen.ehrenamtjustiz.backend.configuration;

import de.muenchen.ehrenamtjustiz.backend.rest.BasicAuthRestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    @Value("${de.muenchen.ewo.eai.user:ohneuser default}")
    private String userEwoEai;

    @Value("${de.muenchen.ewo.eai.password:ohnepassword default}")
    private String pwEwoEai;

    @Bean
    public RestTemplate restTemplate() {
        return new BasicAuthRestTemplate(userEwoEai, pwEwoEai);
    }

}
