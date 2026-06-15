package de.muenchen.ehrenamtjustiz.backend.domain;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import de.muenchen.ehrenamtjustiz.backend.testdata.PersonTestDataBuilder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class PersonTest {

    @Autowired
    private RestTestClient restTestClient;

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private UUID testEntityId;

    private static UUID konfigurationId;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        // new configuration
        final Konfiguration konfiguration = konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
        konfigurationId = konfiguration.getId();

        final Konfiguration persistedKonfiguration = konfigurationRepository.findById(konfiguration.getId()).orElse(null);

        // insert test-person
        final Person person = new PersonTestDataBuilder().withKonfigurationid(persistedKonfiguration.getId()).build();

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
        void givenEntityId_thenReturnEntity() {

            restTestClient
                    .get()
                    .uri("/personen/{theEntityID}", testEntityId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(new MediaType("application", "vnd.hal+json"))
                    .expectBody(Person.class)
                    .value(person -> {
                        assertNotNull(person);
                        assertThat(person.getId()).isEqualTo(testEntityId);
                    });

        }
    }

    @Nested
    class GetEntitiesPage {
        @Test
        void givenPageNumberAndPageSize_thenReturnPageOfEntities() {

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/personen")
                            .queryParam("pageNumber", "0")
                            .queryParam("pageSize", "10")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(new MediaType("application", "vnd.hal+json"))
                    .expectBody()
                    .jsonPath("$._embedded.personen")
                    .value(new ParameterizedTypeReference<List<Person>>() {
                    }, content -> assertThat(content.size()).isGreaterThan(0));

        }
    }

    @Nested
    class SaveEntity {
        @Test
        void givenEntity_thenEntityIsSaved() {
            final Person request = new PersonTestDataBuilder().withKonfigurationid(konfigurationId).build();

            final Person response = restTestClient.post()
                    .uri("/personen")
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Person.class)
                    .value(person -> {
                        assertNotNull(person);
                        assertThat(person.getFamilienname()).isEqualTo(request.getFamilienname());
                    })
                    .returnResult()
                    .getResponseBody();

            assertThat(response).isNotNull();
            final Optional<Person> theEntity = personRepository.findById(response.getId());
            assertThat(theEntity).isPresent();
            assertThat(theEntity.get().getFamilienname()).isEqualTo(request.getFamilienname());

        }
    }

    @Nested
    class UpdateEntity {
        @Test
        void givenEntity_thenEntityIsUpdated() {
            final Person request = new PersonTestDataBuilder().withKonfigurationid(konfigurationId).build();

            restTestClient.put()
                    .uri("/personen/{theEntityId}", testEntityId)
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Person.class)
                    .value(person -> {
                        assertNotNull(person);
                        assertThat(person.getId()).isEqualTo(testEntityId);
                        assertThat(person.getFamilienname()).isEqualTo(request.getFamilienname());
                    });

            assertThat(personRepository.findById(testEntityId).orElseThrow().getFamilienname()).isEqualTo(request.getFamilienname());

        }
    }

    @Nested
    class DeleteEntity {
        @Test
        void givenEntityId_thenEntityIsDeleted() {

            restTestClient.delete()
                    .uri("/personen/{theEntityID}", testEntityId)
                    .exchange()
                    .expectStatus().isNoContent();

            assertThat(personRepository.findById(testEntityId)).isEmpty();
        }
    }

}
