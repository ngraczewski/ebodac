package org.motechproject.bookingapp.service;

import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.ScreeningDto;
import org.motechproject.commons.api.Range;

import java.util.List;

public interface ScreeningService {

    List<Screening> getScreenings(int page, int pageSize, String sortColumn, String sortDirection, Range<LocalDate> dateRange);

    long countScreeningsForDateRange(Range<LocalDate> dateRange);

    Screening addOrUpdate(ScreeningDto screeningDto);

    Screening add(ScreeningDto screeningDto);

    Screening update(ScreeningDto screeningDto);

    ScreeningDto getScreeningById(Long id);

}
