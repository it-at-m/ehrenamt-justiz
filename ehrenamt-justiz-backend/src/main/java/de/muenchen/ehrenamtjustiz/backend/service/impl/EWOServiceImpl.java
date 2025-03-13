package de.muenchen.ehrenamtjustiz.backend.service.impl;

import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import de.muenchen.ehrenamtjustiz.backend.service.EWOService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class EWOServiceImpl implements EWOService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";

    @Override
    @PreAuthorize(Authorities.HAS_AUTHORITY_EWOSUCHEMITOM)
    public EWOBuergerDatenDto ewoSucheMitOM(final String om) {

        return new EWOBuergerDatenDto();

    }

    @Override
    @PreAuthorize(Authorities.HAS_AUTHORITY_EWOSUCHE)
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public List<EWOBuergerDatenDto> ewoSuche(final EWOBuergerSucheDto eWOBuergerSucheDto) {
        EWOBuergerDatenDto eWOBuergerDaten=new EWOBuergerDatenDto();

        // Für Testzwecke
        eWOBuergerDaten.setId(UUID.randomUUID());
        eWOBuergerDaten.setOrdnungsmerkmal(String.valueOf((int)(Math.random() * 10000)));
        eWOBuergerDaten.setFamilienname(eWOBuergerSucheDto.getFamilienname());
        eWOBuergerDaten.setVorname(eWOBuergerSucheDto.getVorname());
        eWOBuergerDaten.setGeburtsdatum(eWOBuergerSucheDto.getGeburtsdatum());
        eWOBuergerDaten.setGeschlecht(Geschlecht.MAENNLICH);
        eWOBuergerDaten.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
        eWOBuergerDaten.setFamilienstand("verheiratet");
        eWOBuergerDaten.setGeburtsland("Deutschland");
        eWOBuergerDaten.setGeburtsort("München");
        eWOBuergerDaten.setInmuenchenseit(LocalDate.of(2000,1,1));
        eWOBuergerDaten.getStaatsangehoerigkeit().add("deutsch");
        eWOBuergerDaten.setPostleitzahl("80634");
        eWOBuergerDaten.setOrt("München");
        eWOBuergerDaten.setStrasse("Leopoldstr.");
        eWOBuergerDaten.setHausnummer("9");

        List<EWOBuergerDatenDto> eWOBuergerDatenDtos=new ArrayList<>();
        eWOBuergerDatenDtos.add(eWOBuergerDaten);


        // Für Testzwecke
        eWOBuergerDaten.setId(UUID.randomUUID());
        eWOBuergerDaten.setOrdnungsmerkmal(String.valueOf((int)(Math.random() * 10000)));
        eWOBuergerDaten.setFamilienname(eWOBuergerSucheDto.getFamilienname());
        eWOBuergerDaten.setVorname(eWOBuergerSucheDto.getVorname());
        eWOBuergerDaten.setGeburtsdatum(eWOBuergerSucheDto.getGeburtsdatum());
        eWOBuergerDaten.setGeschlecht(Geschlecht.MAENNLICH);
        eWOBuergerDaten.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
        eWOBuergerDaten.setFamilienstand("verheiratet");
        eWOBuergerDaten.setGeburtsland("Deutschland");
        eWOBuergerDaten.setGeburtsort("München");
        eWOBuergerDaten.setInmuenchenseit(LocalDate.of(2000,1,1));
        eWOBuergerDaten.getStaatsangehoerigkeit().add("deutsch");
        eWOBuergerDaten.setPostleitzahl("80634");
        eWOBuergerDaten.setOrt("München");
        eWOBuergerDaten.setStrasse("Leopoldstr.");
        eWOBuergerDaten.setHausnummer("9");

        eWOBuergerDatenDtos.add(eWOBuergerDaten);

        return eWOBuergerDatenDtos;

    }

    @Override
    public ResponseEntity<String> ewoEaiStatus() {

        return new ResponseEntity<>(STATUS_DOWN, HttpStatus.OK);

    }

}
