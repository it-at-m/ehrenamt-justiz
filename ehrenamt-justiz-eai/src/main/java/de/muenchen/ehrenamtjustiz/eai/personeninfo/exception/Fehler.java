package de.muenchen.ehrenamtjustiz.eai.personeninfo.exception;

public class Fehler {

    /**
     * ID of the error that was triggered.
     *
     * @see InterneFehlerCodes for defined error codes
     */
    private String id;
    /**
     * Additional text to the error code. Is free text that is defined by the creator of the error.
     */
    private String message;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Fehler other = (Fehler) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (message == null) {
            return other.message == null;
        } else {
            return message.equals(other.message);
        }
    }

}
