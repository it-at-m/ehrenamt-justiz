package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.service.EhrenamtJustizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/aenderungsservice")
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class AenderungsServiceRestController {

    public static final String X_CAUSE = "X-Cause";
    public static final String BACKEND_ERROR = "BACKEND_ERROR";

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @Autowired
    private final PersonRepository personRepository;

    @Autowired
    private final EhrenamtJustizService ehrenamtJustizService;

    /**
     * Ermitteln der Konflikte für eine Person und den Status evtl. auf KONFLIKT setzen
     *
     * @param om The person identifier
     * @return HTTP status indicating success or failure
     */
    @Operation(
            summary = "Update person status auf KONFLIKT, falls Konflikte vorhanden sind",
            description = "Ermittelt die Konflikte, setzt den KONFLIKT-Status und macht einen Update auf die Tabelle Person"
    )
    @ApiResponses(
        {
                @ApiResponse(responseCode = "200", description = "Person updated successfully"),
                @ApiResponse(responseCode = "404", description = "Person not found"),
                @ApiResponse(responseCode = "500", description = "Internal error during processing")
        }
    )
    @PostMapping("/aenderungsservicePerson")
    public ResponseEntity<Void> aenderungsServicePerson(@RequestBody final String om) {

        final Person personByOM;

        try {
            final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
            personByOM = personRepository.findByOM(om, konfiguration[0].getId());
        } catch (Exception e) {
            log.error("Fehler beim Lesen der OM {}", om, e);
            final MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add(X_CAUSE, BACKEND_ERROR + ": Fehler beim Lesen bei om " + om);
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (personByOM == null) {
            log.info("Keine Person für om {} in aenderungsservicePerson: gefunden", om);
            final MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add(X_CAUSE, BACKEND_ERROR + ": Fehler beim Lesen bei om " + om);
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // get conflicts
        final List<String> konflikte;
        try {
            konflikte = ermittelnKonflikte(personByOM);

            if (!konflikte.isEmpty()) {
                log.info("Status auf KONFLIKT setzen für Person mit om {}", om);
                personByOM.setStatus(Status.KONFLIKT);
            }

        } catch (Exception e) {
            log.error("Fehler beim Ermitteln der Konflikte bei om {}", om, e);
            final MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add(X_CAUSE, BACKEND_ERROR + ": Fehler beim Ermitteln der Konflikte bei om " + om);
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Person U P D A T E
        try {
            personRepository.save(personByOM);
        } catch (Exception e) {
            log.error("Fehler beim Speichern der Person mit om {}", om, e);
            final MultiValueMap<String, String> headers = new HttpHeaders();
            headers.add(X_CAUSE, BACKEND_ERROR + ": Fehler beim Update auf Datenbank bei om " + om);
            return new ResponseEntity<>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private List<String> ermittelnKonflikte(final Person person) {
        log.debug("Ermittle Konflikte für Person mit ID {}", person.getId());

        // Get the conflicts
        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        person.setKonfliktfeld(konflikte);

        if (konflikte.isEmpty()) {
            log.debug("Keine Konflikte gefunden");
        } else {
            log.debug("Folgende Konflikte wurden gefunden: {}", String.join(", ", konflikte));
        }

        return konflikte;
    }

}
