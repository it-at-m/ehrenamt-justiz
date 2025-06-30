package de.muenchen.ehrenamtjustiz.eai.personeninfo.config;

/**
 * collection of constants that are relevant for the connection to the EAI.
 *
 */
public final class Konstanten {
    /**
     * Subpath to the documentation of the API
     */
    public static final String API_DOC_SUB_PATH = "/api-doc";
    /**
     * Subpath for retrieving personal information
     */
    public static final String PERSONENINFO_SUB_PATH_EWO_SUCHE_MIT_OM = "/eairoutes/ewosuchemitom";

    public static final String PERSONENINFO_SUB_PATH_EWO_SUCHE = "/eairoutes/ewosuche";

    private Konstanten() {
        throw new IllegalStateException("Eine Instance von Konstanten soll nicht erzeugt werden");
    }

}
