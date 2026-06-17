package de.muenchen.ehrenamtjustiz.backend.integration;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import de.muenchen.ehrenamtjustiz.backend.testdata.PersonTestDataBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { EhrenamtJustizApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@AutoConfigureRestTestClient
class EhrenamtJustizIntegrationsTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    public static final String MUENCHEN = "München";

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private PersonRepository personRepository;

    private static UUID uuidKonfiguration;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        konfigurationRepository.deleteAll();

        final Konfiguration konfiguration = konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
        uuidKonfiguration = konfiguration.getId();
    }

    @Test
    void givenNewEWOBuergerDaten_thenCheckNotFound() {

        final var headers = new HttpHeaders(); //NOPMD
        headers.setContentType(MediaType.APPLICATION_JSON);
        final EWOBuergerDatenDto eWOBuergerDatenDto = new EWOBuergerDatenDto();
        eWOBuergerDatenDto.setFamilienname("Test");
        eWOBuergerDatenDto.setVorname("Test");
        eWOBuergerDatenDto.setGeburtsdatum(LocalDate.of(1980, 1, 1));
        eWOBuergerDatenDto.setOrdnungsmerkmal("4712"); // gibt es nicht

        restTestClient.post()
                .uri("/ehrenamtjustiz/pruefenNeuePerson")
                .body(eWOBuergerDatenDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

    }

    @Test
    void givenNewEWOBuergerDaten_thenCheckOK() {

        // new person
        final Person person = new PersonTestDataBuilder().withKonfigurationid(uuidKonfiguration).build();
        personRepository.save(person);

        final var headers = new HttpHeaders(); //NOPMD
        headers.setContentType(MediaType.APPLICATION_JSON);
        final EWOBuergerDatenDto eWOBuergerDatenDto = new EWOBuergerDatenDto();
        eWOBuergerDatenDto.setOrdnungsmerkmal("4711");

        restTestClient.post()
                .uri("/ehrenamtjustiz/pruefenNeuePerson")
                .body(eWOBuergerDatenDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

    }

    @Test
    void givenSavedEWOBuergerDaten_thenStatusOK() {

        final var headers = new HttpHeaders(); //NOPMD
        headers.setContentType(MediaType.APPLICATION_JSON);
        final EWOBuergerDatenDto eWOBuergerDatenDto = new EWOBuergerDatenDto();
        eWOBuergerDatenDto.setId(UUID.randomUUID());
        eWOBuergerDatenDto.setOrdnungsmerkmal("4711");
        eWOBuergerDatenDto.setFamilienname("Müller");
        eWOBuergerDatenDto.setVorname("Hans");
        eWOBuergerDatenDto.setGeburtsort(MUENCHEN);
        eWOBuergerDatenDto.setGeburtsland("Deutschland");
        eWOBuergerDatenDto.setGeburtsdatum(LocalDate.of(1897, 1, 1)); // Ungültig
        eWOBuergerDatenDto.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
        eWOBuergerDatenDto.setFamilienstand("VH");
        eWOBuergerDatenDto.setPostleitzahl("80634");
        eWOBuergerDatenDto.setOrt(MUENCHEN); // gültig
        eWOBuergerDatenDto.setStrasse("Ludwigstr.");
        eWOBuergerDatenDto.setHausnummer("7");
        eWOBuergerDatenDto.setInmuenchenseit(LocalDate.of(2023, 1, 1));
        eWOBuergerDatenDto.setGeschlecht(Geschlecht.MAENNLICH);
        final List<String> sa = new ArrayList<>();
        sa.add("englisch");
        sa.add("deutsch");
        eWOBuergerDatenDto.setStaatsangehoerigkeit(sa);

        restTestClient.post()
                .uri("/ehrenamtjustiz/vorbereitenUndSpeichernPerson")
                .body(eWOBuergerDatenDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PersonDto.class)
                .value(personDto -> {
                    assertNotNull(personDto);
                    assertEquals(eWOBuergerDatenDto.getFamilienname(), personDto.getFamilienname());
                    assertEquals(eWOBuergerDatenDto.getOrdnungsmerkmal(), personDto.getEwoid());
                    assertEquals(eWOBuergerDatenDto.getVorname(), personDto.getVorname());
                    assertEquals(eWOBuergerDatenDto.getGeburtsort(), personDto.getGeburtsort());
                    assertEquals(eWOBuergerDatenDto.getGeburtsland(), personDto.getGeburtsland());
                    assertEquals(eWOBuergerDatenDto.getGeburtsdatum(), personDto.getGeburtsdatum());
                    assertEquals(eWOBuergerDatenDto.getWohnungsstatus(), personDto.getWohnungsstatus());
                    assertEquals(eWOBuergerDatenDto.getFamilienstand(), personDto.getFamilienstand());
                    assertEquals(eWOBuergerDatenDto.getPostleitzahl(), personDto.getPostleitzahl());
                    assertEquals(eWOBuergerDatenDto.getOrt(), personDto.getOrt());
                    assertEquals(eWOBuergerDatenDto.getStrasse(), personDto.getStrasse());
                    assertEquals(eWOBuergerDatenDto.getHausnummer(), personDto.getHausnummer());
                    assertEquals(eWOBuergerDatenDto.getInmuenchenseit(), personDto.getInmuenchenseit());
                    assertEquals(eWOBuergerDatenDto.getGeschlecht(), personDto.getGeschlecht());
                    assertEquals(eWOBuergerDatenDto.getStaatsangehoerigkeit(), personDto.getStaatsangehoerigkeit());

                })
                .returnResult()
                .getResponseBody();

    }

}
