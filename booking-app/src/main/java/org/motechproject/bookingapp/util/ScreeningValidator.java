package org.motechproject.bookingapp.util;

import org.apache.commons.lang.Validate;
import org.motechproject.bookingapp.domain.ScreeningDto;
import org.motechproject.commons.date.model.Time;

public final class ScreeningValidator {

    public static void validateForAdd(ScreeningDto screeningDto) {
        validate(screeningDto);
        Validate.isTrue(screeningDto.getVolunteerId() == null);
        Validate.notEmpty(screeningDto.getVolunteerName());
    }

    public static void validateForUpdate(ScreeningDto screeningDto) {
        validate(screeningDto);
        Validate.notEmpty(screeningDto.getVolunteerId());
    }

    private static void validate(ScreeningDto screeningDto) {
        Validate.notEmpty(screeningDto.getClinicId(), "Clinic ID cannot be null or empty!");
        Validate.notEmpty(screeningDto.getDate(), "Screening date cannot be null or empty!");
        Validate.notEmpty(screeningDto.getStartTime(), "Screening start time cannot be null or empty!");
        Validate.notEmpty(screeningDto.getEndTime(), "Screening end time cannot be null or empty!");
        validateEndTime(screeningDto.getStartTime(), screeningDto.getEndTime());
    }

    private static void validateEndTime(String startTime, String endTime) {
        Time start = Time.valueOf(startTime);
        Time end = Time.valueOf(endTime);
        Validate.isTrue(start.isBefore(end), "Screening end time must be after start time!");
    }

    private ScreeningValidator() {
    }

}
