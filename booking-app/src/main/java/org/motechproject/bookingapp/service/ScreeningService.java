package org.motechproject.bookingapp.service;

import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.ScreeningDto;

import java.util.List;

public interface ScreeningService {

    List<Screening> getScreenings(int page, int pageSize, String sortColumn, String sortOrder);

    long getTotalInstancesCount();

    ScreeningDto addOrUpdate(ScreeningDto screeningDto);

    ScreeningDto add(ScreeningDto screeningDto);

    ScreeningDto update(ScreeningDto screeningDto);

    ScreeningDto getScreeningById(Long id);

}
