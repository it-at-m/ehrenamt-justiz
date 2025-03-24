package de.muenchen.ehrenamtjustiz.eai.personeninfo.route;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplaceOldBodyStrategy implements AggregationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(ReplaceOldBodyStrategy.class);

    @Override
    public Exchange aggregate(final Exchange oldExchange, final Exchange newExchange) {
        final Message oldMessage = this.getMessageContainingBody(oldExchange);
        final Message newMessage = this.getMessageContainingBody(newExchange);
        final Exception exOccured = this.getException(newExchange);
        if (exOccured != null) {
            this.handleException(oldExchange, newExchange, exOccured);
        } else {
            oldMessage.setBody(newMessage.getBody());
        }

        return oldExchange;
    }

    protected Exception getException(final Exchange exchange) {
        final Exception ex = exchange.getException();
        return ex != null ? ex : exchange.getProperty("CamelExceptionCaught", Exception.class);
    }

    protected void handleException(final Exchange oldExchange, final Exchange newExchange, final Exception exception) {
        LOG.info("Es wurde ein Fehler uebernommen", exception);
        oldExchange.setException(exception);
    }

    protected Message getMessageContainingBody(final Exchange exchange) {
        return exchange.getMessage();
    }
}
