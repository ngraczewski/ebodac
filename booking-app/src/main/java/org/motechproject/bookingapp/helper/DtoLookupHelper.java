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

    private static final String NOT_BLANK_REGEX = ".";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DtoLookupHelper() {
    }

    public static BookingGridSettings changeLookupForPrimeVaccinationSchedule(BookingGridSettings settings) throws IOException {
        Map<String, String> fieldsMap = new HashMap<>();

        if (StringUtils.isBlank(settings.getFields())) {
            settings.setFields("{}");
        }

        if (StringUtils.isBlank(settings.getLookup())) {
            settings.setLookup("Find Visits By Type Date Primer Vaccination Date And Participant Name");
            fieldsMap.put(Visit.SUBJECT_NAME_PROPERTY_NAME, NOT_BLANK_REGEX);
        } else {
            String fields = settings.getFields();
            fieldsMap = OBJECT_MAPPER.readValue(fields, new TypeReference<HashMap>() {});
            if (settings.getLookup().equals("Find Visits By Participant Name")) {
                settings.setLookup(settings.getLookup() + " Type Date And Primer Vaccination Date");
            } else {
                settings.setLookup(settings.getLookup() + " Type Date Primer Vaccination Date And Participant Name");
                fieldsMap.put(Visit.SUBJECT_NAME_PROPERTY_NAME, NOT_BLANK_REGEX);
            }
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
            settings.setLookup("Find By Visit Type And Participant Prime Vaccination Date And Name");
            fieldsMap.put(VisitBookingDetails.SUBJECT_NAME_PROPERTY_NAME, NOT_BLANK_REGEX);
        } else {
            String fields = settings.getFields();
            fieldsMap = OBJECT_MAPPER.readValue(fields, new TypeReference<HashMap>() {});
            if (settings.getLookup().equals("Find Visits By Participant Name")) {
                settings.setLookup(settings.getLookup() + " Visit Type And Participant Prime Vaccination Date");
            } else {
                settings.setLookup(settings.getLookup() + " Visit Type And Participant Prime Vaccination Date And Name");
                fieldsMap.put(Visit.SUBJECT_NAME_PROPERTY_NAME, NOT_BLANK_REGEX);
            }
        }

        fieldsMap.put(VisitBookingDetails.VISIT_TYPE_PROPERTY_NAME, VisitType.PRIME_VACCINATION_DAY.toString());
        fieldsMap.put(VisitBookingDetails.SUBJECT_PRIME_VACCINATION_DATE_PROPERTY_NAME, null);
        settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        return settings;
    }
}
