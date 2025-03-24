package de.muenchen.ehrenamtjustiz.eai.personeninfo.exception;

import java.io.Serial;

/**
 * Exception, if no person found
 *
 */
public class PersonNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String om;

    private final String message;

    public PersonNotFoundException(final String om, final String message) {
        super();
        this.om = om;
        this.message = message;
    }

    public String getom() {
        return om;
    }

    @Override
    public String getMessage() {
        if (om == null || om.isEmpty()) {
            return "keine Person zu Identifier " + om + " gefunden";
        } else {
            return message + " für OM: " + om;
        }
    }

}
