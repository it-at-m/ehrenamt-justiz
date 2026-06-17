package de.muenchen.ehrenamtjustiz.backend.domain;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.rest.DocumentRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.testdata.DocumentTestDataBuilder;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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
public class DocumentTest {
    @Autowired
    private RestTestClient restTestClient;

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final org.testcontainers.postgresql.PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private UUID testEntityId;

    private static UUID konfigurationId;

    @Autowired
    private DocumentRepository DocumentRepository;

    @Autowired
    private KonfigurationRepository konfigurationRepository;
    @Autowired
    private DocumentRepository documentRepository;

    @BeforeEach
    void setUp() {
        // new configuration
        final Konfiguration konfiguration = konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
        konfigurationId = konfiguration.getId();

        final Konfiguration persistedKonfiguration = konfigurationRepository.findById(konfiguration.getId()).orElse(null);

        // insert test-Document
        final Document Document = new DocumentTestDataBuilder().withKonfigurationId(persistedKonfiguration.getId()).build();

        testEntityId = DocumentRepository.save(Document).getId();

    }

    @AfterEach
    public void tearDown() {
        DocumentRepository.deleteById(testEntityId);
        konfigurationRepository.deleteById(konfigurationId);
    }

    @Nested
    class GetEntity {
        @Test
        void givenEntityId_thenReturnEntity() {

            restTestClient
                    .get()
                    .uri("/document/{theEntityID}", testEntityId)
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
    class SaveEntity {
        @Test
        void givenEntity_thenEntityIsSaved() {
            final Document requestDTO = getDocument();

            restTestClient.post()
                    .uri("/document")
                    .body(requestDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Document.class)
                    .value(document -> {
                        assertNotNull(document);
                        assertThat(document.getFileName()).isEqualTo(requestDTO.getFileName());
                    })
                    .returnResult()
                    .getResponseBody();

        }
    }

    private static @NotNull Document getDocument() {
        return new DocumentTestDataBuilder().withKonfigurationId(konfigurationId).build();
    }

    @Nested
    class UpdateEntity {
        @Test
        void givenEntity_thenEntityIsUpdated() {
            final Document requestDTO = getDocument();

            restTestClient.put()
                    .uri("/document/{theEntityId}", testEntityId)
                    .body(requestDTO)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType(MediaType.APPLICATION_JSON)
                    .expectBody(Document.class)
                    .value(document -> {
                        assertNotNull(document);
                        assertThat(document.getId()).isEqualTo(testEntityId);
                        assertThat(document.getFileName()).isEqualTo(requestDTO.getFileName());
                    });

            assertThat(documentRepository.findById(testEntityId).orElseThrow().getFileName()).isEqualTo(requestDTO.getFileName());

        }
    }

    @Nested
    class DeleteEntity {
        @Test
        void givenEntityId_thenEntityIsDeleted() {

            restTestClient.delete()
                    .uri("/document/{theEntityID}", testEntityId)
                    .exchange()
                    .expectStatus().isNoContent();

            assertThat(documentRepository.findById(testEntityId)).isEmpty();
        }
    }

}
