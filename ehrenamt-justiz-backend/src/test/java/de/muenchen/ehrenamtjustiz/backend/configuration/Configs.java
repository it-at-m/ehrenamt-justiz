package de.muenchen.ehrenamtjustiz.backend.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
public class Configs {

    @Value("${ewo.eai.server}")
    private String serverewoeai;

    @Value("${ewo.eai.user:ohneuser default}")
    private String userEwoEai;

    @Value("${ewo.eai.password:ohnepassword default}")
    private String pwEwoEai;

    @Bean
    public RestClient restClient(final RestClient.Builder restClientBuilder) {

        final String basicAuth = Base64.getEncoder().encodeToString(
                (userEwoEai + ":" + pwEwoEai).getBytes(StandardCharsets.UTF_8));

        // Erstelle RestClient mit Basic Auth
        return restClientBuilder
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                .baseUrl(serverewoeai)
                .build();
    }

}
