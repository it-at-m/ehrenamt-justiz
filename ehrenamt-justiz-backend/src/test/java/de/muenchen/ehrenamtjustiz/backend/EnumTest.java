package de.muenchen.ehrenamtjustiz.backend;

import static org.junit.jupiter.api.Assertions.*;

import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import org.junit.jupiter.api.Test;

class EnumTest {

    public static final String ERROR_MSG_PREFIX = "Readable string for ";
    public static final String ERROR_MSG_SUFFIX = " should not be null";

    @Test
    void testEhrenamtjustizart() {

        // Test all Ehrenamtjustizart values
        for (final Ehrenamtjustizart ehrenamtjustizart : Ehrenamtjustizart.values()) {
            assertNotNull(ehrenamtjustizart.toReadableString(), ERROR_MSG_PREFIX + ehrenamtjustizart + ERROR_MSG_SUFFIX);
        }

        assertEquals("Schöffen", Ehrenamtjustizart.SCHOEFFEN.toReadableString());
        assertEquals("Verwaltungsrichter", Ehrenamtjustizart.VERWALTUNGSRICHTER.toReadableString());
    }

    @Test
    void testGeschlecht() {

        // Test all Geschlecht values
        for (final Geschlecht geschlecht : Geschlecht.values()) {
            assertNotNull(geschlecht.toReadableString(), ERROR_MSG_PREFIX + geschlecht + ERROR_MSG_SUFFIX);
        }

        assertEquals("Männlich", Geschlecht.MAENNLICH.toReadableString());
        assertEquals("Weiblich", Geschlecht.WEIBLICH.toReadableString());
        assertEquals("Divers", Geschlecht.DIVERS.toReadableString());
        assertEquals("Unbekannt", Geschlecht.UNBEKANNT.toReadableString());
    }

    @Test
    void testStatus() {

        // Test all Status values
        for (final Status status : Status.values()) {
            assertNotNull(status.toReadableString(), ERROR_MSG_PREFIX + status + ERROR_MSG_SUFFIX);
        }

        assertEquals("In Erfassung", Status.INERFASSUNG.toReadableString());
        assertEquals("Bewerbung", Status.BEWERBUNG.toReadableString());
        assertEquals("Konflikt", Status.KONFLIKT.toReadableString());
        assertEquals("Vorschlag", Status.VORSCHLAG.toReadableString());
    }

    @Test
    void testWohnungsstatus() {

        // Test all Wohnungsstatus values
        for (final Wohnungsstatus status : Wohnungsstatus.values()) {
            assertNotNull(status.toReadableString(), ERROR_MSG_PREFIX + status + ERROR_MSG_SUFFIX);
        }

        assertEquals("Hauptwohnung", Wohnungsstatus.HAUPTWOHNUNG.toReadableString());
        assertEquals("Nebenwohnung", Wohnungsstatus.NEBENWOHNUNG.toReadableString());
    }

}
