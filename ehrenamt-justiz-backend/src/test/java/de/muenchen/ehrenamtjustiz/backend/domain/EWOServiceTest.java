package de.muenchen.ehrenamtjustiz.backend.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ej.eai.personeninfo.api.EWOBuerger;
import de.muenchen.ej.eai.personeninfo.api.Geschlecht;
import de.muenchen.ej.eai.personeninfo.api.Wohnungsstatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { EhrenamtJustizApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:h2:mem:ej-app;DB_CLOSE_ON_EXIT=FALSE",
                "tomcat.gracefulshutdown.pre-wait-seconds=0" }
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
class EWOServiceTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @BeforeAll
    public void beforeAll() {

        //Standardkonfiguration anlegen
        final Konfiguration k = new Konfiguration();
        k.setId(UUID.randomUUID());
        k.setAktiv(true);
        k.setEhrenamtjustizart(Ehrenamtjustizart.VERWALTUNGSRICHTER);
        k.setBezeichnung("Test");
        k.setAltervon(BigInteger.valueOf(18));
        k.setAlterbis(BigInteger.valueOf(75));
        k.setStaatsangehoerigkeit("deutsch");
        k.setWohnsitz("München");
        k.setAmtsperiodevon(LocalDate.of(2025, 1, 1));
        k.setAmtsperiodebis(LocalDate.of(2029, 12, 31));
        konfigurationRepository.save(k);
    }

    @BeforeEach
    public void beforeEach() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testEWOSuche() throws Exception {

        final EWOBuerger[] ewoBuergers = new EWOBuerger[1];
        ewoBuergers[0] = getEwoBuerger();

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8083/personeninfo/api/eairoutes/ewosuche")))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(ewoBuergers)));

        final EWOBuergerSucheDto eWOBuergerSucheDto = new EWOBuergerSucheDto();
        eWOBuergerSucheDto.setFamilienname("Test");
        eWOBuergerSucheDto.setVorname("Test");
        eWOBuergerSucheDto.setGeburtsdatum(LocalDate.of(1980, 1, 1));

        final HttpEntity<EWOBuergerSucheDto> requestEntity = new HttpEntity<>(eWOBuergerSucheDto);

        final ResponseEntity<List<EWOBuergerDatenDto>> result = testRestTemplate.exchange("/ehrenamtjustiz/ewoSuche", HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        testValues(ewoBuergers[0], result.getBody().getFirst());

    }

    @Test
    void testEwoSucheMitOM() throws Exception {
        final EWOBuerger eWOBuerger = getEwoBuerger();

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI("http://localhost:8083/personeninfo/api/eairoutes/ewosuchemitom/4711")))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(eWOBuerger)));

        final ResponseEntity<EWOBuergerDatenDto> result = testRestTemplate.exchange("/ehrenamtjustiz/ewoSucheMitOM?om=4711", HttpMethod.GET, null,
                EWOBuergerDatenDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        testValues(eWOBuerger, result.getBody());

    }

    private static EWOBuerger getEwoBuerger() {

        final EWOBuerger eWOBuerger = new EWOBuerger();
        eWOBuerger.setFamilienname("Königsfamilie");
        eWOBuerger.setVorname("Christopher Manfred");
        eWOBuerger.setGeburtsdatum(LocalDate.of(1940, 1, 3));
        eWOBuerger.setGeschlecht(Geschlecht.männlich);
        eWOBuerger.setOrdnungsmerkmal("5");
        eWOBuerger.setGeburtsort("München");
        eWOBuerger.setGeburtsland("Deutschland");
        eWOBuerger.setFamilienstand("VH");
        eWOBuerger.setStrasse("Taulerstr.");
        eWOBuerger.setHausnummer("6");
        eWOBuerger.setPostleitzahl("80906");
        eWOBuerger.setOrt("München");
        eWOBuerger.setInMuenchenSeit(LocalDate.of(1940, 1, 3));
        eWOBuerger.setWohnungsstatus(Wohnungsstatus.Hauptwohnung);
        final List<String> sa = new ArrayList<>();
        sa.add("deutsch");
        eWOBuerger.setStaatsangehoerigkeit(sa);
        return eWOBuerger;

    }

    private static void testValues(final EWOBuerger eWOBuergerExpected, final EWOBuergerDatenDto eWOBuergerActual) {

        assertEquals(eWOBuergerExpected.getFamilienname(), eWOBuergerActual.getFamilienname());
        assertEquals(eWOBuergerExpected.getVorname(), eWOBuergerActual.getVorname());
        assertEquals(eWOBuergerExpected.getGeburtsdatum(), eWOBuergerActual.getGeburtsdatum());
        assertEquals(eWOBuergerExpected.getOrdnungsmerkmal(), eWOBuergerActual.getOrdnungsmerkmal());
        assertEquals(eWOBuergerExpected.getGeburtsort(), eWOBuergerActual.getGeburtsort());
        assertEquals(eWOBuergerExpected.getGeburtsland(), eWOBuergerActual.getGeburtsland());
        assertEquals(eWOBuergerExpected.getFamilienstand(), eWOBuergerActual.getFamilienstand());
        assertEquals(eWOBuergerExpected.getStrasse(), eWOBuergerActual.getStrasse());
        assertEquals(eWOBuergerExpected.getHausnummer(), eWOBuergerActual.getHausnummer());
        assertEquals(eWOBuergerExpected.getPostleitzahl(), eWOBuergerActual.getPostleitzahl());
        assertEquals(eWOBuergerExpected.getOrt(), eWOBuergerActual.getOrt());
        assertEquals(eWOBuergerExpected.getInMuenchenSeit(), eWOBuergerActual.getInmuenchenseit());
        assertEquals(eWOBuergerExpected.getStaatsangehoerigkeit(), eWOBuergerActual.getStaatsangehoerigkeit());

    }

}
