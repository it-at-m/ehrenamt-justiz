package de.muenchen.ehrenamtjustiz.backend.exception;

import de.muenchen.ehrenamtjustiz.exception.AenderungsServiceException;
import de.muenchen.ehrenamtjustiz.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAendungsService(final AenderungsServiceException e) {
        final ErrorResponse error = new ErrorResponse(
                e.getMessage(),
                e.getOm(),
                e.isBlockingEntry());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
