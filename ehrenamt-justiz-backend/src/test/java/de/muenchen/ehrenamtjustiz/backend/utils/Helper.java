package de.muenchen.ehrenamtjustiz.backend.utils;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class Helper {

    private Helper() {
    }

    public static @NotNull
    Konfiguration createKonfiguration() {
        final Konfiguration konfiguration = new Konfiguration();
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
        return konfiguration;
    }

}
