package de.muenchen.ehrenamtjustiz.backend.domain.enums;

import java.util.Locale;

public enum Ehrenamtjustizart {
    SCHOEFFEN,
    VERWALTUNGSRICHTER;

    public String toReadableString() {
        if (this == SCHOEFFEN) {
            return "Schöffen";
        }
        return this.name().substring(0, 1).concat(this.name().substring(1).toLowerCase(Locale.getDefault()));
    }
}
