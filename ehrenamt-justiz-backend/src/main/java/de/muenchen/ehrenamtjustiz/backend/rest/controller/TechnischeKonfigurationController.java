package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import de.muenchen.ehrenamtjustiz.backend.domain.dto.TechnischeKonfigurationDto;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/technischeKonfiguration")
public class TechnischeKonfigurationController {

    @Value("${ehrju.bestaetigungVerfassungstreue.maxSize}")
    private BigInteger bestaetigungVerfassungstreueMaxSize;

    @Value("${ehrju.bestaetigungVerfassungstreue.fileExtension}")
    private String bestaetigungVerfassungstreueFileExtension;

    @GetMapping(value = "/getTechnischeKonfiguration", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<TechnischeKonfigurationDto> getTechnischeKonfiguration() {

        final TechnischeKonfigurationDto technischeKonfigurationDto = new TechnischeKonfigurationDto();
        technischeKonfigurationDto.setBestaetigungVerfassungstreueMaxSize(bestaetigungVerfassungstreueMaxSize);
        technischeKonfigurationDto.setBestaetigungVerfassungstreueFileExtension(bestaetigungVerfassungstreueFileExtension);
        return new ResponseEntity<>(technischeKonfigurationDto, HttpStatus.OK);

    }

}
