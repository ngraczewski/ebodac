package org.motechproject.bookingapp.web;

import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;
import org.motechproject.bookingapp.service.PrimeVaccinationScheduleService;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.mds.web.domain.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PrimeVaccinationScheduleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimeVaccinationScheduleController.class);

    @Autowired
    private PrimeVaccinationScheduleService primeVaccinationScheduleService;

    @RequestMapping("/primeVaccinationSchedule")
    @ResponseBody
    public Records<PrimeVaccinationScheduleDto> getVisitBookingDetails(BookingGridSettings settings) {

        int page = settings.getPage();
        int pageSize = settings.getRows();
        String sortColumn = settings.getSortColumn();
        String sortDirection = settings.getSortDirection();

        List<PrimeVaccinationScheduleDto> visits = primeVaccinationScheduleService.getVisitDtos(page, pageSize, sortColumn, sortDirection);

        long recordCount = primeVaccinationScheduleService.countVisitDtos();
        int rowCount = (int) Math.ceil(recordCount / (double) pageSize);

        return new Records<>(page, rowCount, (int) recordCount, visits);
    }

    @RequestMapping(value = "/primeVaccinationSchedule", method = RequestMethod.POST)
    @ResponseBody
    public PrimeVaccinationScheduleDto updateVisitBookingDetails(@RequestBody PrimeVaccinationScheduleDto visitDto) {
        return primeVaccinationScheduleService.createOrUpdateWithDto(visitDto);
    }

    @ExceptionHandler
    public void handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);
    }

}
