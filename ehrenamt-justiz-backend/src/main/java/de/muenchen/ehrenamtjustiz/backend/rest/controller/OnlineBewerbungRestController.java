package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.OnlineBewerbungDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.service.EWOService;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/onlinebewerbung")
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class OnlineBewerbungRestController {

    public static final String ERROR = "ERROR";
    public static final String OK = "OK";

    @Autowired
    EWOService eWOService;

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @Autowired
    private final PersonRepository personRepository;

    @SuppressWarnings({ "PMD.CognitiveComplexity", "PMD.CyclomaticComplexity" })
    @PostMapping(value = "/bewerbungSpeichern", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public String bewerbungSpeichern(@RequestBody final OnlineBewerbungDatenDto onlineBewerbungDatenDto) {

        final List<String> nullWerte = pruefeNullWerte(onlineBewerbungDatenDto);
        if (!nullWerte.isEmpty()) {

            log.info("Pflichtfelder: String vorname, String nachname, LocalDate geburtsdatum, String beruf, String mail - fehlen " + nullWerte);

            return OK;
        }

        // The regex check here, allows only alphanumeric characters to pass.
        // Hence, does not result in log injection
        if (onlineBewerbungDatenDto.getVorname().matches("^[\\p{L}\\p{M}\\s'-]*$") &&
                onlineBewerbungDatenDto.getNachname().matches("^[\\p{L}\\p{M}\\s'-]*$")) {
            log.debug("Online-Bewerbung erhalten! Vorname: '{}' | Nachname: '{}' | Geburtsdatum: '{}'",
                    EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(onlineBewerbungDatenDto.getVorname(), 100)),
                    EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(onlineBewerbungDatenDto.getNachname(), 100)),
                    EhrenamtJustizUtility
                            .sanitizeInput(onlineBewerbungDatenDto.getGeburtsdatum() != null ? onlineBewerbungDatenDto.getGeburtsdatum().toString() : "null"));
        } else {
            log.debug("Online-Bewerbung erhalten!");
        }

        // EWO-search
        final List<EWOBuergerDatenDto> eWOBuergerDaten = ewoSuche(onlineBewerbungDatenDto);
        if (eWOBuergerDaten == null) {
            return ERROR;
        }

        // succussful search: save data
        try {

            final Person person = EhrenamtJustizUtility.getPersonAusEWOBuergerDaten(eWOBuergerDaten.getFirst());

            person.setDerzeitausgeuebterberuf(onlineBewerbungDatenDto.getBeruf());
            person.setMailadresse(onlineBewerbungDatenDto.getMail());
            person.setTelefonnummer(onlineBewerbungDatenDto.getTelefonnummer());
            person.setStatus(Status.BEWERBUNG);
            person.setOnlinebewerbung(true);

            final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
            person.setKonfigurationid(konfiguration[0].getId());

            log.debug("Bewerbung von OM {}", eWOBuergerDaten.getFirst().getOrdnungsmerkmal());

            if (!EhrenamtJustizUtility.validateGueltigesGeburtsdatum(konfiguration[0], person.getGeburtsdatum()).isEmpty() ||
                    !EhrenamtJustizUtility.validateGueltigerWohnsitz(konfiguration[0], person.getOrt()).isEmpty() ||
                    !EhrenamtJustizUtility.validateGueltigeStaatsangehoerigkeit(konfiguration[0], person.getStaatsangehoerigkeit()).isEmpty()) {
                log.info("Bewerbung erfüllt nicht die Anforderungen der Konfiguration. Gebe false zurück.");
                return ERROR;
            }

            final Person personByOM;
            try {
                personByOM = personRepository.findByOM(person.getEwoid(), konfiguration[0].getId());
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                log.error("Fehler beim Aufruf von 'findByOM: {}", person.getEwoid(), e);
                return ERROR;
            }

            if (personByOM == null) {
                final Person save = personRepository.save(person);
                return save.getId() != null ? OK : ERROR;
            } else {
                log.info("OM Bereits vorhanden. Gebe false zurück.");
                return ERROR;
            }

        } catch (Exception e) {
            log.info("Ein Fehler ist aufgetreten. Nachricht:", e);
            return ERROR;
        }
    }

    private static List<String> pruefeNullWerte(final OnlineBewerbungDatenDto onlineBewerbungDatenDto) {
        final List<String> nullWerte = new ArrayList<>();
        if (onlineBewerbungDatenDto.getVorname() == null) {
            nullWerte.add("vorname");
        }
        if (onlineBewerbungDatenDto.getNachname() == null) {
            nullWerte.add("nachname");
        }
        if (onlineBewerbungDatenDto.getGeburtsdatum() == null) {
            nullWerte.add("geburtsdatum");
        }
        if (onlineBewerbungDatenDto.getBeruf() == null) {
            nullWerte.add("beruf");
        }
        if (onlineBewerbungDatenDto.getMail() == null) {
            nullWerte.add("mailadresse");
        }
        return nullWerte;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private List<EWOBuergerDatenDto> ewoSuche(final OnlineBewerbungDatenDto onlineBewerbungDatenDto) {
        final EWOBuergerSucheDto eWOBuergerSucheDto = new EWOBuergerSucheDto();
        eWOBuergerSucheDto.setFamilienname(onlineBewerbungDatenDto.getNachname());
        eWOBuergerSucheDto.setVorname(onlineBewerbungDatenDto.getVorname());
        eWOBuergerSucheDto.setGeburtsdatum(onlineBewerbungDatenDto.getGeburtsdatum());
        final List<EWOBuergerDatenDto> eWOBuergerDaten;
        try {
            eWOBuergerDaten = eWOService.ewoSuche(eWOBuergerSucheDto);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Fehler beim Aufruf von 'ewoSuche: {} {}", e, e.getStackTrace());
            return Collections.emptyList();
        }

        if (eWOBuergerDaten.size() != 1) {
            log.info("Ergebnisliste != 1. Gebe false zurück");
            return Collections.emptyList();
        }
        return eWOBuergerDaten;
    }

}
