package org.motechproject.bookingapp.service;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.ScreeningDto;
import org.motechproject.commons.api.Range;

import java.util.List;

public interface ScreeningService {

    List<Screening> getScreenings(int page, int pageSize, String sortColumn, String sortDirection, Range dateRange);

    long countScreeningsForDateRange(Range<LocalDate> dateRange);

    ScreeningDto addOrUpdate(ScreeningDto screeningDto);

    ScreeningDto add(ScreeningDto screeningDto);

    ScreeningDto update(ScreeningDto screeningDto);

    ScreeningDto getScreeningById(Long id);

}
