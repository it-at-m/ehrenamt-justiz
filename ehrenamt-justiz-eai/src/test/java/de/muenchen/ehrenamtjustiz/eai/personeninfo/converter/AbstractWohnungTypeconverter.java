package de.muenchen.ehrenamtjustiz.eai.personeninfo.converter;

import com.google.gson.*;
import de.muenchen.eai.ewo.api.fachlich.model.person.v2.*;

import java.lang.reflect.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractWohnungTypeconverter {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractWohnungTypeconverter.class);

    public static class Deserializer implements JsonDeserializer<AbstractWohnungType> {
        @Override
        public AbstractWohnungType deserialize(final JsonElement jsonElement, Type type,
                final JsonDeserializationContext jsonDeserializationContext) {
            try {
                final JsonObject jsonObject = jsonElement.getAsJsonObject();
                final JsonElement wohnungsType = jsonObject.get("@type");
                if (wohnungsType != null) {
                    switch (wohnungsType.getAsString()) {
                    case "ns2:AktuelleWohnungType":
                        return jsonDeserializationContext.deserialize(jsonObject,
                                AktuelleWohnungType.class);
                    case "ns2:InaktuelleWohnungType":
                        return jsonDeserializationContext.deserialize(jsonObject,
                                InaktuelleWohnungType.class);
                    case "ns2:ZuzugsWohnungType":
                        return jsonDeserializationContext.deserialize(jsonObject,
                                ZuzugsWohnungType.class);
                    case "ns2:WegzugsWohnungType":
                        return jsonDeserializationContext.deserialize(jsonObject,
                                WegzugsWohnungType.class);
                    default:
                        return null;
                    }
                }
                return null;

            } catch (Exception e) {
                LOG.error("Exception im Deserializer:", e);
                return null;
            }
        }
    }

}
