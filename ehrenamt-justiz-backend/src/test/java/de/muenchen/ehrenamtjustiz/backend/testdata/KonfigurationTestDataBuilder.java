package de.muenchen.ehrenamtjustiz.backend.testdata;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

public class KonfigurationTestDataBuilder {

    private final Konfiguration konfiguration = new Konfiguration();

    public KonfigurationTestDataBuilder() {
        konfiguration.setId(UUID.randomUUID());
        konfiguration.setAktiv(true);
        konfiguration.setEhrenamtjustizart(Ehrenamtjustizart.VERWALTUNGSRICHTER);
        konfiguration.setBezeichnung("Verwaltungsrichter");
        konfiguration.setAltervon(BigInteger.valueOf(25));
        konfiguration.setAlterbis(BigInteger.valueOf(120));
        konfiguration.setStaatsangehoerigkeit("deutsch");
        konfiguration.setWohnsitz("München");
        konfiguration.setAmtsperiodevon(LocalDate.of(2030, 1, 1));
        konfiguration.setAmtsperiodebis(LocalDate.of(2034, 12, 31));
    }

    public KonfigurationTestDataBuilder withAktiv(final boolean aktiv) {
        konfiguration.setAktiv(aktiv);
        return this;
    }

    public KonfigurationTestDataBuilder withBezeichnung(final String bezeichnung) {
        konfiguration.setBezeichnung(bezeichnung);
        return this;
    }

    public Konfiguration build() {
        return konfiguration;
    }

}
