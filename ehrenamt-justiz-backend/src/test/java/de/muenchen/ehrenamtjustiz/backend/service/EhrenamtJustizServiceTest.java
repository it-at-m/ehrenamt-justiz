package de.muenchen.ehrenamtjustiz.backend.service;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.testdata.PersonTestDataBuilder;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import java.util.List;
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
    public static final int ANZAHL_KONFLIKTE = 24;

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

        when(ewoService.ewoSucheMitOM(anyString())).thenReturn(ewoBuergerDatenDto);

        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        assertEquals(0, konflikte.size());

    }

    @Test
    void testKonfliktEwoBuergerDatenLeer() {

        final EWOBuergerDatenDto emptyEwoBuergerDatenDto = EWOBuergerDatenDto.builder().build();

        when(ewoService.ewoSucheMitOM(anyString())).thenReturn(emptyEwoBuergerDatenDto);

        final Person person = getBewerberDaten();

        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        assertEquals(ANZAHL_KONFLIKTE, konflikte.size());

        checkKonfliktFelder(konflikte);

    }

    @Test
    void testKonfliktBewerberDatenLeer() {

        final Person person = getBewerberDaten();
        final EWOBuergerDatenDto ewoBuergerDatenDto = EhrenamtJustizUtility.getEwoBuergerDatenDto(person);

        when(ewoService.ewoSucheMitOM(isNull())).thenReturn(ewoBuergerDatenDto);

        final Person emptyPerson = Person.builder().build();

        final List<String> konflikte = ehrenamtJustizService.getKonflikte(emptyPerson);

        assertEquals(ANZAHL_KONFLIKTE, konflikte.size());

        checkKonfliktFelder(konflikte);

    }

    @Test
    void testKonflikteEwoServiceLiefertKeineDaten() {

        final Person person = getBewerberDaten();

        when(ewoService.ewoSucheMitOM(anyString())).thenReturn(null);

        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        assertEquals(1, konflikte.size());
        assertEquals(EhrenamtJustizUtility.ERROR_NO_HITS, konflikte.getFirst());

    }

    @Test
    void testAenderungsservice_NoKonflikte() {

        final Person person = getBewerberDaten();

        final EWOBuergerDatenDto ewoBuergerDatenDto = EhrenamtJustizUtility.getEwoBuergerDatenDto(person);

        when(ewoService.ewoSucheMitOMAenderungsService(anyString())).thenReturn(ewoBuergerDatenDto);

        final List<String> konflikte = ehrenamtJustizService.getKonflikteAenderungsService(person);

        assertEquals(0, konflikte.size());

    }

    @Test
    void testAenderungsservice_KonfliktEwoBuergerDatenLeer() {

        final EWOBuergerDatenDto emptyEwoBuergerDatenDto = EWOBuergerDatenDto.builder().build();

        when(ewoService.ewoSucheMitOMAenderungsService(anyString())).thenReturn(emptyEwoBuergerDatenDto);

        final Person person = getBewerberDaten();

        final List<String> konflikte = ehrenamtJustizService.getKonflikteAenderungsService(person);

        assertEquals(ANZAHL_KONFLIKTE, konflikte.size());

        checkKonfliktFelder(konflikte);

    }

    @Test
    void testAenderungsservice_KonfliktBewerberDatenLeer() {

        final Person person = getBewerberDaten();
        final EWOBuergerDatenDto ewoBuergerDatenDto = EhrenamtJustizUtility.getEwoBuergerDatenDto(person);

        when(ewoService.ewoSucheMitOMAenderungsService(isNull())).thenReturn(ewoBuergerDatenDto);

        final Person emptyPerson = Person.builder().build();

        final List<String> konflikte = ehrenamtJustizService.getKonflikteAenderungsService(emptyPerson);

        assertEquals(ANZAHL_KONFLIKTE, konflikte.size());

        checkKonfliktFelder(konflikte);

    }

    @Test
    void testAenderungsservice_KonflikteEwoServiceLiefertKeineDaten() {

        final Person person = getBewerberDaten();

        when(ewoService.ewoSucheMitOMAenderungsService(anyString())).thenReturn(null);

        final List<String> konflikte = ehrenamtJustizService.getKonflikteAenderungsService(person);

        assertEquals(0, konflikte.size());

    }

    private static void checkKonfliktFelder(final List<String> konflikte) {
        final List<String> expectedConflictFields = List.of(
                "Ordnungsmerkmal", "Vorname", "Familienname", "Geburtsname",
                "Geburtsdatum", "Geschlecht", "Familienstand", "Geburtsland",
                "Geburtsort", "Staatsangehoerigkeit", "Akademischergrad",
                "Postleitzahl", "Ort", "Strasse", "Hausnummer",
                "Buchstabehausnummer", "Wohnungsstatus", "Appartmentnummer",
                "Teilnummerhausnummer", "Stockwerk", "Adresszusatz",
                "Wohnungsgeber", "Inmuenchenseit", "Auskunftssperre");

        expectedConflictFields.forEach(field -> assertTrue(konflikte.contains(field),
                "Expected conflict field '" + field + "' not found in conflicts"));
    }

    private static Person getBewerberDaten() {
        return new PersonTestDataBuilder().withGeburtsname("Meyer").withAkademischergrad("Dr.").withAppartmentnummer("7").withBuchstabehausnummer("A")
                .withStockwerk("1")
                .withTeilnummerhausnummer("1").withAdresszusatz("--").withWohnungsgeber("Fam. Bauer").withAuskunftssperre(List.of("S")).build();

    }

}
