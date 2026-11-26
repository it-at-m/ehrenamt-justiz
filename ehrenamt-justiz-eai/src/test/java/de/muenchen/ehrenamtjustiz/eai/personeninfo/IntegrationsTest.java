package de.muenchen.ehrenamtjustiz.eai.personeninfo;

import static de.muenchen.ehrenamtjustiz.eai.personeninfo.config.Konstanten.API_DOC_SUB_PATH;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.api.Geschlecht;
import de.muenchen.ehrenamtjustiz.api.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.config.Configuration;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.route.RouteBuilder;
import de.muenchen.ewo.generated.PersonErweitert;
import java.io.File;
import java.util.List;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit6.UseAdviceWith;
import org.apache.commons.io.FileUtils;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.client.RestTestClient;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration()
@ActiveProfiles(profiles = "integrationstest")
@AutoConfigureRestTestClient
@UseAdviceWith
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.NcssCount", "CPD-START" })
class IntegrationsTest {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationsTest.class);

    @Autowired
    private RestTestClient restTestClient;

    @LocalServerPort
    public int serverport;

    @Value(Configuration.BASEPATH_VALUE)
    public String basePath;

    /* default */
    @EndpointInject("mock:producer")
    MockEndpoint producerMock;

    @Autowired
    public ObjectMapper objectMapper;
    @Autowired
    private CamelContext camelContext;

    private static boolean routeAdvised = false;

    @BeforeEach
    void setUp() throws Exception {
        if (!routeAdvised) {
            AdviceWith.adviceWith(camelContext, "route.sndToEWOPERSONENINFO", a -> {
                a.weaveById(RouteBuilder.ID_PERSONENINFOCALL).replace().to("mock:producer");
            });
            routeAdvised = true;
        }
        producerMock.reset();
    }

    @Test
    void givenValidOM_thenSuccessEwoSucheMitOM() throws Exception {

        producerMock.whenAnyExchangeReceived(exchange -> {

            @SuppressWarnings("PMD.LooseCoupling")
            final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheMitOMResponse.json"),
                    UTF_8);
            exchange.getIn().setBody(jsonPersonErweitert);
        });
        producerMock.expectedMessageCount(1);

        LOG.info("port > {}", serverport);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpw"))
                .build();

        client
                .get()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/ewosuchemitom/162015039514")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {

                    assertNotNull(responseBody);
                    final EWOBuerger ewoBuerger = objectMapper.readValue(responseBody, EWOBuerger.class);

                    assertEquals("König", ewoBuerger.getFamilienname());
                    assertEquals("1", ewoBuerger.getOrdnungsmerkmal());
                    assertEquals("1940-01-03", ewoBuerger.getGeburtsdatum().toString());
                    assertEquals("München", ewoBuerger.getGeburtsort());
                    assertEquals("Deutschland", ewoBuerger.getGeburtsland());
                    assertEquals(Geschlecht.MAENNLICH, ewoBuerger.getGeschlecht());
                    assertEquals("VH", ewoBuerger.getFamilienstand());
                    assertEquals("Christopher Manfred", ewoBuerger.getVorname());
                    assertEquals("1940-01-03", ewoBuerger.getInMuenchenSeit().toString());
                    assertEquals("deutsch", ewoBuerger.getStaatsangehoerigkeit().getFirst());
                    assertEquals("Taulerstr.", ewoBuerger.getStrasse());
                    assertEquals("10", ewoBuerger.getHausnummer());
                    assertEquals("München", ewoBuerger.getOrt());
                    assertEquals("80906", ewoBuerger.getPostleitzahl());
                    assertEquals(Wohnungsstatus.HAUPTWOHNUNG, ewoBuerger.getWohnungsstatus());
                    assertNull(ewoBuerger.getFamiliennameZusatz());
                    assertNull(ewoBuerger.getGeburtsname());
                    assertNull(ewoBuerger.getGeburtsnameZusatz());
                    assertNull(ewoBuerger.getAkademischerGrad());
                    assertNull(ewoBuerger.getWohnungsgeber());
                    assertNull(ewoBuerger.getAppartmentnummer());
                    assertNull(ewoBuerger.getBuchstabeHausnummer());
                    assertNull(ewoBuerger.getStockwerk());
                    assertNull(ewoBuerger.getTeilnummerHausnummer());
                    assertNull(ewoBuerger.getZusatz());

                });

        producerMock.assertIsSatisfied();

    }

    @Test
    void givenValidSearch_thenSuccessEwoSuche() throws Exception {
        producerMock.whenAnyExchangeReceived(exchange -> {

            @SuppressWarnings("PMD.LooseCoupling")
            final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheResponse.json"),
                    UTF_8);
            exchange.getIn().setBody(jsonPersonErweitert);
        });
        producerMock.expectedMessageCount(1);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpw"))
                .build();

        client
                .post()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/ewosuche")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    LOG.error("Fehlerli: " + responseBody);
                    final JavaType type = objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, EWOBuerger.class);

                    assertNotNull(responseBody);
                    final List<EWOBuerger> ewoBuergers = objectMapper.readValue(responseBody, type);

                    assertEquals("1", ewoBuergers.getFirst().getOrdnungsmerkmal());
                    assertEquals("König", ewoBuergers.getFirst().getFamilienname());
                    assertEquals("1940-01-03", ewoBuergers.getFirst().getGeburtsdatum().toString());
                    assertEquals("München", ewoBuergers.getFirst().getGeburtsort());
                    assertEquals("Deutschland", ewoBuergers.getFirst().getGeburtsland());
                    assertEquals(Geschlecht.MAENNLICH, ewoBuergers.getFirst().getGeschlecht());
                    assertEquals("VH", ewoBuergers.getFirst().getFamilienstand());
                    assertEquals("Christopher Manfred", ewoBuergers.getFirst().getVorname());
                    assertEquals("1940-01-03", ewoBuergers.getFirst().getInMuenchenSeit().toString());
                    assertEquals("Taulerstr.", ewoBuergers.getFirst().getStrasse());
                    assertEquals("10", ewoBuergers.getFirst().getHausnummer());
                    assertEquals("München", ewoBuergers.getFirst().getOrt());
                    assertEquals("80906", ewoBuergers.getFirst().getPostleitzahl());
                    assertEquals(Wohnungsstatus.HAUPTWOHNUNG, ewoBuergers.getFirst().getWohnungsstatus());
                    assertEquals("deutsch", ewoBuergers.getFirst().getStaatsangehoerigkeit().getFirst());
                    assertNull(ewoBuergers.getFirst().getStockwerk());
                    assertNull(ewoBuergers.getFirst().getFamiliennameZusatz());
                    assertNull(ewoBuergers.getFirst().getGeburtsname());
                    assertNull(ewoBuergers.getFirst().getGeburtsnameZusatz());
                    assertNull(ewoBuergers.getFirst().getAkademischerGrad());
                    assertNull(ewoBuergers.getFirst().getWohnungsgeber());
                    assertNull(ewoBuergers.getFirst().getAppartmentnummer());
                    assertNull(ewoBuergers.getFirst().getBuchstabeHausnummer());
                    assertNull(ewoBuergers.get(0).getTeilnummerHausnummer());
                    assertNull(ewoBuergers.get(0).getZusatz());

                    assertEquals("162015039514", ewoBuergers.get(1).getOrdnungsmerkmal());
                    assertEquals("Wimtest-zwei-ohne-Pass", ewoBuergers.get(1).getFamilienname());
                    assertEquals("1990-09-20", ewoBuergers.get(1).getGeburtsdatum().toString());
                    assertEquals("Lagos", ewoBuergers.get(1).getGeburtsort());
                    assertEquals("Deutschland", ewoBuergers.get(1).getGeburtsland());
                    assertEquals(Geschlecht.MAENNLICH, ewoBuergers.get(1).getGeschlecht());
                    assertEquals("LD", ewoBuergers.get(1).getFamilienstand());
                    assertEquals("Franklin", ewoBuergers.get(1).getVorname());
                    assertEquals("2020-04-09", ewoBuergers.get(1).getInMuenchenSeit().toString());
                    assertEquals("Alexandrastr.", ewoBuergers.get(1).getStrasse());
                    assertEquals("0", ewoBuergers.get(1).getHausnummer());
                    assertEquals("München", ewoBuergers.get(1).getOrt());
                    assertEquals("80538", ewoBuergers.get(1).getPostleitzahl());
                    assertEquals(Wohnungsstatus.HAUPTWOHNUNG, ewoBuergers.get(1).getWohnungsstatus());
                    assertEquals("nigerianisch", ewoBuergers.get(1).getStaatsangehoerigkeit().getFirst());
                    assertNull(ewoBuergers.get(1).getFamiliennameZusatz());
                    assertNull(ewoBuergers.get(1).getGeburtsname());
                    assertNull(ewoBuergers.get(1).getGeburtsnameZusatz());
                    assertNull(ewoBuergers.get(1).getAkademischerGrad());
                    assertNull(ewoBuergers.get(1).getWohnungsgeber());
                    assertNull(ewoBuergers.get(1).getAppartmentnummer());
                    assertNull(ewoBuergers.get(1).getBuchstabeHausnummer());
                    assertNull(ewoBuergers.get(1).getStockwerk());
                    assertNull(ewoBuergers.get(1).getTeilnummerHausnummer());
                    assertNull(ewoBuergers.get(1).getZusatz());

                });

        producerMock.assertIsSatisfied();

    }

    @Test
    void givenOMAndInvalidCredentials_thenFailedEwoSuche() throws Exception {

        producerMock.expectedMessageCount(0);

        LOG.info("port > {}", serverport);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("invaliduser", "invalidpassword"))
                .build();

        client
                .get()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/ewosuchemitom/162015039514")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();

        producerMock.assertIsSatisfied();

    }

    @Test
    void givenSearchAndInvalidCredentials_thenFailedEwoSuche() throws Exception {

        producerMock.expectedMessageCount(0);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("invaliduser", "invalidpassword"))
                .build();

        client
                .post()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/ewosuche")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();

        producerMock.assertIsSatisfied();
    }

    @Test
    void givenOMAndInvalidPath_thenFailedEwoSucheMitOM() throws Exception {

        producerMock.expectedMessageCount(0);

        LOG.info("port > {}", serverport);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpw"))
                .build();

        client
                .get()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/invalidPath/162015039514")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

        producerMock.assertIsSatisfied();
    }

    @Test
    void givenSearchAndInvalidPath_thenFailedEwoSuche() throws Exception {

        producerMock.expectedMessageCount(0);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpw"))
                .build();

        client
                .post()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/invalidPath")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

        producerMock.assertIsSatisfied();
    }

    @Test
    void givenOMAndInvalidResponse_thenFailedEwoSucheMitOM() throws Exception {
        producerMock.whenAnyExchangeReceived(exchange -> {

            final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheKeinResponse.json"),
                    UTF_8);
            ObjectMapper objectMapper = new JsonMapper();
            final PersonErweitert personErweitert = objectMapper.readValue(jsonPersonErweitert, PersonErweitert.class);
            exchange.getIn().setBody(personErweitert);
        });
        producerMock.expectedMessageCount(1);

        LOG.info("port > {}", serverport);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpw"))
                .build();

        client
                .get()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/ewosuchemitom/162015039514")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();

        producerMock.assertIsSatisfied();
    }

    @Test
    void givenSearchAndInvalidResponse_thenFailedEwoSuche() throws Exception {
        producerMock.whenAnyExchangeReceived(exchange -> {

            @SuppressWarnings("PMD.LooseCoupling")
            final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheKeinResponse.json"),
                    UTF_8);
            exchange.getIn().setBody(jsonPersonErweitert);
        });
        producerMock.expectedMessageCount(1);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpw"))
                .build();

        client
                .post()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/ewosuche")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();

        producerMock.assertIsSatisfied();

    }

    @Test
    void givenRunningEAI_thenGetApiDoc() throws Exception {

        producerMock.expectedMessageCount(0);

        LOG.info("port > {}", serverport);

        restTestClient
                .get()
                .uri("http://localhost:" + serverport + basePath + API_DOC_SUB_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    assert responseBody != null;
                    assertTrue(responseBody.contains("\"title\" : \"Camel REST API\""));
                });

        producerMock.assertIsSatisfied();
    }

    @Test
    void givenRunningEAI_thenGetInfoWithoutCredentials() throws Exception {
        producerMock.expectedMessageCount(0);

        LOG.info("port > {}", serverport);

        restTestClient
                .get()
                .uri("http://localhost:" + serverport + "/actuator/info")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertNotNull(responseBody);
                    assertTrue(responseBody.contains("\"description\":"));
                    assertTrue(responseBody.contains("\"version\":"));
                    assertTrue(responseBody.contains("\"name\":"));
                });

        producerMock.assertIsSatisfied();
    }

    @Test
    void givenRunningEAI_thenCheckHealthWithoutCredentials() throws Exception {
        producerMock.expectedMessageCount(0);

        LOG.info("port > {}", serverport);

        restTestClient
                .get()
                .uri("http://localhost:" + serverport + "/actuator/health")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {
                    assertNotNull(responseBody);
                    assertTrue(responseBody.contains("\"status\":\"UP\""));
                });

        producerMock.assertIsSatisfied();
    }
}
