package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;

import java.util.List;

public interface PrimeVaccinationScheduleService {
    List<PrimeVaccinationScheduleDto> getVisitDtos(int page, int pageSize, String sortColumn, String sortDirection);

    long countVisitDtos();

    PrimeVaccinationScheduleDto createOrUpdateWithDto(PrimeVaccinationScheduleDto visitDto);
}
