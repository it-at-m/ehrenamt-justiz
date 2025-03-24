package de.muenchen.ehrenamtjustiz.eai.personeninfo.bean;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import de.muenchen.eai.ewo.api.fachlich.model.person.v2.AbstractWohnungType;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.LesePersonErweitertResponse;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.SuchePersonErweitert;
import de.muenchen.eai.ewo.api.fachlich.service.erweitert.person.v2.SuchePersonErweitertResponse;
import de.muenchen.ehrenamtjustiz.api.BuergerSucheAnfrage;
import de.muenchen.ehrenamtjustiz.api.EWOBuerger;
import de.muenchen.ehrenamtjustiz.api.Geschlecht;
import de.muenchen.ehrenamtjustiz.api.Wohnungsstatus;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.converter.AbstractWohnungTypeconverter;
import de.muenchen.ehrenamtjustiz.eai.personeninfo.converter.XMLGregorianCalendarConverter;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.message.MessageContentsList;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MappingBeanTest {
    private final MappingBean unitUnderTest = new MappingBean();

    private static final String FILE_CHARSET = "UTF-8";

    @Test
    void fromEWOLesen() throws Exception {

        @SuppressWarnings("PMD.LooseCoupling")
        final MessageContentsList messageContentsList = new MessageContentsList();
        final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheMitOMResponse.json"),
                Charset.forName(FILE_CHARSET));
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Deserializer())
                .registerTypeAdapter(AbstractWohnungType.class, new AbstractWohnungTypeconverter.Deserializer())
                .create();
        final LesePersonErweitertResponse lesePersonErweitertResponse = gson.fromJson(jsonPersonErweitert, LesePersonErweitertResponse.class);
        messageContentsList.add(lesePersonErweitertResponse.getPersonErweitert());
        final EWOBuerger result = unitUnderTest.fromEWOLesen(messageContentsList);

        assertEquals("Wimtest-zwei-ohne-Pass", result.getFamilienname());
        assertEquals("162015039514", result.getOrdnungsmerkmal());
        assertEquals("1990-09-20", result.getGeburtsdatum().toString());
        assertEquals("Lagos", result.getGeburtsort());
        assertEquals("Deutschland", result.getGeburtsland());
        assertEquals(Geschlecht.MAENNLICH, result.getGeschlecht());
        assertEquals("LD", result.getFamilienstand());
        assertEquals("Franklin", result.getVorname());
        assertEquals("2020-04-09", result.getInMuenchenSeit().toString());
        assertEquals("nigerianisch", result.getStaatsangehoerigkeit().getFirst());
        assertEquals("Alexandrastr.", result.getStrasse());
        assertEquals("0", result.getHausnummer());
        assertEquals("München", result.getOrt());
        assertEquals("80538", result.getPostleitzahl());
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
    void toEWOSuche() {
        final BuergerSucheAnfrage buergerSucheAnfrage = BuergerSucheAnfrage.builder().familienname("Test1").vorname("Test2")
                .geburtsdatum(LocalDate.of(2000, 1, 1)).build();

        final SuchePersonErweitert.Anfrage result = unitUnderTest.toEWOSuche(buergerSucheAnfrage);

        assertEquals("Test1", result.getSuchkriterien().getFamilienname());
        assertEquals("Test2", result.getSuchkriterien().getVorname());
        assertEquals("2000-01-01", result.getSuchkriterien().getGeburtsdatum().getDatum());
    }

    @Test
    void fromEWOSuche() throws Exception {

        @SuppressWarnings("PMD.LooseCoupling")
        final MessageContentsList messageContentsList = new MessageContentsList();
        final String jsonPersonErweitert = FileUtils.readFileToString(new File("src/test/resources/testnachrichten/EWOSucheResponse.json"),
                Charset.forName(FILE_CHARSET));
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(XMLGregorianCalendar.class, new XMLGregorianCalendarConverter.Deserializer())
                .registerTypeAdapter(AbstractWohnungType.class, new AbstractWohnungTypeconverter.Deserializer())
                .create();
        final SuchePersonErweitertResponse suchePersonErweitertResponse = gson.fromJson(jsonPersonErweitert, SuchePersonErweitertResponse.class);
        messageContentsList.add(suchePersonErweitertResponse.getAntwortErweitert());
        final List<EWOBuerger> result = unitUnderTest.fromEWOSuche(messageContentsList);

        assertEquals("162015022725", result.getFirst().getOrdnungsmerkmal());
        assertEquals("WIM-GV Dreizehn neu", result.getFirst().getFamilienname());
        assertEquals("1960-01-01", result.getFirst().getGeburtsdatum().toString());
        assertEquals("Berlin", result.getFirst().getGeburtsort());
        assertEquals("Deutschland", result.getFirst().getGeburtsland());
        assertEquals(Geschlecht.WEIBLICH, result.getFirst().getGeschlecht());
        assertEquals("VH", result.getFirst().getFamilienstand());
        assertEquals("Frau", result.getFirst().getVorname());
        assertEquals("2000-01-01", result.getFirst().getInMuenchenSeit().toString());
        assertEquals("Widmannstr.", result.getFirst().getStrasse());
        assertEquals("16", result.getFirst().getHausnummer());
        assertEquals("München", result.getFirst().getOrt());
        assertEquals("81829", result.getFirst().getPostleitzahl());
        assertEquals("0", result.getFirst().getStockwerk());
        assertEquals(Wohnungsstatus.HAUPTWOHNUNG, result.getFirst().getWohnungsstatus());
        assertEquals("deutsch", result.getFirst().getStaatsangehoerigkeit().getFirst());
        assertNull(result.getFirst().getFamiliennameZusatz());
        assertNull(result.getFirst().getGeburtsname());
        assertNull(result.getFirst().getGeburtsnameZusatz());
        assertNull(result.getFirst().getAkademischerGrad());
        assertNull(result.getFirst().getWohnungsgeber());
        assertNull(result.getFirst().getAppartmentnummer());
        assertNull(result.getFirst().getBuchstabeHausnummer());
        assertNull(result.get(0).getTeilnummerHausnummer());
        assertNull(result.get(0).getZusatz());

        assertEquals("162015039514", result.get(1).getOrdnungsmerkmal());
        assertEquals("Wimtest-zwei-ohne-Pass", result.get(1).getFamilienname());
        assertEquals("1990-09-20", result.get(1).getGeburtsdatum().toString());
        assertEquals("Lagos", result.get(1).getGeburtsort());
        assertEquals("Deutschland", result.get(1).getGeburtsland());
        assertEquals(Geschlecht.MAENNLICH, result.get(1).getGeschlecht());
        assertEquals("LD", result.get(1).getFamilienstand());
        assertEquals("Franklin", result.get(1).getVorname());
        assertEquals("2020-04-09", result.get(1).getInMuenchenSeit().toString());
        assertEquals("Alexandrastr.", result.get(1).getStrasse());
        assertEquals("0", result.get(1).getHausnummer());
        assertEquals("München", result.get(1).getOrt());
        assertEquals("80538", result.get(1).getPostleitzahl());
        assertEquals(Wohnungsstatus.HAUPTWOHNUNG, result.get(1).getWohnungsstatus());
        assertEquals("nigerianisch", result.get(1).getStaatsangehoerigkeit().getFirst());
        assertNull(result.get(1).getFamiliennameZusatz());
        assertNull(result.get(1).getGeburtsname());
        assertNull(result.get(1).getGeburtsnameZusatz());
        assertNull(result.get(1).getAkademischerGrad());
        assertNull(result.get(1).getWohnungsgeber());
        assertNull(result.get(1).getAppartmentnummer());
        assertNull(result.get(1).getBuchstabeHausnummer());
        assertNull(result.get(1).getStockwerk());
        assertNull(result.get(1).getTeilnummerHausnummer());
        assertNull(result.get(1).getZusatz());
    }
}
