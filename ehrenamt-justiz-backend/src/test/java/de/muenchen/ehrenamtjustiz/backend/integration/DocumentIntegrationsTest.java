package de.muenchen.ehrenamtjustiz.backend.integration;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.ehrenamtjustiz.backend.EhrenamtJustizApplication;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.domain.Document;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.DocumentDto;
import de.muenchen.ehrenamtjustiz.backend.rest.DocumentRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.testdata.DocumentTestDataBuilder;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import de.muenchen.ehrenamtjustiz.backend.testdata.PersonTestDataBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MultiValueMap;
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
@SuppressWarnings({ "PMD.CouplingBetweenObjects", "PMD.AvoidDuplicateLiterals" })
class DocumentIntegrationsTest {

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private DocumentRepository documentRepository;

    private static UUID uuidKonfiguration;

    private static UUID uuidDocumentByConfiguration;

    private static UUID uuidPerson;

    private static UUID uuidDocumentByPerson;

    @LocalServerPort
    public int serverport;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        documentRepository.deleteAll();
        konfigurationRepository.deleteAll();

        // insert new configuration
        final Konfiguration konfiguration = konfigurationRepository.save(new KonfigurationTestDataBuilder().build());
        uuidKonfiguration = konfiguration.getId();

        // insert person
        final Person person = initPerson();
        personRepository.save(person);
        uuidPerson = person.getId();

        // insert document by configuration
        final Document documentByConfiguration = initDocumentByConfiguration();
        documentRepository.save(documentByConfiguration);
        uuidDocumentByConfiguration = documentByConfiguration.getId();

        // insert document by person
        final Document documentByPerson = initDocumentByPerson();
        documentRepository.save(documentByPerson);
        uuidDocumentByPerson = documentByPerson.getId();

    }

    private static @NotNull Person initPerson() {
        return new PersonTestDataBuilder().withKonfigurationid(uuidKonfiguration).build();
    }

    private static @NotNull Document initDocumentByConfiguration() {
        return new DocumentTestDataBuilder().withKonfigurationId(uuidKonfiguration).build();
    }

    private static @NotNull Document initDocumentByPerson() {
        return new DocumentTestDataBuilder().withPersonId(uuidPerson).build();
    }

    @Test
    void givenDocument_thenCheckGetDocumentByKonfigurationId() throws URISyntaxException {

        final MultiValueMap<String, String> headers = new HttpHeaders();
        RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
                new URI("http://localhost:" + serverport + "/document/getDocumentByKonfigurationId/" + uuidKonfiguration));
        final ResponseEntity<DocumentDto> result = testRestTemplate.exchange(request, DocumentDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        final DocumentDto documentDto = result.getBody();
        assertNotNull(documentDto);
        assertEquals(uuidDocumentByConfiguration, documentDto.getId());

    }

    @Test
    void givenDocument_thenCheckGetDocumentByPersonId() throws URISyntaxException {

        final MultiValueMap<String, String> headers = new HttpHeaders();
        RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
                new URI("http://localhost:" + serverport + "/document/getDocumentByPersonId/" + uuidPerson));
        final ResponseEntity<DocumentDto> result = testRestTemplate.exchange(request, DocumentDto.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        final DocumentDto documentDto = result.getBody();
        assertNotNull(documentDto);
        assertEquals(uuidDocumentByPerson, documentDto.getId());

    }

}
