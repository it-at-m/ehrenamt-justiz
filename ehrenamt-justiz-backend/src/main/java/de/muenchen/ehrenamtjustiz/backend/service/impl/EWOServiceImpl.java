package de.muenchen.ehrenamtjustiz.backend.service.impl;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import de.muenchen.ehrenamtjustiz.backend.service.EWOService;
import de.muenchen.ej.eai.personeninfo.api.BuergerSucheAnfrage;
import de.muenchen.ej.eai.personeninfo.api.EWOBuerger;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class EWOServiceImpl implements EWOService {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @Value("${de.muenchen.ewo.eai.server}")
    private String serverEwoEai;

    @Value("${de.muenchen.ewo.eai.basepath:personeninfo/ap}")
    private String basepathEwoEai;

    @Autowired
    private RestTemplate restTemplate;

    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";

    @Override
    @PreAuthorize(Authorities.HAS_AUTHORITY_EWOSUCHEMITOM)
    public EWOBuergerDatenDto ewoSucheMitOM(final String om) {

        final MultiValueMap<String, String> headers = new HttpHeaders();
        final RequestEntity<String> request;
        try {
            request = new RequestEntity<>(headers, HttpMethod.GET, new URL(serverEwoEai + basepathEwoEai + "/eairoutes/ewosuchemitom/" + om).toURI());
        } catch (URISyntaxException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        final ResponseEntity<EWOBuerger> responseEntity;
        EWOBuergerDatenDto eWOBuergerDaten = null;
        try {
            responseEntity = restTemplate.exchange(request, EWOBuerger.class);
            eWOBuergerDaten = mapToEWOBuerger(responseEntity.getBody());
        } catch (HttpClientErrorException e) {
            log.warn("Fehler in ewoSucheMitOM mit Fehlercode {} beim OM {}", e.getMessage(), om);
        }

        return eWOBuergerDaten;

    }

    @Override
    @PreAuthorize(Authorities.HAS_AUTHORITY_EWOSUCHE)
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public List<EWOBuergerDatenDto> ewoSuche(final EWOBuergerSucheDto eWOBuergerSucheDto) {

        final List<EWOBuergerDatenDto> eWOBuerger = new ArrayList<>();

        final BuergerSucheAnfrage buergerSucheAnfrage = mapToSuchAnfrage(eWOBuergerSucheDto);

        final HttpEntity<BuergerSucheAnfrage> entity = new HttpEntity<>(buergerSucheAnfrage);
        final ResponseEntity<EWOBuerger[]> responseEntity = restTemplate.exchange(serverEwoEai + basepathEwoEai + "/eairoutes/ewosuche", HttpMethod.POST,
                entity,
                EWOBuerger[].class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            for (int i = 0; i < Objects.requireNonNull(responseEntity.getBody()).length; i++) {
                eWOBuerger.add(mapToEWOBuerger(responseEntity.getBody()[i]));
                /*
                 * // Nur für Testzwecke:
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
            final ResponseEntity<Void> responseEntity = restTemplate.getForEntity(serverEwoEai + "/actuator/health", Void.class);
            return new ResponseEntity<>(responseEntity.getStatusCode() == HttpStatus.OK ? STATUS_UP : STATUS_DOWN,
                    responseEntity.getStatusCode());
        } catch (RuntimeException ex) {
            log.error(ex.getMessage());
            return new ResponseEntity<>(STATUS_DOWN, HttpStatus.OK);
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
        case männlich: {
            eWOBuergerDaten.setGeschlecht(Geschlecht.MAENNLICH);
            break;
        }
        case weiblich: {
            eWOBuergerDaten.setGeschlecht(Geschlecht.WEIBLICH);
            break;
        }
        case divers: {
            eWOBuergerDaten.setGeschlecht(Geschlecht.DIVERS);
            break;
        }
        case unbekannt: {
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
        eWOBuergerDaten.getStaatsangehoerigkeit().addAll(ewoBuergers.getStaatsangehoerigkeit());
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

        if (ewoBuergers.getWohnungsstatus() != null) { // Ab Java 21 dann in den switch
            switch (ewoBuergers.getWohnungsstatus()) {
            case Hauptwohnung:
                eWOBuergerDaten.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
                break;
            case Nebenwohnung:
                eWOBuergerDaten.setWohnungsstatus(Wohnungsstatus.NEBENWOHNUNG);
                break;
            }
        }
        eWOBuergerDaten.setInmuenchenseit(ewoBuergers.getInMuenchenSeit());
        eWOBuergerDaten.getKonfliktfeld().addAll(ewoBuergers.getKonfliktFelder());

        // Prüfen, ob EWO-ID bereits als Person vorhanden:
        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        final Person person = personRepository.findByOM(eWOBuergerDaten.getOrdnungsmerkmal(), konfiguration[0].getId());
        eWOBuergerDaten.setEwoidbereitserfasst(person != null);

        return eWOBuergerDaten;
    }

}
