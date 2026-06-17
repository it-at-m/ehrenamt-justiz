package de.muenchen.ehrenamtjustiz.backend.configuration.filter;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(
        classes = { EhrenamtJustizApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureRestTestClient
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class UnicodeFilterConfigurationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private static final String ENTITY_ENDPOINT_URL = "/konfigurationen";

    /**
     * Decomposed string:
     * String "Ä-é" represented with unicode letters "A◌̈-e◌́"
     */
    private static final String TEXT_ATTRIBUTE_DECOMPOSED = "\u0041\u0308-\u0065\u0301";

    /**
     * Composed string:
     * String "Ä-é" represented with unicode letters "Ä-é".
     */
    private static final String TEXT_ATTRIBUTE_COMPOSED = "\u00c4-\u00e9";

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Test
    void givenDecomposedString_thenConvertToNfcNormalized() {
        // Given
        // Persist entity with decomposed string.
        final Konfiguration testKonfiguration = new KonfigurationTestDataBuilder().withBezeichnung(TEXT_ATTRIBUTE_DECOMPOSED).build();

        // When
        TestConstants.KonfigurationDto response = restTestClient.post()
                .uri(ENTITY_ENDPOINT_URL)
                .body(testKonfiguration)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TestConstants.KonfigurationDto.class)
                .returnResult()
                .getResponseBody();

        final Konfiguration konfiguration = konfigurationRepository.findById(testKonfiguration.getId()).orElse(null);

        // Check persisted entity contains a composed string via JPA repository.
        assertNotNull(konfiguration);
        assertNotNull(konfiguration.getBezeichnung());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, konfiguration.getBezeichnung());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), konfiguration.getBezeichnung().length());
    }

}
