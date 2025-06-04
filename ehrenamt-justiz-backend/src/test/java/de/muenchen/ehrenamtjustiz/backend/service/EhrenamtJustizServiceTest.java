package de.muenchen.ehrenamtjustiz.backend.service;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
class EhrenamtJustizServiceTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    @Autowired
    private EhrenamtJustizService ehrenamtJustizService;

    @MockitoBean
    private EWOService ewoService;

    @Test
    void testValidEhrenamtJustizService() {

        assertNotNull(ehrenamtJustizService);
        assertNotNull(ewoService);

    }

    @Test
    void testNoKonflikte() {

        final Person person = getBewerberDaten();

        final EWOBuergerDatenDto ewoBuergerDatenDto = EhrenamtJustizUtility.getEwoBuergerDatenDto(person);

        when(ewoService.ewoSucheMitOM(any(String.class))).thenReturn(ewoBuergerDatenDto);

        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        assertEquals(0, konflikte.size());

    }

    @Test
    void testKonfliktEwoBuergerDatenLeer() {

        final Person person = getBewerberDaten();

        final EWOBuergerDatenDto ewoBuergerDatenDto = EWOBuergerDatenDto.builder().build();

        when(ewoService.ewoSucheMitOM(any(String.class))).thenReturn(ewoBuergerDatenDto);

        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        assertEquals(24, konflikte.size());

    }

    @Test
    void testKonfliktBewerberDatenLeer() {

        Person person = getBewerberDaten();

        final EWOBuergerDatenDto ewoBuergerDatenDto = EhrenamtJustizUtility.getEwoBuergerDatenDto(person);

        person = Person.builder().build();

        when(ewoService.ewoSucheMitOM(null)).thenReturn(ewoBuergerDatenDto);

        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        assertEquals(24, konflikte.size());

    }

    @Test
    void testKonflikteEwoServiceLiefertKeineDaten() {

        final Person person = getBewerberDaten();

        when(ewoService.ewoSucheMitOM(any(String.class))).thenReturn(null);

        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        assertEquals(1, konflikte.size());
        assertEquals(EhrenamtJustizUtility.ERROR_NO_HITS, konflikte.getFirst());

    }

    private static Person getBewerberDaten() {
        return Person.builder()
                .id(UUID.randomUUID())
                .ewoid("1")
                .konfigurationid(UUID.randomUUID())
                .familienname("Huber")
                .geburtsname("Müller")
                .vorname("Peter")
                .geburtsdatum(LocalDate.of(1960, 1, 1))
                .geschlecht(Geschlecht.MAENNLICH)
                .akademischergrad("Dr.")
                .geburtsort("München")
                .geburtsland("Deutschland")
                .familienstand("LD")
                .strasse("Ludwigstr.")
                .hausnummer("1")
                .appartmentnummer("7")
                .buchstabehausnummer("A")
                .stockwerk("5")
                .teilnummerhausnummer("1")
                .adresszusatz("-")
                .postleitzahl("80634")
                .ort("München")
                .inmuenchenseit(LocalDate.of(2000, 1, 1))
                .wohnungsgeber("Fam. Bauer")
                .wohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG)
                .derzeitausgeuebterberuf("Lehrer")
                .arbeitgeber("LHM München").telefonnummer("089-3084544")
                .telefongesch("089-233-30584")
                .telefonmobil("0151-308543544")
                .mailadresse("peter.huber@gmx.de")
                .ausgeuebteehrenaemter("-")
                .onlinebewerbung(false)
                .neuervorschlag(false)
                .warbereitstaetigals(false)
                .warbereitstaetigalsvorvorperiode(false)
                .bewerbungvom(LocalDate.now())
                .status(Status.VORSCHLAG)
                .bemerkung("-")
                .staatsangehoerigkeit(Stream.of("deutsch", "englisch").collect(Collectors.toList()))
                .auskunftssperre(Stream.of("S").collect(Collectors.toList()))
                .konfliktfeld(Collections.emptyList()).build();
    }

}
