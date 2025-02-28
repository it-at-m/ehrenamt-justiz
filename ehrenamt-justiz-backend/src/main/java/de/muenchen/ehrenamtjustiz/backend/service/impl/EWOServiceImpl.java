package de.muenchen.ehrenamtjustiz.backend.service.impl;

import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
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

import java.util.ArrayList;
import java.util.List;

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

        return new ArrayList<>();

    }

    @Override
    public ResponseEntity<String> ewoEaiStatus() {

        return new ResponseEntity<>(STATUS_DOWN, HttpStatus.OK);

    }

}
