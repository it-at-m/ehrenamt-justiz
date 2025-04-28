package de.muenchen.ehrenamtjustiz.aenderungsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Application class for starting the micro-service.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@SuppressWarnings("PMD.UseUtilityClass")
public class AenderungsserviceApplication {
    public static void main(final String[] args) {
        SpringApplication.run(AenderungsserviceApplication.class, args);
    }
}
