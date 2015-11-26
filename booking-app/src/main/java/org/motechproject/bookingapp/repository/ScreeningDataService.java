package org.motechproject.bookingapp.repository;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;

public interface ScreeningDataService extends MotechDataService<Screening> {

    @Lookup
    List<Screening> findByDate(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Screening> findByDate(@LookupField(name = "date") Range<LocalDate> dateRange, QueryParams queryParams);

    long countFindByDate(Range<LocalDate> dateRange);

    @Lookup
    List<Screening> findByDateAndClinicId(@LookupField(name = "date") LocalDate date, @LookupField(name = "clinic.id") Long clinicId);

    @Lookup
    List<Screening> findByClinicLocationAndDate(@LookupField(name = "date") Range<LocalDate> date,
                                                @LookupField(name = "clinic.location", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String location);

    @Lookup
    List<Screening> findByClinicLocation(@LookupField(name = "clinic.location", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String location);

    @Lookup
    List<Screening> findByVolunteerName(@LookupField(name = "volunteer.name", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String volunteerName);

    @Lookup
    List<Screening> findByVolunteerNameAndDate(@LookupField(name = "date") Range<LocalDate> date,
                                               @LookupField(name = "volunteer.name", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String volunteerName);
}
