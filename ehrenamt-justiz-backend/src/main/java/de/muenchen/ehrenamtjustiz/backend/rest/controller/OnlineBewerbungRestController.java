package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import de.muenchen.ehrenamtjustiz.backend.domain.Document;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.DocumentSource;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.rest.DocumentRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.service.EWOService;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

@RestController
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
    DocumentRepository documentRepository;

    @Autowired
    PersonRepository personRepository;

    @Value("${verfassungstreue.muster}")
    private String verfassungstreueMuster;

    @GetMapping("/lesenVerfassungstreueMuster")
    public ResponseEntity<String> lesenVerfassungstreueMuster() {

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(verfassungstreueMuster);
    }

    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.UseObjectForClearerAPI" })
    @PostMapping(path = "/pruefen", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public String pruefen(@RequestParam("vorname") final String vorname,
            @RequestParam("nachname") final String nachname,
            @RequestParam("beruf") final String beruf,
            @RequestParam("geburtsdatum") final LocalDate geburtsdatum,
            @RequestParam("mail") final String mail,
            @RequestParam("telefonnummer") final String telefonnummer) {

        final List<String> nullWerte = pruefeNullWerte(vorname, nachname, beruf, geburtsdatum, mail);

        if (!nullWerte.isEmpty()) {

            log.info("Pflichtfelder: String vorname, String nachname, LocalDate geburtsdatum, String beruf, String mail - fehlen " + nullWerte);

            return OK;
        }

        // The regex check here, allows only alphanumeric characters to pass.
        // Hence, does not result in log injection
        if (vorname.matches("^[\\p{L}\\p{M}\\s'-]*$") &&
                nachname.matches("^[\\p{L}\\p{M}\\s'-]*$")) {
            log.debug("Online-Bewerbung erhalten! Vorname: '{}' | Nachname: '{}' | Geburtsdatum: '{}'",
                    EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(vorname, 100)),
                    EhrenamtJustizUtility.sanitizeInput(EhrenamtJustizUtility.truncateIfNeeded(nachname, 100)),
                    EhrenamtJustizUtility
                            .sanitizeInput(geburtsdatum != null ? geburtsdatum.toString() : "null"));
        } else {
            log.debug("Online-Bewerbung erhalten!");
        }

        // EWO-search
        final List<EWOBuergerDatenDto> eWOBuergerDaten = ewoSuche(vorname, nachname, geburtsdatum);
        if (eWOBuergerDaten == null || eWOBuergerDaten.isEmpty()) {
            return ERROR;
        }

        // succussful search: save data
        try {

            final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);

            log.debug("Bewerbung von OM {}", eWOBuergerDaten.getFirst().getOrdnungsmerkmal());

            if (!EhrenamtJustizUtility.validateGueltigesGeburtsdatum(konfiguration[0], eWOBuergerDaten.getFirst().getGeburtsdatum()).isEmpty() ||
                    !EhrenamtJustizUtility.validateGueltigerWohnsitz(konfiguration[0], eWOBuergerDaten.getFirst().getOrt()).isEmpty() ||
                    !EhrenamtJustizUtility.validateGueltigeStaatsangehoerigkeit(konfiguration[0], eWOBuergerDaten.getFirst().getStaatsangehoerigkeit())
                            .isEmpty()) {
                log.info("Bewerbung erfüllt nicht die Anforderungen der Konfiguration. Gebe false zurück.");
                return ERROR;
            }

            final Person personByOM;
            try {
                personByOM = personRepository.findByOM(eWOBuergerDaten.getFirst().getOrdnungsmerkmal(), konfiguration[0].getId());
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                log.error("Fehler beim Aufruf von 'findByOM: {}", eWOBuergerDaten.getFirst().getOrdnungsmerkmal(), e);
                return ERROR;
            }

            if (personByOM != null) {
                log.info("OM Bereits vorhanden. Gebe false zurück.");
                return ERROR;
            }

            return OK;

        } catch (Exception e) {
            log.info("Ein Fehler ist aufgetreten. Nachricht:", e);
            return ERROR;
        }
    }

    @SuppressWarnings({ "PMD.UseObjectForClearerAPI" })
    @PostMapping(path = "/bewerbungSpeichern", consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    @Transactional
    public String bewerbungSpeichern(@RequestParam(value = "dateiVerfassungstreue", required = false) final MultipartFile dateiVerfassungstreue,
            @RequestParam("vorname") final String vorname,
            @RequestParam("nachname") final String nachname,
            @RequestParam("beruf") final String beruf,
            @RequestParam("geburtsdatum") final LocalDate geburtsdatum,
            @RequestParam("mail") final String mail,
            @RequestParam("telefonnummer") final String telefonnummer) {

        // EWO-search
        final List<EWOBuergerDatenDto> eWOBuergerDaten = ewoSuche(vorname, nachname, geburtsdatum);
        if (eWOBuergerDaten == null || eWOBuergerDaten.isEmpty()) {
            return ERROR;
        }

        try {

            final Person person = EhrenamtJustizUtility.getPersonAusEWOBuergerDaten(eWOBuergerDaten.getFirst());

            person.setDerzeitausgeuebterberuf(beruf);
            person.setMailadresse(mail);
            person.setTelefonnummer(telefonnummer);
            person.setStatus(Status.BEWERBUNG);
            person.setOnlinebewerbung(true);

            final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
            person.setKonfigurationid(konfiguration[0].getId());

            log.debug("Bewerbung von OM {}", eWOBuergerDaten.getFirst().getOrdnungsmerkmal());

            final Person personByOM;
            try {
                personByOM = personRepository.findByOM(person.getEwoid(), konfiguration[0].getId());
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                log.error("Fehler beim Aufruf von 'findByOM: {}", person.getEwoid(), e);
                return ERROR;
            }

            if (personByOM == null) {
                final Person savedPerson = personRepository.save(person);

                dateiVerfassungstreueSpeichern(dateiVerfassungstreue, savedPerson);

                return savedPerson.getId() != null ? OK : ERROR;

            } else {
                log.info("OM Bereits vorhanden. Gebe false zurück.");
                return ERROR;
            }

        } catch (Exception e) {
            log.info("Ein Fehler ist aufgetreten. Nachricht:", e);
            return ERROR;
        }
    }

    private void dateiVerfassungstreueSpeichern(final MultipartFile dateiVerfassungstreue, final Person savedPerson) throws IOException {
        if (dateiVerfassungstreue == null) {
            log.error("Datei für die Bestätigung der Verfassungstreue ist null Für persion ID ={}.", savedPerson.getEwoid());
            return;
        }
        final String originalFilename = dateiVerfassungstreue.getOriginalFilename();
        if (originalFilename == null) {
            log.error("OriginalFilename für die Bestätigung der Verfassungstreue ist null für Person ID={}", savedPerson.getEwoid());
            return;
        }
        log.info(String.format("Starts storing document with name =%s for person ID =%s", originalFilename, savedPerson.getEwoid()));
        final String fileName = StringUtils.cleanPath(originalFilename);
        final Document document = new Document();
        document.setId(UUID.randomUUID());
        document.setFileName(fileName);
        document.setFileLength(dateiVerfassungstreue.getSize());
        document.setContentType(dateiVerfassungstreue.getContentType());
        document.setFileData(dateiVerfassungstreue.getBytes());
        document.setPersonid(savedPerson.getId());
        document.setDocumentSource(DocumentSource.ONLINE);
        documentRepository.save(document);

    }

    private static List<String> pruefeNullWerte(final String vorname,
            final String nachname,
            final String beruf,
            final LocalDate geburtsdatum,
            final String mail) {
        final List<String> nullWerte = new ArrayList<>();
        if (!StringUtils.hasText(vorname)) {
            nullWerte.add("vorname");
        }
        if (!StringUtils.hasText(nachname)) {
            nullWerte.add("nachname");
        }
        if (geburtsdatum == null) {
            nullWerte.add("geburtsdatum");
        }
        if (!StringUtils.hasText(beruf)) {
            nullWerte.add("beruf");
        }
        if (!StringUtils.hasText(mail)) {
            nullWerte.add("mailadresse");
        }
        return nullWerte;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private List<EWOBuergerDatenDto> ewoSuche(final String vorname,
            final String nachname,
            final LocalDate geburtsdatum) {
        final EWOBuergerSucheDto eWOBuergerSucheDto = new EWOBuergerSucheDto();
        eWOBuergerSucheDto.setFamilienname(nachname);
        eWOBuergerSucheDto.setVorname(vorname);
        eWOBuergerSucheDto.setGeburtsdatum(geburtsdatum);
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
