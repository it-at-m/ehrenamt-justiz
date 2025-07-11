package de.muenchen.ehrenamtjustiz.eai.personeninfo.converter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLGregorianCalendarConverter {

    private static final Logger LOG = LoggerFactory.getLogger(XMLGregorianCalendarConverter.class);

    public static class Serializer implements JsonSerializer<XMLGregorianCalendar> {
        @Override
        public JsonElement serialize(final XMLGregorianCalendar xmlGregorianCalendar, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(xmlGregorianCalendar.toXMLFormat());
        }
    }

    public static class Deserializer implements JsonDeserializer<XMLGregorianCalendar> {
        @Override
        public XMLGregorianCalendar deserialize(final JsonElement jsonElement, Type type,
                JsonDeserializationContext jsonDeserializationContext) {
            try {
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(jsonElement.getAsString());
            } catch (Exception e) {
                LOG.error("Exception im Deserializer:", e);
                return null;
            }
        }
    }
}
