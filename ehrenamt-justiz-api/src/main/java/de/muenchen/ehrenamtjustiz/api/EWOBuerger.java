package de.muenchen.ehrenamtjustiz.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import de.muenchen.ehrenamtjustiz.konstanten.Konstanten;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
public class EWOBuerger {

    private String familienname;

    private String familiennameZusatz;

    private String geburtsname;

    private String geburtsnameZusatz;

    private String vorname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = Konstanten.DATE_FORMAT)
    private LocalDate geburtsdatum;

    private Geschlecht geschlecht;

    private String ordnungsmerkmal;

    private String akademischerGrad;

    private String geburtsort;

    private String geburtsland;

    private String familienstand;

    private List<String> staatsangehoerigkeit = new ArrayList<>();

    private String wohnungsgeber;

    private String strasse;

    private String hausnummer;

    private String appartmentnummer;

    private String buchstabeHausnummer;

    private String stockwerk;

    private String teilnummerHausnummer;

    private String zusatz;

    private final List<String> konfliktFelder = new ArrayList<>();

    private String postleitzahl;

    private String ort;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = Konstanten.DATE_FORMAT)
    private LocalDate inMuenchenSeit;

    private Wohnungsstatus wohnungsstatus;

    private List<String> auskunftssperren = new ArrayList<>();

    // because of EI_EXPOSE_REP
    public List<String> getAuskunftssperren() {
        return new ArrayList<>(auskunftssperren);
    }

    // because of EI_EXPOSE_REP
    public List<String> getKonfliktFelder() {
        return new ArrayList<>(konfliktFelder);
    }

    // because of EI_EXPOSE_REP
    public List<String> getStaatsangehoerigkeit() {
        return new ArrayList<>(staatsangehoerigkeit);
    }

}
