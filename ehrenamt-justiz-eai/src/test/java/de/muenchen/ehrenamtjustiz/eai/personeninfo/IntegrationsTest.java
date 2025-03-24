package de.muenchen.ehrenamtjustiz.eai.personeninfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import de.muenchen.eai.ewo.api.fachlich.model.person.v2.AbstractWohnungType;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.LesePersonErweitertResponse;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.SuchePersonErweitertResponse;
import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.api.Geschlecht;
import de.muenchen.ehrenamtjustiz.api.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.config.Configuration;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.converter.AbstractWohnungTypeconverter;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.converter.XMLGregorianCalendarConverter;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MultiValueMap;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.net.URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration()
@ActiveProfiles(profiles = "integrationstest")
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.NcssCount", "PMD.DataClass", "CPD-START" })
class IntegrationsTest {

    private static final Logger LOG = LoggerFactory.getLogger(IntegrationsTest.class);

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    public int serverport;

    @Value(Configuration.BASEPATH_VALUE)
    public String basePath;

    /* default */
    @EndpointInject("mock:cxfProducer")
    MockEndpoint cxfProducer;

    @Autowired
    private ConfigurableApplicationContext ctx;

    @Autowired
    public ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        cxfProducer.reset();
    }

    @Test
    void test_correctRequestSucheMitOM() throws Exception {
        cxfProducer.whenAnyExchangeReceived(new Processor() {

            @Override
            public void process(final Exchange exchange) throws Exception {

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
            }
        });
        cxfProducer.expectedMessageCount(1);

        LOG.info("port > {}", serverport);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVzdHVzZXI6dGVzdHB3");
        final RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
                new URL("http://localhost:" + serverport + basePath + "/eairoutes/ewosuchemitom/162015039514").toURI());
        final ResponseEntity<String> result = testRestTemplate.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertEquals(200, result.getStatusCode().value());
        final EWOBuerger ewoBuerger = objectMapper.readValue(result.getBody(), EWOBuerger.class);

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

        cxfProducer.assertIsSatisfied();

    }

    @Test
    void test_correctRequestSuche() throws Exception {
        cxfProducer.whenAnyExchangeReceived(new Processor() {

            @Override
            public void process(final Exchange exchange) throws Exception {

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
            }
        });
        cxfProducer.expectedMessageCount(1);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        final RequestEntity<String> request = new RequestEntity<>(requestBody, headers, HttpMethod.POST,
                new URL("http://localhost:" + serverport + basePath + "/eairoutes/ewosuche").toURI());
        final TestRestTemplate template = testRestTemplate.withBasicAuth("testuser", "testpw");

        final ResponseEntity<String> result = template.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertEquals(200, result.getStatusCode().value());
        final List<EWOBuerger> ewoBuergers = objectMapper.readValue(result.getBody(), new TypeReference<>() {
        });

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

        cxfProducer.assertIsSatisfied();

    }

    @Test
    void test_invalidCredentialsSucheMitOM() throws Exception {
        LOG.info("port > {}", serverport);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", "Basic wrongCredentials");
        final RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
                new URL("http://localhost:" + serverport + basePath + "/eairoutes/ewosuchemitom/162015039514").toURI());
        final ResponseEntity<String> result = testRestTemplate.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    void test_invalidCredentialsSuche() throws Exception {
        LOG.info("port > {}", serverport);
        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        final RequestEntity<String> request = new RequestEntity<>(requestBody, headers, HttpMethod.POST,
                new URL("http://localhost:" + serverport + basePath + "/eairoutes/ewosuche").toURI());
        final TestRestTemplate template = testRestTemplate.withBasicAuth("falscherUser", "falschesPassword");

        final ResponseEntity<String> result = template.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertEquals(401, result.getStatusCode().value());
    }

    @Test
    void test_wrongPathSucheMitOM() throws Exception {
        LOG.info("port > {}", serverport);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVzdHVzZXI6dGVzdHB3");
        final RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
                new URL("http://localhost:" + serverport + basePath + "/eairoutes/wrongpath/162015039514").toURI());
        final ResponseEntity<String> result = testRestTemplate.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    void test_wrongPathSuche() throws Exception {
        LOG.info("port > {}", serverport);
        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        final RequestEntity<String> request = new RequestEntity<>(requestBody, headers, HttpMethod.POST,
                new URL("http://localhost:" + serverport + basePath + "/wrongpath/ewosuche").toURI());
        final TestRestTemplate template = testRestTemplate.withBasicAuth("testuser", "testpw");

        final ResponseEntity<String> result = template.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertEquals(403, result.getStatusCode().value());
    }

    @Test
    void test_invalidSucheMitOM() throws Exception {
        cxfProducer.whenAnyExchangeReceived(new Processor() {

            @Override
            public void process(final Exchange exchange) throws Exception {

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
            }
        });
        cxfProducer.expectedMessageCount(1);

        LOG.info("port > {}", serverport);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVzdHVzZXI6dGVzdHB3");
        final RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
                new URL("http://localhost:" + serverport + basePath + "/eairoutes/ewosuchemitom/162015039514").toURI());
        final ResponseEntity<String> result = testRestTemplate.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertEquals(404, result.getStatusCode().value());
        cxfProducer.assertIsSatisfied();
    }

    @Test
    void test_invalidSuche() throws Exception {
        cxfProducer.whenAnyExchangeReceived(new Processor() {

            @Override
            public void process(final Exchange exchange) throws Exception {

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
            }
        });
        cxfProducer.expectedMessageCount(1);

        LOG.info("port > {}", serverport);

        final String requestBody = Files.contentOf(new File("src/test/resources/testnachrichten/EWOSucheRequest.json"), UTF_8);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        final RequestEntity<String> request = new RequestEntity<>(requestBody, headers, HttpMethod.POST,
                new URL("http://localhost:" + serverport + basePath + "/eairoutes/ewosuche").toURI());
        final TestRestTemplate template = testRestTemplate.withBasicAuth("testuser", "testpw");

        final ResponseEntity<String> result = template.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertEquals(404, result.getStatusCode().value());
        cxfProducer.assertIsSatisfied();
    }

    @Test
    void test_apiDocsIsDelivered() throws Exception {

        LOG.info("port > {}", serverport);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        final RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
                new URL("http://localhost:" + serverport + basePath + "/api-doc").toURI());
        final ResponseEntity<String> result = testRestTemplate.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody().contains("\"title\" : \"Camel REST API\""));
    }

    @Test
    void test_infoEndpointOhneBenutzerErreichbar() throws Exception {
        LOG.info("port > {}", serverport);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        final RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
                new URL("http://localhost:" + serverport + "/actuator/info").toURI());
        final ResponseEntity<String> result = testRestTemplate.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertTrue(result.getBody().contains("\"description\":"));
        assertTrue(result.getBody().contains("\"version\":"));
        assertTrue(result.getBody().contains("\"name\":"));
        assertEquals(200, result.getStatusCodeValue());
    }

    @Test
    void test_infoEndpointMitBenutzerErreichbar() throws Exception {
        LOG.info("port > {}", serverport);
        final MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWN0dWF0b3I6YWN0dWF0b3I=");
        final RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET,
                new URL("http://localhost:" + serverport + "/actuator/info").toURI());
        final ResponseEntity<String> result = testRestTemplate.exchange(request, String.class);
        LOG.info("result-Status > {}", result.getStatusCode());
        LOG.info("result > {}", result.getBody());

        assertTrue(result.getBody().contains("\"description\":"));
        assertTrue(result.getBody().contains("\"version\":"));
        assertTrue(result.getBody().contains("\"name\":"));
        assertEquals(200, result.getStatusCodeValue());
    }

}
