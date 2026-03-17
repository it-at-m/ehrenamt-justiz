package de.muenchen.ehrenamtjustiz.eai.personeninfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.muenchen.ehrenamtjustiz.eai.personeninfo.exception.Fehler;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.exception.FehlerWrapper;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FehlerWrapperTest {
    private FehlerWrapper unitUnderTest;

    @BeforeEach
    void setUp() {
        unitUnderTest = new FehlerWrapper();
    }

    @Test
    void givenRuntimeExcetion_thenMessageValid() {
        final Exception exception = new RuntimeException("Bla blubb");
        final Exchange exchange = createExchange(exception);
        unitUnderTest.process(exchange);

        assertEquals("Bla blubb", exchange.getIn().getBody(Fehler.class).getMessage());
    }

    @Test
    void givenRuntimeExceptionWithoutText_thenMessageValid() {
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
