package de.muenchen.ehrenamtjustiz.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@SuppressWarnings("PMD.UseUtilityClass")
public class EhrenamtJustizApplication {

    /* package */ static void main(final String... args) {
        SpringApplication.run(EhrenamtJustizApplication.class, args);
    }

}
