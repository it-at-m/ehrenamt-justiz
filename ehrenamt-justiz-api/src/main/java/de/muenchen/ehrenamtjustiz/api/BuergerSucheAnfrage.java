package de.muenchen.ehrenamtjustiz.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import de.muenchen.ehrenamtjustiz.konstanten.Konstanten;
import java.time.LocalDate;
import lombok.*;

/**
 * This class represents a BuergerSucheAnfrage
 * <p>
 * Only oid and reference will be stored in the database.
 * The entity's content will be loaded according to the reference variable.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuergerSucheAnfrage {

    private String familienname;

    private String vorname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = Konstanten.DATE_FORMAT)
    private LocalDate geburtsdatum;

}
