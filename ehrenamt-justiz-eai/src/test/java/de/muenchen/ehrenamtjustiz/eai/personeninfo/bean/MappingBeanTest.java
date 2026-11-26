package de.muenchen.ehrenamtjustiz.eai.personeninfo.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import de.muenchen.ehrenamtjustiz.api.BuergerSucheAnfrage;
import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.api.Geschlecht;
import de.muenchen.ehrenamtjustiz.api.Wohnungsstatus;
import de.muenchen.ewo.generated.PersonErweitert;
import de.muenchen.ewo.generated.SuchePersonerweitertAnfrage;
import de.muenchen.ewo.generated.SuchePersonerweitertAntwort;
import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

class MappingBeanTest {
    private final MappingBean unitUnderTest = new MappingBean();

    private static final String FILE_CHARSET = "UTF-8";

    @Test
    void givenEwoSucheMitOMResponse_thenMappingOK() throws Exception {

        @SuppressWarnings("PMD.LooseCoupling")
        final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheMitOMResponse.json"),
                Charset.forName(FILE_CHARSET));
        ObjectMapper objectMapper = new JsonMapper();
        final PersonErweitert lesePersonErweitertResponse = objectMapper.readValue(jsonPersonErweitert, PersonErweitert.class);
        final EWOBuerger result = unitUnderTest.fromEWOLesen(lesePersonErweitertResponse);

        assertEquals("König", result.getFamilienname());
        assertEquals("1", result.getOrdnungsmerkmal());
        assertEquals("1940-01-03", result.getGeburtsdatum().toString());
        assertEquals("München", result.getGeburtsort());
        assertEquals("Deutschland", result.getGeburtsland());
        assertEquals(Geschlecht.MAENNLICH, result.getGeschlecht());
        assertEquals("VH", result.getFamilienstand());
        assertEquals("Christopher Manfred", result.getVorname());
        assertEquals("1940-01-03", result.getInMuenchenSeit().toString());
        assertEquals("deutsch", result.getStaatsangehoerigkeit().getFirst());
        assertEquals("Taulerstr.", result.getStrasse());
        assertEquals("10", result.getHausnummer());
        assertEquals("München", result.getOrt());
        assertEquals("80906", result.getPostleitzahl());
        assertEquals(Wohnungsstatus.HAUPTWOHNUNG, result.getWohnungsstatus());
        assertNull(result.getFamiliennameZusatz());
        assertNull(result.getGeburtsname());
        assertNull(result.getGeburtsnameZusatz());
        assertNull(result.getAkademischerGrad());
        assertNull(result.getWohnungsgeber());
        assertNull(result.getAppartmentnummer());
        assertNull(result.getBuchstabeHausnummer());
        assertNull(result.getStockwerk());
        assertNull(result.getTeilnummerHausnummer());
        assertNull(result.getZusatz());

    }

    @Test
    @SuppressWarnings("PMD.LinguisticNaming")
    void givenEwoDataRequest_thenMappingOK() {
        final BuergerSucheAnfrage buergerSucheAnfrage = BuergerSucheAnfrage.builder().familienname("Test1").vorname("Test2")
                .geburtsdatum(LocalDate.of(2000, 1, 1)).build();

        final SuchePersonerweitertAnfrage result = unitUnderTest.toEWOSuche(buergerSucheAnfrage);

        assertEquals("Test1", result.getSuchkriterien().getFamilienname());
        assertEquals("Test2", result.getSuchkriterien().getVorname());
        assertEquals("2000-01-01", result.getSuchkriterien().getGeburtsdatum().getDatum());
    }

    @Test
    void givenEwoSucheResponse_thenMappingOK() throws Exception {

        @SuppressWarnings("PMD.LooseCoupling")
        final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheResponse.json"),
                Charset.forName(FILE_CHARSET));

        ObjectMapper objectMapper = new JsonMapper();

        final SuchePersonerweitertAntwort suchePersonErweitertResponse = objectMapper.readValue(jsonPersonErweitert, SuchePersonerweitertAntwort.class);
        final List<EWOBuerger> result = unitUnderTest.fromEWOSuche(suchePersonErweitertResponse);

        assertEquals("König", result.getFirst().getFamilienname());
        assertEquals("1", result.getFirst().getOrdnungsmerkmal());
        assertEquals("1940-01-03", result.getFirst().getGeburtsdatum().toString());
        assertEquals("München", result.getFirst().getGeburtsort());
        assertEquals("Deutschland", result.getFirst().getGeburtsland());
        assertEquals(Geschlecht.MAENNLICH, result.getFirst().getGeschlecht());
        assertEquals("VH", result.getFirst().getFamilienstand());
        assertEquals("Christopher Manfred", result.getFirst().getVorname());
        assertEquals("1940-01-03", result.getFirst().getInMuenchenSeit().toString());
        assertEquals("deutsch", result.getFirst().getStaatsangehoerigkeit().getFirst());
        assertEquals("Taulerstr.", result.getFirst().getStrasse());
        assertEquals("10", result.getFirst().getHausnummer());
        assertEquals("München", result.getFirst().getOrt());
        assertEquals("80906", result.getFirst().getPostleitzahl());
        assertEquals(Wohnungsstatus.HAUPTWOHNUNG, result.getFirst().getWohnungsstatus());
        assertEquals("deutsch", result.getFirst().getStaatsangehoerigkeit().getFirst());
        assertNull(result.getFirst().getFamiliennameZusatz());
        assertNull(result.getFirst().getGeburtsname());
        assertNull(result.getFirst().getGeburtsnameZusatz());
        assertNull(result.getFirst().getAkademischerGrad());
        assertNull(result.getFirst().getWohnungsgeber());
        assertNull(result.getFirst().getAppartmentnummer());
        assertNull(result.getFirst().getBuchstabeHausnummer());
        assertNull(result.getFirst().getTeilnummerHausnummer());
        assertNull(result.getFirst().getZusatz());

    }
}
