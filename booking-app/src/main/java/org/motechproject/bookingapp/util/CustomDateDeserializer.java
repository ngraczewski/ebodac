package org.motechproject.bookingapp.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

public class CustomDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return LocalDate.parse(jp.getText(), FORMATTER);
    }
}
