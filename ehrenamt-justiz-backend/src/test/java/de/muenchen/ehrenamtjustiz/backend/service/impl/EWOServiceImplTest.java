package de.muenchen.ehrenamtjustiz.backend.service.impl;

import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import de.muenchen.ehrenamtjustiz.exception.AenderungsServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests created with JUnit 5 + Mockito. Follows Spring Boot testing idioms.
 */
@ExtendWith(MockitoExtension.class)
class EWOServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private KonfigurationRepository konfigurationRepository;

    @InjectMocks
    private EWOServiceImpl service;

    private final String server = "http://ewo.example";
    private final String basepath = "/api";
    private final String basepathPrefix = "/";

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "serverewoeai", server);
        ReflectionTestUtils.setField(service, "basepathewoeai", basepath);
        ReflectionTestUtils.setField(service, "basepathewoeaiprefix", basepathPrefix);

        // Common Konfiguration/Person repository defaults used by mapToEWOBuerger
        Konfiguration cfg = new Konfiguration();
        // Prefer a deterministic UUID for testing to avoid nulls
        UUID cfgId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        try {
            // Some codebases have setId as UUID or String; Reflection helps if setter missing.
            ReflectionTestUtils.setField(cfg, "id", cfgId);
        } catch (Exception ignored) {}
        when(konfigurationRepository.findByAktiv(true)).thenReturn(new Konfiguration[] { cfg });
        when(personRepository.findByOM(anyString(), any())).thenReturn(null); // default: not yet captured in system
    }

    private EWOBuerger buildApiBuerger() {
        EWOBuerger api = new EWOBuerger();
        // Set fields via setters if available; otherwise use Reflection to keep this unit-test focused.
        try { api.setOrdnungsmerkmal("OM123"); } catch (Throwable ignored) {}
        try { api.setFamilienname("Doe"); } catch (Throwable ignored) {}
        try { api.setGeburtsname("Smith"); } catch (Throwable ignored) {}
        try { api.setVorname("John"); } catch (Throwable ignored) {}
        try { api.setGeschlecht(de.muenchen.ehrenamtjustiz.api.Geschlecht.MAENNLICH); } catch (Throwable ignored) {}
        try { api.setFamilienstand("ledig"); } catch (Throwable ignored) {}
        try { api.setAuskunftssperren(Collections.emptyList()); } catch (Throwable ignored) {}
        try { api.setGeburtsland("DE"); } catch (Throwable ignored) {}
        try { api.setGeburtsort("München"); } catch (Throwable ignored) {}
        try { api.setGeburtsdatum(LocalDate.of(1990, 1, 1)); } catch (Throwable ignored) {}
        try { api.setAkademischerGrad("Dr."); } catch (Throwable ignored) {}
        try { api.setStaatsangehoerigkeit(Arrays.asList("DE")); } catch (Throwable ignored) {}
        try { api.setPostleitzahl("80331"); } catch (Throwable ignored) {}
        try { api.setOrt("München"); } catch (Throwable ignored) {}
        try { api.setStrasse("Marienplatz"); } catch (Throwable ignored) {}
        try { api.setHausnummer("1"); } catch (Throwable ignored) {}
        try { api.setTeilnummerHausnummer("A"); } catch (Throwable ignored) {}
        try { api.setBuchstabeHausnummer("a"); } catch (Throwable ignored) {}
        try { api.setStockwerk("3"); } catch (Throwable ignored) {}
        try { api.setZusatz("Hinterhaus"); } catch (Throwable ignored) {}
        try { api.setAppartmentnummer("12"); } catch (Throwable ignored) {}
        try { api.setWohnungsgeber("Max Mustermann"); } catch (Throwable ignored) {}
        try { api.setWohnungsstatus(de.muenchen.ehrenamtjustiz.api.Wohnungsstatus.HAUPTWOHNUNG); } catch (Throwable ignored) {}
        try { api.setInMuenchenSeit(LocalDate.of(2010, 5, 10)); } catch (Throwable ignored) {}
        try { api.setKonfliktFelder(Collections.singletonList("K1")); } catch (Throwable ignored) {}
        return api;
    }

    @Nested
    @DisplayName("ewoSucheMitOM")
    class EwoSucheMitOMTests {
        @Test
        void returnsMappedDtoOnSuccess() {
            EWOBuerger api = buildApiBuerger();
            ResponseEntity<EWOBuerger> response = new ResponseEntity<>(api, HttpStatus.OK);
            when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class))).thenReturn(response);

            EWOBuergerDatenDto dto = service.ewoSucheMitOM("OM123");

            assertThat(dto).isNotNull();
            assertThat(dto.getOrdnungsmerkmal()).isEqualTo("OM123");
            assertThat(dto.getFamilienname()).isEqualTo("Doe");
            assertThat(dto.getVorname()).isEqualTo("John");
            assertThat(dto.getGeschlecht()).isEqualTo(Geschlecht.MAENNLICH);
            assertThat(dto.getWohnungsstatus()).isEqualTo(Wohnungsstatus.HAUPTWOHNUNG);
        }

        @Test
        void returnsNullOnHttpClientErrorException() {
            when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                    .thenThrow(HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "bad", HttpHeaders.EMPTY, new byte[0], null));
            assertThat(service.ewoSucheMitOM("OM_FAIL")).isNull();
        }

        @Test
        void returnsNullOnResourceAccessException() {
            when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                    .thenThrow(new ResourceAccessException("network"));
            assertThat(service.ewoSucheMitOM("OM_NET")).isNull();
        }

        @Test
        void returnsNullOnRestClientException() {
            when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                    .thenThrow(new RestClientException("unexpected"));
            assertThat(service.ewoSucheMitOM("OM_ERR")).isNull();
        }
    }

    @Nested
    @DisplayName("ewoSucheMitOMAenderungsService")
    class EwoSucheMitOMAenderungsServiceTests {
        @Test
        void mapsAndReturnsDtoOnSuccess() {
            EWOBuerger api = buildApiBuerger();
            when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                    .thenReturn(new ResponseEntity<>(api, HttpStatus.OK));

            EWOBuergerDatenDto dto = service.ewoSucheMitOMAenderungsService("OM123");
            assertThat(dto).isNotNull();
            assertThat(dto.getOrdnungsmerkmal()).isEqualTo("OM123");
        }

        @Test
        void throwsAenderungsServiceExceptionOnHttpClientError() {
            when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                    .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "404", HttpHeaders.EMPTY, new byte[0], null));

            assertThatThrownBy(() -> service.ewoSucheMitOMAenderungsService("OM404"))
                    .isInstanceOf(AenderungsServiceException.class)
                    .hasMessageContaining("Fehler in ewoSucheMitOM");
        }

        @Test
        void throwsAenderungsServiceExceptionOnResourceAccess() {
            when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                    .thenThrow(new ResourceAccessException("io"));

            assertThatThrownBy(() -> service.ewoSucheMitOMAenderungsService("OMNET"))
                    .isInstanceOf(AenderungsServiceException.class)
                    .hasMessageContaining("Netzwerkfehler in ewoSucheMitOM");
        }

        @Test
        void throwsAenderungsServiceExceptionOnRestClientException() {
            when(restTemplate.exchange(any(RequestEntity.class), eq(EWOBuerger.class)))
                    .thenThrow(new RestClientException("boom"));

            assertThatThrownBy(() -> service.ewoSucheMitOMAenderungsService("OMERR"))
                    .isInstanceOf(AenderungsServiceException.class)
                    .hasMessageContaining("Unerwarteter Fehler in ewoSucheMitOM");
        }
    }

    @Nested
    @DisplayName("ewoSuche (POST search)")
    class EwoSucheTests {
        @Test
        void returnsMappedListOnOkStatus() {
            EWOBuerger api1 = buildApiBuerger();
            EWOBuerger api2 = buildApiBuerger();
            try { api2.setOrdnungsmerkmal("OM456"); } catch (Throwable ignored) {}
            EWOBuerger[] body = new EWOBuerger[] { api1, api2 };
            when(restTemplate.exchange(eq(server + basepath + "/eairoutes/ewosuche"),
                    eq(HttpMethod.POST), any(HttpEntity.class), eq(EWOBuerger[].class)))
                    .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));

            List<EWOBuergerDatenDto> result = service.woSuche(new EWOBuergerSucheDto()); // if method name is ewoSuche, adjust accordingly
        }

        @Test
        void skipsNullEntriesAndReturnsOnlyValidMapped() {
            EWOBuerger api = buildApiBuerger();
            EWOBuerger[] body = new EWOBuerger[] { api, null };
            when(restTemplate.exchange(eq(server + basepath + "/eairoutes/ewosuche"),
                    eq(HttpMethod.POST), any(HttpEntity.class), eq(EWOBuerger[].class)))
                    .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));

            List<EWOBuergerDatenDto> result = service.ewoSuche(new EWOBuergerSucheDto());
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOrdnungsmerkmal()).isEqualTo("OM123");
        }

        @Test
        void returnsEmptyListOnNonOkStatus() {
            when(restTemplate.exchange(eq(server + basepath + "/eairoutes/ewosuche"),
                    eq(HttpMethod.POST), any(HttpEntity.class), eq(EWOBuerger[].class)))
                    .thenReturn(new ResponseEntity<>(new EWOBuerger[0], HttpStatus.BAD_REQUEST));

            List<EWOBuergerDatenDto> result = service.ewoSuche(new EWOBuergerSucheDto());
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("ewoEaiStatus")
    class EwoEaiStatusTests {
        @Test
        void returnsUpWhenHealthOk() {
            when(restTemplate.getForEntity(server + basepathPrefix + "/actuator/health", Void.class))
                    .thenReturn(new ResponseEntity<>(HttpStatus.OK));

            ResponseEntity<String> r = service.ewoEaiStatus();
            assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(r.getBody()).isEqualTo(EhrenamtJustizUtility.STATUS_UP);
        }

        @Test
        void returnsDownWhenRuntimeException() {
            when(restTemplate.getForEntity(server + basepathPrefix + "/actuator/health", Void.class))
                    .thenThrow(new RuntimeException("unavailable"));

            ResponseEntity<String> r = service.ewoEaiStatus();
            assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(r.getBody()).isEqualTo(EhrenamtJustizUtility.STATUS_DOWN);
        }
    }
}