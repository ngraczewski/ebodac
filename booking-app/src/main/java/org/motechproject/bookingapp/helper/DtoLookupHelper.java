package org.motechproject.bookingapp.helper;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class DtoLookupHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DtoLookupHelper() {
    }

    public static BookingGridSettings changeLookupForPrimeVaccinationSchedule(BookingGridSettings settings) throws IOException {
        Map<String, String> fieldsMap = new HashMap<>();

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }

        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find Visits By Type Date And Primer Vaccination Date");
        } else {
            String fields = settings.getFields();
            fieldsMap = OBJECT_MAPPER.readValue(fields, new TypeReference<HashMap>() {});
            settings.setLookup(settings.getLookup() + " Type Date And Primer Vaccination Date");
        }

        fieldsMap.put(Visit.VISIT_TYPE_PROPERTY_NAME, VisitType.SCREENING.toString());
        fieldsMap.put(Visit.ACTUAL_VISIT_DATE_PROPERTY_NAME, null);
        fieldsMap.put(Visit.SUBJECT_PRIME_VACCINATION_DATE_PROPERTY_NAME, null);
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }

    public static BookingGridSettings changeLookupForPrimeVaccinationScheduleExport(BookingGridSettings settings) throws IOException {
        Map<String, String> fieldsMap = new HashMap<>();

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }

        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find By Visit Type And Participant Prime Vaccination Date");
        } else {
            String fields = settings.getFields();
            fieldsMap = OBJECT_MAPPER.readValue(fields, new TypeReference<HashMap>() {});
            settings.setLookup(settings.getLookup() + " Visit Type And Participant Prime Vaccination Date");
        }

        fieldsMap.put(VisitBookingDetails.VISIT_TYPE_PROPERTY_NAME, VisitType.PRIME_VACCINATION_DAY.toString());
        fieldsMap.put(VisitBookingDetails.SUBJECT_PRIME_VACCINATION_DATE_PROPERTY_NAME, null);
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }
}
