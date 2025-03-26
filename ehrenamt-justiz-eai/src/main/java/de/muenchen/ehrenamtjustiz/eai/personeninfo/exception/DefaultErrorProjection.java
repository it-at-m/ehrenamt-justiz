package de.muenchen.ehrenamtjustiz.eai.personeninfo.exception;

import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mapping the default attributes in the event of a Spring error
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressFBWarnings("EI_EXPOSE_REP")
public class DefaultErrorProjection {
    private Date timestamp;

    private int status;

    private String error;

    private String message;

    private String path;
}
