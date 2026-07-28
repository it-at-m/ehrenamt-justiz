package de.muenchen.ehrenamtjustiz.aenderungsservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withGatewayTimeout;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import de.muenchen.ehrenamtjustiz.aenderungsservice.service.AenderungsService;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { AenderungsserviceApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = { "test", "no-security" })
class AenderungsServiceTest {

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Value("${aenderungsservice.backend.server}")
    private String serverBackend;

    @Value("${aenderungsservice.backend.base-path}")
    private String basePathBackend;

    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    private AenderungsService aenderungsService;

    @AfterEach
    void tearDown() {
        mockRestServiceServer.verify();
    }

    @Test
    void givenSetUp_thenCheckNotNull() {

        assertNotNull(aenderungsService);
        assertNotNull(mockRestServiceServer);

    }

    @Test
    void givenOM_thenModificationServiceSuccessful() throws BadRequestException {

        mockRestServiceServer.expect(requestTo(serverBackend + basePathBackend + "/backendaenderungsservice/aenderungsservicePerson")).andExpect(method(POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of()), MediaType.APPLICATION_JSON));

        // Send EWO-OM
        final HttpStatusCode httpStatusCode = aenderungsService.consumeDirect("4711");

        assertEquals(HttpStatus.OK.value(), httpStatusCode.value());

    }

    @Test
    void givenOM_thenModificationServiceWithTimeout() {

        mockRestServiceServer.expect(requestTo(serverBackend + basePathBackend + "/backendaenderungsservice/aenderungsservicePerson")).andExpect(method(POST))
                .andRespond(withGatewayTimeout());

        assertThrows(RuntimeException.class,
                () -> {
                    // Send EWO-OM
                    aenderungsService.consumeDirect("4711");
                });

    }

    @Test
    void givenMissingOM_thenModificationServiceWithBadRequest() throws BadRequestException {

        assertThrows(BadRequestException.class,
                () -> {
                    // Send EWO-OM
                    aenderungsService.consumeDirect(null);
                });

    }

}
