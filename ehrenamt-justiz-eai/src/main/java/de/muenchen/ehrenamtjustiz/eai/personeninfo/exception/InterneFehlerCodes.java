package de.muenchen.ehrenamtjustiz.eai.personeninfo.exception;

/**
 * Error codes for errors that can occur during the processing of a request.
 *
 */
@SuppressWarnings("PMD.DataClass")
public final class InterneFehlerCodes {
    /**
     * Die Antwort des angeschlossenen Systems erfolgte nicht erhalb der erwarteten Zeit
     */
    public static final String TIMEOUT = "001";

    /**
     * Es konnte keine Verbindung zu dem angeschlossenen System aufgebaut werden.
     */
    public static final String KEINE_VERBINDUNG = "002";

    /**
     * Es kam zu einen unbekannten Fehler während der Verarbeitung
     */
    public static final String UNBEKANNTER_FEHLER = "999";
}
