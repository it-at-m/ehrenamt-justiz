package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode()
@NoArgsConstructor
public class EhrenamtJustizStatusDto {

    private long anzahlBewerbungen;
    private long anzahlKonflikte;
    private long anzahlVorschlaege;
    private long anzahlVorschlaegeNeu;

}
