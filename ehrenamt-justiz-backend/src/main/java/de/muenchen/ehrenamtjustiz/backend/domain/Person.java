package de.muenchen.ehrenamtjustiz.backend.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import de.muenchen.ehrenamtjustiz.backend.common.BaseEntity;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.util.Collections;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * This class represents a Person.
 * <p>
 * The entity's content will be loaded according to the reference variable.
 * </p>
 */
@Entity
// Definition of getter, setter, ...
@Setter
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@SuppressWarnings({ "PMD.TooManyFields", "CPD-START" })
public class Person extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "ewoid")
    @Size(max = 255) private String ewoid;

    @Column(name = "konfigurationid")
    private UUID konfigurationid;

    @Column(name = "familienname")
    @NotNull @Size(max = 300) private String familienname;

    @Column(name = "geburtsname")
    @Size(max = 300) private String geburtsname;

    @Column(name = "vorname")
    @NotNull @Size(max = 255) private String vorname;

    @Column(name = "geburtsdatum")
    @NotNull @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private java.time.LocalDate geburtsdatum;

    @Column(name = "geschlecht")
    @NotNull @Enumerated(EnumType.STRING)
    private Geschlecht geschlecht;

    @Column(name = "akademischergrad")
    @Size(max = 255) private String akademischergrad;

    @Column(name = "geburtsort")
    @NotNull @Size(max = 255) private String geburtsort;

    @Column(name = "geburtsland")
    @NotNull @Size(max = 255) private String geburtsland;

    @Column(name = "familienstand")
    @NotNull @Size(max = 255) private String familienstand;

    @Column(name = "strasse")
    @NotNull @Size(max = 255) private String strasse;

    @Column(name = "hausnummer")
    @NotNull @Size(max = 255) private String hausnummer;

    @Column(name = "appartmentnummer")
    @Size(max = 255) private String appartmentnummer;

    @Column(name = "buchstabehausnummer")
    @Size(max = 255) private String buchstabehausnummer;

    @Column(name = "stockwerk")
    @Size(max = 255) private String stockwerk;

    @Column(name = "teilnummerhausnummer")
    @Size(max = 255) private String teilnummerhausnummer;

    @Column(name = "adresszusatz")
    @Size(max = 255) private String adresszusatz;

    @Column(name = "postleitzahl")
    @NotNull @Size(max = 255) private String postleitzahl;

    @Column(name = "ort")
    @NotNull @Size(max = 255) private String ort;

    @Column(name = "inmuenchenseit")
    @NotNull @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private java.time.LocalDate inmuenchenseit;

    @Column(name = "wohnungsgeber")
    @Size(max = 255) private String wohnungsgeber;

    @Column(name = "wohnungsstatus")
    @NotNull @Enumerated(EnumType.STRING)
    private Wohnungsstatus wohnungsstatus;

    @Column(name = "derzeitausgeuebterberuf")
    @Size(max = 255) private String derzeitausgeuebterberuf;

    @Column(name = "arbeitgeber")
    @Size(max = 255) private String arbeitgeber;

    @Column(name = "telefonnummer")
    @Size(max = 255) private String telefonnummer;

    @Column(name = "telefongesch")
    @Size(max = 255) private String telefongesch;

    @Column(name = "telefonmobil")
    @Size(max = 255) private String telefonmobil;

    @Column(name = "mailadresse")
    @Size(max = 150) private String mailadresse;

    @Column(name = "ausgeuebteehrenaemter")
    @Size(max = 4000) private String ausgeuebteehrenaemter;

    @Column(name = "onlinebewerbung")
    private boolean onlinebewerbung = false;

    @Column(name = "neuervorschlag")
    private boolean neuervorschlag = false;

    @Column(name = "warbereitstaetigals")
    private boolean warbereitstaetigals = false;

    @Column(name = "warbereitstaetigalsvorvorperiode")
    private boolean warbereitstaetigalsvorvorperiode = false;

    @Column(name = "bewerbungvom")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private java.time.LocalDate bewerbungvom;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "bemerkung")
    @Size(max = 255) private String bemerkung;

    @Column(name = "sperrentyp")
    @OrderColumn(name = "lfdnr")
    @CollectionTable(name = "auskunftssperre", joinColumns = { @JoinColumn(name = "personid") })
    @ElementCollection(fetch = FetchType.EAGER)
    @Size(max = 255) private java.util.List<String> auskunftssperre = new java.util.ArrayList<>();

    @Column(name = "person_attribut")
    @OrderColumn(name = "lfdnr")
    @CollectionTable(name = "konfliktfeld", joinColumns = { @JoinColumn(name = "personid") })
    @ElementCollection(fetch = FetchType.EAGER)
    @Size(max = 255) private java.util.List<String> konfliktfeld = new java.util.ArrayList<>();

    @Column(name = "staatsangehoerigkeit_text")
    @OrderColumn(name = "lfdnr")
    @CollectionTable(name = "staatsangehoerigkeit", joinColumns = { @JoinColumn(name = "personid") })
    @ElementCollection(fetch = FetchType.EAGER)
    @Size(max = 255) private java.util.List<String> staatsangehoerigkeit = new java.util.ArrayList<>();

    // because of EI_EXPOSE_REP
    public java.util.List<String> getAuskunftssperre() {
        if (auskunftssperre == null) {
            return Collections.emptyList();
        }
        return new java.util.ArrayList<>(auskunftssperre);
    }

    // because of EI_EXPOSE_REP
    public java.util.List<String> getKonfliktfeld() {
        if (konfliktfeld == null) {
            return Collections.emptyList();
        }
        return new java.util.ArrayList<>(konfliktfeld);
    }

    // because of EI_EXPOSE_REP
    public java.util.List<String> getStaatsangehoerigkeit() {
        if (staatsangehoerigkeit == null) {
            return Collections.emptyList();
        }
        return new java.util.ArrayList<>(staatsangehoerigkeit);
    }

}
