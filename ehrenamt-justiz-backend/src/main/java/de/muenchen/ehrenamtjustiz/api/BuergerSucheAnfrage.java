package de.muenchen.ehrenamtjustiz.api;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * This class represents a BuergerSucheAnfrage
 * <p>
 * Only oid and reference will be stored in the database.
 * The entity's content will be loaded according to the reference variable.
 * </p>
 */
@Setter
@Getter
public class BuergerSucheAnfrage {

    private String familienname;

    private String vorname;

    private LocalDate geburtsdatum;

}
