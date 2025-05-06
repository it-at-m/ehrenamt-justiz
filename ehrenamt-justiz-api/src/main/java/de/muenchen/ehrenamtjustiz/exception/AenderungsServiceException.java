package de.muenchen.ehrenamtjustiz.exception;

import java.io.Serial;
import lombok.Getter;

@Getter
public class AenderungsServiceException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String om;
    private final boolean blockingEntry;

    public AenderungsServiceException(final String message, final String om, final boolean blockingEntry) {
        super(message);
        this.om = om;
        this.blockingEntry = blockingEntry;
    }
}
