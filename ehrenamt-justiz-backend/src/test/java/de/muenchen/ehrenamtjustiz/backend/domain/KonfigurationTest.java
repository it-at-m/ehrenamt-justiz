package de.muenchen.ehrenamtjustiz.backend.domain;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
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
class KonfigurationTest {

    @Autowired
    private RestTestClient restTestClient;

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private UUID testEntityId;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @BeforeEach
    void setUp() {
        testEntityId = konfigurationRepository.save(new KonfigurationTestDataBuilder().build()).getId();
    }

    @AfterEach
    public void tearDown() {
        konfigurationRepository.deleteById(testEntityId);
    }

    @Nested
    class GetEntity {
        @Test
        void givenEntityId_thenReturnEntity() {

            restTestClient
                    .get()
                    .uri("/konfigurationen/{theEntityID}", testEntityId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(new MediaType("application", "vnd.hal+json"))
                    .expectBody(Konfiguration.class)
                    .value(konfiguration -> {
                        assertNotNull(konfiguration);
                        assertThat(konfiguration.getId()).isEqualTo(testEntityId);
                    });

        }
    }

    @Nested
    class GetEntitiesPage {
        @Test
        void givenPageNumberAndPageSize_thenReturnPageOfEntities() {

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/konfigurationen")
                            .queryParam("pageNumber", "0")
                            .queryParam("pageSize", "10")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(new MediaType("application", "vnd.hal+json"))
                    .expectBody()
                    .jsonPath("$._embedded.konfigurationen")
                    .value(new ParameterizedTypeReference<List<Konfiguration>>() {
                    }, content -> assertThat(content.size()).isGreaterThan(0));

        }
    }

    @Nested
    class SaveEntity {
        @Test
        void givenEntity_thenEntityIsSaved() {
            final Konfiguration request = new KonfigurationTestDataBuilder().build();

            final Konfiguration response = restTestClient.post()
                    .uri("/konfigurationen")
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Konfiguration.class)
                    .value(konfiguration -> {
                        assertNotNull(konfiguration);
                        assertThat(konfiguration.getBezeichnung()).isEqualTo(request.getBezeichnung());
                    })
                    .returnResult()
                    .getResponseBody();

            assertThat(response).isNotNull();
            final Optional<Konfiguration> theEntity = konfigurationRepository.findById(response.getId());
            assertThat(theEntity).isPresent();
            assertThat(theEntity.get().getBezeichnung()).isEqualTo(request.getBezeichnung());

        }
    }

    @Nested
    class UpdateEntity {
        @Test
        void givenEntity_thenEntityIsUpdated() {
            final Konfiguration request = new KonfigurationTestDataBuilder().build();

            restTestClient.put()
                    .uri("/konfigurationen/{theEntityId}", testEntityId)
                    .body(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Konfiguration.class)
                    .value(konfiguration -> {
                        assertNotNull(konfiguration);
                        assertThat(konfiguration.getId()).isEqualTo(testEntityId);
                        assertThat(konfiguration.getBezeichnung()).isEqualTo(request.getBezeichnung());
                    });

            assertThat(konfigurationRepository.findById(testEntityId).orElseThrow().getBezeichnung()).isEqualTo(request.getBezeichnung());

        }
    }

    @Nested
    class DeleteEntity {
        @Test
        void givenEntityId_thenEntityIsDeleted() {

            restTestClient.delete()
                    .uri("/konfigurationen/{theEntityID}", testEntityId)
                    .exchange()
                    .expectStatus().isNoContent();

            assertThat(konfigurationRepository.findById(testEntityId)).isEmpty();
        }
    }

}
