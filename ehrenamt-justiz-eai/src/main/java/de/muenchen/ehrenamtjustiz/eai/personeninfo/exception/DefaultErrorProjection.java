package de.muenchen.ehrenamtjustiz.eai.personeninfo.exception;

import java.time.LocalDate;
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
public class DefaultErrorProjection {
    private LocalDate timestamp;

    private int status;

    private String error;

    private String message;

    private String path;
}
