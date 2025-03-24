package de.muenchen.ehrenamtjustiz.eai.personeninfo.config;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "PMD.ShortClassName", "PMD.DataClass" })
public class User {
    private String username;
    private String password;
    private List<String> roles;

    public User(final String username, final String password, final List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = this.cloneRoleList(roles);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return this.cloneRoleList(this.roles);
    }

    public void setRoles(final List<String> roles) {
        this.roles = this.cloneRoleList(roles);
    }

    @Override
    public String toString() {
        return "User{username='" + this.username + "', roles=" + this.roles + "}";
    }

    private List<String> cloneRoleList(final Iterable<String> roleList) {
        final List<String> cloneList = new ArrayList<>();

        for (final String role : roleList) {
            cloneList.add(role);
        }

        return cloneList;
    }
}
