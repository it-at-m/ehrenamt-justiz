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

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class EWOServiceImpl implements EWOService {

    public static final String MUENCHEN = "München";
    public static final String DEUTSCHLAND = "Deutschland";
    public static final String LEOPOLDSTR = "Leopoldstr.";
    public static final String VERHEIRATET = "verheiratet";
    public static final int ZEHNTAUSEND = 10_000;
    public static final String DEUTSCH = "deutsch";
    public static final String PLZ = "80634";
    @Autowired
    private RestTemplate restTemplate;

    private static final String STATUS_UP = "UP";
    private static final String STATUS_DOWN = "DOWN";

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @PreAuthorize(Authorities.HAS_AUTHORITY_EWOSUCHEMITOM)
    public EWOBuergerDatenDto ewoSucheMitOM(final String om) {

        return new EWOBuergerDatenDto();

    }

    @Override
    @PreAuthorize(Authorities.HAS_AUTHORITY_EWOSUCHE)
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public List<EWOBuergerDatenDto> ewoSuche(final EWOBuergerSucheDto eWOBuergerSucheDto) {

        // Für Testzwecke
        final List<EWOBuergerDatenDto> eWOBuergerDatenDtos = new ArrayList<>();
        eWOBuergerDatenDtos.add(getExampleBuergerDaten(eWOBuergerSucheDto));

        // Für Testzwecke
        eWOBuergerDatenDtos.add(getExampleBuergerDaten(eWOBuergerSucheDto));

        return eWOBuergerDatenDtos;

    }

    private static EWOBuergerDatenDto getExampleBuergerDaten(final EWOBuergerSucheDto eWOBuergerSucheDto) {
        final EWOBuergerDatenDto eWOBuergerDaten = new EWOBuergerDatenDto();
        eWOBuergerDaten.setId(UUID.randomUUID());
        eWOBuergerDaten.setOrdnungsmerkmal(String.valueOf(RANDOM.nextInt(ZEHNTAUSEND)));
        eWOBuergerDaten.setFamilienname(eWOBuergerSucheDto.getFamilienname());
        eWOBuergerDaten.setVorname(eWOBuergerSucheDto.getVorname());
        eWOBuergerDaten.setGeburtsdatum(eWOBuergerSucheDto.getGeburtsdatum());
        eWOBuergerDaten.setGeschlecht(Geschlecht.MAENNLICH);
        eWOBuergerDaten.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
        eWOBuergerDaten.setFamilienstand(VERHEIRATET);
        eWOBuergerDaten.setGeburtsland(DEUTSCHLAND);
        eWOBuergerDaten.setGeburtsort(MUENCHEN);
        eWOBuergerDaten.setInmuenchenseit(LocalDate.of(2000, 1, 1));
        eWOBuergerDaten.getStaatsangehoerigkeit().add(DEUTSCH);
        eWOBuergerDaten.setPostleitzahl(PLZ);
        eWOBuergerDaten.setOrt(MUENCHEN);
        eWOBuergerDaten.setStrasse(LEOPOLDSTR);
        eWOBuergerDaten.setHausnummer("9");
        return eWOBuergerDaten;
    }

    @Override
    public ResponseEntity<String> ewoEaiStatus() {

        return new ResponseEntity<>(STATUS_DOWN, HttpStatus.OK);

    }

}
