package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.KonfigurationDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper.KonfigurationMapper;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/konfiguration")
public class KonfigurationRestController {

    private final KonfigurationRepository konfigurationRepository;

    private final KonfigurationMapper konfigurationMapper;

    public KonfigurationRestController(final KonfigurationRepository konfigurationRepository, final KonfigurationMapper konfigurationMapper) {
        this.konfigurationRepository = konfigurationRepository;
        this.konfigurationMapper = konfigurationMapper;
    }

    @PostMapping(value = "/updateKonfiguration", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_KONFIGURATION)
    public ResponseEntity<KonfigurationDto> updateKonfiguration(@RequestHeader Map<String, Object> headers,
            @RequestBody final KonfigurationDto konfigurationDto) {

        final Konfiguration konfiguration = konfigurationMapper.model2Entity(konfigurationDto);

        if (konfiguration.getId() == null || konfiguration.getId().toString().isEmpty()) {
            konfiguration.setId(UUID.randomUUID());
        }

        // konfiguration update
        final Konfiguration konfigurationResult = konfigurationRepository.save(konfiguration);

        return new ResponseEntity<>(konfigurationMapper.entity2Model(konfigurationResult), HttpStatus.OK);
    }

    @GetMapping(value = "/getAktiveKonfiguration", produces = { MediaType.APPLICATION_JSON_VALUE })
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ResponseEntity<KonfigurationDto> getAktiveKonfiguration() {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        if (konfiguration == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else if (konfiguration.length == 1) {
            return new ResponseEntity<>(konfigurationMapper.entity2Model(konfiguration[0]), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/setActive", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_KONFIGURATION)
    @SuppressWarnings("PMD.LinguisticNaming")
    public ResponseEntity<KonfigurationDto> setActive(@RequestBody final KonfigurationDto konfigurationDto) {

        log.info("setActive mit von: {} bis: {}", konfigurationDto.getAmtsperiodevon(), konfigurationDto.getAmtsperiodebis());

        Konfiguration aktiveKonfiguration = null;

        final Iterable<Konfiguration> konfigurationen = konfigurationRepository.findAll(Sort.unsorted());
        for (final Konfiguration tempKonfiguration : konfigurationen) {
            if (tempKonfiguration.getId().toString().equals(konfigurationDto.getId().toString())) {
                tempKonfiguration.setAktiv(true);
                aktiveKonfiguration = tempKonfiguration;
            } else {
                tempKonfiguration.setAktiv(false);
            }
            konfigurationRepository.save(tempKonfiguration);
        }
        if (aktiveKonfiguration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(konfigurationMapper.entity2Model(aktiveKonfiguration), HttpStatus.OK);
        }
    }
}
