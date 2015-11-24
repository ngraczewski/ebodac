package org.motechproject.bookingapp.constants;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BookingAppConstants {

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

    public static final String PDF_EXPORT_FORMAT="pdf";
    public static final String CSV_EXPORT_FORMAT="csv";
    public static final String XLS_EXPORT_FORMAT="xls";

    public static final String SCREENING_NAME = "Screening";
    public static final String PRIME_VACCINATION_SCHEDULE_NAME = "Prime Vaccination Schedule";

    public static final Map<String, String> SCREENING_FIELDS_MAP = new LinkedHashMap<String, String>() {
        {
            put("Booking Id",       "volunteer.id");
            put("Volunteer Name",   "volunteer.name");
            put("Site Id",          "site.siteId");
            put("Clinic",           "clinic.location");
            put("Screening Date",   "date");
            put("Start Time",       "startTime");
            put("End Time",         "endTime");
        }
    };

    public static final Map<String, String> PRIME_VACCINATION_SCHEDULE_FIELDS_MAP = new LinkedHashMap<String, String>() {
        {
            put("Location",                 "location");
            put("Participant Id",           "participantId");
            put("Participant Name",         "participantName");
            put("Female Child Bearing Age", "femaleChildBearingAge");
            put("Actual Screening Date",    "actualScreeningDate");
            put("Prime Vac. Date",          "date");
            put("Start Time",               "startTime");
            put("End Time",                 "endTime");
        }
    };

    private BookingAppConstants() {
    }
}
