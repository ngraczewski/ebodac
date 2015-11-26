package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.ebodac.web.domain.Records;

import java.io.IOException;

public interface PrimeVaccinationScheduleService {

    Records<PrimeVaccinationScheduleDto> getPrimeVaccinationScheduleRecords(BookingGridSettings settings) throws IOException;

    PrimeVaccinationScheduleDto createOrUpdateWithDto(PrimeVaccinationScheduleDto visitDto, Boolean ignoreLimitation);
}
