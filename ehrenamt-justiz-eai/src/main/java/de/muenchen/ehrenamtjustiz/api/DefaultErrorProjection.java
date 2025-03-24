package de.muenchen.ehrenamtjustiz.api;

import java.util.Date;

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
    private Date timestamp;

    private int status;

    private String error;

    private String message;

    private String path;
}
