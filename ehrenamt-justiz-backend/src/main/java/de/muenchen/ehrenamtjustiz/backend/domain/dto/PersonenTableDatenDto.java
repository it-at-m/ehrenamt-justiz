package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode
@NoArgsConstructor
@SuperBuilder
@SuppressWarnings("CPD-START")
@SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
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

    // because of EI_EXPOSE_REP
    public List<String> getAuskunftssperre() {
        return new java.util.ArrayList<>(auskunftssperre);
    }

    // because of EI_EXPOSE_REP
    public List<String> getKonfliktfeld() {
        return new java.util.ArrayList<>(konfliktfeld);
    }

}
