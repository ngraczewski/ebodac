package org.motechproject.bookingapp.service.impl;

import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.VisitBookingDetailsService;
import org.motechproject.ebodac.domain.VisitType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("visitBookingDetailsService")
public class VisitBookingDetailsServiceImpl implements VisitBookingDetailsService {

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Override
    public VisitBookingDetails findByVisitId(Long visitId) {
        return visitBookingDetailsDataService.findByVisitId(visitId);
    }

    @Override
    public List<VisitBookingDetails> findByVisitIds(Set<Long> visitIds) {
        if (visitIds != null && !visitIds.isEmpty()) {
            return visitBookingDetailsDataService.findByVisitIds(visitIds);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Map<VisitType, VisitBookingDetails> findByVisitIdsAsMap(Set<Long> visitIds) {
        Map<VisitType, VisitBookingDetails> visitBookingDetailsMap = new HashMap<>();

        List<VisitBookingDetails> visitBookingDetailsList = findByVisitIds(visitIds);

        for (VisitBookingDetails details : visitBookingDetailsList) {
            visitBookingDetailsMap.put(details.getVisit().getType(), details);
        }

        return visitBookingDetailsMap;
    }

    @Override
    public VisitBookingDetails create(VisitBookingDetails visitBookingDetails) {
        return visitBookingDetailsDataService.create(visitBookingDetails);
    }

    @Override
    public VisitBookingDetails update(VisitBookingDetails visitBookingDetails) {
        return visitBookingDetailsDataService.update(visitBookingDetails);
    }

    @Override
    public VisitBookingDetails createOrUpdate(VisitBookingDetails visitBookingDetails) {
        if (visitBookingDetails.getId() != null) {
            return update(visitBookingDetails);
        } else {
            return create(visitBookingDetails);
        }
    }

    @Override
    public void createOrUpdate(Collection<VisitBookingDetails> visitBookingDetailsList) {
        if (visitBookingDetailsList != null) {
            for (VisitBookingDetails details : visitBookingDetailsList) {
                createOrUpdate(details);
            }
        }
    }
}
