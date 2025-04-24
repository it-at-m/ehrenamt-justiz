package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.service.EhrenamtJustizService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/aenderungsservice")
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class AenderungsServiceRestController {

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @Autowired
    private final PersonRepository personRepository;

    @Autowired
    private final EhrenamtJustizService ehrenamtJustizService;

    @PostMapping("/aenderungsservicePerson")
    public ResponseEntity<Void> aenderungsServicePerson(@RequestBody final String om) {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);

        final Person personByOM;
        try {
            personByOM = personRepository.findByOM(om, konfiguration[0].getId());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // The regex check here, allows only alphanumeric characters to pass.
            // Hence, does not result in log injection
            if (om.matches("\\w*")) {
                log.error("Fehler beim Aufruf von 'aenderungsservicePerson: {}", om, e);
            } else {
                log.error("Fehler beim Aufruf von 'aenderungsservicePerson", e);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (personByOM == null) {
            if (om.matches("\\w*")) {
                log.info("Keine Person für om {} in aenderungsservicePerson: gefunden", om);
            } else {
                log.info("Keine Person aenderungsservicePerson: gefunden");
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // get conflicts
        final List<String> konflikte = ermittelnKonflikte(personByOM);

        if (!konflikte.isEmpty()) {
            personByOM.setStatus(Status.KONFLIKT);
        }

        // Person U P D A T E
        try {
            personRepository.save(personByOM);
        } catch (Exception e) {
            log.error("Fehler beim Speichern der Person mit om {}", om, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private List<String> ermittelnKonflikte(final Person person) {

        // Get the conflicts
        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        person.setKonfliktfeld(konflikte);

        return konflikte;
    }

}
