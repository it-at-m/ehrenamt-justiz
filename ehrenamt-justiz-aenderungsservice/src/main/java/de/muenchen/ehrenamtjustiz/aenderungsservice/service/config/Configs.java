package de.muenchen.ehrenamtjustiz.aenderungsservice.service.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public static final String X_CAUSE = "X-Cause";
    public static final String BACKEND_ERROR = "BACKEND_ERROR";
    @Value("${aenderungsservice.backend.connecttimeout}")
    private int connectTimeout;

    @Value("${aenderungsservice.backend.readtimeout}")
    private int readTimeout;

    @Value("${aenderungsservice.backend.retry.maxRetries}")
    private int maxRetries;

    @Value("${aenderungsservice.backend.retry.initialInterval}")
    private int initialInterval;

    @Value("${aenderungsservice.backend.retry.multiplier}")
    private int multiplier;

    @Value("${aenderungsservice.backend.retry.maxInterval}")
    private int maxInterval;

    @Bean
    @SuppressWarnings("PMD.DoubleBraceInitialization")
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            final ConsumerFactory<String, String> consumerFactory) {
        final ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        // Setze einen ExponentialBackOff
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new ExponentialBackOffWithMaxRetries(maxRetries) {
                    {
                        setInitialInterval(initialInterval);
                        setMultiplier(multiplier);
                        setMaxInterval(maxInterval);
                    }
                }));
        return factory;
    }

    @Bean
    @SuppressWarnings({ "PMD.CognitiveComplexity", "PMD.AvoidDeeplyNestedIfStmts" })
    public KafkaListenerErrorHandler kafkaListenerErrorHandler() {
        return (message, exception) -> {

            final Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException && cause.getCause() != null) {

                final Throwable causeOfRuntimeException = cause.getCause();

                if (causeOfRuntimeException instanceof HttpServerErrorException.InternalServerError) {
                    // Vom Backend http 500 internalServerError
                    final Map<String, List<String>> headers = ((HttpServerErrorException.InternalServerError) causeOfRuntimeException).getResponseHeaders();
                    if (checkHeaders(headers, causeOfRuntimeException)) {
                        // --> Keine weiteren Versuche (Non blocking entry)
                        return null;
                    }
                }
                if (causeOfRuntimeException instanceof HttpClientErrorException) {
                    // Vom Backend http 404 Not Found
                    final Map<String, List<String>> headers = ((HttpClientErrorException) causeOfRuntimeException).getResponseHeaders();
                    if (checkHeaders(headers, causeOfRuntimeException)) {
                        // --> Keine weiteren Versuche (Non blocking entry)
                        return null;
                    }
                }
            } else if (cause instanceof BadRequestException) {
                // Im Änderungsservice fehlerhafter Parameter --> Keine weiteren Versuche (Non blocking entry)
                log.error("Bad request", cause);
                return null;
            }
            // Weitere Versuche gemäß Konfiguration in kafkaListenerContainerFactory
            throw exception;
        };
    }

    private static boolean checkHeaders(final Map<String, List<String>> headers, final Throwable causeOfRuntimeException) {
        // Header, der im Backend im Fehlerfall gesetzt wurde?
        if (headers != null && headers.containsKey(X_CAUSE)
                && Objects.requireNonNull(headers.get(X_CAUSE)).getFirst().startsWith(BACKEND_ERROR)) {

            // Fehler in der Logik des Backend --> Keine weiteren Versuche (Non blocking entry)
            log.error("Server Fehler", causeOfRuntimeException);
            return true;
        }
        return false;
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
