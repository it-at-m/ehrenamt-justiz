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
import de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper.PersonMapper;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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
class PersonIntegrationsTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final int ANZAHL_PERSONEN = 123;

    public static final String DEUTSCH = "deutsch";
    public static final String ITALIENISCH = "italienisch";
    public static final String ENGLISCH = "englisch";
    public static final String MUENCHEN = "München";
    public static final String HAMBURG = "Hamburg";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonMapper personMapper;

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

        final HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(request);

        final ResponseEntity<List<PersonCSVDto>> result = testRestTemplate.exchange("/personen/lesenPersonenCSV", HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());

        final List<PersonCSVDto> personCSVS = result.getBody();

        // check csv data
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

    }

    @Test
    void givenPerson_thenCheckFindPerson() {

        // get persons for table
        final ResponseEntity<RestPageImpl<PersonenTableDatenDto>> result = testRestTemplate.exchange(
                "/personen/findPersonen?search=Hans&sortDirection=asc&page=0&size=20&status=VORSCHLAG", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        final Page<PersonenTableDatenDto> pageOfPersonen = result.getBody();
        assertNotNull(pageOfPersonen);
        assertTrue(pageOfPersonen.getNumberOfElements() > 0);

    }

    @Test
    void givenApplication_thenCheckCancel() {

        // Insert person with status INERFASSUNG:
        final Person person = initPerson();
        person.setStatus(Status.INERFASSUNG);
        final Person personInErfassung = personRepository.save(person);

        PersonDto personInErfassungDto = personMapper.entity2Model(personInErfassung);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("person", personInErfassungDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Person with state INERFASSUNG will be deleted:
        final ResponseEntity<?> result = testRestTemplate.postForEntity("/personen/cancelBewerbung", requestEntity, PersonDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
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

        final HttpEntity<UUID[]> requestEntity = new HttpEntity<>(uuids);

        final ResponseEntity<List<PersonDto>> result = testRestTemplate.exchange("/personen/validiereAufVorschlagslisteSetzen", HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size()); // Ein fehlerhafte Person

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

        final HttpEntity<UUID[]> requestEntity = new HttpEntity<>(uuids);

        final ResponseEntity<List<PersonDto>> result = testRestTemplate.exchange("/personen/validiereAufVorschlagslisteSetzen", HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size()); // Ein fehlerhafte Person

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

        final HttpEntity<UUID[]> requestEntity = new HttpEntity<>(uuids);

        final ResponseEntity<List<PersonDto>> result = testRestTemplate.exchange("/personen/validiereAufVorschlagslisteSetzen", HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().size()); // Ein fehlerhafte Person

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

        final HttpEntity<UUID[]> requestEntity = new HttpEntity<>(uuids);

        final ResponseEntity<List<PersonDto>> result = testRestTemplate.exchange("/personen/validiereAufVorschlagslisteSetzen", HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(0, result.getBody().size()); // keine fehlerhafte Person

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
        final HttpEntity<UUID[]> requestEntity = new HttpEntity<>(uuids);
        final ResponseEntity<Void> result = testRestTemplate.exchange("/personen/deletePersonen", HttpMethod.POST, requestEntity,
                Void.class);

        // Prüfungen
        assertEquals(HttpStatus.OK, result.getStatusCode());

        for (int i = 0; i < ANZAHL_PERSONEN; i++) {

            final Optional<Person> personCancelled = personRepository.findById(uuids[i]);

            assertTrue(personCancelled.isEmpty());
        }

    }

    @Test
    void givenApplication_thenCheckMarkAsNotified() {

        final ResponseEntity<Void> result = testRestTemplate.postForEntity("/personen/alsBenachrichtigtMarkieren", null, Void.class);

        // Prüfungen
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void givenApplication_thenCheckUpdate() {

        // Personen anlegen
        final Person person = initPerson();
        person.setStatus(Status.INERFASSUNG); // Muss nach update auf Bewerbung gesetzt sein
        personRepository.save(person);

        PersonDto personInErfassungDto = personMapper.entity2Model(person);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("person", personInErfassungDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        final ResponseEntity<PersonDto> result = testRestTemplate.postForEntity("/personen/updatePerson", requestEntity, PersonDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        // Person erneut lesen und prüfen, ob Status ist Bewerbung
        final Person personResult = personRepository.findById(person.getId()).orElse(null);
        assertNotNull(personResult);
        assertEquals(Status.BEWERBUNG, personResult.getStatus());

    }

}
