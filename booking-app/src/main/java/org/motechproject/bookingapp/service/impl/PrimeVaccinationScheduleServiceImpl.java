package org.motechproject.bookingapp.service.impl;

import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.PrimeVaccinationScheduleService;
import org.motechproject.bookingapp.util.PrimeVaccinationScheduleDtoUtil;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrimeVaccinationScheduleServiceImpl implements PrimeVaccinationScheduleService {

    @Autowired
    private VisitBookingDetailsDataService visitBookingDetailsDataService;

    @Autowired
    private ClinicDataService clinicDataService;

    @Autowired
    private VisitDataService visitDataService;

    @Override
    public List<PrimeVaccinationScheduleDto> getVisitDtos(int page, int pageSize, String sortColumn, String sortDirection) {

        QueryParams queryParams = buildQueryParams(page, pageSize, sortColumn, sortDirection);

        List<Visit> visits = visitDataService.findVisitsByTypeDateAndPrimerVaccinationDate(VisitType.SCREENING, null,
                null, queryParams);

        List<PrimeVaccinationScheduleDto> dtos = new ArrayList<>();

        Map<Long, Visit> visitIds = getVisitIds(visits);

        List<VisitBookingDetails> visitDetails = visitBookingDetailsDataService.findByVisitIds(visitIds.keySet());

        for (Map.Entry<Long, Visit> visitId : visitIds.entrySet()) {

            Visit visit = visitId.getValue();
            VisitBookingDetails details = getDetailsForVisitWithId(visitDetails, visitId.getKey());

            PrimeVaccinationScheduleDto dto;

            if (details != null) {
                dto = PrimeVaccinationScheduleDtoUtil.createFrom(details, visit.getDate());
            } else {
                dto = PrimeVaccinationScheduleDtoUtil.createFrom(visit.getDate(), visit.getSubject(), visitId.getKey());
            }

            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public long countVisitDtos() {
        return visitDataService.countFindVisitsByTypeDateAndPrimerVaccinationDate(VisitType.SCREENING, null, null);
    }

    @Override
    @Transactional
    public PrimeVaccinationScheduleDto createOrUpdateWithDto(PrimeVaccinationScheduleDto dto) {
        VisitBookingDetails visitBookingDetails = visitBookingDetailsDataService.findById(dto.getVisitBookingDetailsId());

        VisitBookingDetails updated;

        if (visitBookingDetails != null) {
            updated = updateVisitWithDto(visitBookingDetails, dto);
        } else {
            updated = createVisitFromDto(dto);
        }

        return PrimeVaccinationScheduleDtoUtil.createFrom(updated);
    }

    private QueryParams buildQueryParams(int page, int pageSize, String sortColumn, String sortDirection) {
        QueryParams queryParams;

        if (sortColumn != null && sortDirection != null) {
            queryParams = new QueryParams(page, pageSize, new Order(sortColumn, sortDirection));
        } else {
            queryParams = new QueryParams(page, pageSize);
        }

        return queryParams;
    }

    private VisitBookingDetails getDetailsForVisitWithId(List<VisitBookingDetails> visitDetails, Long key) {
        for (VisitBookingDetails details : visitDetails) {
            if (details.getVisit().getId().equals(key)) {
                return details;
            }
        }
        return null;
    }


    private Map<Long, Visit> getVisitIds(List<Visit> visits) {
        Map<Long, Visit> ids = new LinkedHashMap<>();

        for (Visit visit : visits) {
            ids.put(getPrimeVaccinationVisit(visit.getSubject().getVisits()), visit);
        }

        return ids;
    }

    private Long getPrimeVaccinationVisit(List<Visit> visits) {
        for (Visit visit : visits) {
            if (visit.getType().equals(VisitType.PRIME_VACCINATION_DAY)) {
                return visit.getId();
            }
        }
        return null;
    }

    private VisitBookingDetails createVisitFromDto(PrimeVaccinationScheduleDto dto) {
        VisitBookingDetails visit = new VisitBookingDetails();
        visit.setStartTime(dto.getStartTime());
        visit.setEndTime(dto.getEndTime());
        visit.setBookingPlannedDate(dto.getDate());
        visit.setFemaleChildBearingAge(dto.getFemaleChildBearingAge());
        visit.setClinic(clinicDataService.findById(dto.getClinicId()));
        visit.setVisit(visitDataService.findById(dto.getVisitId()));
        return visitBookingDetailsDataService.create(visit);
    }

    private VisitBookingDetails updateVisitWithDto(VisitBookingDetails visit, PrimeVaccinationScheduleDto dto) {
        visit.setStartTime(dto.getStartTime());
        visit.setEndTime(dto.getEndTime());
        visit.setBookingPlannedDate(dto.getDate());
        visit.setFemaleChildBearingAge(dto.getFemaleChildBearingAge());
        visit.setClinic(clinicDataService.findById(dto.getClinicId()));
        return visitBookingDetailsDataService.update(visit);
    }
}
