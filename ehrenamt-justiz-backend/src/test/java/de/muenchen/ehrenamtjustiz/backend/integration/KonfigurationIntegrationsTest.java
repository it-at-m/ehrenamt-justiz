package de.muenchen.ehrenamtjustiz.backend.integration;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.*;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;
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

    public static final String MUENCHEN = "München";
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

        final Konfiguration konfiguration = new Konfiguration();
        konfiguration.setId(UUID.randomUUID());
        konfiguration.setAktiv(true);
        konfiguration.setEhrenamtjustizart(Ehrenamtjustizart.SCHOEFFEN);
        konfiguration.setBezeichnung("Schöffen");
        konfiguration.setAltervon(BigInteger.valueOf(25));
        konfiguration.setAlterbis(BigInteger.valueOf(120));
        konfiguration.setStaatsangehoerigkeit("deutsch");
        konfiguration.setWohnsitz(MUENCHEN);
        konfiguration.setAmtsperiodevon(LocalDate.of(2030, 4, 1));
        konfiguration.setAmtsperiodebis(LocalDate.of(2035, 3, 31));
        konfigurationRepository.save(konfiguration);
    }

    @Test
    void testGetAktiveKonfiguration() {

        final ResponseEntity<Konfiguration> result = testRestTemplate.getForEntity("/konfiguration/getAktiveKonfiguration", Konfiguration.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        final Konfiguration konfiguration = result.getBody();
        assertNotNull(konfiguration.getId());
        assertTrue(konfiguration.isAktiv());
        assertNotNull(konfiguration.getEhrenamtjustizart());
        assertNotNull(konfiguration.getBezeichnung());
        assertTrue(konfiguration.getAltervon().intValue() > 0);
        assertTrue(konfiguration.getAlterbis().intValue() > 0);
        assertNotNull(konfiguration.getStaatsangehoerigkeit());
        assertNotNull(konfiguration.getWohnsitz());
        assertNotNull(konfiguration.getAmtsperiodevon());
        assertNotNull(konfiguration.getAmtsperiodebis());

    }

    @Test
    void testUpdateKonfiguration() {

        final Konfiguration konfiguration = new Konfiguration();
        konfiguration.setId(UUID.randomUUID());
        konfiguration.setAktiv(false);
        konfiguration.setEhrenamtjustizart(Ehrenamtjustizart.SCHOEFFEN);
        konfiguration.setBezeichnung("Test");
        konfiguration.setAltervon(BigInteger.valueOf(25));
        konfiguration.setAlterbis(BigInteger.valueOf(120));
        konfiguration.setStaatsangehoerigkeit("deutsch");
        konfiguration.setWohnsitz(MUENCHEN);
        konfiguration.setAmtsperiodevon(LocalDate.of(2029, 1, 1));
        konfiguration.setAmtsperiodebis(LocalDate.of(2033, 12, 31));

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

        final Konfiguration konfiguration = new Konfiguration();
        konfiguration.setId(UUID.randomUUID());
        konfiguration.setAktiv(false);
        konfiguration.setEhrenamtjustizart(Ehrenamtjustizart.VERWALTUNGSRICHTER);
        konfiguration.setBezeichnung("Verwaltungsrichter");
        konfiguration.setAltervon(BigInteger.valueOf(25));
        konfiguration.setAlterbis(BigInteger.valueOf(120));
        konfiguration.setStaatsangehoerigkeit("deutsch");
        konfiguration.setWohnsitz(MUENCHEN);
        konfiguration.setAmtsperiodevon(LocalDate.of(2030, 1, 1));
        konfiguration.setAmtsperiodebis(LocalDate.of(2034, 12, 31));
        konfigurationRepository.save(konfiguration);

        final ResponseEntity<Konfiguration> result = testRestTemplate.postForEntity("/konfiguration/setactive", konfiguration, Konfiguration.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        final Konfiguration konfigurationResult = result.getBody();
        assertNotNull(konfigurationResult.getId());
        assertTrue(konfigurationResult.isAktiv());
        assertNotNull(konfigurationResult.getEhrenamtjustizart());
        assertNotNull(konfigurationResult.getBezeichnung());
        assertTrue(konfigurationResult.getAltervon().intValue() > 0);
        assertTrue(konfigurationResult.getAlterbis().intValue() > 0);
        assertNotNull(konfigurationResult.getStaatsangehoerigkeit());
        assertNotNull(konfigurationResult.getWohnsitz());
        assertNotNull(konfigurationResult.getAmtsperiodevon());
        assertNotNull(konfigurationResult.getAmtsperiodebis());
    }

}
