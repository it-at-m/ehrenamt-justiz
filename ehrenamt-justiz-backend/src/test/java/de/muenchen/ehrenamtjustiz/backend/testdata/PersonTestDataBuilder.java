package de.muenchen.ehrenamtjustiz.backend.testdata;

import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Geschlecht;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Wohnungsstatus;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PersonTestDataBuilder {

    public static final String MUENCHEN = "München";

    final private Person person = new Person();

    public PersonTestDataBuilder() {

        person.setId(UUID.randomUUID());
        person.setStatus(Status.VORSCHLAG);
        person.setEwoid("4711");
        person.setFamilienname("Müller");
        person.setVorname("Hans");
        person.setGeburtsort(MUENCHEN);
        person.setGeburtsland("Deutschland");
        person.setGeburtsdatum(LocalDate.of(1980, 1, 1));
        person.setKonfigurationid(UUID.randomUUID());
        person.setWohnungsstatus(Wohnungsstatus.HAUPTWOHNUNG);
        person.setFamilienstand("VH");
        person.setPostleitzahl("80634");
        person.setOrt(MUENCHEN);
        person.setStrasse("Ludwigstr.");
        person.setHausnummer("7");
        person.setInmuenchenseit(LocalDate.of(2023, 1, 1));
        person.setGeschlecht(Geschlecht.MAENNLICH);
        person.setBewerbungvom(LocalDate.of(2024, 9, 17));
        person.setStaatsangehoerigkeit(Arrays.asList("englisch", "deutsch"));
    }

    public PersonTestDataBuilder withKonfigurationid(final UUID konfigurationid) {
        person.setKonfigurationid(konfigurationid);
        return this;
    }

    public PersonTestDataBuilder withAkademischergrad(final String akademischergrad) {
        person.setAkademischergrad(akademischergrad);
        return this;
    }

    public PersonTestDataBuilder withAppartmentnummer(final String appartmentnummer) {
        person.setAppartmentnummer(appartmentnummer);
        return this;
    }

    public PersonTestDataBuilder withBuchstabehausnummer(final String buchstabehausnummer) {
        person.setBuchstabehausnummer(buchstabehausnummer);
        return this;
    }

    public PersonTestDataBuilder withStockwerk(final String stockwerk) {
        person.setStockwerk(stockwerk);
        return this;
    }

    public PersonTestDataBuilder withTeilnummerhausnummer(final String teilnummerhausnummer) {
        person.setTeilnummerhausnummer(teilnummerhausnummer);
        return this;
    }

    public PersonTestDataBuilder withWohnungsgeber(final String wohnungsgeber) {
        person.setWohnungsgeber(wohnungsgeber);
        return this;
    }

    public PersonTestDataBuilder withAdresszusatz(final String adresszusatz) {
        person.setAdresszusatz(adresszusatz);
        return this;
    }

    public PersonTestDataBuilder withGeburtsname(final String geburtsname) {
        person.setGeburtsname(geburtsname);
        return this;
    }

    public PersonTestDataBuilder withAuskunftssperre(final List<String> auskunftssperre) {
        person.setAuskunftssperre(auskunftssperre);
        return this;
    }

    public Person build() {
        return person;
    }
}
