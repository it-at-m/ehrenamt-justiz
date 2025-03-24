package de.muenchen.ehrenamtjustiz.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * This class represents a BuergerSucheAnfrage
 * <p>
 * Only oid and reference will be stored in the database.
 * The entity's content will be loaded according to the reference variable.
 * </p>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "", propOrder = {
                "familienname",
                "vorname",
                "geburtsdatum"
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuergerSucheAnfrage {

    @Schema(description = "Familienname der gesuchten Person")
    private String familienname;

    @Schema(description = "Vorname der gesuchten Person")
    private String vorname;

    @XmlSchemaType(name = "geburtsdatum")
    @Schema(description = "Geburtsdatum der gesuchten Person")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = Konstanten.DATE_FORMAT)
    private LocalDate geburtsdatum;

}
