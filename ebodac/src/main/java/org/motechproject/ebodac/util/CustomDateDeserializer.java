package org.motechproject.ebodac.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Deserializer for LocalDate representation in UI
 */
public class CustomDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormat.forPattern("yyyy-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
        LocalDate date = null;
        String dateString = parser.getText();

        if (NumberUtils.isNumber(dateString)) {
            date = new LocalDate(Long.parseLong(dateString));
        } else if (StringUtils.isNotBlank(dateString)) {
            date = FORMATTER.parseLocalDate(dateString);
        }
        return date;
    }

}
