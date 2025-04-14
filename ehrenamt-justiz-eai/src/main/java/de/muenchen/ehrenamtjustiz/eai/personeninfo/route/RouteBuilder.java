package de.muenchen.ehrenamtjustiz.eai.personeninfo.route;

import de.muenchen.ehrenamtjustiz.api.BuergerSucheAnfrage;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.config.Configuration;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.config.Konstanten;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.exception.DefaultErrorProjection;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.exception.Fehler;
import java.nio.charset.StandardCharsets;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestHostNameResolver;
import org.apache.camel.model.rest.RestParamType;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Class used for defining the camel-route.<br>
 * Defines the following rest-endpoints:
 * <ul>
 * <li>GET /eairoutes/ewosuchemitom/{om} Get citizen by id
 * <li>POST /eairoutes/ewosuche search for citizens
 * </ul>
 *
 */
@Component
@SuppressWarnings("CPD-START")
public class RouteBuilder extends BasicRouteBuilder {

    public static final String EP_PROCESS_GET_PERSONENINFO = "direct:processGetEWOPERSONENINFO";
    public static final String EP_PROCESS_GET_PERSONENINFO_EWOSUCHE = "direct:processGetEWOPERSONENINFO_EWOSUCHE";
    public static final String EP_SEND_TO_EWO_PERSONENINFO = "direct:sndToEWOPERSONENINFO";

    public static final String EP_SEND_TO_EWO_PERSONENINFO_SUCHE = "direct:sndToEWOPERSONENINFO_EWOSUCHE";

    private static final String BEAN_MAPPER = "mappingBean";

    /**
     * Version of the schema to be displayed in the API description
     */
    @Value("${api.api.schema-version:unknownSchemaVersion}")
    private String schemaVersion;
    /**
     * Version of the service to be displayed in the API description
     */
    @Value("${api.api.service-version:unknownServiceVersion}")
    private String serviceVersion;

    @Value(Configuration.BASEPATH_VALUE)
    private String basePath;

    @Value("${server.port:8080}")
    private int port;

    /**
     * Init Routebuilder
     *
     * @param doInterceptFrom To activate the Logging-Intercepter
     * @param doProcesstimeLogging To log outputs for time measurements
     * @param doTracing To activate the Camel Tracer
     */
    @Autowired
    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public RouteBuilder(@Value("${camel.doInterceptFrom:false}") final boolean doInterceptFrom,
            @Value("${camel.doProcesstimeLogging:false}") final boolean doProcesstimeLogging, @Value("${camel.doTracing:false}") final boolean doTracing) {
        super();
        setDoInterceptFrom(doInterceptFrom);
        setDoProcesstimeLogging(doProcesstimeLogging);
        setDoTracing(doTracing);
    }

    @Override
    public void configure() throws Exception {
        super.configure();

        //@formatter:off
        onException(Throwable.class)
                .handled(true)
                .removeHeaders("*").id("removeHeadersOnException")
                .log(LoggingLevel.WARN, "onException von ProcessCxfRequest - ${exception}")
                .process("fehlerWrapper").id("fehlerWrapper")
        ;

        restConfiguration()
                .apiContextPath(Konstanten.API_DOC_SUB_PATH)
                .apiProperty("api.title", "Camel REST API")
                .apiProperty("api.version", "Schema " + schemaVersion + " Service " + serviceVersion)
                .contextPath(basePath)
                .port(port)
                .hostNameResolver(RestHostNameResolver.localHostName)
                .bindingMode(RestBindingMode.auto)
        ;

        // Get citizen by id
        rest(Konstanten.PERSONENINFO_SUB_PATH_EWO_SUCHE_MIT_OM).description("Zugriff auf Personeninformationen von EWO über Suche mit OM")
                .get("/{"+RouteKonstanten.HEAD_PERSON_OM+"}").id("getPersoneninfoOm").description("Lesen von Personeninformationen")
                .param().name(RouteKonstanten.HEAD_PERSON_OM).type(RestParamType.path).required(Boolean.TRUE).description("EWO-OM").endParam()
                .produces("application/json;charset=UTF-8")
                .responseMessage().code(HttpStatus.SC_NOT_FOUND)
                .message("Keine Person zu OM gefunden").responseModel(Fehler.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_UNAUTHORIZED)
                .message("Es liegt keine Authentifizierung vor").responseModel(DefaultErrorProjection.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_FORBIDDEN)
                .message("Die notwendigen Rechte fehlen").responseModel(DefaultErrorProjection.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .message("Interner Verarbeitungsfehler").responseModel(Fehler.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_BAD_GATEWAY)
                .message("Fehlerhafte Antwort vom Fachverfahren").responseModel(Fehler.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_GATEWAY_TIMEOUT)
                .message("Timeout bei Bearbeitung durch Fachverfahren oder keine Verbindung").responseModel(Fehler.class).endResponseMessage()
                .to(EP_PROCESS_GET_PERSONENINFO)
        ;

        // Search for citizens
        rest(Konstanten.PERSONENINFO_SUB_PATH_EWO_SUCHE).description("Zugriff auf Personeninformationen von EWO über freie Suchfelder")
                .post()
                .type(BuergerSucheAnfrage.class)
                .produces("application/json;charset=UTF-8")
                .responseMessage().code(HttpStatus.SC_NOT_FOUND)
                .message("Die angeforderte Ressource wurde nicht gefunden").responseModel(Fehler.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_UNAUTHORIZED)
                .message("Es liegt keine Authentifizierung vor").responseModel(DefaultErrorProjection.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_FORBIDDEN)
                .message("Die notwendigen Rechte fehlen").responseModel(DefaultErrorProjection.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .message("Interner Verarbeitungsfehler").responseModel(Fehler.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_BAD_GATEWAY)
                .message("Fehlerhafte Antwort vom Fachverfahren").responseModel(Fehler.class).endResponseMessage()
                .responseMessage().code(HttpStatus.SC_GATEWAY_TIMEOUT)
                .message("Timeout bei Bearbeitung durch Fachverfahren oder keine Verbindung").responseModel(Fehler.class).endResponseMessage()
                .to(EP_PROCESS_GET_PERSONENINFO_EWOSUCHE)
        ;

        // preparation and post-processing to get a citizen by id
        from(EP_PROCESS_GET_PERSONENINFO)
                .routeId(buildRouteID(EP_PROCESS_GET_PERSONENINFO))
                .bean(BEAN_MAPPER, "toEWOLesen").id("mapToEWOLesen")
                .enrich(EP_SEND_TO_EWO_PERSONENINFO, new ReplaceOldBodyStrategy())
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .setHeader(Exchange.CONTENT_ENCODING, constant(StandardCharsets.UTF_8))
                .bean(BEAN_MAPPER, "fromEWOLesen").id("mapFromEWOLesen")
        ;

        // Call the service of registration of residents to get a citizen by id
        from(EP_SEND_TO_EWO_PERSONENINFO)
                .routeId(buildRouteID(EP_SEND_TO_EWO_PERSONENINFO))
                .removeHeaders("*")
                .to("{{api.cxf.producer.uri.personeninfo}}")
                ;

        // preparation and post-processing to search for citizens
        from(EP_PROCESS_GET_PERSONENINFO_EWOSUCHE)
                .routeId(EP_PROCESS_GET_PERSONENINFO_EWOSUCHE)
                .bean(BEAN_MAPPER, "toEWOSuche").id("mapToEWOSuche")
                .enrich(EP_SEND_TO_EWO_PERSONENINFO_SUCHE, new ReplaceOldBodyStrategy())
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .setHeader(Exchange.CONTENT_ENCODING, constant(StandardCharsets.UTF_8))
                .bean(BEAN_MAPPER, "fromEWOSuche").id("mapFromEWOSuche")
        ;

        // Call the service of registration of residents to search for citizens
        from(EP_SEND_TO_EWO_PERSONENINFO_SUCHE)
                .routeId(buildRouteID(EP_SEND_TO_EWO_PERSONENINFO_SUCHE))
                .removeHeaders("*")
                .to("{{api.cxf.producer.uri.personeninfoewosuche}}")
        ;


        //@formatter:on
    }
}
