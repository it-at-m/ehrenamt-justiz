package de.muenchen.ehrenamtjustiz.aenderungsservice.service.config;

import de.muenchen.ehrenamtjustiz.exception.AenderungsServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import org.springframework.web.client.*;

@Slf4j
@Configuration
public class Configs {

    @Value("${aenderungsservice.backend.connecttimeout}")
    private int connectTimeout;

    @Value("${aenderungsservice.backend.readtimeout}")
    private int readTimeout;

    @Value("${aenderungsservice.backend.retry.maxRetries}")
    private int maxRetries;

    @Value("${aenderungsservice.backend.retry.initialInterval}")
    private long initialInterval;

    @Value("${aenderungsservice.backend.retry.multiplier}")
    private double multiplier;

    @Value("${aenderungsservice.backend.retry.maxInterval}")
    private long maxInterval;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            final ConsumerFactory<String, String> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // Setze einen ExponentialBackOff
        final ExponentialBackOffWithMaxRetries backoff = new ExponentialBackOffWithMaxRetries(maxRetries);
        backoff.setInitialInterval(initialInterval);
        backoff.setMultiplier(multiplier);
        backoff.setMaxInterval(maxInterval);
        factory.setCommonErrorHandler(new DefaultErrorHandler(backoff));

        return factory;
    }

    @Bean
    @SuppressWarnings({ "PMD.CognitiveComplexity", "PMD.AvoidDeeplyNestedIfStmts" })
    public KafkaListenerErrorHandler kafkaListenerErrorHandler() {
        return (message, exception) -> {

            final Throwable cause = exception.getCause();
            if (cause instanceof AenderungsServiceException aenderungsServiceException) {
                if (aenderungsServiceException.isBlockingEntry()) {
                    // Vom Backend: Blocking Entry:
                    // Weitere Versuche gemäß Konfiguration in kafkaListenerContainerFactory
                    throw exception;
                }
                // Vom Backend Error: Kein Blocking Entry:
                // --> Keine weiteren Versuche (Non blocking entry)
                return null;

            } else if (cause instanceof BadRequestException) {
                // Im Änderungsservice fehlerhafter Parameter --> Keine weiteren Versuche (Non blocking entry)
                log.error("Bad request", cause);
                return null;
            }
            // Weitere Versuche gemäß Konfiguration in kafkaListenerContainerFactory
            throw exception;
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
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
