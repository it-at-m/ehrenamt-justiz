package de.muenchen.ehrenamtjustiz.eai.personeninfo.bean;

import de.muenchen.ehrenamtjustiz.api.BuergerSucheAnfrage;
import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.api.Geschlecht;
import de.muenchen.ehrenamtjustiz.api.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.exception.PersonNotFoundException;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.util.CountryCode;
import de.muenchen.ewo.generated.AbstractWohnungType;
import de.muenchen.ewo.generated.AktuelleWohnung;
import de.muenchen.ewo.generated.AnschriftErweitertType;
import de.muenchen.ewo.generated.BenutzerType;
import de.muenchen.ewo.generated.GeburtsdatenType;
import de.muenchen.ewo.generated.GeburtsdatumType;
import de.muenchen.ewo.generated.NamenType;
import de.muenchen.ewo.generated.PersonErweitert;
import de.muenchen.ewo.generated.PersonenSuchkriterienErweitertType;
import de.muenchen.ewo.generated.StaatsangehoerigkeitErweitertType;
import de.muenchen.ewo.generated.SuchePersonerweitertAnfrage;
import de.muenchen.ewo.generated.SuchePersonerweitertAntwort;
import de.muenchen.ewo.generated.VornameType;
import de.muenchen.ewo.generated.ZuzugdatenType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/// Central mapping class for converting from one data type to another
@Component
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity" })
@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
public class MappingBean {

    @Value("${ewo.eai.benutzerid:ehrju}")
    private String benutzerid;

    public EWOBuerger fromEWOLesen(final PersonErweitert personErweitert) {
        final EWOBuerger eWOBuerger = new EWOBuerger();

        if (personErweitert != null) {
            return toEWOBuerger(personErweitert);
        }

        return eWOBuerger;
    }

    public SuchePersonerweitertAnfrage toEWOSuche(final BuergerSucheAnfrage buergerSucheAnfrage) {

        final PersonenSuchkriterienErweitertType suchkriterien = new PersonenSuchkriterienErweitertType();
        suchkriterien.setFamilienname(buergerSucheAnfrage.getFamilienname());
        suchkriterien.setVorname(buergerSucheAnfrage.getVorname());
        final GeburtsdatumType geburtsdatum = new GeburtsdatumType();
        geburtsdatum.setDatum(buergerSucheAnfrage.getGeburtsdatum().format(DateTimeFormatter.ISO_DATE));
        suchkriterien.setGeburtsdatum(geburtsdatum);
        suchkriterien.setDatensatzstatus(PersonenSuchkriterienErweitertType.DatensatzstatusEnum.AKTUELL);

        final SuchePersonerweitertAnfrage suchAnfrage = new SuchePersonerweitertAnfrage();
        final BenutzerType benutzerType = new BenutzerType();
        benutzerType.setBenutzerId(benutzerid);
        suchAnfrage.setBenutzer(benutzerType);
        suchAnfrage.setSuchkriterien(suchkriterien);

        return suchAnfrage;
    }

    public List<EWOBuerger> fromEWOSuche(final SuchePersonerweitertAntwort suchePersonenErweitertAntwort) {
        final List<EWOBuerger> ewoBuergers = new ArrayList<>();

        if (suchePersonenErweitertAntwort == null || suchePersonenErweitertAntwort.getPersonen() == null
                || suchePersonenErweitertAntwort.getPersonen().isEmpty()) {
            throw new PersonNotFoundException(null, "Kein Ergebnis für Suchanfrage");
        } else {
            for (final PersonErweitert personErweitert : suchePersonenErweitertAntwort.getPersonen()) {
                ewoBuergers.add(toEWOBuerger(personErweitert));
            }
        }

        return ewoBuergers;
    }

    @SuppressWarnings({ "PMD.CognitiveComplexity", "CyclomaticComplexity", "NPathComplexity" })
    private static EWOBuerger toEWOBuerger(final PersonErweitert person) {
        final EWOBuerger response = new EWOBuerger();
        final NamenType namen = person.getNamen();
        response.setAkademischerGrad(person.getNamen() != null ? person.getNamen().getDoktorgrad() : null);
        response.setAuskunftssperren(person.getAuskunftssperre() == null ? Collections.emptyList() : Collections.singletonList(person.getAuskunftssperre()));
        if (namen != null) {
            if (namen.getVornamen() != null) {
                response.setVorname(namen.getVornamen()
                        .stream()
                        .map(VornameType::getVorname)
                        .reduce((s1, s2) -> s1 + " " + s2)
                        .orElse(null));
            }

            response.setFamilienname(
                    (namen.getFamilienname() == null) ? null
                            : namen.getFamilienname().getName());

            response.setGeburtsname(
                    (namen.getGeburtsname() == null) ? null
                            : namen.getGeburtsname().getName());

        }
        final GeburtsdatenType geburtsdaten = person.getGeburtsdaten();
        if (geburtsdaten != null) {
            response.setGeburtsort(geburtsdaten.getOrt());
            if (geburtsdaten.getDatum() != null && geburtsdaten.getDatum().getDatum() != null) {
                response.setGeburtsdatum(
                        geburtsdaten.getDatum() == null ? null : LocalDate.parse(geburtsdaten.getDatum().getDatum(), DateTimeFormatter.ISO_DATE));
            }
            final String geburtsland = geburtsdaten.getStaat();

            if (geburtsland != null) {
                try {
                    final CountryCode countryCode = CountryCode.getByCode(Integer.parseInt(geburtsland));
                    response.setGeburtsland(countryCode != null ? countryCode.getName() : CountryCode.DE.getName());
                } catch (NumberFormatException e) {
                    response.setGeburtsland(CountryCode.DE.getName());
                }
            } else {
                response.setGeburtsland(CountryCode.DE.getName());
            }
        }
        response.setFamilienstand(person.getFamilienstanddaten() == null ? null : person.getFamilienstanddaten().getFamilienstand());
        final ZuzugdatenType zuzugdaten = person.getZuzugsdaten();
        if (zuzugdaten != null) {
            if (zuzugdaten.getMitHauptwohnungGemeldetSeit() != null) {
                response.setInMuenchenSeit(zuzugdaten.getMitHauptwohnungGemeldetSeit());
            } else if (zuzugdaten.getZuzugGemeinde() != null) {
                response.setInMuenchenSeit(zuzugdaten.getZuzugGemeinde());
            }
        }
        if (person.getStaatsangehoerigkeiten() != null) {
            response.setStaatsangehoerigkeit(
                    person.getStaatsangehoerigkeiten().stream().map(StaatsangehoerigkeitErweitertType::getText).collect(Collectors.toList()));
        }
        response.setOrdnungsmerkmal(person.getOrdnungsmerkmal());
        mapGeschlecht(person, response);
        mapToWohnung(person, response);
        return response;
    }

    private static void mapToWohnung(final PersonErweitert person, final EWOBuerger response) {

        // Check whether a current apartment is available
        if (person.getWohnungen() == null) {
            return;
        }

        Optional<AktuelleWohnung> wohnung = person.getWohnungen()
                .stream()
                .filter(abstractWohnungType -> abstractWohnungType instanceof AktuelleWohnung)
                .map(abstractWohnungType -> (AktuelleWohnung) abstractWohnungType)
                .filter(AbstractWohnungType::getInnerhalb)
                .findFirst();

        // If no apartment available, use a current apartment
        if (wohnung.isEmpty()) {
            wohnung = person.getWohnungen()
                    .stream()
                    .filter(abstractWohnungType -> abstractWohnungType instanceof AktuelleWohnung)
                    .map(abstractWohnungType -> (AktuelleWohnung) abstractWohnungType)
                    .findFirst();
        }

        // Map address
        if (wohnung.isPresent()) {
            final AnschriftErweitertType anschrift = wohnung.get().getMelderegisterAnschrift();
            if (anschrift != null) {
                response.setHausnummer(anschrift.getHausnummer());
                response.setAppartmentnummer(anschrift.getAppartmentnummer());
                response.setBuchstabeHausnummer(anschrift.getBuchstabeHausnummer());
                response.setStockwerk(anschrift.getStockwerk());
                response.setTeilnummerHausnummer(anschrift.getTeilnummerHausnummer());
                response.setWohnungsgeber(anschrift.getWohnungsgeber());
                response.setZusatz(anschrift.getZusatz());
                response.setOrt(anschrift.getOrt());
                response.setStrasse(anschrift.getStrasse());
                response.setPostleitzahl(anschrift.getPostleitzahl());
            }

            final AktuelleWohnung.NutzungszustandEnum nutzungszustand = wohnung.get().getNutzungszustand();
            if (nutzungszustand == AktuelleWohnung.NutzungszustandEnum.HAUPTWOHNUNG
                    || nutzungszustand == AktuelleWohnung.NutzungszustandEnum.EINZIGE_WOHNUNG) {
                response.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
            } else if (nutzungszustand == AktuelleWohnung.NutzungszustandEnum.NEBENWOHNUNG) {
                response.setWohnungsstatus(Wohnungsstatus.NEBENWOHNUNG);
            }
        }
    }

    private static void mapGeschlecht(final PersonErweitert person, final EWOBuerger response) {
        if (person.getGeschlecht() == null) {
            response.setGeschlecht(Geschlecht.UNBEKANNT);
            return;
        }
        final String geschlecht = person.getGeschlecht().toLowerCase(Locale.GERMAN);
        if (geschlecht.startsWith("m")) {
            response.setGeschlecht(Geschlecht.MAENNLICH);
        } else if (geschlecht.startsWith("w") ||
                geschlecht.startsWith("f")) {
                    response.setGeschlecht(Geschlecht.WEIBLICH);
                } else
            if (geschlecht.startsWith("d")) {
                response.setGeschlecht(Geschlecht.DIVERS);
            } else {
                response.setGeschlecht(Geschlecht.UNBEKANNT);
            }
    }

}
