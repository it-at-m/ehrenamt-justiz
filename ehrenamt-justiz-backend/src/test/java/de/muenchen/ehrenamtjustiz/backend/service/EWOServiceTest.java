package de.muenchen.ehrenamtjustiz.backend.service;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withGatewayTimeout;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.api.Geschlecht;
import de.muenchen.ehrenamtjustiz.api.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.service.impl.EWOServiceImpl;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import de.muenchen.ehrenamtjustiz.exception.AenderungsServiceException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import tools.jackson.databind.ObjectMapper;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { EhrenamtJustizApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockRestServiceServer
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class EWOServiceTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final EWOBuerger EWO_BUERGER = EWOBuerger.builder()
            .ordnungsmerkmal("1")
            .familienname("Huber")
            .geburtsname("Müller")
            .vorname("Peter")
            .geburtsdatum(LocalDate.of(1960, 1, 1))
            .geschlecht(Geschlecht.MAENNLICH)
            .akademischerGrad("Dr.")
            .geburtsort("München")
            .geburtsland("Deutschland")
            .familienstand("LD")
            .strasse("Ludwigstr.")
            .hausnummer("1")
            .appartmentnummer("7")
            .buchstabeHausnummer("a")
            .stockwerk("5")
            .teilnummerHausnummer("1")
            .zusatz("-")
            .postleitzahl("80634")
            .ort("München")
            .inMuenchenSeit(LocalDate.of(2000, 1, 1))
            .wohnungsgeber("Fam. Bauer")
            .wohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG)
            .staatsangehoerigkeit(List.of("deutsch", "englisch"))
            .auskunftssperren(List.of("S")).build();

    private static final EWOBuergerSucheDto EWO_BUERGER_SUCHE_DTO = EWOBuergerSucheDto.builder()
            .vorname("Peter")
            .familienname("Huber")
            .geburtsdatum(LocalDate.of(1960, 1, 1)).build();

    @Value("${ewo.eai.basepath}")
    private String basepathewoeai;

    @Value("${ewo.eai.basepathprefix}")
    private String basepathewoeaiprefix;

    @Value("${ewo.eai.server}")
    private String serverewoeai;

    @Autowired
    private EWOServiceImpl ewoService;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Autowired
    public ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

        // insert new active configuration
        konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
    }

    @Test
    void givenSetUp_thenCheckNotNull() {

        assertNotNull(mockRestServiceServer);
        assertNotNull(ewoService);
        assertNotNull(konfigurationRepository);

    }

    @Test
    void givenCitizen_thenReadingWithSuccess() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuchemitom/1")).andExpect(method(GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(EWO_BUERGER), MediaType.APPLICATION_JSON));

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertEWOResponse(EWO_BUERGER, actualDto);

        mockRestServiceServer.verify();

    }

    @Test
    void givenCitizen_thenReadingWithBadRequest() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuchemitom/1")).andExpect(method(GET))
                .andRespond(withBadRequest());

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
    }

    @Test
    void givenCitizen_thenReadingWithTimeout() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuchemitom/1")).andExpect(method(GET))
                .andRespond(withGatewayTimeout());

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
    }

    @Test
    void givenCitizen_thenReadingWitServerError() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuchemitom/1")).andExpect(method(GET))
                .andRespond(withServerError());

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
    }

    @Test
    void givenCitizen_thenSearchWithSuccess() {

        final EWOBuerger expectedEWOBuerger = EWO_BUERGER;

        final EWOBuerger[] expectedEWOBuergers = { expectedEWOBuerger };

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuche")).andExpect(method(POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(expectedEWOBuergers), MediaType.APPLICATION_JSON));

        final List<EWOBuergerDatenDto> eWOBuergerDatenDto = ewoService.ewoSuche(EWO_BUERGER_SUCHE_DTO);

        assertEWOResponse(expectedEWOBuerger, eWOBuergerDatenDto.getFirst());

    }

    @Test
    void givenCitizen_thenSearchWithBadRequest() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuche")).andExpect(method(POST))
                .andRespond(withBadRequest());

        assertThrows(HttpClientErrorException.class, () -> ewoService.ewoSuche(EWO_BUERGER_SUCHE_DTO));

    }

    @Test
    void givenCitizen_thenReadingForModificationServiceWithSuccess() {

        final EWOBuerger expectedEWOBuerger = EWO_BUERGER;

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuchemitom/1")).andExpect(method(GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(expectedEWOBuerger), MediaType.APPLICATION_JSON));

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOMAenderungsService("1");

        assertEWOResponse(expectedEWOBuerger, actualDto);

    }

    @Test
    void givenCitizen_thenReadingForModificationServiceWithBadRequest() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuchemitom/1")).andExpect(method(GET))
                .andRespond(withBadRequest());

        assertThrows(AenderungsServiceException.class, () -> ewoService.ewoSucheMitOMAenderungsService("1"));

    }

    @Test
    void givenCitizen_thenReadingForModificationServiceWithTimeout() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuchemitom/1")).andExpect(method(GET))
                .andRespond(withGatewayTimeout());

        assertThrows(AenderungsServiceException.class, () -> ewoService.ewoSucheMitOMAenderungsService("1"));
    }

    @Test
    void givenCitizen_thenReadingForModificationServiceWithServerError() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeai + "/eairoutes/ewosuchemitom/1")).andExpect(method(GET))
                .andRespond(withServerError());

        assertThrows(AenderungsServiceException.class, () -> ewoService.ewoSucheMitOMAenderungsService("1"));
    }

    @Test
    void givenRunningEAI_thenCheckStatusWithSuccess() {

        final ResponseEntity<Void> responseEntity = ResponseEntity.ok(null);

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeaiprefix + "/actuator/health")).andExpect(method(GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(responseEntity), MediaType.APPLICATION_JSON));

        final ResponseEntity<String> status = ewoService.ewoEaiStatus();

        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals(EhrenamtJustizUtility.STATUS_UP, status.getBody());

    }

    @Test
    void givenRunningEAI_thenCheckStatusWithTimeout() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeaiprefix + "/actuator/health")).andExpect(method(GET))
                .andRespond(withGatewayTimeout());

        final ResponseEntity<String> status = ewoService.ewoEaiStatus();

        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals(EhrenamtJustizUtility.STATUS_DOWN, status.getBody());

    }

    @Test
    void givenRunningEAI_thenCheckStatusWithServerError() {

        mockRestServiceServer.expect(requestTo(serverewoeai + basepathewoeaiprefix + "/actuator/health")).andExpect(method(GET))
                .andRespond(withServerError());

        final ResponseEntity<String> status = ewoService.ewoEaiStatus();

        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals(EhrenamtJustizUtility.STATUS_DOWN, status.getBody());

    }

    private static void assertEWOResponse(final EWOBuerger expectedEWOBuerger, final EWOBuergerDatenDto actualDto) {
        assertEquals(expectedEWOBuerger.getOrdnungsmerkmal(), actualDto.getOrdnungsmerkmal());
        assertEquals(expectedEWOBuerger.getFamilienname(), actualDto.getFamilienname());
        assertEquals(expectedEWOBuerger.getGeburtsname(), actualDto.getGeburtsname());
        assertEquals(expectedEWOBuerger.getVorname(), actualDto.getVorname());
        assertEquals(expectedEWOBuerger.getGeburtsdatum(), actualDto.getGeburtsdatum());
        assertEquals(expectedEWOBuerger.getGeschlecht().name(), actualDto.getGeschlecht().name());
        assertEquals(expectedEWOBuerger.getAkademischerGrad(), actualDto.getAkademischergrad());
        assertEquals(expectedEWOBuerger.getGeburtsort(), actualDto.getGeburtsort());
        assertEquals(expectedEWOBuerger.getGeburtsland(), actualDto.getGeburtsland());
        assertEquals(expectedEWOBuerger.getFamilienstand(), actualDto.getFamilienstand());
        assertEquals(expectedEWOBuerger.getStrasse(), actualDto.getStrasse());
        assertEquals(expectedEWOBuerger.getHausnummer(), actualDto.getHausnummer());
        assertEquals(expectedEWOBuerger.getAppartmentnummer(), actualDto.getAppartmentnummer());
        assertEquals(expectedEWOBuerger.getBuchstabeHausnummer(), actualDto.getBuchstabehausnummer());
        assertEquals(expectedEWOBuerger.getStockwerk(), actualDto.getStockwerk());
        assertEquals(expectedEWOBuerger.getTeilnummerHausnummer(), actualDto.getTeilnummerhausnummer());
        assertEquals(expectedEWOBuerger.getZusatz(), actualDto.getAdresszusatz());
        assertEquals(expectedEWOBuerger.getPostleitzahl(), actualDto.getPostleitzahl());
        assertEquals(expectedEWOBuerger.getOrt(), actualDto.getOrt());
        assertEquals(expectedEWOBuerger.getInMuenchenSeit(), actualDto.getInmuenchenseit());
        assertEquals(expectedEWOBuerger.getWohnungsgeber(), actualDto.getWohnungsgeber());
        assertEquals(expectedEWOBuerger.getWohnungsstatus().name(), actualDto.getWohnungsstatus().name());
        assertEquals(expectedEWOBuerger.getStaatsangehoerigkeit(), actualDto.getStaatsangehoerigkeit());
        assertEquals(expectedEWOBuerger.getAuskunftssperren(), actualDto.getAuskunftssperre());
    }

}
