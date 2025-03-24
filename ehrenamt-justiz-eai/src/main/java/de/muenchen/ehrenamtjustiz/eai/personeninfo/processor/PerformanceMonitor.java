package de.muenchen.ehrenamtjustiz.eai.personeninfo.processor;

import org.apache.camel.CamelContext;
import org.apache.camel.NamedNode;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.spi.InterceptStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerformanceMonitor implements InterceptStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(PerformanceMonitor.class);

    @Override
    public Processor wrapProcessorInInterceptors(final CamelContext context, final NamedNode definition, final Processor target, Processor nextTarget) {
        if (definition instanceof ProcessDefinition) {
            LOG.debug("definition ist eine ProcessDefinition (label > {} id > {}), daher kein Wrapping", definition.getLabel(), definition.getId());
            return target;
        } else {
            LOG.debug("definition {} wrapped", definition.getLabel());
            return new CountingProcessor(definition, target);
        }
    }
}
