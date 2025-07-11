package de.muenchen.ehrenamtjustiz.backend.integration;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.KonfigurationDto;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
class KonfigurationIntegrationsTest {

    @Autowired
    public KonfigurationRepository konfigurationRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    @BeforeEach
    public void before() {
        konfigurationRepository.deleteAll();

        konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
    }

    @Test
    void testGetAktiveKonfiguration() {

        final ResponseEntity<KonfigurationDto> result = testRestTemplate.getForEntity("/konfiguration/getAktiveKonfiguration", KonfigurationDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        final KonfigurationDto konfigurationDto = result.getBody();
        assertNotNull(konfigurationDto.getId());
        assertTrue(konfigurationDto.isAktiv());
        assertNotNull(konfigurationDto.getEhrenamtjustizart());
        assertNotNull(konfigurationDto.getBezeichnung());
        assertTrue(konfigurationDto.getAltervon().intValue() > 0);
        assertTrue(konfigurationDto.getAlterbis().intValue() > 0);
        assertNotNull(konfigurationDto.getStaatsangehoerigkeit());
        assertNotNull(konfigurationDto.getWohnsitz());
        assertNotNull(konfigurationDto.getAmtsperiodevon());
        assertNotNull(konfigurationDto.getAmtsperiodebis());

    }

    @Test
    void testUpdateKonfiguration() {

        final Konfiguration konfiguration = new KonfigurationTestDataBuilder().withAktiv(false).build();

        final ResponseEntity<Konfiguration> result = testRestTemplate.postForEntity("/konfiguration/updateKonfiguration", konfiguration, Konfiguration.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        final Konfiguration konfigurationResult = result.getBody();
        assertNotNull(konfigurationResult.getId());
        assertFalse(konfigurationResult.isAktiv());
        assertNotNull(konfigurationResult.getEhrenamtjustizart());
        assertNotNull(konfigurationResult.getBezeichnung());
        assertTrue(konfigurationResult.getAltervon().intValue() > 0);
        assertTrue(konfigurationResult.getAlterbis().intValue() > 0);
        assertNotNull(konfigurationResult.getStaatsangehoerigkeit());
        assertNotNull(konfigurationResult.getWohnsitz());
        assertNotNull(konfigurationResult.getAmtsperiodevon());
        assertNotNull(konfigurationResult.getAmtsperiodebis());
    }

    @Test
    void testSetActive() {

        final Konfiguration konfiguration = new KonfigurationTestDataBuilder().withAktiv(false).build();
        konfigurationRepository.save(konfiguration);

        final ResponseEntity<KonfigurationDto> result = testRestTemplate.postForEntity("/konfiguration/setActive", konfiguration, KonfigurationDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        final KonfigurationDto konfigurationDtoResult = result.getBody();
        assertNotNull(konfigurationDtoResult.getId());
        assertTrue(konfigurationDtoResult.isAktiv());
        assertNotNull(konfigurationDtoResult.getEhrenamtjustizart());
        assertNotNull(konfigurationDtoResult.getBezeichnung());
        assertTrue(konfigurationDtoResult.getAltervon().intValue() > 0);
        assertTrue(konfigurationDtoResult.getAlterbis().intValue() > 0);
        assertNotNull(konfigurationDtoResult.getStaatsangehoerigkeit());
        assertNotNull(konfigurationDtoResult.getWohnsitz());
        assertNotNull(konfigurationDtoResult.getAmtsperiodevon());
        assertNotNull(konfigurationDtoResult.getAmtsperiodebis());
    }

}
