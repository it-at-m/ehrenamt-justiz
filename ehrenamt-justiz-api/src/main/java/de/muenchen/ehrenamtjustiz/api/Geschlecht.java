package de.muenchen.ehrenamtjustiz.api;

public enum Geschlecht {

    MAENNLICH("maennlich"),
    WEIBLICH("weiblich"),
    DIVERS("divers"),
    UNBEKANNT("unbekannt");

    public final String label;

    Geschlecht(final String label) {
        this.label = label;
    }
}
