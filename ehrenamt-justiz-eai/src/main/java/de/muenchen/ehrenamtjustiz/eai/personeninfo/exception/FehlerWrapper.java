package de.muenchen.ehrenamtjustiz.eai.personeninfo.exception;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.util.ArrayList;
import java.util.List;

import de.muenchen.ehrenamtjustiz.api.Fehler;
import de.muenchen.ehrenamtjustiz.api.InterneFehlerCodes;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.interceptor.Fault;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ErrorWrapper
 * The following error classes are handled:
 * <ul>
 * <li>{@link PersonNotFoundException} -&gt; --> http 404</li>
 * <li>{@link SocketTimeoutException} -&gt; --> http 504</li>
 * <li>{@link ConnectException} -&gt; --> http 504</li>
 * <li>{@link HttpTimeoutException} -&gt; --> http 504</li>
 * <li>{@link Fault} -&gt; Unknown error --> error code: 999</li>
 * </ul>
 */
@Component
public class FehlerWrapper implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(FehlerWrapper.class);

    private static final String UNKNOWN_ERROR_MSG = "Es ist ein unbekannter Fehler aufgetreten";

    @Override
    public void process(final Exchange exchange) {
        Throwable cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
        LOG.info("Exception caught > ", cause);

        if (cause instanceof CamelExecutionException) {
            cause = cause.getCause();
            LOG.debug("internal cause of CamelExecutionException > ", cause);
        }

        final Fehler fehler = new Fehler();
        fehler.setMessage(cause.getMessage());
        if (fehler.getMessage() == null) {
            fehler.setMessage(UNKNOWN_ERROR_MSG);
        }
        exchange.getIn().setBody(fehler);
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "application/json");

        if (isOrHasCauseClass(cause, PersonNotFoundException.class)) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_NOT_FOUND);
        } else if (isOrHasCauseClass(cause, SocketTimeoutException.class)) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_GATEWAY_TIMEOUT);
            fehler.setId(InterneFehlerCodes.TIMEOUT);
        } else if (isOrHasCauseClass(cause, ConnectException.class)) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_GATEWAY_TIMEOUT);
            fehler.setId(InterneFehlerCodes.KEINE_VERBINDUNG);
        } else if (isOrHasCauseClass(cause, HttpTimeoutException.class)) {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_GATEWAY_TIMEOUT);
            fehler.setId(InterneFehlerCodes.KEINE_VERBINDUNG);
        } else {
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.SC_INTERNAL_SERVER_ERROR);
            fehler.setId(InterneFehlerCodes.UNBEKANNTER_FEHLER);
        }
    }

    /**
     * Check whether the throwable is a cause of a certain class.
     *
     * @param throwable Throwable to validate
     * @param causeToCheck Errorclass to check
     * @return true if the Throwable or a Cause object is an instance (or derivation) of the
     *         causeToCheck.
     */
    protected boolean isOrHasCauseClass(final Throwable throwable, final Class<? extends Throwable> causeToCheck) {
        final List<Object> causesVisited = new ArrayList<>();
        Throwable cause = throwable;
        do {
            if (causeToCheck.isAssignableFrom(cause.getClass())) {
                return true;
            } else {
                causesVisited.add(cause);
                cause = cause.getCause();
            }
        } while (cause != null && !causesVisited.contains(cause));

        return false;
    }

}
