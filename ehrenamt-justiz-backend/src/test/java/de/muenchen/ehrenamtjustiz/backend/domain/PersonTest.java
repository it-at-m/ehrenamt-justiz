package de.muenchen.ehrenamtjustiz.backend.domain;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class PersonTest {

    public static final String MUENCHEN = "München";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private UUID testEntityId;

    private static UUID konfigurationId;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @BeforeEach
    public void setUp() {
        // new configuration
        final Konfiguration konfiguration = new Konfiguration();
        konfiguration.setId(UUID.randomUUID());
        konfiguration.setAktiv(true);
        konfiguration.setEhrenamtjustizart(Ehrenamtjustizart.VERWALTUNGSRICHTER);
        konfiguration.setBezeichnung("Verwaltungsrichter");
        konfiguration.setAltervon(BigInteger.valueOf(25));
        konfiguration.setAlterbis(BigInteger.valueOf(120));
        konfiguration.setStaatsangehoerigkeit("deutsch");
        konfiguration.setWohnsitz(MUENCHEN);
        konfiguration.setAmtsperiodevon(LocalDate.of(2030, 4, 1));
        konfiguration.setAmtsperiodebis(LocalDate.of(2035, 3, 31));
        konfigurationRepository.save(konfiguration);

        konfigurationId = konfiguration.getId();

        final Konfiguration persistedKonfiguration = konfigurationRepository.findById(konfiguration.getId()).orElse(null);

        // insert test-person
        final Person person = new Person();
        person.setId(UUID.randomUUID());
        person.setStatus(Status.VORSCHLAG);
        person.setNeuervorschlag(true);
        person.setEwoid("4711");
        person.setFamilienname("Müller");
        person.setVorname("Hans");
        person.setGeburtsort(MUENCHEN);
        person.setGeburtsland("Deutschland");
        person.setGeburtsdatum(LocalDate.of(1997, 1, 1));
        person.setKonfigurationid(persistedKonfiguration.getId());
        person.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
        person.setFamilienstand("ledig");
        person.setPostleitzahl("80634");
        person.setOrt(MUENCHEN);
        person.setStrasse("Ludwigstr.");
        person.setHausnummer("7");
        person.setInmuenchenseit(LocalDate.of(2023, 1, 1));
        person.setGeschlecht(Geschlecht.MAENNLICH);
        person.setBewerbungvom(LocalDate.of(2024, 9, 17));

        testEntityId = personRepository.save(person).getId();

    }

    @AfterEach
    public void tearDown() {
        personRepository.deleteById(testEntityId);
        konfigurationRepository.deleteById(konfigurationId);
    }

    @Nested
    class GetEntity {
        @Test
        void givenEntityId_thenReturnEntity() throws Exception {
            mockMvc.perform(get("/personen/{theEntityID}", testEntityId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(new MediaType("application", "hal+json")))
                    .andExpect(jsonPath("$.id", is(testEntityId.toString())));
        }
    }

    @Nested
    class GetEntitiesPage {
        @Test
        void givenPageNumberAndPageSize_thenReturnPageOfEntities() throws Exception {
            mockMvc.perform(get("/personen")
                    .param("pageNumber", "0")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(new MediaType("application", "hal+json")))
                    .andExpect(jsonPath("$._embedded.personen", hasSize(greaterThanOrEqualTo(0))));
        }
    }

    @Nested
    class SaveEntity {
        @Test
        void givenEntity_thenEntityIsSaved() throws Exception {
            final Person requestDTO = getPerson();
            final String requestBody = objectMapper.writeValueAsString(requestDTO);

            mockMvc.perform(post("/personen")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }
    }

    private static @NotNull
    Person getPerson() {
        final Person person = new Person();
        person.setId(UUID.randomUUID());
        person.setStatus(Status.VORSCHLAG);
        person.setNeuervorschlag(true);
        person.setEwoid("4711");
        person.setFamilienname("Müller");
        person.setVorname("Anna");
        person.setGeburtsort(MUENCHEN);
        person.setGeburtsland("Deutschland");
        person.setGeburtsdatum(LocalDate.of(1997, 1, 1));
        person.setKonfigurationid(konfigurationId);
        person.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
        person.setFamilienstand("ledig");
        person.setPostleitzahl("80634");
        person.setOrt(MUENCHEN);
        person.setStrasse("Leopoldstr.");
        person.setHausnummer("232");
        person.setInmuenchenseit(LocalDate.of(2000, 1, 9));
        person.setGeschlecht(Geschlecht.WEIBLICH);
        person.setBewerbungvom(LocalDate.of(2024, 9, 16));
        return person;
    }

    @Nested
    class UpdateEntity {
        @Test
        void givenEntity_thenEntityIsUpdated() throws Exception {
            final Person requestDTO = getPerson();
            final String requestBody = objectMapper.writeValueAsString(requestDTO);

            mockMvc.perform(put("/personen/{theEntityId}", testEntityId)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class DeleteEntity {
        @Test
        void givenEntityId_thenEntityIsDeleted() throws Exception {
            mockMvc.perform(delete("/personen/{theEntityId}", testEntityId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }

}
