package org.motechproject.bookingapp.repository;

import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;
import java.util.Set;

public interface VisitBookingDetailsDataService extends MotechDataService<VisitBookingDetails> {

    @Lookup
    List<VisitBookingDetails> findByVisitIds(@LookupField(name = "visit.id") Set<Long> visitIds);

    @Lookup
    VisitBookingDetails findByVisitId(@LookupField(name = "visit.id") Long visitId);
}
