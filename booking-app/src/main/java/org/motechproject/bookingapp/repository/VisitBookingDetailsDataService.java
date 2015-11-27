package org.motechproject.bookingapp.repository;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;
import java.util.Set;

public interface VisitBookingDetailsDataService extends MotechDataService<VisitBookingDetails> {

    @Lookup
    List<VisitBookingDetails> findByVisitIds(@LookupField(name = "visit.id") Set<Long> visitIds);

    @Lookup
    VisitBookingDetails findByVisitId(@LookupField(name = "visit.id") Long visitId);

    @Lookup
    VisitBookingDetails findByParticipantIdAndVisitType(@LookupField(name = "subject.subjectId") String subjectId,
                                                        @LookupField(name = "visit.type") VisitType type);

    @Lookup
    List<VisitBookingDetails> findByVisitTypeAndParticipantPrimeVaccinationDateAndName(@LookupField(name = "visit.type") VisitType type,
                                                                                       @LookupField(name = "subject.primerVaccinationDate",
                                                                                               customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate,
                                                                                       @LookupField(name = "subject.name",
                                                                                               customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);
    @Lookup
    List<VisitBookingDetails> findByBookingPlannedDateClinicIdAndVisitType(@LookupField(name = "bookingPlannedDate", customOperator = Constants.Operators.EQ) LocalDate bookingPlannedDate,
                                                                           @LookupField(name = "clinic.id") Long clinicId,
                                                                           @LookupField(name = "visit.type") VisitType type);

    @Lookup
    List<VisitBookingDetails> findByVisitTypeAndParticipantPrimeVaccinationDate(@LookupField(name = "visit.type") VisitType type,
                                                                                @LookupField(name = "subject.primerVaccinationDate",
                                                                                        customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate);

    @Lookup
    List<VisitBookingDetails> findVisitsByParticipantIdVisitTypeAndParticipantPrimeVaccinationDateAndName(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                                                                          @LookupField(name = "visit.type") VisitType visitType,
                                                                                                          @LookupField(name = "subject.primerVaccinationDate",
                                                                                                                  customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate,
                                                                                                          @LookupField(name = "subject.name",
                                                                                                                  customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    @Lookup
    List<VisitBookingDetails> findVisitsByParticipantNameVisitTypeAndParticipantPrimeVaccinationDate(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                                                     @LookupField(name = "visit.type") VisitType visitType,
                                                                                                     @LookupField(name = "subject.primerVaccinationDate",
                                                                                                             customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate);
}
