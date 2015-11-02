package org.motechproject.ebodac.util;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.motechproject.ebodac.domain.Subject;

import java.io.IOException;

public class CustomSubjectSerializer extends JsonSerializer<Subject> {

    @Override
    public void serialize(Subject subject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if (subject.getVisits() != null) {
            subject.setVisits(null);
        }
        jsonGenerator.writeObject(subject);
    }
}
