package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode
@NoArgsConstructor
@SuperBuilder
@SuppressWarnings("PMD.TooManyFields")
public class PersonCSVDto {

    private String id;

    private String ewoid;

    private String konfigurationid;

    private String familienname;

    private String geburtsname;

    private String vorname;

    private String geburtsdatum;

    private String geschlecht;

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

    private String inmuenchenseit;

    private String wohnungsgeber;

    private String wohnungsstatus;

    private String derzeitausgeuebterberuf;

    private String arbeitgeber;

    private String telefonnummer;

    private String telefongesch;

    private String telefonmobil;

    private String mailadresse;

    private String ausgeuebteehrenaemter;

    private String onlinebewerbung;

    private String neuervorschlag;

    private String warbereitstaetigals;

    private String warbereitstaetigalsvorvorperiode;

    private String bewerbungvom;

    private String status;

    private String bemerkung;

    private String auskunftssperre;

    private String konfliktfeld;

    private String staatsangehoerigkeit;
}
