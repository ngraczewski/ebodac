package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.domain.VisitScheduleOffset;
import org.motechproject.ebodac.domain.VisitType;

import java.util.List;
import java.util.Map;

public interface VisitScheduleOffsetService {

    VisitScheduleOffset findByVisitType(VisitType visitType);

    List<VisitScheduleOffset> getAll();

    Map<VisitType, VisitScheduleOffset> getAllAsMap();
}
