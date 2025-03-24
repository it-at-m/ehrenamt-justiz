package de.muenchen.ehrenamtjustiz.backend.rest.controller;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonCSVDto;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonenTableDatenDto;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.rest.PersonRepository;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import de.muenchen.ehrenamtjustiz.backend.service.EhrenamtJustizService;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@AllArgsConstructor
@PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
@Slf4j
@RequestMapping("/personen")
@SuppressWarnings(
    { "PMD.UseVarargs", "PMD.CommentDefaultAccessModifier", "PMD.CouplingBetweenObjects",
            "PMD.CyclomaticComplexity" }
)
public class PersonRestController {

    public static final String JA = "Ja";
    public static final String NEIN = "Nein";
    public static final String REQUEST_UUIDS = "uuids";
    public static final String REQUEST_STATUS = "status";

    @Autowired
    KonfigurationRepository konfigurationRepository;

    @Autowired
    private final PersonRepository personRepository;

    @Autowired
    private final EhrenamtJustizService ehrenamtJustizService;

    @GetMapping(value = "/findPersonen", produces = { MediaType.APPLICATION_JSON_VALUE })
    Page<PersonenTableDatenDto> findPersonen(@RequestParam(name = "search", defaultValue = "*") final String search,
            @RequestParam(name = "sortby", defaultValue = "") final String sortby,
            @RequestParam(name = "page", defaultValue = "0") final int page,
            @RequestParam(name = "size", defaultValue = "20") final int size,
            @RequestParam(name = "status") final Status status) {

        String searchFilter = search;
        int sizeFilter = size;
        log.info("findPersonen: Suchstring: {}, Seite: {}, Seitengröße: {}, Status: {}, Sortiert nach: {}", searchFilter, page, sizeFilter, status, sortby);

        if (sizeFilter < 0) {
            // get all
            sizeFilter = Integer.MAX_VALUE;
        }

        final Pageable pageWithGivenElements;
        if (sortby.isEmpty()) {
            // without sorting
            pageWithGivenElements = PageRequest.of(page, sizeFilter);

        } else {
            // with sorting
            final String[] sortDefinition = sortby.split(",");
            final String[] sorts = new String[sortDefinition.length];
            String direction = "false";
            for (int i = 0; i < sortDefinition.length; i++) {
                final val sortAtr = sortDefinition[i].split("/");
                sorts[i] = sortAtr[0];
                direction = sortAtr[1];
            }

            log.info("findPersonen: Sorting: Sortierrichtung {}, Sortierfelder: {}", direction, Arrays.toString(sorts));

            pageWithGivenElements = PageRequest.of(page, sizeFilter, Sort.Direction.fromString(direction), sorts);

        }

        if (searchFilter == null || searchFilter.isEmpty() || "null".equals(searchFilter)) {
            // get all
            searchFilter = "*";
        }

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);

        final Page<Person> personBySearch = personRepository.findPersonBySearch(searchFilter, status, konfiguration[0].getId(), pageWithGivenElements);

        log.info("findPersonen: TotalPages(): {}, TotalElements: {}, PageNumber: {}, PageSize: {} ", personBySearch.getTotalPages(),
                personBySearch.getTotalElements(), personBySearch.getPageable().getPageNumber(),
                personBySearch.getPageable().getPageSize());

        return personBySearch.map(entity -> {
            final PersonenTableDatenDto personenTableDatenDto = new PersonenTableDatenDto();
            personenTableDatenDto.setId(entity.getId());
            personenTableDatenDto.setFamilienname(entity.getFamilienname());
            personenTableDatenDto.setVorname(entity.getVorname());
            personenTableDatenDto.setGeburtsdatum(entity.getGeburtsdatum());
            personenTableDatenDto.setKonfliktfeld(entity.getKonfliktfeld());
            personenTableDatenDto.setAuskunftssperre(entity.getAuskunftssperre());
            personenTableDatenDto.setDerzeitausgeuebterberuf(entity.getDerzeitausgeuebterberuf());
            personenTableDatenDto.setArbeitgeber(entity.getArbeitgeber());
            personenTableDatenDto.setMailadresse(entity.getMailadresse());
            personenTableDatenDto.setAusgeuebteehrenaemter(entity.getAusgeuebteehrenaemter());
            personenTableDatenDto.setStatus(entity.getStatus());

            return personenTableDatenDto;
        });
    }

    @PostMapping(value = "/deletePersonen", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ResponseEntity<?> deletePersonen(@RequestBody final UUID[] uuids) {

        log.info("deletePersonen aufgerufen für {} IDs", uuids.length);

        int geloeschtePersonen = 0;

        final List<Person> personsToDelete = new ArrayList<>();
        for (final UUID uuid : uuids) {

            final Optional<Person> person = personRepository.findById(uuid);
            person.ifPresent(personsToDelete::add);

            if (personsToDelete.size() == 100) {
                // Size of transaction is 100
                personRepository.deleteAll(personsToDelete);
                geloeschtePersonen += personsToDelete.size();
                personsToDelete.clear();
            }
        }

        if (!personsToDelete.isEmpty()) {
            personRepository.deleteAll(personsToDelete);
            geloeschtePersonen += personsToDelete.size();
            personsToDelete.clear();
        }

        log.info("deletePersonen aufgerufen und {} Personen gelöscht", geloeschtePersonen);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/updatePerson", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    public ResponseEntity<Person> updatePerson(@RequestBody final Person person) {

        // get conflicts
        if (person.getStatus() != Status.INERFASSUNG && person.getStatus() != Status.BEWERBUNG) {
            ermittelnKonflikte(person);
        }

        if (person.getStatus() == Status.INERFASSUNG) {
            person.setStatus(Status.BEWERBUNG);
        }

        // Person U P D A T E
        personRepository.save(person);

        /*
         * for (int i = 0; i < 1000; i++) {
         * int leftLimit = 97; // letter 'a'
         * int rightLimit = 122; // letter 'z'
         * int targetStringLength = 10;
         * Random random = new Random();
         *
         * String generatedString2 = random.ints(leftLimit, rightLimit + 1)
         * .limit(targetStringLength)
         * .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
         * .toString();
         * person.setFamilienname(generatedString2);
         *
         * for (int j = 0; j < 10; j++) {
         *
         * String generatedString = random.ints(leftLimit, rightLimit + 1)
         * .limit(targetStringLength)
         * .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
         * .toString();
         * person.setVorname(generatedString);
         * byte[] array2 = new byte[7]; // length is bounded by 7
         * new Random().nextBytes(array2);
         *
         * person.setId(UUID.randomUUID());
         * person.setEwoid(String.valueOf(i + j * 1000 + 3973));
         * personRepository.save(person);
         * }
         *
         * }
         */

        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @PostMapping(value = "/cancelBewerbung", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    public ResponseEntity<Person> cancelBewerbung(@RequestBody final Person person) {

        if (person.getStatus() == Status.INERFASSUNG) {
            // delete person, because inserting was interrupted
            personRepository.deleteInErfassung(person.getId());
        }

        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @PostMapping(value = "/lesenPersonenCSV", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    public ResponseEntity<List<PersonCSVDto>> lesenPersonenCSV(@RequestBody final Map<String, Object> body) {

        final List<String> uuids = (List<String>) body.get(REQUEST_UUIDS);
        final String status = (String) body.get(REQUEST_STATUS);

        final List<PersonCSVDto> persons;
        if (uuids.isEmpty()) {
            // get persons with specified status
            persons = getAllPersonCSV(status);
        } else {
            // get person by UUID
            persons = getPersonCSVbyUUID(uuids);
        }

        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    private List<PersonCSVDto> getPersonCSVbyUUID(final List<String> uuids) {
        final List<PersonCSVDto> persons = new ArrayList<>();

        for (final String uuid : uuids) {

            final Optional<Person> person = personRepository.findById(UUID.fromString(uuid));
            if (person.isPresent()) {
                final PersonCSVDto personCSV = mapPerson2PersonCSV(person.get());
                persons.add(personCSV);
            }
        }
        return persons;
    }

    private List<PersonCSVDto> getAllPersonCSV(final String status) {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);

        final List<PersonCSVDto> personsCSV = new ArrayList<>();
        final List<Person> personen = personRepository.findPersonByStatus(Status.valueOf(status), konfiguration[0].getId());

        for (final Person person : personen) {
            final PersonCSVDto personCSV = mapPerson2PersonCSV(person);
            personsCSV.add(personCSV);
        }

        return personsCSV;
    }

    @SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity" })
    private PersonCSVDto mapPerson2PersonCSV(final Person person) {

        final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        return PersonCSVDto.builder()
                .id(person.getId() == null ? "" : person.getId().toString())
                .familienname(person.getFamilienname() == null ? "" : person.getFamilienname())
                .geburtsname(person.getGeburtsname() == null ? "" : person.getGeburtsname())
                .vorname(person.getVorname() == null ? "" : person.getVorname())
                .geburtsdatum(person.getGeburtsdatum() == null ? "" : person.getGeburtsdatum().format(pattern))
                .geschlecht(person.getGeschlecht() == null ? "" : person.getGeschlecht().toString())
                .ewoid(person.getEwoid() == null ? "" : person.getEwoid())
                .akademischergrad(person.getAkademischergrad() == null ? "" : person.getAkademischergrad())
                .geburtsort(person.getGeburtsort() == null ? "" : person.getGeburtsort())
                .geburtsland(person.getGeburtsland() == null ? "" : person.getGeburtsland())
                .familienstand(person.getFamilienstand() == null ? "" : person.getFamilienstand())
                .staatsangehoerigkeit(person.getStaatsangehoerigkeit() == null || person.getStaatsangehoerigkeit().isEmpty() ? ""
                        : person.getStaatsangehoerigkeit().toString())
                .wohnungsgeber(person.getWohnungsgeber() == null ? "" : person.getWohnungsgeber())
                .strasse(person.getStrasse() == null ? "" : person.getStrasse())
                .hausnummer(person.getHausnummer() == null ? "" : person.getHausnummer())
                .appartmentnummer(person.getAppartmentnummer() == null ? "" : person.getAppartmentnummer())
                .buchstabehausnummer(person.getBuchstabehausnummer() == null ? "" : person.getBuchstabehausnummer())
                .stockwerk(person.getStockwerk() == null ? "" : person.getStockwerk())
                .teilnummerhausnummer(person.getTeilnummerhausnummer() == null ? "" : person.getTeilnummerhausnummer())
                .adresszusatz(person.getAdresszusatz() == null ? "" : person.getAdresszusatz())
                .konfliktfeld(person.getKonfliktfeld() == null || person.getKonfliktfeld().isEmpty() ? "" : person.getKonfliktfeld().toString())
                .postleitzahl(person.getPostleitzahl() == null ? "" : person.getPostleitzahl())
                .ort(person.getOrt() == null ? "" : person.getOrt())
                .inmuenchenseit(person.getInmuenchenseit() == null ? "" : person.getInmuenchenseit().format(pattern))
                .wohnungsstatus(person.getWohnungsstatus() == null ? "" : person.getWohnungsstatus().toString())
                .auskunftssperre(person.getAuskunftssperre() == null || person.getAuskunftssperre().isEmpty() ? "" : person.getAuskunftssperre().toString())
                .derzeitausgeuebterberuf(person.getDerzeitausgeuebterberuf() == null ? "" : person.getDerzeitausgeuebterberuf())
                .arbeitgeber(person.getArbeitgeber() == null ? "" : person.getArbeitgeber())
                .telefonnummer(person.getTelefonnummer() == null ? "" : person.getTelefonnummer())
                .telefongesch(person.getTelefongesch() == null ? "" : person.getTelefongesch())
                .telefonmobil(person.getTelefonmobil() == null ? "" : person.getTelefonmobil())
                .mailadresse(person.getMailadresse() == null ? "" : person.getMailadresse())
                .ausgeuebteehrenaemter(person.getAusgeuebteehrenaemter() == null ? "" : person.getAusgeuebteehrenaemter())
                .onlinebewerbung(person.isOnlinebewerbung() ? JA : NEIN)
                .neuervorschlag(person.isNeuervorschlag() ? JA : NEIN)
                .warbereitstaetigals(person.isWarbereitstaetigals() ? JA : NEIN)
                .bemerkung(person.getBemerkung() == null ? "" : person.getBemerkung())
                .bewerbungvom(person.getBewerbungvom() == null ? "" : person.getBewerbungvom().format(pattern))
                .status(person.getStatus() == null ? "" : person.getStatus().toString())
                .konfigurationid(person.getKonfigurationid() == null ? "" : person.getKonfigurationid().toString())
                .build();
    }

    @PostMapping(value = "/validiereAufVorschlagslisteSetzen", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    public ResponseEntity<List<Person>> validiereAufVorschlagslisteSetzen(@RequestBody final UUID[] uuids) {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);

        final List<Person> personsNotValide = new ArrayList<>();
        for (final UUID uuid : uuids) {

            final Optional<Person> person = personRepository.findById(uuid);
            if (person.isPresent()) {
                // check birthday
                String response = EhrenamtJustizUtility.validateGueltigesGeburtsdatum(konfiguration[0], person.get().getGeburtsdatum());
                if (!response.isEmpty()) {
                    log.info("Person {}, {}: ungültiges Geburtsdatum: {} mit Fehlermeldung {}.", person.get().getVorname(), person.get().getFamilienname(),
                            person.get().getGeburtsdatum(), response);
                    personsNotValide.add(person.get());
                    continue;
                }

                // check nationality
                response = EhrenamtJustizUtility.validateGueltigeStaatsangehoerigkeit(konfiguration[0], person.get().getStaatsangehoerigkeit());
                if (!response.isEmpty()) {
                    log.info("Person {}, {}: ungültige Staatsangehörigkeit: {} mit Fehlermeldung {}.", person.get().getVorname(),
                            person.get().getFamilienname(), person.get().getStaatsangehoerigkeit().toString(), response);
                    personsNotValide.add(person.get());
                    continue;
                }

                // check residence
                response = EhrenamtJustizUtility.validateGueltigerWohnsitz(konfiguration[0], person.get().getOrt());
                if (!response.isEmpty()) {
                    log.info("Person {}, {}: ungültiger Wohnsitz: {} mit Fehlermeldung {}.", person.get().getVorname(), person.get().getFamilienname(),
                            person.get().getOrt(), response);
                    personsNotValide.add(person.get());
                }
            }

        }

        log.info("validiereAufVorschlagslisteSetzen aufgerufen und {} invalide Personen ermittelt", personsNotValide.size());

        return new ResponseEntity<>(personsNotValide, HttpStatus.OK);

    }

    @PostMapping(value = "/aufVorschlagslisteSetzen", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ResponseEntity<List<Person>> aufVorschlagslisteSetzen(@RequestBody final UUID[] uuids) {

        log.info("aufVorschlagslisteSetzen aufgerufen für {} IDs", uuids.length);

        int aufVorschlagslisteGesetzt = 0;

        final List<Person> personsAufVorschlagslisteZuSetzen = new ArrayList<>();
        for (final UUID uuid : uuids) {

            final Optional<Person> person = personRepository.findById(uuid);
            if (person.isPresent()) {

                // get conflicts
                ermittelnKonflikte(person.get());

                personsAufVorschlagslisteZuSetzen.add(person.get());
            }

            if (personsAufVorschlagslisteZuSetzen.size() == 100) {
                // Size of transaction 100
                personRepository.saveAll(personsAufVorschlagslisteZuSetzen);
                aufVorschlagslisteGesetzt += personsAufVorschlagslisteZuSetzen.size();

                personsAufVorschlagslisteZuSetzen.clear();
            }
        }

        if (!personsAufVorschlagslisteZuSetzen.isEmpty()) {
            personRepository.saveAll(personsAufVorschlagslisteZuSetzen);
            aufVorschlagslisteGesetzt += personsAufVorschlagslisteZuSetzen.size();
            personsAufVorschlagslisteZuSetzen.clear();
        }

        log.info("aufVorschlagslisteSetzen aufgerufen und {} Person(en) geändert!", aufVorschlagslisteGesetzt);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/aufBewerberlisteSetzen", consumes = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public ResponseEntity<List<Person>> aufBewerberlisteSetzen(@RequestBody final UUID[] uuids) {

        log.info("aufBewerberlisteSetzen aufgerufen für {} IDs", uuids.length);

        int aufBewerberlisteGesetzt = 0;

        final List<Person> personsAufBewerberlisteZuSetzen = new ArrayList<>();
        for (final UUID uuid : uuids) {

            final Optional<Person> person = personRepository.findById(uuid);
            if (person.isPresent()) {

                // get conflicts
                ermittelnKonflikte(person.get());
                person.get().setStatus(Status.BEWERBUNG);
                person.get().setNeuervorschlag(false);

                personsAufBewerberlisteZuSetzen.add(person.get());
            }

            if (personsAufBewerberlisteZuSetzen.size() == 100) {
                // Size of transaction 100
                personRepository.saveAll(personsAufBewerberlisteZuSetzen);
                aufBewerberlisteGesetzt += personsAufBewerberlisteZuSetzen.size();

                personsAufBewerberlisteZuSetzen.clear();
            }
        }

        if (!personsAufBewerberlisteZuSetzen.isEmpty()) {
            personRepository.saveAll(personsAufBewerberlisteZuSetzen);
            aufBewerberlisteGesetzt += personsAufBewerberlisteZuSetzen.size();
            personsAufBewerberlisteZuSetzen.clear();
        }

        log.info("aufBewerberlisteSetzen aufgerufen und {} Person(en) geändert!", aufBewerberlisteGesetzt);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void ermittelnKonflikte(final Person person) {

        // Get the conflicts
        final List<String> konflikte = ehrenamtJustizService.getKonflikte(person);

        person.setKonfliktfeld(konflikte);

        if (person.getKonfliktfeld().isEmpty()) {
            log.info("Person mit om {} hat keine Konflikte!", person.getEwoid());
            person.setStatus(Status.VORSCHLAG);
            person.setNeuervorschlag(true);
        } else {
            log.info("Person mit om {} hat Konflikte!", person.getEwoid());
            person.setStatus(Status.KONFLIKT);
        }

    }

    @GetMapping(value = "/getNeueVorschlaege", produces = { MediaType.APPLICATION_JSON_VALUE })
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    public ResponseEntity<List<PersonCSVDto>> getNeueVorschlaege() {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        final Person[] personen = personRepository.getNeueVorschlaege(konfiguration[0].getId());

        final List<PersonCSVDto> personsCSV = new ArrayList<>();

        for (final Person person : personen) {
            final PersonCSVDto personCSV = mapPerson2PersonCSV(person);
            personsCSV.add(personCSV);
        }

        return new ResponseEntity<>(personsCSV, HttpStatus.OK);
    }

    @PostMapping("/alsBenachrichtigtMarkieren")
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    public ResponseEntity<?> alsBenachrichtigtMarkieren() {

        final Konfiguration[] konfiguration = konfigurationRepository.findByAktiv(true);
        personRepository.alsBenachrichtigtMarkieren(konfiguration[0].getId());

        return new ResponseEntity<>(HttpStatus.OK);

    }

}
