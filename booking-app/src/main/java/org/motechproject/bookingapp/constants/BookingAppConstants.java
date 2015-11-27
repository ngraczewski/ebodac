package org.motechproject.bookingapp.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class BookingAppConstants {

    public static final String UI_CONFIG = "custom-ui.js";

    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

    public static final String PDF_EXPORT_FORMAT="pdf";
    public static final String CSV_EXPORT_FORMAT="csv";
    public static final String XLS_EXPORT_FORMAT="xls";

    public static final String SCREENING_NAME = "Screening";
    public static final String PRIME_VACCINATION_SCHEDULE_NAME = "Prime Vaccination Schedule";

    public static final String SCREENING_TAB_PERMISSION = "screeningBookingTab";
    public static final String PRIME_VAC_TAB_PERMISSION = "primeVaccinationBookingTab";
    public static final String CLINIC_VISIT_SCHEDULE_TAB_PERMISSION = "clinicVisitBookingTab";
    public static final String ADVANCED_SETTINGS_TAB_PERMISSION = "bookingAdvancedSettings";

    public static final String HAS_SCREENING_TAB_ROLE = "hasRole('" + SCREENING_TAB_PERMISSION + "')";
    public static final String HAS_PRIME_VAC_TAB_ROLE = "hasRole('" + PRIME_VAC_TAB_PERMISSION + "')";
    public static final String HAS_CLINIC_VISIT_SCHEDULE_TAB_ROLE = "hasRole('" + CLINIC_VISIT_SCHEDULE_TAB_PERMISSION + "')";
    public static final String HAS_ADVANCED_SETTINGS_TAB_ROLE = "hasRole('" + ADVANCED_SETTINGS_TAB_PERMISSION + "')";

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

    public static final List<String> AVAILABLE_LOOKUPS_FOR_PRIME_VACCINATION_SCHEDULE = new ArrayList<>(Arrays.asList(
            "Find Visits By Participant Id", "Find Visits By Participant Name"));

    public static final List<String> AVAILABLE_LOOKUPS_FOR_SCREENINGS = new ArrayList<>(Arrays.asList(
            "Find By Clinic Location", "Find By Volunteer Name"));

    private BookingAppConstants() {
    }
}
