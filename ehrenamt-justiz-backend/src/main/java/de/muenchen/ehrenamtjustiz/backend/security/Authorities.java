package de.muenchen.ehrenamtjustiz.backend.security;

/// Each possible authority in this project is represented by a constant in this class.
///
/// The constants are used within the [org.springframework.stereotype.Controller] or
/// [org.springframework.stereotype.Service] classes in the method security annotations (e.g.
/// [org.springframework.security.access.prepost.PreAuthorize]).
@SuppressWarnings("PMD.DataClass")
public final class Authorities {
    public static final String HAS_AUTHORITY_READ_KONFIGURATION = "hasAuthority('READ_KONFIGURATION')";
    public static final String HAS_AUTHORITY_WRITE_KONFIGURATION = "hasAuthority('WRITE_KONFIGURATION')";
    public static final String HAS_AUTHORITY_DELETE_KONFIGURATION = "hasAuthority('DELETE_KONFIGURATION')";
    public static final String HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN = "hasAuthority('READ_EHRENAMTJUSTIZDATEN')";
    public static final String HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN = "hasAuthority('WRITE_EHRENAMTJUSTIZDATEN')";
    public static final String HAS_AUTHORITY_DELETE_EHRENAMTJUSTIZDATEN = "hasAuthority('DELETE_EHRENAMTJUSTIZDATEN')";
    public static final String HAS_AUTHORITY_EWOSUCHE = "hasAuthority('EWOSUCHE')";
    public static final String HAS_AUTHORITY_EWOSUCHEMITOM = "hasAuthority('EWOSUCHEMITOM')";
    public static final String HAS_AUTHORITY_ONLINEBEWERBEN = "hasAuthority('ONLINEBEWERBEN')";

    private Authorities() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
