package de.muenchen.ehrenamtjustiz.api;

public enum Wohnungsstatus {

    HAUPTWOHNUNG("hauptwohnung"),
    NEBENWOHNUNG("nebenwohnuung");

    public final String label;

    Wohnungsstatus(final String label) {
        this.label = label;
    }
}
