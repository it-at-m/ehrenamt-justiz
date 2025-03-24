package de.muenchen.ehrenamtjustiz.eai.personeninfo.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class is used to store users and roles
 */
public interface WebSecProperties {
    List<User> getUsers();

    Map<String, Set<String>> getRoles();
}
