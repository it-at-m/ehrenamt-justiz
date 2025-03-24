package de.muenchen.ehrenamtjustiz.eai.personeninfo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.muenchen.ehrenamtjustiz.eai.personeninfo.util.Utility;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "api.auth",
        ignoreUnknownFields = true
)
@SuppressWarnings("PMD.DataClass")
public class ConfiguredWebSecProperties implements WebSecProperties {
    private List<User> users = new ArrayList<>();
    private Map<String, Set<String>> roles = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return Utility.cloneUserList(this.users);
    }

    public void setUsers(final Collection<User> users) {
        this.users = Utility.cloneUserList(users);
    }

    @Override
    public Map<String, Set<String>> getRoles() {
        return Utility.cloneRoleMap(this.roles);
    }

    public void setRoles(final Map<String, Set<String>> roles) {
        this.roles = Utility.cloneRoleMap(roles);
    }
}
