package org.motechproject.bookingapp.web;

import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;
import org.motechproject.bookingapp.helper.DtoLookupHelper;
import org.motechproject.bookingapp.service.PrimeVaccinationScheduleService;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.exception.EbodacLookupException;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.dto.LookupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PrimeVaccinationScheduleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrimeVaccinationScheduleController.class);

    @Autowired
    private PrimeVaccinationScheduleService primeVaccinationScheduleService;

    @Autowired
    private LookupService lookupService;

    @RequestMapping("/primeVaccinationSchedule")
    @ResponseBody
    public Records<PrimeVaccinationScheduleDto> getVisitBookingDetails(BookingGridSettings settings) throws IOException {
        return primeVaccinationScheduleService.getPrimeVaccinationScheduleRecords(DtoLookupHelper.changeLookupForPrimeVaccinationSchedule(settings));
    }

    @RequestMapping(value = "/primeVaccinationSchedule", method = RequestMethod.POST)
    @ResponseBody
    public PrimeVaccinationScheduleDto updateVisitBookingDetails(@RequestBody PrimeVaccinationScheduleDto visitDto) {
        return primeVaccinationScheduleService.createOrUpdateWithDto(visitDto);
    }

    @RequestMapping(value = "/getLookupsForPrimeVaccinationSchedule", method = RequestMethod.GET)
    @ResponseBody
    public List<LookupDto> getLookupsForPrimeVaccinationSchedule() {
        List<LookupDto> ret = new ArrayList<>();
        List<LookupDto> availableLookups;
        try {
            availableLookups = lookupService.getAvailableLookups(Visit.class.getName());
        } catch (EbodacLookupException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
        List<String> lookupList = BookingAppConstants.AVAILABLE_LOOKUPS_FOR_PRIME_VACCINATION_SCHEDULE;
        for(LookupDto lookupDto : availableLookups) {
            if(lookupList.contains(lookupDto.getLookupName())) {
                ret.add(lookupDto);
            }
        }
        return ret;
    }

    @ExceptionHandler
    public void handleException(Exception e) {
        LOGGER.error(e.getMessage(), e);
    }

}
