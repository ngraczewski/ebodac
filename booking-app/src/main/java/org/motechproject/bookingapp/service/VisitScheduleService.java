package org.motechproject.bookingapp.service;

import org.joda.time.LocalDate;

import java.util.Map;

public interface VisitScheduleService {

    LocalDate getPrimeVaccinationDate(String subjectId);

    Map<String, String> calculatePlannedVisitDates(String subjectId, LocalDate primeVaccinationDate);

    void savePlannedVisitDates(String subjectId, LocalDate primeVaccinationDate);
}
