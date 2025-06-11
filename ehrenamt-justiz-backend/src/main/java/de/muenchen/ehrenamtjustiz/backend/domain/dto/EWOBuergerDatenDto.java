package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import java.util.Collections;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@SuppressWarnings("PMD.TooManyFields")
public class EWOBuergerDatenDto {

    private UUID id;

    private String familienname;

    private String geburtsname;

    private String vorname;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private java.time.LocalDate geburtsdatum;

    private Geschlecht geschlecht;

    private String ordnungsmerkmal;

    private String akademischergrad;

    private String geburtsort;

    private String geburtsland;

    private String familienstand;

    private java.util.List<String> staatsangehoerigkeit = new java.util.ArrayList<>();

    private String wohnungsgeber;

    private String strasse;

    private String hausnummer;

    private String appartmentnummer;

    private String buchstabehausnummer;

    private String stockwerk;

    private String teilnummerhausnummer;

    private String adresszusatz;

    private java.util.List<String> konfliktfeld = new java.util.ArrayList<>();

    private String postleitzahl;

    private String ort;

    private java.time.LocalDate inmuenchenseit;

    private Wohnungsstatus wohnungsstatus;

    private java.util.List<String> auskunftssperre = new java.util.ArrayList<>();

    private boolean ewoidbereitserfasst;

    // because of EI_EXPOSE_REP
    public java.util.List<String> getAuskunftssperre() {
        if (auskunftssperre == null) {
            return Collections.emptyList();
        }
        return new java.util.ArrayList<>(auskunftssperre);
    }

    // because of EI_EXPOSE_REP
    public java.util.List<String> getKonfliktfeld() {
        if (konfliktfeld == null) {
            return Collections.emptyList();
        }
        return new java.util.ArrayList<>(konfliktfeld);
    }

    // because of EI_EXPOSE_REP
    public java.util.List<String> getStaatsangehoerigkeit() {
        if (staatsangehoerigkeit == null) {
            return Collections.emptyList();
        }
        return new java.util.ArrayList<>(staatsangehoerigkeit);
    }

}
