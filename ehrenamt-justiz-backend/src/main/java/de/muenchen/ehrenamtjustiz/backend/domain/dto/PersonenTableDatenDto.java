package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode
@NoArgsConstructor
@SuperBuilder
public class PersonenTableDatenDto {
    private UUID id;

    private String familienname;

    private String vorname;

    private LocalDate geburtsdatum;

    private List<String> konfliktfeld;

    private List<String> auskunftssperre;

    private String derzeitausgeuebterberuf;

    private String arbeitgeber;

    private String mailadresse;

    private String ausgeuebteehrenaemter;

    private Status status;

}
