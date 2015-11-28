package org.motechproject.bookingapp.service.impl;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.domain.VisitScheduleOffset;
import org.motechproject.bookingapp.exception.VisitScheduleException;
import org.motechproject.bookingapp.service.VisitBookingDetailsService;
import org.motechproject.bookingapp.service.VisitScheduleOffsetService;
import org.motechproject.bookingapp.service.VisitScheduleService;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("visitScheduleService")
public class VisitScheduleServiceImpl implements VisitScheduleService {

    public static final int ONE_DAY = 1;
    public static final int TWO_WEEKS = 14;
    public static final int FOUR_WEEKS = 28;

    @Autowired
    private SubjectDataService subjectDataService;

    @Autowired
    private VisitBookingDetailsService visitBookingDetailsService;

    @Autowired
    private VisitScheduleOffsetService visitScheduleOffsetService;

    @Override
    public Map<String, String> getPrimeVaccinationDateAndDateRange(String subjectId) {

        Subject subject = subjectDataService.findSubjectBySubjectId(subjectId);

        LocalDate primeVacDate = null;
        LocalDate earliestDate = null;
        LocalDate latestDate = null;

        if (subject != null) {
            VisitBookingDetails details = getPrimerVaccinationDetails(subject);

            if (subject.getPrimerVaccinationDate() != null) {
                primeVacDate = subject.getPrimerVaccinationDate();
            } else if (details != null){
                primeVacDate = details.getBookingActualDate();
            }

            LocalDate screeningDate = getScreeningDate(subject);
            if (screeningDate != null) {
                if (!isFemaleChildBearingAge(details)) {
                    earliestDate = screeningDate.plusDays(ONE_DAY);
                } else {
                    earliestDate = screeningDate.plusDays(TWO_WEEKS);
                }
                latestDate = screeningDate.plusDays(FOUR_WEEKS);
            }
        }

        Map<String, String> dates = new HashMap<>();
        dates.put("primeVacDate", primeVacDate != null ?
                primeVacDate.toString(BookingAppConstants.SIMPLE_DATE_FORMAT) : "");
        dates.put("earliestDate", earliestDate != null ?
                earliestDate.toString(BookingAppConstants.SIMPLE_DATE_FORMAT) : "");
        dates.put("latestDate", latestDate != null ?
                latestDate.toString(BookingAppConstants.SIMPLE_DATE_FORMAT) : "");

        return dates;

    }

    @Override
    public Map<String, String> calculatePlannedVisitDates(String subjectId, LocalDate primeVaccinationDate) {
        Map<String, String> plannedDates = new HashMap<>();

        Subject subject = subjectDataService.findSubjectBySubjectId(subjectId);

        if (subject == null) {
            throw new VisitScheduleException(String.format("Cannot calculate Planned Dates, because Participant with Id: %s not found", subjectId));
        }

        if (subject.getVisits() == null || subject.getVisits().isEmpty()) {
            throw new VisitScheduleException(String.format("Cannot calculate Planned Dates, because Participant with Id: %s has no Visits", subjectId));
        }

        if (subject.getPrimerVaccinationDate() == null) {
            for (VisitBookingDetails details : calculatePlannedDates(subject.getVisits(), primeVaccinationDate).values()) {
                if (VisitType.PRIME_VACCINATION_DAY.equals(details.getVisit().getType())) {
                    plannedDates.put(details.getVisit().getType().toString(), details.getBookingActualDate().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
                } else {
                    plannedDates.put(details.getVisit().getType().toString(), details.getBookingPlannedDate().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
                }
            }
        } else {
            for (Visit visit : subject.getVisits()) {
                if (visit.getMotechProjectedDate() != null) {
                    plannedDates.put(visit.getType().toString(), visit.getMotechProjectedDate().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
                }
            }
        }

        return plannedDates;
    }

    @Override
    public void savePlannedVisitDates(String subjectId, LocalDate primeVaccinationDate) {
        Subject subject = subjectDataService.findSubjectBySubjectId(subjectId);

        if (subject == null) {
            throw new VisitScheduleException(String.format("Cannot save Planned Dates, because Participant with Id: %s not found", subjectId));
        }

        if (subject.getVisits() == null || subject.getVisits().isEmpty()) {
            throw new VisitScheduleException(String.format("Cannot save Planned Dates, because Participant with Id: %s has no Visits", subjectId));
        }

        if (subject.getPrimerVaccinationDate() != null) {
            throw new VisitScheduleException(String.format("Cannot save Planned Dates, because Participant with Id: %s has been vaccinated", subjectId));
        }

        visitBookingDetailsService.createOrUpdate(calculatePlannedDates(subject.getVisits(), primeVaccinationDate).values());
    }

    private Map<VisitType, VisitBookingDetails> calculatePlannedDates(List<Visit> visits, LocalDate primeVaccinationDate) {
        List<Visit> visitList = new ArrayList<>();
        Set<Long> visitIds = new HashSet<>();

        if (primeVaccinationDate == null) {
            throw new VisitScheduleException("Cannot calculate Planned Dates, because Prime Vaccination Date is empty");
        }

        LocalDate screeningDate = null;

        for (Visit visit : visits) {
            if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType())) {
                if (VisitType.SCREENING.equals(visit.getType())) {
                    screeningDate = visit.getDate();
                } else {
                    visitIds.add(visit.getId());
                    visitList.add(visit);

                }
            }
        }

        Map<VisitType, VisitBookingDetails> visitBookingDetailsMap = visitBookingDetailsService.findByVisitIdsAsMap(visitIds);

        validateDate(primeVaccinationDate, screeningDate, visitBookingDetailsMap.get(VisitType.PRIME_VACCINATION_DAY));

        Map<VisitType, VisitScheduleOffset> offsetMap = visitScheduleOffsetService.getAllAsMap();

        for (Visit visit : visitList) {
            VisitBookingDetails details = visitBookingDetailsMap.get(visit.getType());

            if (details == null) {
                if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                    details = new VisitBookingDetails(visit, primeVaccinationDate);
                } else {
                    details = new VisitBookingDetails(primeVaccinationDate.plusDays(offsetMap.get(visit.getType()).getTimeOffset()), visit);
                }
                visitBookingDetailsMap.put(visit.getType(), details);
            } else {
                if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                    details.setBookingActualDate(primeVaccinationDate);
                } else {
                    details.setBookingPlannedDate(primeVaccinationDate.plusDays(offsetMap.get(visit.getType()).getTimeOffset()));
                }
            }
        }

        return visitBookingDetailsMap;
    }

    private void validateDate(LocalDate date, LocalDate screeningDate, VisitBookingDetails details) {

        LocalDate earliestDate;
        LocalDate latestDate;

        if (!isFemaleChildBearingAge(details)) {
            earliestDate = screeningDate.plusDays(ONE_DAY);
        } else {
            earliestDate = screeningDate.plusDays(TWO_WEEKS);
        }
        latestDate = screeningDate.plusDays(FOUR_WEEKS);

        if (date.isBefore(earliestDate) || date.isAfter(latestDate)) {
            throw new IllegalArgumentException(String.format("The date should be between %s and %s but is %s",
                    earliestDate, latestDate, date));
        }
    }

    private boolean isFemaleChildBearingAge(VisitBookingDetails details) {
        return details != null && details.getFemaleChildBearingAge() != null && details.getFemaleChildBearingAge();
    }

    private LocalDate getScreeningDate(Subject subject) {
        LocalDate screeningDate = null;

        for (Visit visit : subject.getVisits()) {
            if (VisitType.SCREENING.equals(visit.getType())) {
                screeningDate = visit.getDate();
            }
        }

        if (screeningDate == null) {
            throw new VisitScheduleException(String.format("Couldn't save Planned Dates, because Participant with ID:" +
                    "%s didn't participate in screening visit", subject));
        }
        return screeningDate;
    }

    private VisitBookingDetails getPrimerVaccinationDetails(Subject subject) {
        if (subject.getVisits() != null) {
            for (Visit visit : subject.getVisits()) {
                if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                    return visitBookingDetailsService.findByVisitId(visit.getId());
                }
            }
        }
        return null;
    }
}
