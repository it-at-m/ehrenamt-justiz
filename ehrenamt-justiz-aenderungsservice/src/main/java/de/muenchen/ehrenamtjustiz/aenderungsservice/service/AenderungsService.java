package de.muenchen.ehrenamtjustiz.aenderungsservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.ehrenamtjustiz.exception.AenderungsServiceException;
import de.muenchen.ehrenamtjustiz.exception.ErrorResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class AenderungsService {
    @Value("${aenderungsservice.backend.server}")
    private String serverBackend;

    @Value("${aenderungsservice.backend.base-path}")
    private String basePathBackend;

    private final RestTemplate restTemplate;

    @Autowired
    public AenderungsService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @KafkaListener(
            topicPattern = "${aenderungsservice.topicPattern}",
            groupId = "${aenderungsservice.group-Id}",
            errorHandler = "kafkaListenerErrorHandler"
    )
    public void consume(final String om) throws BadRequestException {
        log.debug("Änderungsservice mit EWO-OM aufgerufen: {}", om);

        final HttpStatusCode verarbeitenOMResult = verarbeitenOM(om);

        if (!verarbeitenOMResult.is2xxSuccessful()) {
            throw new RuntimeException("Consumer of KafkaListener mit Http-Code: " + verarbeitenOMResult);
        }
    }

    /**
     * Aufruf consume z.B. durch Unit-Test
     *
     * @param om EWO-OM
     */
    public HttpStatusCode consumeDirect(final String om) throws BadRequestException {
        log.debug("Änderungsservice mit EWO-OM aufgerufen: {}", om);

        return verarbeitenOM(om);

    }

    private HttpStatusCode verarbeitenOM(final String om) throws BadRequestException {

        final HttpStatusCode statusCode = aenderungsService(om);
        if (!statusCode.is2xxSuccessful()) {
            log.warn("Änderungsservice für OM {} nicht erfolgreich abgeschlossen. Status: {}", om, statusCode);
        }
        return statusCode;
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    private HttpStatusCode aenderungsService(final String om) throws BadRequestException {

        if (om == null) {
            throw new BadRequestException("Fehler: Die EWO-ID ist null");
        }

        if (om.trim().isEmpty()) {
            throw new BadRequestException("Fehler: Die EWO-ID ist leer");
        }

        final RequestEntity<String> request;
        request = new RequestEntity<>(om, HttpMethod.POST, UriComponentsBuilder
                .fromUriString(serverBackend)
                .path(basePathBackend + "/aenderungsservice/aenderungsservicePerson")
                .build()
                .toUri());

        final ResponseEntity<List> responseEntity;
        try {
            responseEntity = restTemplate.exchange(request, List.class);
        } catch (RestClientResponseException rcrException) {
            log.error("Fehler beim Aufruf des Änderungsservice bei OM " + om, rcrException);

            final String errorBody = rcrException.getResponseBodyAsString();
            final ObjectMapper mapper = new ObjectMapper();
            final ErrorResponse error;
            try {
                error = mapper.readValue(errorBody, ErrorResponse.class);
            } catch (JsonProcessingException mapperError) {
                // Kein ResponseError vom Backend vorhanden --> Dann normale RuntimeException
                throw rcrException;
            }
            // ResponseError vom Backend --> Dann AenderungsServiceException
            throw new AenderungsServiceException("Fehler in aenderungsService beim OM " + om, error.getOm(),
                    error.isBlockingEntry());
        }

        log.debug("Für OM {} wurde der Änderungsservice aufgerufen mit dem http-Code: {}", om, responseEntity.getStatusCode());

        return responseEntity.getStatusCode();
    }

}
