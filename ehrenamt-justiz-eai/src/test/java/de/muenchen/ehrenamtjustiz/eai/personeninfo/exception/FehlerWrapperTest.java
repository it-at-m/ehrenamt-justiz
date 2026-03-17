package de.muenchen.ehrenamtjustiz.eai.personeninfo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void setUp() {
        unitUnderTest = new FehlerWrapper();
    }

    @Test
    void givenIOExceptionAndIOException_thenIsOrHasCauseClass() {
        assertTrue(unitUnderTest.isOrHasCauseClass(new IOException(), IOException.class));
    }

    @Test
    void givenFileNotFoundExceptionAndIOException_thenIsOrHasCauseClass() {
        assertTrue(unitUnderTest.isOrHasCauseClass(new FileNotFoundException(), IOException.class));
    }

    @Test
    void givenIOExceptionAndNullPointerException_thenIsOrHasCauseClass() {
        assertFalse(unitUnderTest.isOrHasCauseClass(new IOException(), NullPointerException.class));
    }

    @Test
    void givenExceptionOfIOExceptionAndIOException_thenIsOrHasCauseClass() {
        assertTrue(unitUnderTest.isOrHasCauseClass(new Exception(new Exception(new IOException())), IOException.class));
    }

    @Test
    void givenExceptionOfFileNotFoundExceptionAndIOException_thenIsOrHasCauseClass() {
        assertTrue(unitUnderTest.isOrHasCauseClass(new Exception(new Exception(new FileNotFoundException())), IOException.class));
    }

    @Test
    void givenExceptionOfIOExceptionAndNullPointerException_thenIsOrHasCauseClass() {
        assertFalse(unitUnderTest.isOrHasCauseClass(new Exception(new Exception(new IOException())), NullPointerException.class));
    }

    @Test
    void givenException_thenValidMessage() {
        final Exchange ex = createExchangeWithThrowable(new Exception());

        unitUnderTest.process(ex);

        assertEquals("Es ist ein unbekannter Fehler aufgetreten", ex.getIn().getBody(Fehler.class).getMessage());
    }

    @Test
    void givenPersonNotFoundException_thenNotFoundStatus() {
        final Exchange ex = createExchangeWithThrowable(new CamelExecutionException(null, null, new PersonNotFoundException("123", "Keine Person gefunden")));

        unitUnderTest.process(ex);

        assertHttpStatus(ex, 404);
    }

    @Test
    void givenSocketTimeoutException_thenTimeoutStatus() {
        final Exchange ex = createExchangeWithThrowable(new CamelExecutionException(null, null, new SocketTimeoutException()));

        unitUnderTest.process(ex);

        assertHttpStatus(ex, 504);
    }

    @Test
    void givenConnectException_thenTimeoutStatus() {
        final Exchange ex = createExchangeWithThrowable(new CamelExecutionException(null, null, new ConnectException()));

        unitUnderTest.process(ex);

        assertHttpStatus(ex, 504);
    }

    @Test
    void givenNullPointerException_thenInternalServerException() {
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
