package de.muenchen.ehrenamtjustiz.backend.integration;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.service.EhrenamtJustizService;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import de.muenchen.ehrenamtjustiz.backend.testdata.PersonTestDataBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.client.RestClientException;
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
class AenderungsServiceIntegrationsTest {
    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    public static final String TEST_OM = "4711";
    public static final String TEST_INVALID_OM = "4712";
    public static final String URI_AENDERUNGSSERVICE = "/backendaenderungsservice/aenderungsservicePerson";

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private EhrenamtJustizService ehrenamtJustizService;

    private UUID personId;

    private UUID activeConfigurationId;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        konfigurationRepository.deleteAll();

        // insert new active configuration
        final Konfiguration konfiguration = konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
        activeConfigurationId = konfiguration.getId();

        final Person person = createPerson(konfiguration);
        personRepository.save(person);
        personId = person.getId();
    }

    @Test
    void givenSetUp_thenCheckNotNull() {

        assertNotNull(konfigurationRepository);
        assertNotNull(personRepository);
        assertNotNull(restTestClient);
        assertNotNull(ehrenamtJustizService);
        assertNotNull(personId);

    }

    @Test
    void givenOMWithoutConflict_thenNoConflict() {

        Mockito.when(ehrenamtJustizService.getKonflikteAenderungsService(any(Person.class)))
                .thenReturn(new ArrayList<>(List.of()));

        restTestClient.post()
                .uri(URI_AENDERUNGSSERVICE)
                .body(TEST_OM)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<String>>() {
                })
                .value(konflikte -> {
                    assertNotNull(konflikte);
                    assertEquals(0, konflikte.size());
                })
                .returnResult()
                .getResponseBody();
    }

    @Test
    void givenOMWithConflict_thenConfict() {

        Mockito.when(ehrenamtJustizService.getKonflikteAenderungsService(any(Person.class)))
                .thenReturn(new ArrayList<>(Arrays.asList("Familienname", "Vorname", "Geburtsdatum", "Geschlecht")));

        restTestClient.post()
                .uri(URI_AENDERUNGSSERVICE)
                .body(TEST_OM)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<String>>() {
                })
                .value(konflikte -> {
                    assertNotNull(konflikte);
                    assertEquals("Familienname", konflikte.get(0));
                    assertEquals("Vorname", konflikte.get(1));
                    assertEquals("Geburtsdatum", konflikte.get(2));
                    assertEquals("Geschlecht", konflikte.get(3));
                    assertEquals(4, konflikte.size());
                })
                .returnResult()
                .getResponseBody();

        final Optional<Person> person = personRepository.findById(personId);
        assertEquals(Status.KONFLIKT, person.get().getStatus());
        assertEquals(4, person.get().getKonfliktfeld().size());
        assertEquals("Familienname", person.get().getKonfliktfeld().get(0));
        assertEquals("Vorname", person.get().getKonfliktfeld().get(1));
        assertEquals("Geburtsdatum", person.get().getKonfliktfeld().get(2));
        assertEquals("Geschlecht", person.get().getKonfliktfeld().get(3));

    }

    @Test
    void givenOMAndException_thenIsClientError() {

        Mockito.when(ehrenamtJustizService.getKonflikteAenderungsService(any(Person.class)))
                .thenThrow(new RestClientException("Unexpected error"));

        restTestClient.post()
                .uri(URI_AENDERUNGSSERVICE)
                .body(TEST_OM)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void givenInvalidOM_thenIsClientError() {

        restTestClient.post()
                .uri(URI_AENDERUNGSSERVICE)
                .body(TEST_INVALID_OM)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void givenOMAndMissingActiveConfiguration_thenIsClientError() {

        final Konfiguration konfiguration = konfigurationRepository.findById(activeConfigurationId).get();
        konfiguration.setAktiv(false);
        konfigurationRepository.save(konfiguration);

        restTestClient.post()
                .uri(URI_AENDERUNGSSERVICE)
                .body(TEST_OM)
                .exchange()
                .expectStatus().isBadRequest();

    }

    private static @NotNull Person createPerson(final Konfiguration konfiguration) {
        // new person
        return new PersonTestDataBuilder().withKonfigurationid(konfiguration.getId()).withEwoid(TEST_OM).build();
    }
}
