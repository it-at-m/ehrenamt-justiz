package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class DocumentDto {

    private UUID id;

    private String contentType;

    private String fileId;

    private Long fileLength;

    private String fileName;

    private byte[] fileData;

    private UUID personid;

    private String documentSource;
}
