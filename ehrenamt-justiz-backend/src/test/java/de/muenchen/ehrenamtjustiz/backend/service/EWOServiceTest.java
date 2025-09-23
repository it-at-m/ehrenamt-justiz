package de.muenchen.ehrenamtjustiz.backend.service;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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

    @MockitoBean
    private RestTemplate restTemplate;

    @Autowired
    private EWOServiceImpl ewoService;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @BeforeEach
    void setUp() {

        // insert new active configuration
        konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
    }

    @Test
    void testEWOService() {

        assertNotNull(restTemplate);
        assertNotNull(ewoService);
        assertNotNull(konfigurationRepository);

    }

    @Test
    void testEwoSucheMitOM_Success() {

        final EWOBuerger expectedEWOBuerger = EWO_BUERGER;

        final ResponseEntity<EWOBuerger> responseEntity = ResponseEntity.ok(expectedEWOBuerger);
        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenReturn(responseEntity);

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertEWOResponse(expectedEWOBuerger, actualDto);

    }

    @Test
    void testEwoSucheMitOM_HttpClientErrorException() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
    }

    @Test
    void testEwoSucheMitOM_ResourceAccessException() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
    }

    @Test
    void testEwoSucheMitOM_RestClientException() {

        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenThrow(new RestClientException("Unexpected error"));

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
    }

    @Test
    void testEwoSuche_Success() {

        final EWOBuerger expectedEWOBuerger = EWO_BUERGER;

        final EWOBuerger[] expectedEWOBuergers = { expectedEWOBuerger };

        final ResponseEntity<EWOBuerger[]> responseEntity = ResponseEntity.ok(expectedEWOBuergers);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(EWOBuerger[].class)))
                .thenReturn(responseEntity);

        final List<EWOBuergerDatenDto> eWOBuergerDatenDto = ewoService.ewoSuche(EWO_BUERGER_SUCHE_DTO);

        assertEWOResponse(expectedEWOBuerger, eWOBuergerDatenDto.getFirst());

    }

    @Test
    void testEwoSuche_HttpClientErrorException() {

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(EWOBuerger[].class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(HttpClientErrorException.class, () -> ewoService.ewoSuche(EWO_BUERGER_SUCHE_DTO));

    }

    @Test
    void testEwoSucheMitOMAenderungsService_Success() {

        final EWOBuerger expectedEWOBuerger = EWO_BUERGER;

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

    @Test
    void testEwoEaiStatus_Success() {

        final ResponseEntity<Void> responseEntity = ResponseEntity.ok(null);

        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenReturn(responseEntity);

        final ResponseEntity<String> status = ewoService.ewoEaiStatus();

        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals(EhrenamtJustizUtility.STATUS_UP, status.getBody());

    }

    @Test
    void testEwoEaiStatus_Timeout() {

        final ResponseEntity<Void> responseEntity = ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(null);

        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenReturn(responseEntity);

        final ResponseEntity<String> status = ewoService.ewoEaiStatus();

        assertEquals(HttpStatus.REQUEST_TIMEOUT, status.getStatusCode());
        assertEquals(EhrenamtJustizUtility.STATUS_DOWN, status.getBody());

    }

    @Test
    void testEwoEaiStatus_Exception() {

        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenThrow(new RestClientException("Unexpected error"));

        final ResponseEntity<String> status = ewoService.ewoEaiStatus();

        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals(EhrenamtJustizUtility.STATUS_DOWN, status.getBody());

    }

    @Test
    void testEwoEaiStatus_ServiceUnavailable_MapsDown() {
        final ResponseEntity<Void> responseEntity = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenReturn(responseEntity);

        final ResponseEntity<String> status = ewoService.ewoEaiStatus();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, status.getStatusCode());
        assertEquals(EhrenamtJustizUtility.STATUS_DOWN, status.getBody());
    }

    @Test
    void testEwoEaiStatus_ResourceAccessException_TreatedAsDown() {
        when(restTemplate.getForEntity(anyString(), eq(Void.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        final ResponseEntity<String> status = ewoService.ewoEaiStatus();

        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals(EhrenamtJustizUtility.STATUS_DOWN, status.getBody());
    }

    @Test
    void testEwoSucheMitOM_NullBody_ReturnsNull() {
        final ResponseEntity<EWOBuerger> responseEntity = ResponseEntity.ok(null);
        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenReturn(responseEntity);

        final EWOBuergerDatenDto actualDto = ewoService.ewoSucheMitOM("1");

        assertNull(actualDto);
        verify(restTemplate, times(1)).exchange(any(RequestEntity.class), eq(EWOBuerger.class));
    }

    @Test
    void testEwoSuche_EmptyArray_ReturnsEmptyList() {
        final EWOBuerger[] empty = new EWOBuerger[0];
        final ResponseEntity<EWOBuerger[]> responseEntity = ResponseEntity.ok(empty);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(EWOBuerger[].class)))
                .thenReturn(responseEntity);

        final List<EWOBuergerDatenDto> result = ewoService.ewoSuche(EWO_BUERGER_SUCHE_DTO);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(restTemplate, times(1)).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(EWOBuerger[].class));
    }

    @Test
    void testEwoSuche_NullBody_ThrowsNullPointerException() {
        final ResponseEntity<EWOBuerger[]> responseEntity = ResponseEntity.ok(null);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(EWOBuerger[].class)))
                .thenReturn(responseEntity);

        assertThrows(NullPointerException.class, () -> ewoService.ewoSuche(EWO_BUERGER_SUCHE_DTO));

    }

    @Test
    void testEwoSuche_MultipleResults_AllMapped() {
        final EWOBuerger first = EWO_BUERGER;
        final EWOBuerger second = EWOBuerger.builder()
                .ordnungsmerkmal("2")
                .familienname("Meier")
                .geburtsname("Schmidt")
                .vorname("Claudia")
                .geburtsdatum(LocalDate.of(1975, 5, 5))
                .geschlecht(Geschlecht.WEIBLICH)
                .akademischerGrad("")
                .geburtsort("Augsburg")
                .geburtsland("Deutschland")
                .familienstand("VH")
                .strasse("Maximilianstr.")
                .hausnummer("22")
                .appartmentnummer(null)
                .buchstabeHausnummer(null)
                .stockwerk(null)
                .teilnummerHausnummer(null)
                .zusatz(null)
                .postleitzahl("86150")
                .ort("Augsburg")
                .inMuenchenSeit(null)
                .wohnungsgeber(null)
                .wohnungsstatus(Wohnungsstatus.NEBENWOHNUNG)
                .staatsangehoerigkeit(List.of("deutsch"))
                .auskunftssperren(List.of())
                .build();

        final ResponseEntity<EWOBuerger[]> responseEntity = ResponseEntity.ok(new EWOBuerger[] { first, second });
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(EWOBuerger[].class)))
                .thenReturn(responseEntity);

        final List<EWOBuergerDatenDto> result = ewoService.ewoSuche(EWO_BUERGER_SUCHE_DTO);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEWOResponse(first, result.get(0));
        // Validate key fields for second entry as a sanity check
        assertEquals(second.getOrdnungsmerkmal(), result.get(1).getOrdnungsmerkmal());
        assertEquals(second.getFamilienname(), result.get(1).getFamilienname());
        assertEquals(second.getVorname(), result.get(1).getVorname());
        assertEquals(second.getGeburtsort(), result.get(1).getGeburtsort());
        assertEquals(second.getGeburtsland(), result.get(1).getGeburtsland());
        assertEquals(second.getWohnungsstatus().name(), result.get(1).getWohnungsstatus().name());
    }

    @Test
    void testEwoSucheMitOM_RequestEntity_HasGetAndUri() {
        final ResponseEntity<EWOBuerger> responseEntity = ResponseEntity.ok(EWO_BUERGER);

        final ArgumentCaptor<RequestEntity> captor = ArgumentCaptor.forClass(RequestEntity.class);
        when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                .thenReturn(responseEntity);

        ewoService.ewoSucheMitOM("1");

        verify(restTemplate, times(1)).exchange(captor.capture(), eq(EWOBuerger.class));
        final RequestEntity<?> req = captor.getValue();
        assertNotNull(req);
        assertEquals(HttpMethod.GET, req.getMethod());
        assertNotNull(req.getUrl());
        // Basic sanity: the OM should appear somewhere in the request URL (query or path)
        // We avoid asserting the exact URL to keep tests resilient to refactors.
        assertTrue(req.getUrl().toString().contains("1"));
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
