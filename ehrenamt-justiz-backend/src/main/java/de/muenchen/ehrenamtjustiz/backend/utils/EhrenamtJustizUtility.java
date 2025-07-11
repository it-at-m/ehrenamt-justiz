package de.muenchen.ehrenamtjustiz.backend.utils;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class EhrenamtJustizUtility {

    // Don't modify this text:
    public static final String ERROR_NO_HITS = "Keine EWO-Daten gefunden. Evtl. verstorben oder verzogen?";

    public static final String STATUS_UP = "UP";

    public static final String STATUS_DOWN = "DOWN";

    private EhrenamtJustizUtility() {

    }

    public static String validateGueltigesGeburtsdatum(final Konfiguration konfiguration, final LocalDate geburtsdatum) {

        if (!isGueltigesAlter(konfiguration, geburtsdatum)) {
            final StringBuilder meldung = new StringBuilder(
                    "Das Alter muss zwischen " + konfiguration.getAltervon() + " und " + konfiguration.getAlterbis() + " Jahren liegen.");
            log.warn(meldung.toString());
            return meldung.toString();
        }
        return "";
    }

    public static String validateGueltigeStaatsangehoerigkeit(final Konfiguration konfiguration, final List<String> staatsangehoerigkeit) {

        if (!staatsangehoerigkeit.contains(konfiguration.getStaatsangehoerigkeit())) {
            final StringBuilder meldung = new StringBuilder("Die Staatsangehörigkeit enhält nicht  " + konfiguration.getStaatsangehoerigkeit());
            log.warn(meldung.toString());
            return meldung.toString();
        }
        return "";
    }

    public static String validateGueltigerWohnsitz(final Konfiguration konfiguration, final String ort) {

        if (!ort.equals(konfiguration.getWohnsitz())) {
            final StringBuilder meldung = new StringBuilder("Kein Wohnsitz ist in " + konfiguration.getWohnsitz());
            log.warn(meldung.toString());
            return meldung.toString();
        }
        return "";
    }

    private static boolean isGueltigesAlter(final Konfiguration konfiguration, final LocalDate geburtsdatum) {
        final long age = geburtsdatum.until(konfiguration.getAmtsperiodevon(), ChronoUnit.YEARS);
        return age >= konfiguration.getAltervon().intValue() && age <= konfiguration.getAlterbis().intValue();
    }

    public static Person getPersonAusEWOBuergerDaten(final EWOBuergerDatenDto eWOBuergerDaten) {
        // set data of person by data of EWO
        final Person person = new Person();
        person.setId(eWOBuergerDaten.getId());
        person.setFamilienname(eWOBuergerDaten.getFamilienname());
        person.setGeburtsdatum(eWOBuergerDaten.getGeburtsdatum());
        person.setGeburtsname(eWOBuergerDaten.getGeburtsname());
        person.setVorname(eWOBuergerDaten.getVorname());
        person.setGeburtsdatum(eWOBuergerDaten.getGeburtsdatum());
        person.setGeschlecht(eWOBuergerDaten.getGeschlecht());
        person.setEwoid(eWOBuergerDaten.getOrdnungsmerkmal());
        person.setAkademischergrad(eWOBuergerDaten.getAkademischergrad());
        person.setGeburtsort(eWOBuergerDaten.getGeburtsort());
        person.setGeburtsland(eWOBuergerDaten.getGeburtsland());
        person.setFamilienstand(eWOBuergerDaten.getFamilienstand());
        person.setStaatsangehoerigkeit(eWOBuergerDaten.getStaatsangehoerigkeit());
        person.setWohnungsgeber(eWOBuergerDaten.getWohnungsgeber());
        person.setStrasse(eWOBuergerDaten.getStrasse());
        person.setHausnummer(eWOBuergerDaten.getHausnummer());
        person.setAppartmentnummer(eWOBuergerDaten.getAppartmentnummer());
        person.setBuchstabehausnummer(eWOBuergerDaten.getBuchstabehausnummer());
        person.setStockwerk(eWOBuergerDaten.getStockwerk());
        person.setTeilnummerhausnummer(eWOBuergerDaten.getTeilnummerhausnummer());
        person.setAdresszusatz(eWOBuergerDaten.getAdresszusatz());
        person.setKonfliktfeld(eWOBuergerDaten.getKonfliktfeld());
        person.setPostleitzahl(eWOBuergerDaten.getPostleitzahl());
        person.setOrt(eWOBuergerDaten.getOrt());
        person.setInmuenchenseit(eWOBuergerDaten.getInmuenchenseit());
        person.setWohnungsstatus(eWOBuergerDaten.getWohnungsstatus());
        person.setAuskunftssperre(eWOBuergerDaten.getAuskunftssperre());
        person.setStatus(Status.INERFASSUNG);
        person.setBewerbungvom(LocalDate.now());

        return person;
    }

    public static EWOBuergerDatenDto getEwoBuergerDatenDto(final Person person) {
        return EWOBuergerDatenDto.builder()
                .familienname(person.getFamilienname())
                .geburtsname(person.getGeburtsname())
                .vorname(person.getVorname())
                .geburtsdatum(person.getGeburtsdatum())
                .geschlecht(person.getGeschlecht())
                .ordnungsmerkmal(person.getEwoid())
                .akademischergrad(person.getAkademischergrad())
                .geburtsort(person.getGeburtsort())
                .geburtsland(person.getGeburtsland())
                .familienstand(person.getFamilienstand())
                .staatsangehoerigkeit(person.getStaatsangehoerigkeit())
                .wohnungsgeber(person.getWohnungsgeber())
                .strasse(person.getStrasse())
                .hausnummer(person.getHausnummer())
                .appartmentnummer(person.getAppartmentnummer())
                .buchstabehausnummer(person.getBuchstabehausnummer())
                .stockwerk(person.getStockwerk())
                .teilnummerhausnummer(person.getTeilnummerhausnummer())
                .adresszusatz(person.getAdresszusatz())
                .konfliktfeld(person.getKonfliktfeld())
                .postleitzahl(person.getPostleitzahl())
                .ort(person.getOrt())
                .inmuenchenseit(person.getInmuenchenseit())
                .wohnungsstatus(person.getWohnungsstatus())
                .auskunftssperre(person.getAuskunftssperre()).build();
    }

    /**
     * Sanitizes user input by removing potentially dangerous characters.
     *
     * @param input The user-provided input string.
     * @return A sanitized version of the input string.
     */
    public static String sanitizeInput(final String input) {
        if (input == null) {
            return null;
        }
        // Remove newlines and carriage returns
        return input.replaceAll("[\\p{Cntrl}\n\r]", "");
    }

    /**
     * Truncates a string if it exceeds the specified maximum length.
     *
     * @param input The string to truncate.
     * @param maxLength The maximum allowed length.
     * @return The original string if it's null or shorter than maxLength,
     *         otherwise the truncated string with an ellipsis appended.
     */
    public static String truncateIfNeeded(final String input, final int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength darf nicht negativ sein!");
        }
        if (input != null && input.length() > maxLength) {
            return input.substring(0, maxLength) + "...";
        }
        return input;
    }

}
