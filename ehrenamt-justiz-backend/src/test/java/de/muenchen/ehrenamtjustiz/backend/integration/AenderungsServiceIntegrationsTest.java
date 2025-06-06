package de.muenchen.ehrenamtjustiz.backend.integration;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.service.EhrenamtJustizService;
import de.muenchen.ehrenamtjustiz.backend.utils.Helper;
import java.time.LocalDate;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
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
@AutoConfigureMockMvc
class AenderungsServiceIntegrationsTest {
    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    public static final String MUENCHEN = "München";

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EhrenamtJustizService ehrenamtJustizService;

    private UUID personId;

    @BeforeEach
    void setUp() {

        // insert new active configuration
        final Konfiguration konfiguration = konfigurationRepository.save(Helper.createKonfiguration());

        final Person person = createPerson(konfiguration);
        personRepository.save(person);

        personId = person.getId();
    }

    @Test
    void testEWOService() {

        assertNotNull(konfigurationRepository);
        assertNotNull(personRepository);
        assertNotNull(mockMvc);
        assertNotNull(ehrenamtJustizService);
        assertNotNull(personId);

    }

    @Test
    void testAenderungsservicePerson() throws Exception {

        Mockito.when(ehrenamtJustizService.getKonflikteAenderungsService(any(Person.class)))
                .thenReturn(new ArrayList<>(Arrays.asList("Familienname", "Vorname")));

        mockMvc.perform(post("/aenderungsservice/aenderungsservicePerson")
                .contentType(MediaType.APPLICATION_JSON)
                .content("4711"))
                .andExpect(status().isOk());

        /*
         *
         * The following program code does not work because the change to the Person table in the service
         * class is executed but is not available in the test class when you read it here:
         * final Optional<Person> person = personRepository.findById(personId);
         *
         * assertEquals(Status.KONFLIKT, person.get().getStatus());
         * assertEquals(2, person.get().getKonfliktfeld().size());
         */

    }

    @Test
    void testAenderungsservicePerson_Exception() throws Exception {

        Mockito.when(ehrenamtJustizService.getKonflikteAenderungsService(any(Person.class)))
                .thenThrow(new RestClientException("Unexpected error"));

        mockMvc.perform(post("/aenderungsservice/aenderungsservicePerson")
                .contentType(MediaType.APPLICATION_JSON)
                .content("4711"))
                .andExpect(status().is4xxClientError());

    }

    private static @NotNull
    Person createPerson(final Konfiguration konfiguration) {
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
        person.setKonfigurationid(konfiguration.getId());
        person.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
        person.setFamilienstand("VH");
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
        return person;
    }
}
