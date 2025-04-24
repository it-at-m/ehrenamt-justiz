package de.muenchen.ehrenamtjustiz.aenderungsservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
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
            groupId = "${aenderungsservice.groupId}"
    )
    public void consume(final String om) {
        log.debug("Änderungsservice mit EWO-OM aufgerufen: {}", om);

        aenderungsService(om);

    }

    /**
     * Call consume manual e.g. by UnitTest
     *
     * @param om EWO-OM
     */
    public HttpStatusCode consumeDirect(final String om) {
        log.debug("Änderungsservice mit EWO-OM aufgerufen: {}", om);

        return aenderungsService(om);

    }

    private HttpStatusCode aenderungsService(final String om) {

        if (om == null) {
            log.error("Fehler: Die EWO-ID ist null");
            return HttpStatus.BAD_REQUEST;
        }

        if (om.trim().isEmpty()) {
            log.error("Fehler: Die EWO-ID ist leer");
            return HttpStatus.BAD_REQUEST;
        }

        final RequestEntity<String> request;
        request = new RequestEntity<>(om, HttpMethod.POST, UriComponentsBuilder
                .fromUriString(serverBackend)
                .path(basePathBackend + "/aenderungsservice/aenderungsservicePerson")
                .build()
                .toUri());

        final ResponseEntity<Void> responseEntity;
        try {
            responseEntity = restTemplate.exchange(request, Void.class);
        } catch (HttpClientErrorException e) {
            log.error("Fehler in aenderungsService beim OM {}", om, e);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (ResourceAccessException e) {
            log.error("Netzwerkfehler in aenderungsService beim OM {}", om, e);
            return HttpStatus.SERVICE_UNAVAILABLE;
        } catch (RestClientException e) {
            log.error("Unerwarteter Fehler in aenderungsService beim OM {}", om, e);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        log.debug("Für OM {} wurde der Änderungsservice aufgerufen mit dem http-Code: {}", om, responseEntity.getStatusCode());

        return responseEntity.getStatusCode();
    }

}
