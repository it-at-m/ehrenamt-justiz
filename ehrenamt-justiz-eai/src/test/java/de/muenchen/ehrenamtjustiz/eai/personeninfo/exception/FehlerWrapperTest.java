package de.muenchen.ehrenamtjustiz.eai.personeninfo.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FehlerWrapperTest {
    private FehlerWrapper unitUnderTest;

    @BeforeEach
    void setup() {
        unitUnderTest = new FehlerWrapper();
    }

    @Test
    void isOrHasCauseClass_throwableIstVonGesuchterKlasse() {
        assertTrue(unitUnderTest.isOrHasCauseClass(new IOException(), IOException.class));
    }

    @Test
    void isOrHasCauseClass_throwableIstSubklasseVonGesuchterKlasse() {
        assertTrue(unitUnderTest.isOrHasCauseClass(new FileNotFoundException(), IOException.class));
    }

    @Test
    void isOrHasCauseClass_throwableIstWederKlasseNochAbleitungVonGesuchterKlasse() {
        assertFalse(unitUnderTest.isOrHasCauseClass(new IOException(), NullPointerException.class));
    }

    @Test
    void isOrHasCauseClass_CauseIstVonGesuchterKlasse() {
        assertTrue(unitUnderTest.isOrHasCauseClass(new Exception(new Exception(new IOException())), IOException.class));
    }

    @Test
    void isOrHasCauseClass_CauseIstSubklasseVonGesuchterKlasse() {
        assertTrue(unitUnderTest.isOrHasCauseClass(new Exception(new Exception(new FileNotFoundException())), IOException.class));
    }

    @Test
    void isOrHasCauseClass_CauseIstWederKlasseNochAbleitungVonGesuchterKlasse() {
        assertFalse(unitUnderTest.isOrHasCauseClass(new Exception(new Exception(new IOException())), NullPointerException.class));
    }

    @Test
    void process_setzeDefaultMessageWennKeineMessageImFehler() {
        final Exchange ex = createExchangeWithThrowable(new Exception());

        unitUnderTest.process(ex);

        assertEquals("Es ist ein unbekannter Fehler aufgetreten", ex.getIn().getBody(Fehler.class).getMessage());
    }

    @Test
    void process_HttpStatus404BeiPersonNotFoundException() {
        final Exchange ex = createExchangeWithThrowable(new CamelExecutionException(null, null, new PersonNotFoundException("123", "Keine Person gefunden")));

        unitUnderTest.process(ex);

        assertHttpStatus(ex, 404);
    }

    @Test
    void process_HttpStatus504BeiTimeout() {
        final Exchange ex = createExchangeWithThrowable(new CamelExecutionException(null, null, new SocketTimeoutException()));

        unitUnderTest.process(ex);

        assertHttpStatus(ex, 504);
    }

    @Test
    void process_HttpStatus504BeiConnectException() {
        final Exchange ex = createExchangeWithThrowable(new CamelExecutionException(null, null, new ConnectException()));

        unitUnderTest.process(ex);

        assertHttpStatus(ex, 504);
    }

    @Test
    void process_HttpStatus500BeiAlleAnderenException() {
        final Exchange ex = createExchangeWithThrowable(new CamelExecutionException(null, null, new NullPointerException()));

        unitUnderTest.process(ex);

        assertHttpStatus(ex, 500);
    }

    private Exchange createExchangeWithThrowable(final Throwable throwable) {
        final Exchange ex = new DefaultExchange(new DefaultCamelContext());

        ex.setProperty(Exchange.EXCEPTION_CAUGHT, throwable);

        return ex;
    }

    private void assertHttpStatus(final Exchange exchangeToCheck, final int httpStatus) {
        assertEquals(httpStatus, exchangeToCheck.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE));
    }
}
