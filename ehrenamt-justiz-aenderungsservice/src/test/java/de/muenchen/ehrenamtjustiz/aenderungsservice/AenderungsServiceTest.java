package de.muenchen.ehrenamtjustiz.aenderungsservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
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

    @Test
    void testConsumeDirect_withWhitespaceOm_throwsBadRequest_andSkipsRestCall() {
        // Arrange
        final String invalidOm = "   ";

        // Act + Assert
        assertThrows(BadRequestException.class, () -> aenderungsService.consumeDirect(invalidOm));

        // Verify no exchange call was made at all
        verifyNoInteractions(restTemplate);
    }

    @Test
    void testConsumeDirect_withEmptyOm_throwsBadRequest_andSkipsRestCall() {
        // Arrange
        final String invalidOm = "";

        // Act + Assert
        assertThrows(BadRequestException.class, () -> aenderungsService.consumeDirect(invalidOm));

        // Verify no exchange call was made at all
        verifyNoInteractions(restTemplate);
    }

    @Test
    void testConsumeDirect_http400_isSurfaced() throws BadRequestException {
        // Arrange
        final ResponseEntity<List<String>> badRequest = ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).body(List.of("invalid"));
        when(restTemplate.exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {})))
                .thenReturn(badRequest);

        // Act
        final HttpStatusCode status = aenderungsService.consumeDirect("12345");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), status.value());
        verify(restTemplate, times(1)).exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {}));
    }

    @Test
    void testConsumeDirect_http500_isSurfaced() throws BadRequestException {
        // Arrange
        final ResponseEntity<List<String>> serverError = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(restTemplate.exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {})))
                .thenReturn(serverError);

        // Act
        final HttpStatusCode status = aenderungsService.consumeDirect("99999");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), status.value());
        verify(restTemplate, times(1)).exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {}));
    }

    @Test
    void testConsumeDirect_okWithBody_exchangeCalledOnce_returns200() throws BadRequestException {
        // Arrange
        final List<String> payload = List.of("one", "two");
        final ResponseEntity<List<String>> okResponse = ResponseEntity.ok().header(HttpHeaders.CACHE_CONTROL, "no-cache").body(payload);
        when(restTemplate.exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {})))
                .thenReturn(okResponse);

        // Act
        final HttpStatusCode status = aenderungsService.consumeDirect("4712");

        // Assert
        assertEquals(HttpStatus.OK.value(), status.value());
        verify(restTemplate, times(1)).exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {}));
    }

    @Test
    void testConsumeDirect_restTemplateThrowsUnexpected_runtimeExceptionBubblesAsRuntime() {
        // Arrange
        when(restTemplate.exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<String>>() {})))
                .thenThrow(new RuntimeException("boom"));

        // Act + Assert
        assertThrows(RuntimeException.class, () -> aenderungsService.consumeDirect("5555"));
    }

    @Test
    void testConsumeDirect_doesNotCallRest_whenOmIsEmptyString() {
        // Act + Assert
        assertThrows(BadRequestException.class, () -> aenderungsService.consumeDirect(""));

        // Verify no interaction with RestTemplate
        verifyNoInteractions(restTemplate);
    }
