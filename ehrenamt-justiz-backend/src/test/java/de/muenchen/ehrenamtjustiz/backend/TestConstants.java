package de.muenchen.ehrenamtjustiz.backend;

import de.muenchen.ehrenamtjustiz.backend.domain.enums.Ehrenamtjustizart;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({ "PMD.TestClassWithoutTestCases", "PMD.DataClass" })
public final class TestConstants {

    public static final String SPRING_TEST_PROFILE = "test";

    public static final String SPRING_NO_SECURITY_PROFILE = "no-security";

    public static final String TESTCONTAINERS_POSTGRES_IMAGE = "postgres:16.0-alpine3.18";

    @NoArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    public static class KonfigurationDto extends RepresentationModel {

        private Ehrenamtjustizart ehrenamtjustizart;
        private String bezeichnung;
        private boolean aktiv;
        private java.time.LocalDate amtsperiodevon;
        private java.time.LocalDate amtsperiodebis;
        private long altervon;
        private long alterbis;
        private String staatsangehoerigkeit;
        private String wohnsitz;

    }

}
