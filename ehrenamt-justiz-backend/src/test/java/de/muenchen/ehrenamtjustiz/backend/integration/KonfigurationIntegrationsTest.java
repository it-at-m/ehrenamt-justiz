package de.muenchen.ehrenamtjustiz.backend.integration;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.KonfigurationDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.TechnischeKonfigurationDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper.KonfigurationMapper;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(
        classes = { EhrenamtJustizApplication.class },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureRestTestClient
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class KonfigurationIntegrationsTest {

    @Autowired
    public KonfigurationRepository konfigurationRepository;

    @Autowired
    private KonfigurationMapper konfigurationMapper;

    @Autowired
    private RestTestClient restTestClient;

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    @BeforeEach
    void setUp() {
        konfigurationRepository.deleteAll();

        konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
    }

    @Test
    void givenConfiguration_thenCheckData() {

        restTestClient.get()
                .uri("/konfiguration/getAktiveKonfiguration")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(KonfigurationDto.class)
                .value(konfigurationDto -> {
                    assertNotNull(konfigurationDto);
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

                })
                .returnResult()
                .getResponseBody();

    }

    @Test
    void givenTechnicalConfiguration_thenCheckData() {

        restTestClient.get()
                .uri("/technischeKonfiguration/getTechnischeKonfiguration")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(TechnischeKonfigurationDto.class)
                .value(technischeKonfigurationDto -> {
                    assertNotNull(technischeKonfigurationDto);
                    assertNotNull(technischeKonfigurationDto.getBestaetigungVerfassungstreueFileExtension());
                    assertNotNull(technischeKonfigurationDto.getBestaetigungVerfassungstreueMaxSize());
                    assertTrue(technischeKonfigurationDto.getBestaetigungVerfassungstreueMaxSize().intValue() > 0);

                })
                .returnResult()
                .getResponseBody();

    }

    @Test
    void givenConfiguration_thenValidateUpdate() {

        final Konfiguration konfiguration = new KonfigurationTestDataBuilder().withAktiv(false).build();
        KonfigurationDto konfigurationDto = konfigurationMapper.entity2Model(konfiguration);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("object", konfigurationDto);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        restTestClient.post()
                .uri("/konfiguration/updateKonfiguration")
                .body(body)
                .headers(hdrs -> hdrs.addAll(headers))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Konfiguration.class)
                .value(konfigurationResult -> {
                    assertNotNull(konfigurationResult);
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

                })
                .returnResult()
                .getResponseBody();
    }

    @Test
    void givenConfiguration_thenCheckActivation() {

        final Konfiguration konfiguration = new KonfigurationTestDataBuilder().withAktiv(false).build();
        konfigurationRepository.save(konfiguration);

        restTestClient.post()
                .uri("/konfiguration/setActive")
                .body(konfiguration)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Konfiguration.class)
                .value(konfigurationResult -> {
                    assertNotNull(konfigurationResult);
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

                })
                .returnResult()
                .getResponseBody();

    }

}
