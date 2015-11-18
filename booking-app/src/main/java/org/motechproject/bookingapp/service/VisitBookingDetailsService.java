package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.ebodac.domain.VisitType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface VisitBookingDetailsService {

    VisitBookingDetails findByVisitId(Long visitId);

    List<VisitBookingDetails> findByVisitIds(Set<Long> visitIds);

    Map<VisitType, VisitBookingDetails> findByVisitIdsAsMap(Set<Long> visitIds);

    VisitBookingDetails create(VisitBookingDetails visitBookingDetails);

    VisitBookingDetails update(VisitBookingDetails visitBookingDetails);

    VisitBookingDetails createOrUpdate(VisitBookingDetails visitBookingDetails);

    void createOrUpdate(Collection<VisitBookingDetails> visitBookingDetailsList);
}
