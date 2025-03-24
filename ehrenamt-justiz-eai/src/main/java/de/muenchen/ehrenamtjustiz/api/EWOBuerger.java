package de.muenchen.ehrenamtjustiz.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "", propOrder = {
                "familienname",
                "familiennameZusatz",
                "geburtsname",
                "geburtsnameZusatz",
                "vorname",
                "geburtsdatum",
                "geschlecht",
                "ordnungsmerkmal",
                "akademischerGrad",
                "geburtsort",
                "geburtsland",
                "familienstand",
                "staatsangehoerigkeit",
                "wohnungsgeber",
                "strasse",
                "hausnummer",
                "appartmentnummer",
                "buchstabeHausnummer",
                "stockwerk",
                "teilnummerHausnummer",
                "zusatz",
                "konfliktFelder",
                "postleitzahl",
                "ort",
                "inMuenchenSeit",
                "wohnungsstatus",
                "auskunftssperren"
        }
)
@XmlRootElement(name = "EWOBuerger")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("PMD.TooManyFields")
public class EWOBuerger {

    @Schema(description = "Familienname der gesuchten Person")
    private String familienname;

    private String familiennameZusatz;

    private String geburtsname;

    private String geburtsnameZusatz;

    @Schema(description = "Vorname der gesuchten Person")
    private String vorname;

    @XmlSchemaType(name = "geburtsdatum")
    @Schema(description = "Geburtsdatum der gesuchten Person")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = Konstanten.DATE_FORMAT)
    private java.time.LocalDate geburtsdatum;

    private Geschlecht geschlecht;

    private String ordnungsmerkmal;

    private String akademischerGrad;

    private String geburtsort;

    private String geburtsland;

    private String familienstand;

    private java.util.List<String> staatsangehoerigkeit = new java.util.ArrayList<>();

    private String wohnungsgeber;

    private String strasse;

    private String hausnummer;

    private String appartmentnummer;

    private String buchstabeHausnummer;

    private String stockwerk;

    private String teilnummerHausnummer;

    private String zusatz;

    private final java.util.List<String> konfliktFelder = new java.util.ArrayList<>();

    private String postleitzahl;

    private String ort;

    @XmlSchemaType(name = "inMuenchenSeit")
    @Schema(description = "Datum in München seit")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = Konstanten.DATE_FORMAT)
    private java.time.LocalDate inMuenchenSeit;

    private Wohnungsstatus wohnungsstatus;

    private java.util.List<String> auskunftssperren = new java.util.ArrayList<>();

}
