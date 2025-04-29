package de.muenchen.ehrenamtjustiz.backend.integration;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
class EhrenamtJustizIntegrationsTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    public static final String MUENCHEN = "München";

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private PersonRepository personRepository;

    private static UUID uuidKonfiguration;

    @BeforeEach
    void before() {
        konfigurationRepository.deleteAll();

        final Konfiguration konfiguration = new Konfiguration();
        konfiguration.setId(UUID.randomUUID());
        konfiguration.setAktiv(true);
        konfiguration.setEhrenamtjustizart(Ehrenamtjustizart.VERWALTUNGSRICHTER);
        konfiguration.setBezeichnung("Test");
        konfiguration.setAltervon(BigInteger.valueOf(18));
        konfiguration.setAlterbis(BigInteger.valueOf(100));
        konfiguration.setStaatsangehoerigkeit("deutsch");
        konfiguration.setWohnsitz(MUENCHEN);
        konfiguration.setAmtsperiodevon(LocalDate.of(2025, 1, 1));
        konfiguration.setAmtsperiodebis(LocalDate.of(2029, 12, 31));
        konfigurationRepository.save(konfiguration);

        uuidKonfiguration = konfiguration.getId();
    }

    @Test
    void test_pruefenNeuePerson_NOT_FOUND() {

        final var headers = new HttpHeaders(); //NOPMD
        headers.setContentType(MediaType.APPLICATION_JSON);
        final EWOBuergerDatenDto eWOBuergerDatenDto = new EWOBuergerDatenDto();
        eWOBuergerDatenDto.setFamilienname("Test");
        eWOBuergerDatenDto.setVorname("Test");
        eWOBuergerDatenDto.setGeburtsdatum(LocalDate.of(1980, 1, 1));
        eWOBuergerDatenDto.setOrdnungsmerkmal("4712"); // gibt es nicht

        final HttpEntity<EWOBuergerDatenDto> request = new HttpEntity<>(eWOBuergerDatenDto, headers);

        final ResponseEntity<String> result = testRestTemplate.postForEntity("/ehrenamtjustiz/pruefenNeuePerson", request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());

    }

    @Test
    void test_pruefenNeuePerson_FOUND() {

        // new person
        final Person person = new Person();
        person.setId(UUID.randomUUID());
        person.setStatus(Status.VORSCHLAG);
        person.setEwoid("4711");
        person.setFamilienname("Müller");
        person.setVorname("Hans");
        person.setGeburtsort(MUENCHEN);
        person.setGeburtsland("Deutschland");
        person.setGeburtsdatum(LocalDate.of(1897, 1, 1)); // Ungültig
        person.setKonfigurationid(uuidKonfiguration);
        person.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
        person.setFamilienstand("ledig");
        person.setPostleitzahl("80634");
        person.setOrt(MUENCHEN); // gültig
        person.setStrasse("Ludwigstr.");
        person.setHausnummer("7");
        person.setInmuenchenseit(LocalDate.of(2023, 1, 1));
        person.setGeschlecht(Geschlecht.MAENNLICH);
        person.setBewerbungvom(LocalDate.of(2024, 9, 17));
        final List<String> sa = new ArrayList<>();
        sa.add("englisch");
        sa.add("deutsch");
        person.setStaatsangehoerigkeit(sa); // gültig
        personRepository.save(person);

        final var headers = new HttpHeaders(); //NOPMD
        headers.setContentType(MediaType.APPLICATION_JSON);
        final EWOBuergerDatenDto eWOBuergerDatenDto = new EWOBuergerDatenDto();
        eWOBuergerDatenDto.setOrdnungsmerkmal("4711");

        final HttpEntity<EWOBuergerDatenDto> request = new HttpEntity<>(eWOBuergerDatenDto, headers);

        final ResponseEntity<PersonDto> result = testRestTemplate.postForEntity("/ehrenamtjustiz/pruefenNeuePerson", request, PersonDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());

    }

    @Test
    void test_vorbereitenUndSpeichernPerson() {

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
        eWOBuergerDatenDto.setFamilienstand("ledig");
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

        final HttpEntity<EWOBuergerDatenDto> request = new HttpEntity<>(eWOBuergerDatenDto, headers);

        final ResponseEntity<PersonDto> result = testRestTemplate.postForEntity("/ehrenamtjustiz/vorbereitenUndSpeichernPerson", request, PersonDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        final PersonDto personDto = result.getBody();
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

    }

}
