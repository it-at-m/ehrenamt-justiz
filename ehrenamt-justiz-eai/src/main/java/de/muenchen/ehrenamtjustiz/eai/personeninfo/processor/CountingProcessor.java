package de.muenchen.ehrenamtjustiz.eai.personeninfo.processor;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.support.processor.DelegateAsyncProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor for time measurements
 */
public class CountingProcessor extends DelegateAsyncProcessor {
    private final Logger logger;
    private final NamedNode node;

    public CountingProcessor(final NamedNode definition, final Processor target) {
        super(target);
        this.node = definition;
        final String var10001 = CountingProcessor.class.getName();
        this.logger = LoggerFactory.getLogger(var10001 + "." + this.node.getId());
    }

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        final long time = System.currentTimeMillis();
        return super.process(exchange, (doneSync) -> {
            if (doneSync) {
                final long processingTime = System.currentTimeMillis() - time;
                this.logger.trace("{}", processingTime);
            }

            callback.done(doneSync);
        });
    }
}
