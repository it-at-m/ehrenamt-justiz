package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.ehrenamtjustiz.backend.domain.*;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EhrenamtJustizStatusDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper.PersonMapper;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import de.muenchen.ehrenamtjustiz.backend.service.EWOService;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@Slf4j
@RequestMapping("/ehrenamtjustiz")
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class EhrenamtJustizRestController {

    private final PersonRepository personRepository;

    private final KonfigurationRepository konfigurationRepository;

    private final EWOService eWOService;

    private final PersonMapper personMapper;

    public EhrenamtJustizRestController(final PersonRepository personRepository, final KonfigurationRepository konfigurationRepository,
            final EWOService eWOService, final PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.konfigurationRepository = konfigurationRepository;
        this.eWOService = eWOService;
        this.personMapper = personMapper;
    }

    @PostMapping(value = "/ewoSuche", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_EWOSUCHE)
    public ResponseEntity<List<EWOBuergerDatenDto>> ewoSuche(@RequestBody final EWOBuergerSucheDto eWOBuergerSuche) {

        log.info("ewoSuche mit Familienname: {}, Vorname: {}, Geburtsdatum: {}",
                EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(eWOBuergerSuche.getFamilienname(), 100)),
                EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(eWOBuergerSuche.getVorname(), 100)),
                EhrenamtJustizUtility
                        .sanitizeInput(eWOBuergerSuche.getGeburtsdatum() != null ? eWOBuergerSuche.getGeburtsdatum().toString() : "null"));

        final List<EWOBuergerDatenDto> eWOBuergerDaten;
        try {
            eWOBuergerDaten = eWOService.ewoSuche(eWOBuergerSuche);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Fehler beim Aufruf von 'ewoSuche: {} {}", e, e.getStackTrace());
            return ResponseEntity.status(e.getStatusCode()).header("errormessage", e.getMessage()).body(null);
        }

        log.info("Anzahl Ergebnisse der ewoSuche: {}", eWOBuergerDaten != null ? eWOBuergerDaten.size() : "0");

        return new ResponseEntity<>(eWOBuergerDaten, HttpStatus.OK);

    }

    @GetMapping(value = "/ewoSucheMitOM", produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_EWOSUCHEMITOM)
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    public ResponseEntity<EWOBuergerDatenDto> ewoSucheMitOm(@RequestParam("om") final String om) {

        log.info("ewoSucheMitOm mit OM: {}", om);

        final EWOBuergerDatenDto ewoResponse = eWOService.ewoSucheMitOM(om);
        if (ewoResponse == null) {
            log.info("ewoSucheMitOm: NOT_FOUND");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {

            log.info("Ergebnis ewoSucheMitOm mit Familienname: {}, Vorname: {}, Geburtsdatum: {}",
                    EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(ewoResponse.getFamilienname(), 100)),
                    EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(ewoResponse.getVorname(), 100)),
                    EhrenamtJustizUtility
                            .sanitizeInput(ewoResponse.getGeburtsdatum() != null ? ewoResponse.getGeburtsdatum().toString() : "null"));

            return new ResponseEntity<>(ewoResponse, HttpStatus.OK);
        }
    }

    @GetMapping("/ewoEaiStatus")
    public ResponseEntity<String> ewoEaiStatus() {

        log.info("ewoEaiStatus wird ermittelt");

        final ResponseEntity<String> ewoResponse = eWOService.ewoEaiStatus();

        log.info("ewoEaiStatus: {}", ewoResponse.getStatusCode());

        return ewoResponse;
    }

    @PostMapping(value = "/pruefeGeburtsdatum", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> pruefeGeburtsdatum(@RequestBody final LocalDate geburtsdatum) {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        final String response = EhrenamtJustizUtility.validateGueltigesGeburtsdatum(konfiguration[0], geburtsdatum);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(value = "/pruefeStaatsangehoerigkeit", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> pruefetaatsangehoerigkeit(@RequestBody final List<String> staatsangehoerigkeit) {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        final String response = EhrenamtJustizUtility.validateGueltigeStaatsangehoerigkeit(konfiguration[0], staatsangehoerigkeit);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(value = "/pruefeWohnsitz", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> pruefeWohnsitz(@RequestBody final String ort) throws JsonProcessingException {

        final String ortFilter = new ObjectMapper().readValue(ort, String.class); // To avoid double ""

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        final String response = EhrenamtJustizUtility.validateGueltigerWohnsitz(konfiguration[0], ortFilter);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping(value = "/pruefenNeuePerson", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    public ResponseEntity<PersonDto> pruefenNeuePerson(@RequestBody final EWOBuergerDatenDto eWOBuergerDaten) {

        log.info("pruefenNeuePerson mit EWO-OM: {}", eWOBuergerDaten.getOrdnungsmerkmal());

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);

        // OM from EWO already in table Person?
        final Person person = personRepository.findByOM(eWOBuergerDaten.getOrdnungsmerkmal(), konfiguration[0].getId());
        if (person == null) {
            log.info("pruefenNeuePerson: NOT_FOUND");
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            log.info("pruefenNeuePerson: OK");
            final PersonDto personDto = personMapper.entity2Model(person);
            return new ResponseEntity<>(personDto, HttpStatus.OK);
        }
    }

    @PostMapping(value = "/vorbereitenUndSpeichernPerson", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    public ResponseEntity<PersonDto> vorbereitenUndSpeichernPerson(@RequestBody final EWOBuergerDatenDto eWOBuergerDaten) {

        log.info("vorbereitenUndSpeichernPerson mit Familienname: {}, Vorname: {}, Geburtsdatum: {}",
                EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(eWOBuergerDaten.getFamilienname(), 100)),
                EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(eWOBuergerDaten.getVorname(), 100)),
                EhrenamtJustizUtility
                        .sanitizeInput(eWOBuergerDaten.getGeburtsdatum() != null ? eWOBuergerDaten.getGeburtsdatum().toString() : "null"));

        final Person person = EhrenamtJustizUtility.getPersonAusEWOBuergerDaten(eWOBuergerDaten);

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        if (konfiguration != null) {
            person.setKonfigurationid(konfiguration[0].getId());
        }

        // SchoeffenGrunddaten insert record
        final PersonDto personDtoResult = personMapper.entity2Model(personRepository.save(person));

        return new ResponseEntity<>(personDtoResult, HttpStatus.OK);
    }

    @GetMapping(value = "/ehrenamtJustizStatus", produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    public ResponseEntity<EhrenamtJustizStatusDto> ehrenamtJustizStatus() {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        final EhrenamtJustizStatusDto ehrenamtJustizStatusDto = new EhrenamtJustizStatusDto();
        ehrenamtJustizStatusDto.setAnzahlBewerbungen(personRepository.countByStatus(Status.BEWERBUNG, konfiguration[0].getId()));
        ehrenamtJustizStatusDto.setAnzahlVorschlaege(personRepository.countByStatus(Status.VORSCHLAG, konfiguration[0].getId()));
        ehrenamtJustizStatusDto.setAnzahlVorschlaegeNeu(personRepository.countByNeuerVorschlagStatus(Status.VORSCHLAG, konfiguration[0].getId()));
        ehrenamtJustizStatusDto.setAnzahlKonflikte(personRepository.countByStatus(Status.KONFLIKT, konfiguration[0].getId()));

        return new ResponseEntity<>(ehrenamtJustizStatusDto, HttpStatus.OK);
    }

}
