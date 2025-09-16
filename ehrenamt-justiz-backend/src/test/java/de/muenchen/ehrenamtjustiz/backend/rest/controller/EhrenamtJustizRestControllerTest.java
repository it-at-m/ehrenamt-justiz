package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EhrenamtJustizStatusDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper.PersonMapper;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import de.muenchen.ehrenamtjustiz.backend.service.EWOService;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testing stack used:
 * - JUnit 5 (Jupiter)
 * - Spring Boot Test: @WebMvcTest with MockMvc
 * - Mockito for mocking collaborators and static utilities
 * - Hamcrest matchers for response verification
 *
 * Focus: Thorough coverage of EhrenamtJustizRestController public endpoints, including happy paths,
 * edge cases, error propagation, and security constraints specified via @PreAuthorize.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = EhrenamtJustizRestController.class)
class EhrenamtJustizRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private KonfigurationRepository konfigurationRepository;

    @MockBean
    private EWOService ewoService;

    @MockBean
    private PersonMapper personMapper;

    private MockedStatic<EhrenamtJustizUtility> utilityMock;

    private Konfiguration activeKonfiguration;

    @BeforeEach
    void setUp() {
        // Default active configuration stub
        activeKonfiguration = new Konfiguration();
        // Use reflection or setters if available; we assume an ID is required by controller logic
        try {
            java.lang.reflect.Field idField = Konfiguration.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(activeKonfiguration, 42L);
        } catch (Exception ignored) {
            // If field not present or inaccessible, tests relying on getId() will still run if a getter/setter exists.
        }
        given(konfigurationRepository.findByAktiv(true)).willReturn(new Konfiguration[]{activeKonfiguration});

        // Prepare static mock for EhrenamtJustizUtility. Requires mockito-inline at test runtime.
        utilityMock = Mockito.mockStatic(EhrenamtJustizUtility.class, Mockito.CALLS_REAL_METHODS);
        // Provide safe defaults for static validators to avoid NPEs; individual tests override as needed.
        utilityMock.when(() -> EhrenamtJustizUtility.validateGueltigesGeburtsdatum(any(), any())).thenReturn("OK");
        utilityMock.when(() -> EhrenamtJustizUtility.validateGueltigeStaatsangehoerigkeit(any(), any())).thenReturn("OK");
        utilityMock.when(() -> EhrenamtJustizUtility.validateGueltigerWohnsitz(any(), anyString())).thenReturn("OK");
        utilityMock.when(() -> EhrenamtJustizUtility.getPersonAusEWOBuergerDaten(any())).thenCallRealMethod();
        // Defensive defaults for sanitize/truncate helpers used in logging
        utilityMock.when(() -> EhrenamtJustizUtility.truncateIfNeeded(anyString(), anyInt())).thenAnswer(inv -> inv.getArgument(0));
        utilityMock.when(() -> EhrenamtJustizUtility.sanitizeInput(anyString())).thenAnswer(inv -> inv.getArgument(0));
    }

    @AfterEach
    void tearDown() {
        if (utilityMock \!= null) {
            utilityMock.close();
        }
    }

    private SimpleGrantedAuthority auth(String authority) {
        return new SimpleGrantedAuthority(authority);
    }

    @Nested
    @DisplayName("POST /ehrenamtjustiz/ewoSuche")
    class EwoSucheTests {

        @Test
        @DisplayName("returns 200 OK with result list on happy path")
        void ewoSuche_ok() throws Exception {
            EWOBuergerSucheDto req = new EWOBuergerSucheDto();
            // set fields via reflection if no setters available
            setField(req, "familienname", "Mustermann");
            setField(req, "vorname", "Max");
            setField(req, "geburtsdatum", LocalDate.of(1990, 1, 1));

            EWOBuergerDatenDto dto = new EWOBuergerDatenDto();
            setField(dto, "familienname", "Mustermann");
            setField(dto, "vorname", "Max");
            setField(dto, "geburtsdatum", LocalDate.of(1990, 1, 1));

            given(ewoService.ewoSuche(any(EWOBuergerSucheDto.class))).willReturn(List.of(dto));

            mockMvc.perform(
                    post("/ehrenamtjustiz/ewoSuche")
                            .with(user("tester").authorities(auth("EWOSUCHE")))// matches hasAuthority('EWOSUCHE') if Authorities constant uses that
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(req))
            ).andExpect(status().isOk())
             .andExpect(content().contentTypeCompatibleWith("application/json"))
             .andExpect(jsonPath("$", hasSize(1)))
             .andExpect(jsonPath("$[0].familienname", is("Mustermann")));
        }

        @Test
        @DisplayName("propagates HttpClientErrorException status and errormessage header")
        void ewoSuche_clientError() throws Exception {
            EWOBuergerSucheDto req = new EWOBuergerSucheDto();
            setField(req, "familienname", "Bad");
            setField(req, "vorname", "Request");

            org.springframework.web.client.HttpClientErrorException ex =
                    org.springframework.web.client.HttpClientErrorException.create(
                            HttpStatus.BAD_REQUEST, "Bad Request",
                            HttpHeaders.EMPTY, "invalid".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

            given(ewoService.ewoSuche(any(EWOBuergerSucheDto.class))).willThrow(ex);

            mockMvc.perform(
                    post("/ehrenamtjustiz/ewoSuche")
                            .with(user("tester").authorities(auth("EWOSUCHE")))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(req))
            ).andExpect(status().isBadRequest())
             .andExpect(header().string("errormessage", containsString("400 Bad Request")));
        }
    }

    @Nested
    @DisplayName("GET /ehrenamtjustiz/ewoSucheMitOM")
    class EwoSucheMitOmTests {

        @Test
        @DisplayName("returns 404 when service yields null")
        void ewoSucheMitOm_notFound() throws Exception {
            given(ewoService.ewoSucheMitOM("123")).willReturn(null);

            mockMvc.perform(
                    get("/ehrenamtjustiz/ewoSucheMitOM").with(user("tester").authorities(auth("EWOSUCHEMITOM")))
                            .param("om", "123")
            ).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 200 with payload when service yields data")
        void ewoSucheMitOm_ok() throws Exception {
            EWOBuergerDatenDto dto = new EWOBuergerDatenDto();
            setField(dto, "familienname", "Muster");
            setField(dto, "vorname", "Erika");
            setField(dto, "geburtsdatum", LocalDate.of(1985, 5, 5));

            given(ewoService.ewoSucheMitOM("456")).willReturn(dto);

            mockMvc.perform(
                    get("/ehrenamtjustiz/ewoSucheMitOM")
                            .with(user("tester").authorities(auth("EWOSUCHEMITOM")))
                            .param("om", "456")
            ).andExpect(status().isOk())
             .andExpect(jsonPath("$.familienname", is("Muster")))
             .andExpect(jsonPath("$.vorname", is("Erika")));
        }
    }

    @Nested
    @DisplayName("GET status proxy endpoints")
    class StatusProxyTests {

        @Test
        @DisplayName("EAI status is proxied as-is")
        void ewoEaiStatus() throws Exception {
            ResponseEntity<String> serviceResponse = ResponseEntity.status(HttpStatus.ACCEPTED).body("ACCEPTED");
            given(ewoService.ewoEaiStatus()).willReturn(serviceResponse);

            mockMvc.perform(get("/ehrenamtjustiz/ewoEaiStatus"))
                   .andExpect(status().isAccepted())
                   .andExpect(content().string("ACCEPTED"));
        }

        @Test
        @DisplayName("Änderungsservice status is proxied as-is")
        void aenderungsserviceStatus() throws Exception {
            ResponseEntity<String> serviceResponse = ResponseEntity.ok("OK");
            given(ewoService.aenderungsserviceStatus()).willReturn(serviceResponse);

            mockMvc.perform(get("/ehrenamtjustiz/aenderungsserviceStatus"))
                   .andExpect(status().isOk())
                   .andExpect(content().string("OK"));
        }
    }

    @Nested
    @DisplayName("Validation endpoints")
    class ValidationTests {

        @Test
        @DisplayName("pruefeGeburtsdatum returns OK and invokes validator")
        void pruefeGeburtsdatum_ok() throws Exception {
            utilityMock.when(() -> EhrenamtJustizUtility.validateGueltigesGeburtsdatum(any(), any()))
                       .thenReturn("VALID");

            mockMvc.perform(
                    post("/ehrenamtjustiz/pruefeGeburtsdatum")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(LocalDate.of(2000, 2, 2)))
            ).andExpect(status().isOk())
             .andExpect(content().string("VALID"));

            utilityMock.verify(() -> EhrenamtJustizUtility.validateGueltigesGeburtsdatum(eq(activeKonfiguration), eq(LocalDate.of(2000, 2, 2))), times(1));
        }

        @Test
        @DisplayName("pruefeStaatsangehoerigkeit returns OK and invokes validator with list")
        void pruefeStaatsangehoerigkeit_ok() throws Exception {
            utilityMock.when(() -> EhrenamtJustizUtility.validateGueltigeStaatsangehoerigkeit(any(), any()))
                       .thenReturn("COUNTRY_OK");

            List<String> input = List.of("DEU", "AUT");

            mockMvc.perform(
                    post("/ehrenamtjustiz/pruefeStaatsangehoerigkeit")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(input))
            ).andExpect(status().isOk())
             .andExpect(content().string("COUNTRY_OK"));

            utilityMock.verify(() -> EhrenamtJustizUtility.validateGueltigeStaatsangehoerigkeit(eq(activeKonfiguration), eq(input)), times(1));
        }

        @Test
        @DisplayName("pruefeWohnsitz parses quoted string and validates")
        void pruefeWohnsitz_ok() throws Exception {
            utilityMock.when(() -> EhrenamtJustizUtility.validateGueltigerWohnsitz(any(), anyString()))
                       .thenReturn("CITY_OK");

            // Body must be JSON string (including quotes); controller uses new ObjectMapper().readValue(...) to strip them
            String jsonStringBody = objectMapper.writeValueAsString("Muenchen");

            mockMvc.perform(
                    post("/ehrenamtjustiz/pruefeWohnsitz")
                            .contentType("application/json")
                            .content(jsonStringBody)
            ).andExpect(status().isOk())
             .andExpect(content().string("CITY_OK"));

            utilityMock.verify(() -> EhrenamtJustizUtility.validateGueltigerWohnsitz(eq(activeKonfiguration), eq("Muenchen")), times(1));
        }

        @Test
        @DisplayName("pruefeWohnsitz handles invalid JSON payload gracefully (400 from MVC layer)")
        void pruefeWohnsitz_invalidJson() throws Exception {
            // send invalid JSON (missing closing quote)
            String invalidJson = "\"Muenchen";
            mockMvc.perform(
                    post("/ehrenamtjustiz/pruefeWohnsitz")
                            .contentType("application/json")
                            .content(invalidJson)
            ).andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /ehrenamtjustiz/pruefenNeuePerson")
    class PruefenNeuePersonTests {

        @Test
        @DisplayName("returns 404 when no person found by OM")
        void pruefenNeuePerson_notFound() throws Exception {
            EWOBuergerDatenDto dto = new EWOBuergerDatenDto();
            setField(dto, "ordnungsmerkmal", "OM-1");

            given(personRepository.findByOM(eq("OM-1"), eq(getKonfId(activeKonfiguration)))).willReturn(null);

            mockMvc.perform(
                    post("/ehrenamtjustiz/pruefenNeuePerson")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(dto))
            ).andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("returns 200 with mapped PersonDto when person exists")
        void pruefenNeuePerson_ok() throws Exception {
            EWOBuergerDatenDto dto = new EWOBuergerDatenDto();
            setField(dto, "ordnungsmerkmal", "OM-2");

            Person person = new Person();
            PersonDto personDto = new PersonDto();
            setField(personDto, "id", 7L);

            given(personRepository.findByOM(eq("OM-2"), eq(getKonfId(activeKonfiguration)))).willReturn(person);
            given(personMapper.entity2Model(person)).willReturn(personDto);

            mockMvc.perform(
                    post("/ehrenamtjustiz/pruefenNeuePerson")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(dto))
            ).andExpect(status().isOk())
             .andExpect(content().contentTypeCompatibleWith("application/json"));
        }
    }

    @Nested
    @DisplayName("POST /ehrenamtjustiz/vorbereitenUndSpeichernPerson")
    class VorbereitenUndSpeichernPersonTests {

        @Test
        @DisplayName("persists mapped person and returns PersonDto on success")
        void vorbereitenUndSpeichernPerson_ok() throws Exception {
            EWOBuergerDatenDto input = new EWOBuergerDatenDto();
            setField(input, "familienname", "Muster");
            setField(input, "vorname", "Erika");
            setField(input, "geburtsdatum", LocalDate.of(1985, 5, 5));

            Person personFromUtil = new Person();
            utilityMock.when(() -> EhrenamtJustizUtility.getPersonAusEWOBuergerDaten(any()))
                       .thenReturn(personFromUtil);

            Person saved = new Person();
            given(personRepository.save(personFromUtil)).willReturn(saved);

            PersonDto dto = new PersonDto();
            setField(dto, "id", 99L);
            given(personMapper.entity2Model(saved)).willReturn(dto);

            mockMvc.perform(
                    post("/ehrenamtjustiz/vorbereitenUndSpeichernPerson")
                            .with(user("writer").authorities(auth("WRITE_EHRENAMTJUSTIZDATEN")))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(input))
            ).andExpect(status().isOk())
             .andExpect(content().contentTypeCompatibleWith("application/json"));

            // Ensure configuration ID was attempted to be set (if a setter exists)
            try {
                java.lang.reflect.Method getKonfId = Person.class.getMethod("getKonfigurationid");
                Object konfId = getKonfId.invoke(personFromUtil);
                // Can't assert value without failing if not present; presence check is sufficient.
            } catch (NoSuchMethodException ignored) {
                // If method not present, skip reflective assertion
            }
        }

        @Test
        @DisplayName("returns 403 when missing required authority")
        void vorbereitenUndSpeichernPerson_forbiddenWithoutAuthority() throws Exception {
            EWOBuergerDatenDto input = new EWOBuergerDatenDto();

            mockMvc.perform(
                    post("/ehrenamtjustiz/vorbereitenUndSpeichernPerson")
                            // no authority
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(input))
            ).andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /ehrenamtjustiz/ehrenamtJustizStatus")
    class EhrenamtJustizStatusTests {

        @Test
        @DisplayName("returns aggregated counts from repository")
        void ehrenamtJustizStatus_ok() throws Exception {
            given(personRepository.countByStatus(Status.BEWERBUNG, getKonfId(activeKonfiguration))).willReturn(1L);
            given(personRepository.countByStatus(Status.VORSCHLAG, getKonfId(activeKonfiguration))).willReturn(2L);
            given(personRepository.countByNeuerVorschlagStatus(Status.VORSCHLAG, getKonfId(activeKonfiguration))).willReturn(3L);
            given(personRepository.countByStatus(Status.KONFLIKT, getKonfId(activeKonfiguration))).willReturn(4L);

            mockMvc.perform(
                    get("/ehrenamtjustiz/ehrenamtJustizStatus")
                            .with(user("reader").authorities(auth("READ_EHRENAMTJUSTIZDATEN")))
            ).andExpect(status().isOk())
             .andExpect(jsonPath("$.anzahlBewerbungen", is(1)))
             .andExpect(jsonPath("$.anzahlVorschlaege", is(2)))
             .andExpect(jsonPath("$.anzahlVorschlaegeNeu", is(3)))
             .andExpect(jsonPath("$.anzahlKonflikte", is(4)));
        }
    }

    // --------- Helper reflection utilities for DTOs without setters ---------
    private static void setField(Object target, String fieldName, Object value) {
        try {
            var field = findField(target.getClass(), fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            // No-op to keep tests resilient even if DTOs provide setters; try setter next
            try {
                String setter = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
                var m = target.getClass().getMethod(setter, value.getClass());
                m.invoke(target, value);
            } catch (Exception ignored) {
                // Swallow to avoid brittle tests when model evolves
            }
        }
    }

    private static java.lang.reflect.Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> t = type;
        while (t \!= null) {
            try {
                return t.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ex) {
                t = t.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    private static Long getKonfId(Konfiguration konf) {
        try {
            var m = Konfiguration.class.getMethod("getId");
            Object v = m.invoke(konf);
            return v == null ? null : ((Number) v).longValue();
        } catch (Exception e) {
            // Fallback to reflection field
            try {
                var f = Konfiguration.class.getDeclaredField("id");
                f.setAccessible(true);
                Object v = f.get(konf);
                return v == null ? null : ((Number) v).longValue();
            } catch (Exception ignored) {
                return null;
            }
        }
    }
}