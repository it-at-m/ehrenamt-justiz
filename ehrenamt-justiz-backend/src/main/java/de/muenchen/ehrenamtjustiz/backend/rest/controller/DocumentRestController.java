package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import de.muenchen.ehrenamtjustiz.backend.domain.Document;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.DocumentDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper.DocumentMapper;
import de.muenchen.ehrenamtjustiz.backend.rest.DocumentRepository;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/document")
public class DocumentRestController {

    private final DocumentRepository documentRepository;

    private final DocumentMapper documentMapper;

    public DocumentRestController(final DocumentRepository documentRepository, final DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    @PostMapping(value = "/getDocumentByPersonId", produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    @SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
    public ResponseEntity<DocumentDto> getDocumentByPersonId(@RequestBody final UUID personId) {

        final Document[] documents = documentRepository.getDocumentByPersonId(personId);
        if (documents == null || documents.length == 0) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(documentMapper.entity2Model(documents[0]), HttpStatus.OK);
        }
    }

}
