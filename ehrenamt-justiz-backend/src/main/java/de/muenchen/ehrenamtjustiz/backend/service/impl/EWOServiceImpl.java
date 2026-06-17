package de.muenchen.ehrenamtjustiz.backend.service.impl;

import de.muenchen.ehrenamtjustiz.api.BuergerSucheAnfrage;
import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.service.EWOService;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import de.muenchen.ehrenamtjustiz.exception.AenderungsServiceException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@Slf4j
@SuppressWarnings({ "PMD.CommentDefaultAccessModifier", "PMD.PreserveStackTrace" })
public class EWOServiceImpl implements EWOService {

    @Autowired
    private RestClient restClient;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @Value("${ewo.eai.basepathprefix}")
    private String basepathewoeaiprefix;

    @Value("${ewo.aenderungsservice.basepathprefix}")
    private String basepathaenderungsserviceprefix;

    @Value("${ewo.eai.basepath}")
    private String basepathewoeai;

    /**
     * Ermittelt die EWO-Daten über die EWO-EAI für die Kernanwendung
     *
     * @param om EWO OM
     * @return EWO-Daten
     */
    @Override
    public EWOBuergerDatenDto ewoSucheMitOM(final String om) {

        final EWOBuerger response;
        EWOBuergerDatenDto eWOBuergerDaten = null;

        try {
            response = restClient
                    .get()
                    .uri(basepathewoeai + "/eairoutes/ewosuchemitom/{om}", om)
                    .retrieve()
                    .body(EWOBuerger.class);
            eWOBuergerDaten = mapToEWOBuerger(response);
        } catch (HttpClientErrorException e) {
            log.error("Fehler in ewoSucheMitOM beim OM {}", om, e);
        } catch (ResourceAccessException e) {
            log.error("Netzwerkfehler in ewoSucheMitOM beim OM {}", om, e);
        } catch (RestClientException e) {
            log.error("Unerwarteter Fehler in ewoSucheMitOM beim OM {}", om, e);
        }

        return eWOBuergerDaten;

    }

    /**
     * Ermittelt die EWO-Daten über die EWO-EAI für den AenderungsService.
     * Im Gegensatz zu {@link #ewoSucheMitOM}, werden im Fehlerfall {@link AenderungsServiceException}
     * geworfen
     *
     * @param om EWO OM
     * @return EWO-Daten
     * @throws AenderungsServiceException If the search operation fails
     */
    @Override
    public EWOBuergerDatenDto ewoSucheMitOMAenderungsService(final String om) {

        final EWOBuerger response;
        final EWOBuergerDatenDto eWOBuergerDaten;

        try {
            response = restClient
                    .get()
                    .uri(basepathewoeai + "/eairoutes/ewosuchemitom/{om}", om)
                    .retrieve()
                    .body(EWOBuerger.class);

            eWOBuergerDaten = mapToEWOBuerger(response);
        } catch (HttpClientErrorException e) {
            log.error("Fehler in ewoSucheMitOM beim OM {}", om, e);
            throw new AenderungsServiceException("Fehler in ewoSucheMitOM", om, true);
        } catch (ResourceAccessException e) {
            log.error("Netzwerkfehler in ewoSucheMitOM beim OM {}", om, e);
            throw new AenderungsServiceException("Netzwerkfehler in ewoSucheMitOM", om, true);
        } catch (RestClientException e) {
            log.error("Unerwarteter Fehler in ewoSucheMitOM beim OM {}", om, e);
            throw new AenderungsServiceException("Unerwarteter Fehler in ewoSucheMitOM", om, true);
        }

        return eWOBuergerDaten;
    }

    /**
     * Sucht EWO-Daten über die EWO-EAI für die Kernanwendung
     *
     * @param eWOBuergerSucheDto Suchkriterien Nachname, Vorname und Geburtsdatum
     * @return List<EWOBuergerDatenDto>
     */
    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public List<EWOBuergerDatenDto> ewoSuche(final EWOBuergerSucheDto eWOBuergerSucheDto) {

        final List<EWOBuergerDatenDto> eWOBuerger = new ArrayList<>();

        final BuergerSucheAnfrage buergerSucheAnfrage = mapToSuchAnfrage(eWOBuergerSucheDto);

        final ResponseEntity<EWOBuerger[]> responseEntity = restClient
                .post()
                .uri(basepathewoeai + "/eairoutes/ewosuche")
                .body(buergerSucheAnfrage)
                .retrieve()
                .toEntity(EWOBuerger[].class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            for (int i = 0; i < Objects.requireNonNull(responseEntity.getBody()).length; i++) {
                final EWOBuergerDatenDto eWOBuergerDatenDto = mapToEWOBuerger(responseEntity.getBody()[i]);
                if (eWOBuergerDatenDto != null) {
                    eWOBuerger.add(eWOBuergerDatenDto);
                }
                /*
                 * // Only for tests:
                 * responseEntity.getBody()[i].setFamilienname("Königsfamilie");
                 * responseEntity.getBody()[i].setOrdnungsmerkmal("5");
                 * List<String> as = new ArrayList<>();
                 * as.add("S");
                 * responseEntity.getBody()[i].setAuskunftssperren(as);
                 * eWOBuerger.add(mapToEWOBuerger(responseEntity.getBody()[i]));
                 * responseEntity.getBody()[i].setFamilienname("Kaiser");
                 * responseEntity.getBody()[i].setOrdnungsmerkmal("3");
                 * eWOBuerger.add(mapToEWOBuerger(responseEntity.getBody()[i]));
                 */
            }
        }

        return eWOBuerger;

    }

    @Override
    public ResponseEntity<String> ewoEaiStatus() {

        try {
            final ResponseEntity<Void> responseEntity = restClient
                    .get()
                    .uri(basepathewoeaiprefix + "/actuator/health")
                    .retrieve()
                    .toEntity(Void.class);

            return new ResponseEntity<>(responseEntity.getStatusCode() == HttpStatus.OK ? EhrenamtJustizUtility.STATUS_UP : EhrenamtJustizUtility.STATUS_DOWN,
                    responseEntity.getStatusCode());
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(EhrenamtJustizUtility.STATUS_DOWN, HttpStatus.OK);
        }

    }

    @Override
    public ResponseEntity<String> aenderungsserviceStatus() {

        try {

            final ResponseEntity<Void> responseEntity = restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(basepathaenderungsserviceprefix + "/actuator/health")
                            .build())
                    .retrieve()
                    .toEntity(Void.class);

            return new ResponseEntity<>(responseEntity.getStatusCode() == HttpStatus.OK ? EhrenamtJustizUtility.STATUS_UP : EhrenamtJustizUtility.STATUS_DOWN,
                    responseEntity.getStatusCode());
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(EhrenamtJustizUtility.STATUS_DOWN, HttpStatus.OK);
        }

    }

    private BuergerSucheAnfrage mapToSuchAnfrage(final EWOBuergerSucheDto eWOBuergerSuche) {
        final BuergerSucheAnfrage buergerSucheAnfrage = new BuergerSucheAnfrage();
        buergerSucheAnfrage.setFamilienname(eWOBuergerSuche.getFamilienname());
        buergerSucheAnfrage.setVorname(eWOBuergerSuche.getVorname());
        buergerSucheAnfrage.setGeburtsdatum(eWOBuergerSuche.getGeburtsdatum());
        return buergerSucheAnfrage;
    }

    private EWOBuergerDatenDto mapToEWOBuerger(final EWOBuerger ewoBuergers) {
        if (ewoBuergers == null) {
            return null;
        }
        final EWOBuergerDatenDto eWOBuergerDaten = new EWOBuergerDatenDto();
        eWOBuergerDaten.setId(UUID.randomUUID());
        eWOBuergerDaten.setOrdnungsmerkmal(ewoBuergers.getOrdnungsmerkmal());
        eWOBuergerDaten.setFamilienname(ewoBuergers.getFamilienname());
        eWOBuergerDaten.setGeburtsname(ewoBuergers.getGeburtsname());
        eWOBuergerDaten.setVorname(ewoBuergers.getVorname());
        switch (ewoBuergers.getGeschlecht()) {
        case de.muenchen.ehrenamtjustiz.api.Geschlecht.MAENNLICH: {
            eWOBuergerDaten.setGeschlecht(Geschlecht.MAENNLICH);
            break;
        }
        case de.muenchen.ehrenamtjustiz.api.Geschlecht.WEIBLICH: {
            eWOBuergerDaten.setGeschlecht(Geschlecht.WEIBLICH);
            break;
        }
        case de.muenchen.ehrenamtjustiz.api.Geschlecht.DIVERS: {
            eWOBuergerDaten.setGeschlecht(Geschlecht.DIVERS);
            break;
        }
        case de.muenchen.ehrenamtjustiz.api.Geschlecht.UNBEKANNT: {
            eWOBuergerDaten.setGeschlecht(Geschlecht.UNBEKANNT);
            break;
        }
        }
        eWOBuergerDaten.setFamilienstand(ewoBuergers.getFamilienstand());
        eWOBuergerDaten.setAuskunftssperre(ewoBuergers.getAuskunftssperren());
        eWOBuergerDaten.setGeburtsland(ewoBuergers.getGeburtsland());
        eWOBuergerDaten.setGeburtsort(ewoBuergers.getGeburtsort());
        eWOBuergerDaten.setGeburtsdatum(ewoBuergers.getGeburtsdatum());
        eWOBuergerDaten.setAkademischergrad(ewoBuergers.getAkademischerGrad());
        eWOBuergerDaten.setStaatsangehoerigkeit(new ArrayList<>(ewoBuergers.getStaatsangehoerigkeit()));
        eWOBuergerDaten.setPostleitzahl(ewoBuergers.getPostleitzahl());
        eWOBuergerDaten.setOrt(ewoBuergers.getOrt());
        eWOBuergerDaten.setStrasse(ewoBuergers.getStrasse());
        eWOBuergerDaten.setHausnummer(ewoBuergers.getHausnummer());
        eWOBuergerDaten.setTeilnummerhausnummer(ewoBuergers.getTeilnummerHausnummer());
        eWOBuergerDaten.setBuchstabehausnummer(ewoBuergers.getBuchstabeHausnummer());
        eWOBuergerDaten.setStockwerk(ewoBuergers.getStockwerk());
        eWOBuergerDaten.setAdresszusatz(ewoBuergers.getZusatz());
        eWOBuergerDaten.setAppartmentnummer(ewoBuergers.getAppartmentnummer());
        eWOBuergerDaten.setWohnungsgeber(ewoBuergers.getWohnungsgeber());

        if (ewoBuergers.getWohnungsstatus() != null) {
            switch (ewoBuergers.getWohnungsstatus()) {
            case de.muenchen.ehrenamtjustiz.api.Wohnungsstatus.HAUPTWOHNUNG:
                eWOBuergerDaten.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
                break;
            case de.muenchen.ehrenamtjustiz.api.Wohnungsstatus.NEBENWOHNUNG:
                eWOBuergerDaten.setWohnungsstatus(Wohnungsstatus.NEBENWOHNUNG);
                break;
            }
        }
        eWOBuergerDaten.setInmuenchenseit(ewoBuergers.getInMuenchenSeit());
        eWOBuergerDaten.setKonfliktfeld(new ArrayList<>(ewoBuergers.getKonfliktFelder()));

        // check, if id already exit:
        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        final Person person = personRepository.findByOM(eWOBuergerDaten.getOrdnungsmerkmal(), konfiguration[0].getId());
        eWOBuergerDaten.setEwoidbereitserfasst(person != null);

        return eWOBuergerDaten;
    }

}
