package de.muenchen.ehrenamtjustiz.backend.configuration;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@SpringBootTest(
        classes = { EhrenamtJustizApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class UnicodeConfigurationTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
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
    private TestRestTemplate testRestTemplate;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Test
    void testForNfcNormalization() {
        // Persist entity with decomposed string.
        final Konfiguration konfiguration = new Konfiguration();
        konfiguration.setId(UUID.randomUUID());
        konfiguration.setAktiv(true);
        konfiguration.setEhrenamtjustizart(Ehrenamtjustizart.VERWALTUNGSRICHTER);
        konfiguration.setBezeichnung(TEXT_ATTRIBUTE_DECOMPOSED);
        konfiguration.setAltervon(BigInteger.valueOf(18));
        konfiguration.setAlterbis(BigInteger.valueOf(75));
        konfiguration.setStaatsangehoerigkeit("deutsch");
        konfiguration.setWohnsitz("München");
        konfiguration.setAmtsperiodevon(LocalDate.of(2025, 1, 1));
        konfiguration.setAmtsperiodebis(LocalDate.of(2029, 12, 31));

        assertEquals(TEXT_ATTRIBUTE_DECOMPOSED.length(), konfiguration.getBezeichnung().length());
        final TestConstants.KonfigurationDto response = testRestTemplate
                .postForEntity(URI.create(ENTITY_ENDPOINT_URL), konfiguration, TestConstants.KonfigurationDto.class).getBody();

        // Check whether response contains a composed string.
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, response.getBezeichnung());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), response.getBezeichnung().length());

        // Extract uuid from self link.
        final UUID uuid = UUID.fromString(StringUtils.substringAfterLast(response.getRequiredLink("self").getHref(), "/"));

        // Check persisted entity contains a composed string via JPA repository.
        final Konfiguration findKonfiguration = konfigurationRepository.findById(uuid).orElse(null);
        assertEquals(TEXT_ATTRIBUTE_COMPOSED, findKonfiguration.getBezeichnung());
        assertEquals(TEXT_ATTRIBUTE_COMPOSED.length(), findKonfiguration.getBezeichnung().length());
    }

}
