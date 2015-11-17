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

    @Autowired
    private SubjectDataService subjectDataService;

    @Autowired
    private VisitBookingDetailsService visitBookingDetailsService;

    @Autowired
    private VisitScheduleOffsetService visitScheduleOffsetService;

    @Override
    public LocalDate getPrimeVaccinationDate(String subjectId) {
        Subject subject = subjectDataService.findSubjectBySubjectId(subjectId);

        if (subject == null) {
            return null;
        }

        if (subject.getPrimerVaccinationDate() != null) {
            return subject.getPrimerVaccinationDate();
        }

        if (subject.getVisits() != null) {
            for (Visit visit : subject.getVisits()) {
                if (VisitType.PRIME_VACCINATION_DAY.equals(visit.getType())) {
                    VisitBookingDetails visitBookingDetails = visitBookingDetailsService.findByVisitId(visit.getId());

                    if (visitBookingDetails != null) {
                        return visitBookingDetails.getBookingActualDate();
                    }

                    return null;
                }
            }
        }

        return null;
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

        for (Visit visit : visits) {
            if (!VisitType.UNSCHEDULED_VISIT.equals(visit.getType()) && !VisitType.SCREENING.equals(visit.getType())) {
                visitIds.add(visit.getId());
                visitList.add(visit);
            }
        }

        Map<VisitType, VisitBookingDetails> visitBookingDetailsMap = visitBookingDetailsService.findByVisitIdsAsMap(visitIds);
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
}
