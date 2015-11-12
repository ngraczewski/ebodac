package org.motechproject.bookingapp.repository;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

public interface ScreeningDataService extends MotechDataService<Screening> {

    @Lookup
    List<Screening> findByDate(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Screening> findByDate(@LookupField(name = "date") Range<LocalDate> dateRange, QueryParams queryParams);

    long countFindByDate(Range<LocalDate> dateRange);

    @Lookup
    List<Screening> findByDateAndRoomId(@LookupField(name = "date") LocalDate date,
                                        @LookupField(name = "room.id") Long roomId);
}
