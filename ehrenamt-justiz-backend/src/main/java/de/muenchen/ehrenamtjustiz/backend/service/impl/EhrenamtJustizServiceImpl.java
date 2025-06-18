package de.muenchen.ehrenamtjustiz.backend.service.impl;

import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.EWOBuergerDatenDto;
import de.muenchen.ehrenamtjustiz.backend.service.EWOService;
import de.muenchen.ehrenamtjustiz.backend.service.EhrenamtJustizService;
import de.muenchen.ehrenamtjustiz.backend.utils.EWOBuergerComparer;
import de.muenchen.ehrenamtjustiz.backend.utils.EhrenamtJustizUtility;
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

    @Override
    public List<String> getKonflikte(final Person person) {

        final EWOBuergerDatenDto ewoBuerger = ewoService.ewoSucheMitOM(person.getEwoid());

        if (ewoBuerger == null) {
            log.info("Person {} führte in EWO zu keinem Treffer.", person.getEwoid());
            final List<String> konfliktfelder = new ArrayList<>();
            konfliktfelder.add(EhrenamtJustizUtility.ERROR_NO_HITS);
            // no EWO-entry
            return konfliktfelder;
        }

        final EWOBuergerDatenDto ewoBuergerFromperson = EhrenamtJustizUtility.getEwoBuergerDatenDto(person);

        return EWOBuergerComparer.getConflictFields(ewoBuergerFromperson, ewoBuerger);

    }

    @Override
    public List<String> getKonflikteAenderungsService(final Person person) {
        final EWOBuergerDatenDto ewoBuerger = ewoService.ewoSucheMitOMAenderungsService(person.getEwoid());

        if (ewoBuerger == null) {
            log.info("Person {} führte in EWO zu keinem Treffer.", person.getEwoid());
            return new ArrayList<>();
        }

        return EWOBuergerComparer.getConflictFields(EhrenamtJustizUtility.getEwoBuergerDatenDto(person), ewoBuerger);
    }

}
