package de.muenchen.ehrenamtjustiz.api;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@SuppressWarnings("PMD.TooManyFields")
public class EWOBuerger {

    private String familienname;

    private String familiennameZusatz;

    private String geburtsname;

    private String geburtsnameZusatz;

    private String vorname;

    private java.time.LocalDate geburtsdatum;

    private Geschlecht geschlecht;

    private String ordnungsmerkmal;

    private String akademischerGrad;

    private String geburtsort;

    private String geburtsland;

    private String familienstand;

    @SuppressFBWarnings({ "EI_EXPOSE_REP" })
    private java.util.List<String> staatsangehoerigkeit = new java.util.ArrayList<>();

    private String wohnungsgeber;

    private String strasse;

    private String hausnummer;

    private String appartmentnummer;

    private String buchstabeHausnummer;

    private String stockwerk;

    private String teilnummerHausnummer;

    private String zusatz;

    @SuppressFBWarnings({ "EI_EXPOSE_REP" })
    private final java.util.List<String> konfliktFelder = new java.util.ArrayList<>();

    private String postleitzahl;

    private String ort;

    private java.time.LocalDate inMuenchenSeit;

    private Wohnungsstatus wohnungsstatus;

    @SuppressFBWarnings({ "EI_EXPOSE_REP" })
    private java.util.List<String> auskunftssperren = new java.util.ArrayList<>();

}
