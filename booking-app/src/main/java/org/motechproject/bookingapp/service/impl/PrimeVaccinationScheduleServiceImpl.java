package org.motechproject.bookingapp.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.VisitBookingDetailsDataService;
import org.motechproject.bookingapp.service.PrimeVaccinationScheduleService;
import org.motechproject.bookingapp.util.PrimeVaccinationScheduleDtoUtil;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.util.QueryParamsBuilder;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

    @Autowired
    private LookupService lookupService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Records<PrimeVaccinationScheduleDto> getPrimeVaccinationScheduleRecords(BookingGridSettings settings) throws IOException{
        QueryParams queryParams = QueryParamsBuilder.buildQueryParams(settings, getFields(settings.getFields()));
        Records<Visit> visitRecords = lookupService.getEntities(Visit.class, settings.getLookup(), settings.getFields(), queryParams);

        List<Visit> visits = visitRecords.getRows();
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
        return new Records<>(visitRecords.getPage(), visitRecords.getTotal(), visitRecords.getRecords(), dtos);
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

    private Map<String, Object> getFields(String json) throws IOException {
        if (json == null) {
            return null;
        } else {
            return objectMapper.readValue(json, new TypeReference<LinkedHashMap>() {});
        }
    }
}
