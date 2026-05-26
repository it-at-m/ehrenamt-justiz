package de.muenchen.ehrenamtjustiz.backend.testdata;

import de.muenchen.ehrenamtjustiz.backend.domain.Document;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.DocumentSource;
import java.util.Random;
import java.util.UUID;

public class DocumentTestDataBuilder {
    public static final int ONEMB = (1024 * 1024);
    private final Document document = new Document();

    public DocumentTestDataBuilder() {
        document.setId(UUID.randomUUID());
        document.setFileName("VerfassungsTreueBestaetitung.pdf");
        document.setContentType("application/pdf");
        final Random rd = new Random();
        final byte[] fileData = new byte[ONEMB];
        rd.nextBytes(fileData);
        document.setFileData(fileData);
        document.setFileLength((long) ONEMB);
    }

    public DocumentTestDataBuilder withPersonId(final UUID personId) {
        document.setPersonid(personId);
        document.setDocumentSource(DocumentSource.CORE);
        return this;
    }

    public DocumentTestDataBuilder withKonfigurationId(final UUID konfigurationId) {
        document.setKonfigurationid(konfigurationId);
        document.setDocumentSource(DocumentSource.CONFIGURATION);
        return this;
    }

    public Document build() {
        return document;
    }

}
