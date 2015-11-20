package org.motechproject.bookingapp.util;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;

public final class PrimeVaccinationScheduleDtoUtil {

    public static PrimeVaccinationScheduleDto createFrom(LocalDate date, Subject participant, Long visitId) {
        PrimeVaccinationScheduleDto dto = new PrimeVaccinationScheduleDto();
        dto.setActualScreeningDate(date);
        dto.setParticipantName(participant.getName());
        dto.setParticipantId(participant.getSubjectId());
        dto.setParticipantGender(participant.getGender());
        dto.setVisitId(visitId);
        return dto;
    }

    public static PrimeVaccinationScheduleDto createFrom(VisitBookingDetails details, LocalDate actualScreeningDate) {
        PrimeVaccinationScheduleDto dto = new PrimeVaccinationScheduleDto();
        dto.setActualScreeningDate(actualScreeningDate);
        dto.setStartTime(details.getStartTime());
        dto.setParticipantId(details.getVisit().getSubject().getSubjectId());
        dto.setParticipantName(details.getVisit().getSubject().getName());
        dto.setClinicId(details.getClinic().getId());
        dto.setSiteId(details.getClinic().getSite().getId());
        dto.setDate(details.getBookingPlannedDate());
        dto.setFemaleChildBearingAge(details.getFemaleChildBearingAge());
        dto.setVisitBookingDetailsId(details.getId());
        dto.setEndTime(details.getEndTime());
        dto.setLocation(details.getClinic().getSite().getSiteId() + " - " + details.getClinic().getLocation());
        dto.setVisitId(details.getVisit().getId());
        dto.setParticipantGender(details.getVisit().getSubject().getGender());
        return dto;
    }

    public static PrimeVaccinationScheduleDto createFrom(VisitBookingDetails details) {
        LocalDate actualScreeningDate = null;
        for (Visit visit : details.getVisit().getSubject().getVisits()) {
            if (visit.getType().equals(VisitType.SCREENING)) {
                actualScreeningDate = visit.getDate();
            }
        }
        return createFrom(details, actualScreeningDate);
    }

    private PrimeVaccinationScheduleDtoUtil() {
    }
}
