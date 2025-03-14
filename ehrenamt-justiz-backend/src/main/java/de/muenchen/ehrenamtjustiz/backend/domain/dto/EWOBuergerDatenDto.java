package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.util.UUID;

import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

    @SuppressFBWarnings({ "EI_EXPOSE_REP" })
    private java.util.List<String> staatsangehoerigkeit = new java.util.ArrayList<>();

    private String wohnungsgeber;

    private String strasse;

    private String hausnummer;

    private String appartmentnummer;

    private String buchstabehausnummer;

    private String stockwerk;

    private String teilnummerhausnummer;

    private String adresszusatz;

    @SuppressFBWarnings({ "EI_EXPOSE_REP" })
    private java.util.List<String> konfliktfeld = new java.util.ArrayList<>();

    private String postleitzahl;

    private String ort;

    private java.time.LocalDate inmuenchenseit;

    private Wohnungsstatus wohnungsstatus;

    @SuppressFBWarnings({ "EI_EXPOSE_REP" })
    private java.util.List<String> auskunftssperre = new java.util.ArrayList<>();

    private boolean ewoidbereitserfasst;

}
