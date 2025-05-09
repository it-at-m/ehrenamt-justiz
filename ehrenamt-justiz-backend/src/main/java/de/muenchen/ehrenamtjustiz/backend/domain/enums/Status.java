package de.muenchen.ehrenamtjustiz.backend.domain.enums;

import java.util.Locale;

public enum Status {
    INERFASSUNG,
    BEWERBUNG,
    VORSCHLAG,
    KONFLIKT;

    public String toReadableString() {
        if (this == INERFASSUNG) {
            return "In Erfassung";
        }
        return this.name().substring(0, 1).concat(this.name().substring(1).toLowerCase(Locale.GERMAN));
    }
}
