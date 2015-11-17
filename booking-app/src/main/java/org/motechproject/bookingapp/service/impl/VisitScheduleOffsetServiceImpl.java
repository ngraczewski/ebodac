package org.motechproject.bookingapp.service.impl;

import org.motechproject.bookingapp.domain.VisitScheduleOffset;
import org.motechproject.bookingapp.repository.VisitScheduleOffsetDataService;
import org.motechproject.bookingapp.service.VisitScheduleOffsetService;
import org.motechproject.ebodac.domain.VisitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("visitScheduleOffsetService")
public class VisitScheduleOffsetServiceImpl implements VisitScheduleOffsetService {

    @Autowired
    private VisitScheduleOffsetDataService visitScheduleOffsetDataService;

    @Override
    public VisitScheduleOffset findByVisitType(VisitType visitType) {
        return visitScheduleOffsetDataService.findByVisitType(visitType);
    }

    @Override
    public List<VisitScheduleOffset> getAll() {
        return visitScheduleOffsetDataService.retrieveAll();
    }

    @Override
    public Map<VisitType, VisitScheduleOffset> getAllAsMap() {
        Map<VisitType, VisitScheduleOffset> visitScheduleOffsetMap = new HashMap<>();
        List<VisitScheduleOffset> visitScheduleOffsetList = visitScheduleOffsetDataService.retrieveAll();

        if (visitScheduleOffsetList != null) {
            for (VisitScheduleOffset offset : visitScheduleOffsetList) {
                visitScheduleOffsetMap.put(offset.getVisitType(), offset);
            }
        }

        return visitScheduleOffsetMap;
    }
}
