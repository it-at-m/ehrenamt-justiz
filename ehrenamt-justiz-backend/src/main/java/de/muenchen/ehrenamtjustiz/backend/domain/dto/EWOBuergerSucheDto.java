package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/// This class represents a BuergerSucheAnfrage_.
///
/// Only oid and reference will be stored in the database. The entity's content will be loaded
/// according to the reference variable.
///
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@EqualsAndHashCode()
@NoArgsConstructor
public class EWOBuergerSucheDto {

    private String familienname;

    private String vorname;

    private LocalDate geburtsdatum;

}
