package de.muenchen.ehrenamtjustiz.backend.service;

import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import java.util.List;

public interface EhrenamtJustizService {
    List<String> getKonflikte(Person person);
}
