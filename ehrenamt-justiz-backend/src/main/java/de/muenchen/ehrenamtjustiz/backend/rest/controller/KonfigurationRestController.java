package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import de.muenchen.ehrenamtjustiz.backend.domain.Document;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.KonfigurationDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper.KonfigurationMapper;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.DocumentSource;
import de.muenchen.ehrenamtjustiz.backend.rest.DocumentRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/konfiguration")
public class KonfigurationRestController {

    private final KonfigurationRepository konfigurationRepository;

    private final KonfigurationMapper konfigurationMapper;

    private final DocumentRepository documentRepository;

    public KonfigurationRestController(final KonfigurationRepository konfigurationRepository, final KonfigurationMapper konfigurationMapper,
            final DocumentRepository documentRepository) {
        this.konfigurationRepository = konfigurationRepository;
        this.konfigurationMapper = konfigurationMapper;
        this.documentRepository = documentRepository;
    }

    @PostMapping(value = "/updateKonfiguration", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_KONFIGURATION)
    public ResponseEntity<KonfigurationDto> updateKonfiguration(
            @RequestPart(value = "dateiVerfassungstreue", required = false) final MultipartFile verfassungstreueMuster,
            @RequestPart("object") final KonfigurationDto konfigurationDto) throws IOException {

        final Konfiguration konfiguration = konfigurationMapper.model2Entity(konfigurationDto);

        if (konfiguration.getId() == null || konfiguration.getId().toString().isEmpty()) {
            konfiguration.setId(UUID.randomUUID());
        }

        // konfiguration update
        final Konfiguration savedKonfiguration = konfigurationRepository.save(konfiguration);

        // Muster Verfassungstreue speichern
        verfassungstreueMusterSpeichern(verfassungstreueMuster, savedKonfiguration);

        return new ResponseEntity<>(konfigurationMapper.entity2Model(savedKonfiguration), HttpStatus.OK);
    }

    private void verfassungstreueMusterSpeichern(final MultipartFile verfassungstreueMuster, final Konfiguration savedKonfiguration) throws IOException {

        // Bestehende Dokumente löschen
        final Document[] documents = documentRepository.getDocumentByKonfigurationId(savedKonfiguration.getId());

        // If the only existing document matches the upload, no work needed.
        if (documents.length == 1
                && verfassungstreueMuster != null
                && documents[0].getFileName().equals(verfassungstreueMuster.getOriginalFilename())
                && documents[0].getFileLength().equals(verfassungstreueMuster.getSize())
                && Objects.equals(documents[0].getContentType(), verfassungstreueMuster.getContentType())) {
            return;
        }
        // Otherwise: drop all existing, then insert the new one (if any).
        documentRepository.deleteAll(Arrays.asList(documents));
        if (verfassungstreueMuster != null) {
            documentSpeichern(verfassungstreueMuster, savedKonfiguration);
        }
    }

    private void documentSpeichern(final MultipartFile dateiVerfassungstreue, final Konfiguration savedKonfiguration) throws IOException {
        // Neues Dokument speichern:
        final String originalFilename = dateiVerfassungstreue.getOriginalFilename();
        if (originalFilename == null) {
            log.error("OriginalFilename für das Muster der Verfassungstreue ist null für Konfiguration {}", savedKonfiguration.getBezeichnung());
            return;
        }

        log.info(String.format("Starts storing document with name =%s for Konfiguration =%s", originalFilename, savedKonfiguration.getBezeichnung()));
        final String fileName = StringUtils.cleanPath(originalFilename);
        final Document newdocument = new Document();
        newdocument.setId(UUID.randomUUID());
        newdocument.setFileName(fileName);
        newdocument.setFileLength(dateiVerfassungstreue.getSize());
        newdocument.setContentType(dateiVerfassungstreue.getContentType());
        newdocument.setFileData(dateiVerfassungstreue.getBytes());
        newdocument.setPersonid(null);
        newdocument.setKonfigurationid(savedKonfiguration.getId());
        newdocument.setDocumentSource(DocumentSource.CONFIGURATION);
        documentRepository.save(newdocument);
    }

    @GetMapping(value = "/getAktiveKonfiguration", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<KonfigurationDto> getAktiveKonfiguration() {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        if (konfiguration == null) {
            return new ResponseEntity<>(konfigurationMapper.entity2Model(null), HttpStatus.NOT_FOUND);
        } else if (konfiguration.length == 1) {
            return new ResponseEntity<>(konfigurationMapper.entity2Model(konfiguration[0]), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(konfigurationMapper.entity2Model(null), HttpStatus.NOT_FOUND);
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
