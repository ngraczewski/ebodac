package org.motechproject.bookingapp.helper;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.DateFilter;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.api.Range;
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
            fieldsMap = getFieldsMap(settings.getFields());
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

    public static BookingGridSettings changeLookupForScreening(BookingGridSettings settings) throws IOException {
        Map<String, Object> fieldsMap = new HashMap<>();
        DateFilter dateFilter = settings.getDateFilter();

        if (dateFilter != null) {

            if (StringUtils.isBlank(settings.getFields())) {
                settings.setFields("{}");
            }

            if (StringUtils.isBlank(settings.getLookup())) {
                settings.setLookup("Find By Date");
            } else {
                fieldsMap = getFields(settings.getFields());
                settings.setLookup(settings.getLookup() + " And Date");
            }

            Map<String, String> rangeMap = new HashMap<>();
            if (DateFilter.DATE_RANGE.equals(dateFilter)) {
                rangeMap.put("min", settings.getStartDate());
                rangeMap.put("max", settings.getEndDate());
            } else {
                Range<LocalDate> dateRange = dateFilter.getRange();
                rangeMap.put("min", dateRange.getMin().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
                rangeMap.put("max", dateRange.getMax().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
            }

            fieldsMap.put(Screening.DATE_PROPERTY_NAME, rangeMap);
            settings.setFields(OBJECT_MAPPER.writeValueAsString(fieldsMap));
        }
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
            fieldsMap = getFieldsMap(settings.getFields());
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

    private static Map<String, Object> getFields(String lookupFields) throws IOException {
        return OBJECT_MAPPER.readValue(lookupFields, new TypeReference<HashMap>() {}); //NO CHECKSTYLE WhitespaceAround
    }

    private static Map<String, String> getFieldsMap(String lookupFields) throws IOException {
        return OBJECT_MAPPER.readValue(lookupFields, new TypeReference<HashMap>() {}); //NO CHECKSTYLE WhitespaceAround
    }
}
