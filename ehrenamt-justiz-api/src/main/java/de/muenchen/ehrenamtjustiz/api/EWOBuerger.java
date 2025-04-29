package de.muenchen.ehrenamtjustiz.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import de.muenchen.ehrenamtjustiz.konstanten.Konstanten;
import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "EWOBuerger")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuppressWarnings("PMD.TooManyFields")
public class EWOBuerger {

    private String familienname;

    private String familiennameZusatz;

    private String geburtsname;

    private String geburtsnameZusatz;

    private String vorname;

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

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = Konstanten.DATE_FORMAT)
    private java.time.LocalDate inMuenchenSeit;

    private Wohnungsstatus wohnungsstatus;

    private java.util.List<String> auskunftssperren = new java.util.ArrayList<>();

    // because of EI_EXPOSE_REP
    public java.util.List<String> getAuskunftssperren() {
        return new java.util.ArrayList<>(auskunftssperren);
    }

    // because of EI_EXPOSE_REP
    public java.util.List<String> getKonfliktfelder() {
        return new java.util.ArrayList<>(konfliktFelder);
    }

    // because of EI_EXPOSE_REP
    public java.util.List<String> getStaatsangehoerigkeit() {
        return new java.util.ArrayList<>(staatsangehoerigkeit);
    }

}
