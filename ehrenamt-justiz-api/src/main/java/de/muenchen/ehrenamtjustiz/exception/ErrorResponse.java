package de.muenchen.ehrenamtjustiz.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ErrorResponse {
    private final String message;
    private final String om;
    private final boolean blockingEntry;
}
