package de.muenchen.ehrenamtjustiz.eai.personeninfo;

import de.muenchen.ehrenamtjustiz.api.Fehler;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.exception.FehlerWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FehlerWrapperTest {
    private FehlerWrapper unitUnderTest;

    @BeforeEach
    void setup() {
        unitUnderTest = new FehlerWrapper();
    }

    @Test
    void test_MessageVonExceptionWirdInFehlerUebernommen() {
        final Exception exception = new RuntimeException("Bla blubb");
        final Exchange exchange = createExchange(exception);
        unitUnderTest.process(exchange);

        assertEquals("Bla blubb", exchange.getIn().getBody(Fehler.class).getMessage());
    }

    @Test
    void test_BeiExceptionOhneMessageWirdDefaultMessageInFehlerGesetzt() {
        final Exception exception = new RuntimeException((String) null);
        final Exchange exchange = createExchange(exception);

        unitUnderTest.process(exchange);

        assertNotNull(exchange.getIn().getBody(Fehler.class).getMessage());
    }

    private Exchange createExchange(final Exception exception) {
        final Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        exchange.setProperty(Exchange.EXCEPTION_CAUGHT, exception);

        return exchange;
    }
}
