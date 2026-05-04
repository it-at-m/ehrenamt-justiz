package de.muenchen.ehrenamtjustiz.backend.domain;

import de.muenchen.ehrenamtjustiz.backend.common.BaseEntity;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.DocumentSource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.sql.Types;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

@Entity
@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@SuperBuilder
public class Document extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_id", length = 51)
    private String fileId;

    @Column(name = "file_length")
    private Long fileLength;

    @Column(name = "file_name", nullable = false, length = 2000)
    @NotNull private String fileName;

    @Lob
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "file_data")
    private byte[] fileData;

    @Column(name = "person_id")
    private UUID personid;

    @Column(name = "document_source", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private DocumentSource documentSource;
}
