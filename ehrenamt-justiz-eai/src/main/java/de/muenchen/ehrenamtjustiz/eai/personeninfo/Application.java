package de.muenchen.ehrenamtjustiz.eai.personeninfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * SpringBoot-main app
 *
 */
@SpringBootApplication()
@ConfigurationPropertiesScan
@SuppressWarnings("PMD.UseUtilityClass")
public class Application {
    /**
     * main method to start the EAI
     *
     * @param args Applikationsparameter
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
