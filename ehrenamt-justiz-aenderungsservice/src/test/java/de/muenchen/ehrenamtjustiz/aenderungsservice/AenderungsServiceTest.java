package de.muenchen.ehrenamtjustiz.aenderungsservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.muenchen.ehrenamtjustiz.aenderungsservice.service.AenderungsService;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { AenderungsserviceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { "test", "no-security" })
class AenderungsServiceTest {

    @MockitoBean
    private RestTemplate restTemplate;

    @InjectMocks
    @Autowired
    private AenderungsService aenderungsService;

    @Test
    void testAenderungsserviceValidAenderungsService() {

        assertNotNull(aenderungsService);
        assertNotNull(restTemplate);

    }

    @Test
    void testAenderungsserviceMitErgebnisHTTP200() throws BadRequestException {

        // Mock the RestTemplate exchange method
        final ResponseEntity<List<String>> mockResponse = ResponseEntity.ok(List.of());

        when(restTemplate.exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {
        }))).thenReturn(mockResponse);

        // Send EWO-OM
        final HttpStatusCode httpStatusCode = aenderungsService.consumeDirect("4711");

        verify(restTemplate, times(1)).exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {
        }));
        assertEquals(HttpStatus.OK.value(), httpStatusCode.value());

    }

    @Test
    void testAenderungsserviceMitHttpClientErrorException() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {
        }))).thenThrow(HttpClientErrorException.class);

        assertThrows(RuntimeException.class,
                () -> {
                    // Send EWO-OM
                    aenderungsService.consumeDirect("4711");
                });

    }

    @Test
    void testAenderungsserviceOMisNull() {

        // Mock the RestTemplate exchange method
        final ResponseEntity<List<String>> mockResponse = ResponseEntity.ok(null);

        when(restTemplate.exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {
        }))).thenReturn(mockResponse);

        assertThrows(BadRequestException.class,
                () -> {
                    // Send EWO-OM
                    aenderungsService.consumeDirect(null);
                });

        verify(restTemplate, times(0)).exchange(any(RequestEntity.class), eq(Void.class));

    }

}
