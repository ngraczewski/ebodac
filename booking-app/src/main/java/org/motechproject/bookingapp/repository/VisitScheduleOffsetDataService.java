package org.motechproject.bookingapp.repository;

import org.motechproject.bookingapp.domain.VisitScheduleOffset;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface VisitScheduleOffsetDataService extends MotechDataService<VisitScheduleOffset> {

    @Lookup
    VisitScheduleOffset findByVisitType(@LookupField(name = "visitType") VisitType visitType);
}
