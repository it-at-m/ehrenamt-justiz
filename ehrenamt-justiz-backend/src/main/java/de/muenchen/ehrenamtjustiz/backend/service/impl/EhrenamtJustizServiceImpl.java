package de.muenchen.ehrenamtjustiz.backend.service.impl;

import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.service.EWOService;
import de.muenchen.ehrenamtjustiz.backend.service.EhrenamtJustizService;
import de.muenchen.ehrenamtjustiz.backend.utils.EWOBuergerComparer;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@SuppressWarnings("PMD.CommentDefaultAccessModifier")
public class EhrenamtJustizServiceImpl implements EhrenamtJustizService {

    @Autowired
    EWOService ewoService;

    private static final String ERROR_NO_HITS = "Keine EWO-Daten gefunden. Evtl. verstorben oder verzogen?";

    @Override
    public List<String> getKonflikte(final Person person) {

        final EWOBuergerDatenDto ewoBuerger = ewoService.ewoSucheMitOM(person.getEwoid());

        if (ewoBuerger == null) {
            log.info("Person {} führte in EWO zu keinem Treffer.", person.getEwoid());
            final List<String> konfliktfelder = new ArrayList<>();
            konfliktfelder.add(ERROR_NO_HITS);
            // no EWO-entry
            return konfliktfelder;
        }

        final EWOBuergerDatenDto ewoBuergerFromperson = getEwoBuergerDatenDto(person);

        return EWOBuergerComparer.getConflictFields(ewoBuergerFromperson, ewoBuerger);

    }

    @Override
    public List<String> getKonflikteAenderungsService(final Person person) {
        final EWOBuergerDatenDto ewoBuerger = ewoService.ewoSucheMitOMAenderungsService(person.getEwoid());

        if (ewoBuerger == null) {
            log.info("Person {} führte in EWO zu keinem Treffer.", person.getEwoid());
            return new ArrayList<>();
        }

        return EWOBuergerComparer.getConflictFields(getEwoBuergerDatenDto(person), ewoBuerger);
    }

    public static EWOBuergerDatenDto getEwoBuergerDatenDto(final Person person) {
        return EWOBuergerDatenDto.builder()
                .familienname(person.getFamilienname())
                .geburtsname(person.getGeburtsname())
                .vorname(person.getVorname())
                .geburtsdatum(person.getGeburtsdatum())
                .geschlecht(person.getGeschlecht())
                .ordnungsmerkmal(person.getEwoid())
                .akademischergrad(person.getAkademischergrad())
                .geburtsort(person.getGeburtsort())
                .geburtsland(person.getGeburtsland())
                .familienstand(person.getFamilienstand())
                .staatsangehoerigkeit(person.getStaatsangehoerigkeit())
                .wohnungsgeber(person.getWohnungsgeber())
                .strasse(person.getStrasse())
                .hausnummer(person.getHausnummer())
                .appartmentnummer(person.getAppartmentnummer())
                .buchstabehausnummer(person.getBuchstabehausnummer())
                .stockwerk(person.getStockwerk())
                .teilnummerhausnummer(person.getTeilnummerhausnummer())
                .adresszusatz(person.getAdresszusatz())
                .konfliktfeld(person.getKonfliktfeld())
                .postleitzahl(person.getPostleitzahl())
                .ort(person.getOrt())
                .inmuenchenseit(person.getInmuenchenseit())
                .wohnungsstatus(person.getWohnungsstatus())
                .auskunftssperre(person.getAuskunftssperre()).build();
    }

}
