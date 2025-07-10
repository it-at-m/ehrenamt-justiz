package de.muenchen.ehrenamtjustiz.backend.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import de.muenchen.ehrenamtjustiz.backend.common.BaseEntity;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.math.BigInteger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
// Definition of getter, setter, ...
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Konfiguration extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    // ========= //
    // Variables //
    // ========= //

    @Column(name = "ehrenamtjustizart")
    @Enumerated(EnumType.STRING)
    private Ehrenamtjustizart ehrenamtjustizart;

    @Column(name = "bezeichnung")
    @NotNull @Size(max = 255) private String bezeichnung;

    @Column(name = "aktiv")
    @NotNull private boolean aktiv;

    @Column(name = "amtsperiodevon")
    @NotNull @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private java.time.LocalDate amtsperiodevon;

    @Column(name = "amtsperiodebis")
    @NotNull @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private java.time.LocalDate amtsperiodebis;

    @Column(name = "altervon")
    @NotNull private BigInteger altervon;

    @Column(name = "alterbis")
    @NotNull private BigInteger alterbis;

    @Column(name = "staatsangehoerigkeit")
    @NotNull @Size(max = 255) private String staatsangehoerigkeit;

    @Column(name = "wohnsitz")
    @NotNull @Size(max = 255) private String wohnsitz;

}
