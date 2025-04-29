package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.Data;

@Data
@SuppressWarnings("PMD.TooManyFields")
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
public class PersonDto {

    private UUID id;

    private String ewoid;

    private UUID konfigurationid;

    private String familienname;

    private String geburtsname;

    private String vorname;

    private java.time.LocalDate geburtsdatum;

    private Geschlecht geschlecht;

    private String akademischergrad;

    private String geburtsort;

    private String geburtsland;

    private String familienstand;

    private String strasse;

    private String hausnummer;

    private String appartmentnummer;

    private String buchstabehausnummer;

    private String stockwerk;

    private String teilnummerhausnummer;

    private String adresszusatz;

    private String postleitzahl;

    private String ort;

    private java.time.LocalDate inmuenchenseit;

    private String wohnungsgeber;

    private Wohnungsstatus wohnungsstatus;

    private String derzeitausgeuebterberuf;

    private String arbeitgeber;

    private String telefonnummer;

    private String telefongesch;

    private String telefonmobil;

    private String mailadresse;

    private String ausgeuebteehrenaemter;

    private boolean onlinebewerbung = false;

    private boolean neuervorschlag = false;

    private boolean warbereitstaetigals = false;

    private boolean warbereitstaetigalsvorvorperiode = false;

    private java.time.LocalDate bewerbungvom;

    private Status status;

    private String bemerkung;

    private java.util.List<String> auskunftssperre = new java.util.ArrayList<>();

    private java.util.List<String> konfliktfeld = new java.util.ArrayList<>();

    private java.util.List<String> staatsangehoerigkeit = new java.util.ArrayList<>();

    // because of EI_EXPOSE_REP
    public java.util.List<String> getAuskunftssperre() {
        return new java.util.ArrayList<>(auskunftssperre);
    }

    // because of EI_EXPOSE_REP
    public java.util.List<String> getKonfliktfeld() {
        return new java.util.ArrayList<>(konfliktfeld);
    }

    // because of EI_EXPOSE_REP
    public java.util.List<String> getStaatsangehoerigkeit() {
        return new java.util.ArrayList<>(staatsangehoerigkeit);
    }
}
