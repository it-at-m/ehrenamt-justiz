package de.muenchen.ehrenamtjustiz.eai.personeninfo;

import static de.muenchen.ehrenamtjustiz.eai.personeninfo.config.Konstanten.API_DOC_SUB_PATH;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.muenchen.eai.ewo.api.fachlich.model.person.v2.AbstractWohnungType;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.LesePersonErweitertResponse;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.SuchePersonErweitertResponse;
import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.api.Geschlecht;
import de.muenchen.ehrenamtjustiz.api.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.config.Configuration;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.converter.AbstractWohnungTypeconverter;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.converter.XMLGregorianCalendarConverter;
import java.io.File;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.message.MessageContentsList;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration()
@ActiveProfiles(profiles = "integrationstest")
@AutoConfigureRestTestClient
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
    @EndpointInject("mock:cxfProducer")
    MockEndpoint cxfProducer;

    @Autowired
    public ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        cxfProducer.reset();
    }

    @Test
    void givenValidOM_thenSuccessEwoSucheMitOM() throws Exception {
        cxfProducer.whenAnyExchangeReceived(exchange -> {

            @SuppressWarnings("PMD.LooseCoupling")
            final MessageContentsList messageContentsList = new MessageContentsList();
            final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheMitOMResponse.json"),
                    UTF_8);
            final Gson gson = new GsonBuilder()
                    .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Deserializer())
                    .registerTypeAdapter(AbstractWohnungType.class, new AbstractWohnungTypeconverter.Deserializer())
                    .create();
            final LesePersonErweitertResponse lesePersonErweitertResponse = gson.fromJson(jsonPersonErweitert, LesePersonErweitertResponse.class);
            messageContentsList.add(lesePersonErweitertResponse.getPersonErweitert());
            exchange.getIn().setBody(messageContentsList);
        });
        cxfProducer.expectedMessageCount(1);

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

                    assert responseBody != null;
                    final EWOBuerger ewoBuerger = objectMapper.readValue(responseBody, EWOBuerger.class);

                    assertEquals("Wimtest-zwei-ohne-Pass", ewoBuerger.getFamilienname());
                    assertEquals("162015039514", ewoBuerger.getOrdnungsmerkmal());
                    assertEquals("1990-09-20", ewoBuerger.getGeburtsdatum().toString());
                    assertEquals("Lagos", ewoBuerger.getGeburtsort());
                    assertEquals("Deutschland", ewoBuerger.getGeburtsland());
                    assertEquals(Geschlecht.MAENNLICH, ewoBuerger.getGeschlecht());
                    assertEquals("LD", ewoBuerger.getFamilienstand());
                    assertEquals("Franklin", ewoBuerger.getVorname());
                    assertEquals("2020-04-09", ewoBuerger.getInMuenchenSeit().toString());
                    assertEquals("nigerianisch", ewoBuerger.getStaatsangehoerigkeit().getFirst());
                    assertEquals("Alexandrastr.", ewoBuerger.getStrasse());
                    assertEquals("0", ewoBuerger.getHausnummer());
                    assertEquals("München", ewoBuerger.getOrt());
                    assertEquals("80538", ewoBuerger.getPostleitzahl());
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

        cxfProducer.assertIsSatisfied();

    }

    @Test
    void givenValidSearch_thenSuccessEwoSuche() throws Exception {
        cxfProducer.whenAnyExchangeReceived(exchange -> {

            @SuppressWarnings("PMD.LooseCoupling")
            final MessageContentsList messageContentsList = new MessageContentsList();
            final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheResponse.json"),
                    UTF_8);
            final Gson gson = new GsonBuilder()
                    .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Deserializer())
                    .registerTypeAdapter(AbstractWohnungType.class, new AbstractWohnungTypeconverter.Deserializer())
                    .create();
            final SuchePersonErweitertResponse suchePersonErweitertResponse = gson.fromJson(jsonPersonErweitert, SuchePersonErweitertResponse.class);
            messageContentsList.add(suchePersonErweitertResponse.getAntwortErweitert());
            exchange.getIn().setBody(messageContentsList);
        });
        cxfProducer.expectedMessageCount(1);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpw"))
                .build();

        client
                .post()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/ewosuche")
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(responseBody -> {

                    final JavaType type = objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, EWOBuerger.class);

                    assert responseBody != null;
                    final List<EWOBuerger> ewoBuergers = objectMapper.readValue(responseBody, type);

                    assertEquals("162015022725", ewoBuergers.getFirst().getOrdnungsmerkmal());
                    assertEquals("WIM-GV Dreizehn neu", ewoBuergers.getFirst().getFamilienname());
                    assertEquals("1960-01-01", ewoBuergers.getFirst().getGeburtsdatum().toString());
                    assertEquals("Berlin", ewoBuergers.getFirst().getGeburtsort());
                    assertEquals("Deutschland", ewoBuergers.getFirst().getGeburtsland());
                    assertEquals(Geschlecht.WEIBLICH, ewoBuergers.getFirst().getGeschlecht());
                    assertEquals("VH", ewoBuergers.getFirst().getFamilienstand());
                    assertEquals("Frau", ewoBuergers.getFirst().getVorname());
                    assertEquals("2000-01-01", ewoBuergers.getFirst().getInMuenchenSeit().toString());
                    assertEquals("Widmannstr.", ewoBuergers.getFirst().getStrasse());
                    assertEquals("16", ewoBuergers.getFirst().getHausnummer());
                    assertEquals("München", ewoBuergers.getFirst().getOrt());
                    assertEquals("81829", ewoBuergers.getFirst().getPostleitzahl());
                    assertEquals("0", ewoBuergers.getFirst().getStockwerk());
                    assertEquals(Wohnungsstatus.HAUPTWOHNUNG, ewoBuergers.getFirst().getWohnungsstatus());
                    assertEquals("deutsch", ewoBuergers.getFirst().getStaatsangehoerigkeit().getFirst());
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

        cxfProducer.assertIsSatisfied();

    }

    @Test
    void givenOMAndInvalidCredentials_thenFailedEwoSuche() throws Exception {

        cxfProducer.expectedMessageCount(0);

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

        cxfProducer.assertIsSatisfied();

    }

    @Test
    void givenSearchAndInvalidCredentials_thenFailedEwoSuche() throws Exception {

        cxfProducer.expectedMessageCount(0);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("invaliduser", "invalidpassword"))
                .build();

        client
                .post()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/ewosuche")
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();

        cxfProducer.assertIsSatisfied();
    }

    @Test
    void givenOMAndInvalidPath_thenFailedEwoSucheMitOM() throws Exception {

        cxfProducer.expectedMessageCount(0);

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

        cxfProducer.assertIsSatisfied();
    }

    @Test
    void givenSearchAndInvalidPath_thenFailedEwoSuche() throws Exception {

        cxfProducer.expectedMessageCount(0);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpw"))
                .build();

        client
                .post()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/invalidPath")
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();

        cxfProducer.assertIsSatisfied();
    }

    @Test
    void givenOMAndInvalidResponse_thenFailedEwoSucheMitOM() throws Exception {
        cxfProducer.whenAnyExchangeReceived(exchange -> {

            @SuppressWarnings("PMD.LooseCoupling")
            final MessageContentsList messageContentsList = new MessageContentsList();
            final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheKeinResponse.json"),
                    UTF_8);
            final Gson gson = new GsonBuilder()
                    .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Deserializer())
                    .registerTypeAdapter(AbstractWohnungType.class, new AbstractWohnungTypeconverter.Deserializer())
                    .create();
            final LesePersonErweitertResponse lesePersonErweitertResponse = gson.fromJson(jsonPersonErweitert, LesePersonErweitertResponse.class);
            messageContentsList.add(lesePersonErweitertResponse.getPersonErweitert());
            exchange.getIn().setBody(messageContentsList);
        });
        cxfProducer.expectedMessageCount(1);

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
                .expectStatus().is4xxClientError();

        cxfProducer.assertIsSatisfied();
    }

    @Test
    void givenSearchAndInvalidResponse_thenFailedEwoSuche() throws Exception {
        cxfProducer.whenAnyExchangeReceived(exchange -> {

            @SuppressWarnings("PMD.LooseCoupling")
            final MessageContentsList messageContentsList = new MessageContentsList();
            final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheKeinResponse.json"),
                    UTF_8);
            final Gson gson = new GsonBuilder()
                    .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Deserializer())
                    .registerTypeAdapter(AbstractWohnungType.class, new AbstractWohnungTypeconverter.Deserializer())
                    .create();
            final SuchePersonErweitertResponse suchePersonErweitertResponse = gson.fromJson(jsonPersonErweitert, SuchePersonErweitertResponse.class);
            messageContentsList.add(suchePersonErweitertResponse.getAntwortErweitert());
            exchange.getIn().setBody(messageContentsList);
        });
        cxfProducer.expectedMessageCount(1);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);

        RestTestClient client = restTestClient
                .mutate()
                .defaultHeaders(headers -> headers.setBasicAuth("testuser", "testpw"))
                .build();

        client
                .post()
                .uri("http://localhost:" + serverport + basePath + "/eairoutes/ewosuche")
                .body(requestBody)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();

        cxfProducer.assertIsSatisfied();

    }

    @Test
    void givenRunningEAI_thenGetApiDoc() throws Exception {

        cxfProducer.expectedMessageCount(0);

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

        cxfProducer.assertIsSatisfied();
    }

    @Test
    void givenRunningEAI_thenGetInfoWithoutCredentials() throws Exception {
        cxfProducer.expectedMessageCount(0);

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

        cxfProducer.assertIsSatisfied();
    }

    @Test
    void givenRunningEAI_thenCheckHealthWithoutCredentials() throws Exception {
        cxfProducer.expectedMessageCount(0);

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

        cxfProducer.assertIsSatisfied();
    }
}
