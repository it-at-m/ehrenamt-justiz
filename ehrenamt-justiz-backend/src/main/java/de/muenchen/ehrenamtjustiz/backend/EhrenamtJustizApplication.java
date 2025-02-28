/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.ehrenamtjustiz.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Application class for starting the micro-service.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@SuppressWarnings("PMD.UseUtilityClass")
public class EhrenamtJustizApplication {

    public static void main(final String[] args) {
        SpringApplication.run(EhrenamtJustizApplication.class, args);
    }

}
