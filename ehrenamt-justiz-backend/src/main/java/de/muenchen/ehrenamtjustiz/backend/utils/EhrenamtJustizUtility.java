package de.muenchen.ehrenamtjustiz.backend.utils;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
public final class EhrenamtJustizUtility {

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
        // EWO-Daten in Personen-Daten übertragen
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

}
