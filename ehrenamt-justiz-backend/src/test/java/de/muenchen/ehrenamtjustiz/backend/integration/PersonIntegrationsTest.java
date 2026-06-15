package de.muenchen.ehrenamtjustiz.backend.integration;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.RestPageImpl;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonCSVDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonenTableDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.controller.PersonRestController;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import de.muenchen.ehrenamtjustiz.backend.testdata.PersonTestDataBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@Nested
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { EhrenamtJustizApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
@SuppressWarnings({ "PMD.CouplingBetweenObjects", "PMD.AvoidDuplicateLiterals" })
@AutoConfigureRestTestClient
class PersonIntegrationsTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final int ANZAHL_PERSONEN = 123;

    public static final String DEUTSCH = "deutsch";
    public static final String ITALIENISCH = "italienisch";
    public static final String ENGLISCH = "englisch";
    public static final String MUENCHEN = "München";
    public static final String HAMBURG = "Hamburg";

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Autowired
    private PersonRepository personRepository;

    private static UUID uuidKonfiguration;

    private UUID uuidPerson;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        konfigurationRepository.deleteAll();

        // insert new configuration
        final Konfiguration konfiguration = konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
        uuidKonfiguration = konfiguration.getId();

        // insert person
        final Person person = initPerson();
        personRepository.save(person);

        uuidPerson = person.getId();
    }

    private static @NotNull Person initPerson() {
        return new PersonTestDataBuilder().withKonfigurationid(uuidKonfiguration).build();
    }

    @Test
    void givenPerson_thenCheckReadingCSV() {

        // Convert to csv. All attributes are string
        final List<UUID> uuids = new ArrayList<>();
        uuids.add(uuidPerson);
        final Map<String, Object> request = new HashMap<>();
        request.put(PersonRestController.REQUEST_UUIDS, uuids);

        restTestClient.post()
                .uri("/personen/lesenPersonenCSV")
                .body(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<PersonCSVDto>>() {
                })
                .value(personCSVS -> {
                    assertNotNull(personCSVS);
                    assertNotNull(personCSVS.getFirst());
                    assertEquals(uuidKonfiguration.toString(), personCSVS.getFirst().getKonfigurationid());
                    assertEquals(uuidPerson.toString(), personCSVS.getFirst().getId());
                    assertEquals(Status.VORSCHLAG.toReadableString(), personCSVS.getFirst().getStatus());
                    assertEquals("Müller", personCSVS.getFirst().getFamilienname());
                    assertEquals("Hans", personCSVS.getFirst().getVorname());
                    assertEquals("01.01.1980", personCSVS.getFirst().getGeburtsdatum());
                    assertEquals(MUENCHEN, personCSVS.getFirst().getGeburtsort());
                    assertEquals("Deutschland", personCSVS.getFirst().getGeburtsland());
                    assertEquals(Wohnungsstatus.HAUPTWOHNUNG.toReadableString(), personCSVS.getFirst().getWohnungsstatus());
                    assertEquals("VH", personCSVS.getFirst().getFamilienstand());
                    assertEquals("80634", personCSVS.getFirst().getPostleitzahl());
                    assertEquals(MUENCHEN, personCSVS.getFirst().getOrt());
                    assertEquals("Ludwigstr.", personCSVS.getFirst().getStrasse());
                    assertEquals("7", personCSVS.getFirst().getHausnummer());
                    assertEquals("01.01.2023", personCSVS.getFirst().getInmuenchenseit());
                    assertEquals(Geschlecht.MAENNLICH.toReadableString(), personCSVS.getFirst().getGeschlecht());
                    assertEquals("17.09.2024", personCSVS.getFirst().getBewerbungvom());
                    assertEquals("Nein", personCSVS.getFirst().getWarbereitstaetigals());
                    assertEquals("Nein", personCSVS.getFirst().getWarbereitstaetigalsvorvorperiode());
                })
                .returnResult()
                .getResponseBody();

    }

    @Test
    void givenPerson_thenCheckFindPerson() {

        restTestClient.get()
                .uri("/personen/findPersonen?search=Hans&sortDirection=asc&page=0&size=20&status=VORSCHLAG")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<RestPageImpl<PersonenTableDatenDto>>() {
                })
                .value(pageOfPersonen -> {
                    assertNotNull(pageOfPersonen);
                    assertTrue(pageOfPersonen.getNumberOfElements() > 0);

                })
                .returnResult()
                .getResponseBody();

    }

    @Test
    void givenApplication_thenCheckCancel() {

        // Insert person with status INERFASSUNG:
        final Person person = initPerson();
        person.setStatus(Status.INERFASSUNG);
        final Person personInErfassung = personRepository.save(person);

        restTestClient.post()
                .uri("/personen/cancelBewerbung")
                .body(personInErfassung)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PersonDto.class)
                .value(expectedPerson -> {
                    assertNotNull(expectedPerson);

                })
                .returnResult()
                .getResponseBody();

        // Check delete:
        final Optional<Person> personCancelled = personRepository.findById(personInErfassung.getId());

        assertTrue(personCancelled.isEmpty());

    }

    @Test
    void givenApplicationWithInvalidBirthday_thenCheckSettingToSuggestion() {
        // prüfen, ob Geburtsdatum, Wohnort und Staatsangehörigkeit gemäß Konfiguration passt:

        // Eine Person anlegen
        // CPD-OFF
        final Person person = initPerson();
        person.setGeburtsdatum(LocalDate.of(1897, 1, 1)); // Ungültig
        person.setOrt(MUENCHEN); // gültig
        final List<String> sa = new ArrayList<>();
        sa.add(ENGLISCH);
        sa.add(DEUTSCH);
        person.setStaatsangehoerigkeit(sa); // gültig
        // CPD-ON
        personRepository.save(person);

        final UUID[] uuids = { person.getId() };

        restTestClient.post()
                .uri("/personen/validiereAufVorschlagslisteSetzen")
                .body(uuids)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<PersonDto>>() {
                })
                .value(personDto -> {
                    assertNotNull(personDto);
                    assertEquals(1, personDto.size()); // eine fehlerhafte Person

                })
                .returnResult()
                .getResponseBody();
    }

    @Test
    void givenApplicationWithInvalidResidence_thenCheckSettingToSuggestion() {
        // prüfen, ob Geburtsdatum, Wohnort und Staatsangehörigkeit gemäß Konfiguration passt:

        // Eine Person anlegen
        // CPD-OFF
        final Person person = initPerson();
        person.setGeburtsdatum(LocalDate.of(1997, 1, 1)); // gültig
        person.setOrt(HAMBURG); // Ungültig
        final List<String> sa = new ArrayList<>();
        sa.add(ENGLISCH);
        sa.add(DEUTSCH);
        person.setStaatsangehoerigkeit(sa); // gültig
        // CPD-ON
        personRepository.save(person);

        final UUID[] uuids = { person.getId() };

        restTestClient.post()
                .uri("/personen/validiereAufVorschlagslisteSetzen")
                .body(uuids)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<PersonDto>>() {
                })
                .value(personDto -> {
                    assertNotNull(personDto);
                    assertEquals(1, personDto.size()); // eine fehlerhafte Person

                })
                .returnResult()
                .getResponseBody();

    }

    @Test
    void givenApplicationWithInvalidNationality_thenCheckSettingToSuggestion() {
        // prüfen, ob Geburtsdatum, Wohnort und Staatsangehörigkeit gemäß Konfiguration passt:

        // Eine Person anlegen
        // CPD-OFF
        final Person person = initPerson();
        person.setGeburtsdatum(LocalDate.of(1997, 1, 1)); // gültig
        person.setOrt(MUENCHEN); // gültig
        final List<String> sa = new ArrayList<>();
        sa.add(ENGLISCH);
        sa.add(ITALIENISCH);
        person.setStaatsangehoerigkeit(sa); // ungültig
        // CPD-ON
        personRepository.save(person);

        final UUID[] uuids = { person.getId() };

        restTestClient.post()
                .uri("/personen/validiereAufVorschlagslisteSetzen")
                .body(uuids)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<PersonDto>>() {
                })
                .value(personDto -> {
                    assertNotNull(personDto);
                    assertEquals(1, personDto.size()); // eine fehlerhafte Person

                })
                .returnResult()
                .getResponseBody();

    }

    @Test
    void givenApplicationWithValidData_thenCheckSettingToSuggestion() {
        // prüfen, ob Geburtsdatum, Wohnort und Staatsangehörigkeit gemäß Konfiguration passt:

        // Eine Person anlegen
        final Person person = initPerson();
        person.setGeburtsdatum(LocalDate.of(1997, 1, 1)); // gültig
        person.setOrt(MUENCHEN); // gültig
        final List<String> sa = new ArrayList<>();
        sa.add(DEUTSCH);
        sa.add(ITALIENISCH);
        person.setStaatsangehoerigkeit(sa); // gültig
        personRepository.save(person);

        final UUID[] uuids = { person.getId() };

        restTestClient.post()
                .uri("/personen/validiereAufVorschlagslisteSetzen")
                .body(uuids)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(new ParameterizedTypeReference<List<PersonDto>>() {
                })
                .value(personDto -> {
                    assertNotNull(personDto);
                    assertEquals(0, personDto.size()); // Keine fehlerhafte Person

                })
                .returnResult()
                .getResponseBody();

    }

    @Test
    void givenApplication_thenCheckDelete() {

        // Blockweises löschen prüfen
        final UUID[] uuids = new UUID[ANZAHL_PERSONEN];
        for (int i = 0; i < ANZAHL_PERSONEN; i++) {

            // Personen anlegen
            final Person person = initPerson();
            personRepository.save(person);

            uuids[i] = person.getId();
        }

        // Personen löschen
        restTestClient.post()
                .uri("/personen/deletePersonen")
                .body(uuids)
                .exchange()
                .expectStatus().isOk();

        // Prüfungen
        for (int i = 0; i < ANZAHL_PERSONEN; i++) {

            final Optional<Person> personCancelled = personRepository.findById(uuids[i]);

            assertTrue(personCancelled.isEmpty());
        }

    }

    @Test
    void givenApplication_thenCheckMarkAsNotified() {

        restTestClient.post()
                .uri("/personen/alsBenachrichtigtMarkieren")
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void givenApplication_thenCheckUpdate() {

        // Personen anlegen
        final Person person = initPerson();
        person.setStatus(Status.INERFASSUNG); // Muss nach update auf Bewerbung gesetzt sein
        personRepository.save(person);

        restTestClient.post()
                .uri("/personen/updatePerson")
                .body(person)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PersonDto.class)
                .value(personDto -> {
                    assertNotNull(personDto);
                    assertEquals(Status.BEWERBUNG, personDto.getStatus());

                })
                .returnResult()
                .getResponseBody();

        // Person erneut lesen und prüfen, ob Status ist Bewerbung
        final Person personResult = personRepository.findById(person.getId()).orElse(null);
        assertNotNull(personResult);
        assertEquals(Status.BEWERBUNG, personResult.getStatus());

    }

}
