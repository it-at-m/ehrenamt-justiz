package de.muenchen.ehrenamtjustiz.eai.personeninfo.util;

import com.google.common.collect.Maps;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.config.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class is used to clone users and roles
 */
public final class Utility {
    private Utility() {
    }

    public static List<User> cloneUserList(final Collection<User> userList) {
        final List<User> cloneList = new ArrayList<>(userList.size());

        for (final User user : userList) {
            cloneList.add(new User(user.getUsername(), user.getPassword(), user.getRoles()));
        }

        return cloneList;
    }

    public static Map<String, Set<String>> cloneRoleMap(final Map<String, Set<String>> roleMap) {
        final Map<String, Set<String>> cloneMap = Maps.newHashMapWithExpectedSize(roleMap.size());

        for (final Map.Entry<String, Set<String>> entry : roleMap.entrySet()) {
            final Set<String> valueSet = new HashSet<>(entry.getValue());
            cloneMap.put(entry.getKey(), valueSet);
        }

        return cloneMap;
    }
}
