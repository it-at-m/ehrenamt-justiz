package de.muenchen.ehrenamtjustiz.backend.service;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.api.Geschlecht;
import de.muenchen.ehrenamtjustiz.api.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.service.impl.EWOServiceImpl;
import de.muenchen.ehrenamtjustiz.exception.AenderungsServiceException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
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
class EWOServiceTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private EWOServiceImpl ewoService;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @BeforeEach
    void setUp() {

        // insert new configuration
        final Konfiguration konfiguration = new Konfiguration();
        konfiguration.setId(UUID.randomUUID());
        konfiguration.setAktiv(true);
        konfiguration.setEhrenamtjustizart(Ehrenamtjustizart.VERWALTUNGSRICHTER);
        konfiguration.setBezeichnung("Verwaltungsrichter");
        konfiguration.setAltervon(BigInteger.valueOf(25));
        konfiguration.setAlterbis(BigInteger.valueOf(120));
        konfiguration.setStaatsangehoerigkeit("deutsch");
        konfiguration.setWohnsitz("München");
        konfiguration.setAmtsperiodevon(LocalDate.of(2030, 1, 1));
        konfiguration.setAmtsperiodebis(LocalDate.of(2034, 12, 31));
        konfigurationRepository.save(konfiguration);
    }

    @Test
    void test_EWOService() {

        assertNotNull(restTemplate);
        assertNotNull(ewoService);
        assertNotNull(konfigurationRepository);

    }

    @Test
    void testEwoSucheMitOM_Success() {

        final EWOBuerger expectedEWOBuerger = getEwoBuerger();

        final ResponseEntity<EWOBuerger> responseEntity = ResponseEntity.ok(expectedEWOBuerger);
        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenReturn(responseEntity);

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertEWOResponse(expectedEWOBuerger, actualDto);

    }

    @Test
    void testEwoSucheMitOM_ClientError() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
    }

    @Test
    void testEwoSucheMitOM_NetworkError() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
    }

    @Test
    void testEwoSucheMitOM_OtherError() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenThrow(new RestClientException("Unexpected error"));

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
    }

    @Test
    void testEwoSucheMitOMAenderungsService_Success() {

        final EWOBuerger expectedEWOBuerger = getEwoBuerger();

        final ResponseEntity<EWOBuerger> responseEntity = ResponseEntity.ok(expectedEWOBuerger);
        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenReturn(responseEntity);

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOMAenderungsService("1");

        assertEWOResponse(expectedEWOBuerger, actualDto);

    }

    @Test
    void testEwoSucheMitOMAenderungsService_HttpClientErrorException() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(AenderungsServiceException.class, () -> ewoService.ewoSucheMitOMAenderungsService("1"));

    }

    @Test
    void testEwoSucheMitOMAenderungsService_ResourceAccessException() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        assertThrows(AenderungsServiceException.class, () -> ewoService.ewoSucheMitOMAenderungsService("1"));
    }

    @Test
    void testEwoSucheMitOMAenderungsService_RestClientException() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenThrow(new RestClientException("Unexpected error"));

        assertThrows(AenderungsServiceException.class, () -> ewoService.ewoSucheMitOMAenderungsService("1"));
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

    private static EWOBuerger getEwoBuerger() {
        return EWOBuerger.builder()
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
    }
}
