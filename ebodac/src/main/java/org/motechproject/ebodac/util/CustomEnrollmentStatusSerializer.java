package org.motechproject.ebodac.util;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.motechproject.ebodac.domain.EnrollmentStatus;

import java.io.IOException;

/**
 * Serializer for EnrollmentStatus representation in UI
 */
public class CustomEnrollmentStatusSerializer extends JsonSerializer<EnrollmentStatus> {

    @Override
    public void serialize(EnrollmentStatus status, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(status.getValue());
    }
}
