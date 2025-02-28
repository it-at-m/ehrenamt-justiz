package de.muenchen.ehrenamtjustiz.backend.service;

import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface EWOService {
    EWOBuergerDatenDto ewoSucheMitOM(String om);

    List<EWOBuergerDatenDto> ewoSuche(EWOBuergerSucheDto eWOBuergerSucheDto);

    ResponseEntity<String> ewoEaiStatus();
}
