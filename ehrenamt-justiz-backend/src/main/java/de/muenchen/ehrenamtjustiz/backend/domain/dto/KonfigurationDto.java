package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import java.math.BigInteger;
import java.util.UUID;
import lombok.Data;

@Data
public class KonfigurationDto {

    private UUID id;

    private Ehrenamtjustizart ehrenamtjustizart;

    private String bezeichnung;

    private boolean aktiv;

    private java.time.LocalDate amtsperiodevon;

    private java.time.LocalDate amtsperiodebis;

    private BigInteger altervon;

    private BigInteger alterbis;

    private String staatsangehoerigkeit;

    private String wohnsitz;
}
