package de.muenchen.ehrenamtjustiz.backend.domain.enums;

import java.util.Locale;

public enum Geschlecht {
    MAENNLICH,
    WEIBLICH,
    DIVERS,
    UNBEKANNT;

    public String toReadableString() {
        if (this == MAENNLICH) {
            return "Männlich";
        }
        return this.name().substring(0, 1).concat(this.name().substring(1).toLowerCase(Locale.GERMAN));
    }
}
