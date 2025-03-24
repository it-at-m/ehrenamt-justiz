package de.muenchen.ehrenamtjustiz.eai.personeninfo.route;

import de.muenchen.ehrenamtjustiz.eai.personeninfo.processor.PerformanceMonitor;
import org.apache.camel.CamelContext;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

@SuppressWarnings("PMD.DataClass")
public class BasicRouteBuilder extends RouteBuilder {

    @Value("${api.basicroutebuilder.doTracing:false}")
    private boolean doTracing = false;
    @Value("${api.basicroutebuilder.doProcesstimeLogging:false}")
    private boolean doProcesstimeLogging = false;
    @Value("${api.basicroutebuilder.doInterceptFrom:false}")
    private boolean doInterceptFrom = false;

    @Override
    @SuppressWarnings("PMD.CloseResource")
    public void configure() throws Exception {
        final CamelContext context = this.getContext();
        context.setUseMDCLogging(Boolean.TRUE);
        if (this.doTracing) {
            context.setTracing(Boolean.TRUE);
        }

        if (this.doProcesstimeLogging) {
            final ExtendedCamelContext ecc = context.getCamelContextExtension();
            ecc.addInterceptStrategy(new PerformanceMonitor());
        }

        if (this.doInterceptFrom) {
            this.interceptFrom().log(LoggingLevel.TRACE, "route.interceptedFrom", "Intercepted from; headers > ${headers}; body > ${body}");
        }

    }

    public String buildRouteID(final String endpointURL) {
        final int optionsStart = endpointURL.indexOf(63);
        if (optionsStart == -1) {
            final String var3 = endpointURL.substring(endpointURL.indexOf(58) + 1);
            return "route." + var3;
        } else {
            final String var10000 = endpointURL.substring(endpointURL.indexOf(58) + 1, optionsStart);
            return "route." + var10000;
        }
    }

    public boolean isDoTracing() {
        return this.doTracing;
    }

    public void setDoTracing(final boolean doTracing) {
        this.doTracing = doTracing;
    }

    public boolean isDoProcesstimeLogging() {
        return this.doProcesstimeLogging;
    }

    public void setDoProcesstimeLogging(final boolean doProcesstimeLogging) {
        this.doProcesstimeLogging = doProcesstimeLogging;
    }

    public boolean isDoInterceptFrom() {
        return this.doInterceptFrom;
    }

    public void setDoInterceptFrom(final boolean doInterceptFrom) {
        this.doInterceptFrom = doInterceptFrom;
    }
}
