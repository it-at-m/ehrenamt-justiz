package de.muenchen.ehrenamtjustiz.backend;

import static org.junit.jupiter.api.Assertions.*;

import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class EnumTest {

    public static final String READABLE_STRING_FOR = "Readable string for ";
    public static final String SHOULD_NOT_BE_NULL = " should not be null";

    @Test
    void testEhrenamtjustizart() {

        // Test all Ehrenamtjustizart values
        for (final Ehrenamtjustizart ehrenamtjustizart : Ehrenamtjustizart.values()) {
            assertNotNull(ehrenamtjustizart.toReadableString(), READABLE_STRING_FOR + ehrenamtjustizart + SHOULD_NOT_BE_NULL);
        }

        assertEquals("Schöffen", Ehrenamtjustizart.SCHOEFFEN.toReadableString());
        assertEquals("Verwaltungsrichter", Ehrenamtjustizart.VERWALTUNGSRICHTER.toReadableString());
    }

    @Test
    void testGeschlecht() {

        // Test all Geschlecht values
        for (final Geschlecht geschlecht : Geschlecht.values()) {
            assertNotNull(geschlecht.toReadableString(), READABLE_STRING_FOR + geschlecht + SHOULD_NOT_BE_NULL);
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
            assertNotNull(status.toReadableString(), READABLE_STRING_FOR + status + SHOULD_NOT_BE_NULL);
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
            assertNotNull(status.toReadableString(), READABLE_STRING_FOR + status + SHOULD_NOT_BE_NULL);
        }

        assertEquals("Hauptwohnung", Wohnungsstatus.HAUPTWOHNUNG.toReadableString());
        assertEquals("Nebenwohnung", Wohnungsstatus.NEBENWOHNUNG.toReadableString());
    }

}
