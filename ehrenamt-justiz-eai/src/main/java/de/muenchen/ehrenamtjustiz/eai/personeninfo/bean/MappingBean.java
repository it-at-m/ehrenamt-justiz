package de.muenchen.ehrenamtjustiz.eai.personeninfo.bean;

import de.muenchen.eai.ewo.api.fachlich.model.person.v2.*;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.LesePersonErweitert;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.LesePersonErweitertResponse;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.SuchePersonErweitert;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.SuchePersonErweitertResponse;
import de.muenchen.ehrenamtjustiz.api.BuergerSucheAnfrage;
import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.api.Geschlecht;
import de.muenchen.ehrenamtjustiz.api.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.exception.PersonNotFoundException;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.util.CountryCode;
import org.apache.camel.Header;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central mapping class for converting from one data type to another
 *
 */
@Component
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.LambdaCanBeMethodReference", "PMD.CouplingBetweenObjects" })
public class MappingBean {

    public LesePersonErweitert.Anfrage toEWOLesen(@Header("om") final String om) {
        final LesePersonErweitert.Anfrage anfrage = new LesePersonErweitert.Anfrage();
        final BenutzerType benutzerType = new BenutzerType();
        benutzerType.setBenutzerId(SecurityContextHolder.getContext().getAuthentication().getName());
        anfrage.setBenutzer(benutzerType);
        anfrage.setOm(om);
        return anfrage;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    public EWOBuerger fromEWOLesen(final MessageContentsList sucheErgebnis) {
        final EWOBuerger eWOBuerger = new EWOBuerger();

        if (sucheErgebnis != null) {

            if (sucheErgebnis.getFirst() instanceof LesePersonErweitertResponse.PersonErweitert) {
                final PersonErweitertType personenErweitert = (LesePersonErweitertResponse.PersonErweitert) sucheErgebnis.getFirst();
                return toEWOBuerger(personenErweitert);
            } else {
                throw new PersonNotFoundException(null, "Keine Ergebnis für das Lesen aus EWO");
            }
        }

        return eWOBuerger;
    }

    public SuchePersonErweitert.Anfrage toEWOSuche(final BuergerSucheAnfrage buergerSucheAnfrage) {

        final PersonenSuchkriterienErweitertType suchkriterien = new PersonenSuchkriterienErweitertType();
        suchkriterien.setFamilienname(buergerSucheAnfrage.getFamilienname());
        suchkriterien.setVorname(buergerSucheAnfrage.getVorname());
        final GeburtsdatumType geburtsdatum = new GeburtsdatumType();
        geburtsdatum.setDatum(buergerSucheAnfrage.getGeburtsdatum().format(DateTimeFormatter.ISO_DATE));
        suchkriterien.setGeburtsdatum(geburtsdatum);
        suchkriterien.setDatensatzstatus(DatensatzstatusType.AKTUELL);

        final SuchePersonErweitert.Anfrage suchAnfrage = new SuchePersonErweitert.Anfrage();
        final BenutzerType benutzerType = new BenutzerType();
        if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            benutzerType.setBenutzerId(SecurityContextHolder.getContext().getAuthentication().getName());
        }
        suchAnfrage.setBenutzer(benutzerType);
        suchAnfrage.setSuchkriterien(suchkriterien);

        return suchAnfrage;
    }

    @SuppressWarnings("PMD.LooseCoupling")
    public List<EWOBuerger> fromEWOSuche(final MessageContentsList sucheErgebnis) {
        final List<EWOBuerger> ewoBuergers = new ArrayList<>();

        if (sucheErgebnis != null) {

            if (sucheErgebnis.getFirst() instanceof SuchePersonErweitertResponse.AntwortErweitert) {

                final SuchePersonErweitertResponse.AntwortErweitert antwortErweitert = (SuchePersonErweitertResponse.AntwortErweitert) sucheErgebnis.get(0);
                final List<PersonErweitert> personenErweitert = antwortErweitert.getPersonErweitert();
                if (personenErweitert.isEmpty()) {
                    throw new PersonNotFoundException(null, "Kein Ergebnis für Suchanfrage");
                } else {
                    for (final PersonErweitert personErweitert : personenErweitert) {
                        ewoBuergers.add(toEWOBuerger(personErweitert));
                    }
                }
            } else {
                throw new PersonNotFoundException(null, "Kein Ergebnis für Suchanfrage");
            }
        }

        return ewoBuergers;
    }

    @SuppressWarnings({ "PMD.CognitiveComplexity", "CyclomaticComplexity", "NPathComplexity" })
    private static EWOBuerger toEWOBuerger(final PersonErweitertType person) {
        final EWOBuerger response = new EWOBuerger();
        final NamenType namen = person.getNamen();
        response.setAkademischerGrad(person.getNamen() != null ? person.getNamen().getDoktorgrad() : null);
        response.setAuskunftssperren(person.getAuskunftssperre() == null ? Collections.emptyList() : Collections.singletonList(person.getAuskunftssperre()));
        if (namen != null) {
            response.setVorname(namen.getVornamen()
                    .stream()
                    .map(VornameType::getVorname)
                    .reduce((s1, s2) -> s1 + " " + s2)
                    .orElse(null));

            response.setFamilienname(
                    (namen.getFamilienname() == null) ? null
                            : (null == namen.getFamilienname().getUnstrukturierteSchreibweise()) ? namen.getFamilienname().getName()
                                    : namen.getFamilienname().getUnstrukturierteSchreibweise());

            response.setGeburtsname(
                    (namen.getGeburtsname() == null) ? null
                            : (null == namen.getGeburtsname().getUnstrukturierteSchreibweise()) ? namen.getGeburtsname().getName()
                                    : namen.getGeburtsname().getUnstrukturierteSchreibweise());

        }
        final GeburtsdatenType geburtsdaten = person.getGeburtsdaten();
        if (geburtsdaten != null) {
            response.setGeburtsort(geburtsdaten.getOrt());
            response.setGeburtsdatum(geburtsdaten.getDatum() == null ? null : LocalDate.parse(geburtsdaten.getDatum().getDatum(), DateTimeFormatter.ISO_DATE));
            final String geburtsland = geburtsdaten.getStaat();
            if (geburtsland != null && CountryCode.getByCode(Integer.parseInt(geburtsland)) != null) {
                response.setGeburtsland(CountryCode.getByCode(Integer.parseInt(geburtsland)).getName());
            } else {
                response.setGeburtsland(CountryCode.DE.getName());
            }
        }
        response.setFamilienstand(person.getFamilienstanddaten() == null ? null : person.getFamilienstanddaten().getFamilienstand());
        final ZuzugdatenType zuzugdaten = person.getZuzugsdaten();
        if (zuzugdaten != null) {
            final XMLGregorianCalendar calendar = zuzugdaten.getMitHauptwohnungGemeldetSeit();
            final XMLGregorianCalendar zuzugGemeinde = zuzugdaten.getZuzugGemeinde();
            if (calendar != null) {
                response.setInMuenchenSeit(LocalDate.parse(calendar.toString(), DateTimeFormatter.ISO_DATE));
            } else if (zuzugGemeinde != null) {
                response.setInMuenchenSeit(LocalDate.parse(zuzugGemeinde.toString(), DateTimeFormatter.ISO_DATE));
            }
        }
        response.setStaatsangehoerigkeit(person.getStaatsangehoerigkeiten().stream().map(StaatsangehoerigkeitType::getText).collect(Collectors.toList()));
        response.setOrdnungsmerkmal(person.getOrdnungsmerkmal());
        mapGeschlecht(person, response);
        mapToWohnung(person, response);
        return response;
    }

    private static void mapToWohnung(final PersonErweitertType person, final EWOBuerger response) {
        // Check whether a current apartment is available
        Optional<AktuelleWohnungType> wohnung = person.getWohnungen()
                .stream()
                .filter(abstractWohnungType -> abstractWohnungType instanceof AktuelleWohnungType)
                .map(abstractWohnungType -> (AktuelleWohnungType) abstractWohnungType)
                .filter(AbstractWohnungType::isInnerhalb)
                .findFirst();

        // If no apartment available, use a current apartment
        if (wohnung.isEmpty()) {
            wohnung = person.getWohnungen()
                    .stream()
                    .filter(abstractWohnungType -> abstractWohnungType instanceof AktuelleWohnungType)
                    .map(abstractWohnungType -> (AktuelleWohnungType) abstractWohnungType)
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

            final WohnungsNutzungszustandType nutzungszustand = wohnung.get().getNutzungszustand();
            if (nutzungszustand == WohnungsNutzungszustandType.HAUPTWOHNUNG
                    || nutzungszustand == WohnungsNutzungszustandType.EINZIGE_WOHNUNG) {
                response.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
            } else if (nutzungszustand == WohnungsNutzungszustandType.NEBENWOHNUNG) {
                response.setWohnungsstatus(Wohnungsstatus.NEBENWOHNUNG);
            }
        }

    }

    private static void mapGeschlecht(final PersonErweitertType person, final EWOBuerger response) {
        if (person.getGeschlecht() != null && person.getGeschlecht().toLowerCase(Locale.getDefault()).startsWith("m")) {
            response.setGeschlecht(Geschlecht.MAENNLICH);
        } else if (person.getGeschlecht() != null &&
                (person.getGeschlecht().toLowerCase(Locale.getDefault()).startsWith("w") ||
                        person.getGeschlecht().toLowerCase(Locale.getDefault()).startsWith("f"))) {
                            response.setGeschlecht(Geschlecht.WEIBLICH);
                        } else
            if (person.getGeschlecht() != null &&
                    person.getGeschlecht().toLowerCase(Locale.getDefault()).startsWith("d")) {
                        response.setGeschlecht(Geschlecht.DIVERS);
                    } else {
                        response.setGeschlecht(Geschlecht.UNBEKANNT);
                    }
    }

}
