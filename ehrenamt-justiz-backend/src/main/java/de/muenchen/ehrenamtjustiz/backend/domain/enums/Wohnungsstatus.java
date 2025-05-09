package de.muenchen.ehrenamtjustiz.backend.domain.enums;

import java.util.Locale;

public enum Wohnungsstatus {
    HAUPTWOHNUNG,
    NEBENWOHNUNG;

    public String toReadableString() {
        return this.name().substring(0, 1).concat(this.name().substring(1).toLowerCase(Locale.getDefault()));
    }
}
