package org.motechproject.bookingapp.service;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.ScreeningDto;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.web.domain.Records;

import java.io.IOException;

public interface ScreeningService {

    Records<Screening> getScreenings(BookingGridSettings bookingGridSettings) throws IOException;

    long countScreeningsForDateRange(Range<LocalDate> dateRange);

    Screening addOrUpdate(ScreeningDto screeningDto, Boolean ignoreLimitation);

    Screening add(ScreeningDto screeningDto, Boolean ignoreLimitation);

    Screening update(ScreeningDto screeningDto, Boolean ignoreLimitation);

    ScreeningDto getScreeningById(Long id);

}
