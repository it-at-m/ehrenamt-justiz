package de.muenchen.ehrenamtjustiz.backend.service;

import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerSucheDto;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface EWOService {
    EWOBuergerDatenDto ewoSucheMitOM(String om);

    EWOBuergerDatenDto ewoSucheMitOMAenderungsService(String om);

    List<EWOBuergerDatenDto> ewoSuche(EWOBuergerSucheDto eWOBuergerSucheDto);

    ResponseEntity<String> ewoEaiStatus();

    ResponseEntity<String> aenderungsserviceStatus();
}
