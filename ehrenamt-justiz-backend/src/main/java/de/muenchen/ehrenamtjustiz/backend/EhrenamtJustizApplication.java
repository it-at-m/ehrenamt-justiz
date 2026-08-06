package de.muenchen.ehrenamtjustiz.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Application class to start the micro-service.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@SuppressWarnings("PMD.UseUtilityClass")
public class EhrenamtJustizApplication {

    public static void main(final String[] args) {
        SpringApplication.run(EhrenamtJustizApplication.class, args);
    }

}
