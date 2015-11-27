package org.motechproject.bookingapp.web;


import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.ScreeningDto;
import org.motechproject.bookingapp.exception.LimitationExceededException;
import org.motechproject.bookingapp.helper.DtoLookupHelper;
import org.motechproject.bookingapp.service.ScreeningService;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.ebodac.exception.EbodacLookupException;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.dto.LookupDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/screenings")
@PreAuthorize(BookingAppConstants.HAS_SCREENING_TAB_ROLE)
public class ScreeningController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreeningController.class);

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private LookupService lookupService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Records<Screening> getScreenings(BookingGridSettings settings) throws IOException{
        return screeningService.getScreenings(DtoLookupHelper.changeLookupForScreening(settings));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ScreeningDto getScreeningById(@PathVariable Long id) {
        return screeningService.getScreeningById(id);
    }

    @RequestMapping(value = "/new/{ignoreLimitation}", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public Object addOrUpdateScreening(@PathVariable Boolean ignoreLimitation, @RequestBody ScreeningDto screening) {
        try {
            return screeningService.addOrUpdate(screening, ignoreLimitation);
        } catch (LimitationExceededException e) {
            return e.getMessage();
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleBadRequest(Exception e) {
        LOGGER.debug("Error while add or updating screening", e);
        return e.getMessage();
    }

    @RequestMapping(value = "/getLookupsForScreening", method = RequestMethod.GET)
    @ResponseBody
    public List<LookupDto> getLookupsForScreening() {
        List<LookupDto> ret = new ArrayList<>();
        List<LookupDto> availableLookups;
        try {
            availableLookups = lookupService.getAvailableLookups(Screening.class.getName());
        } catch (EbodacLookupException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
        List<String> lookupList = BookingAppConstants.AVAILABLE_LOOKUPS_FOR_SCREENINGS;
        for(LookupDto lookupDto : availableLookups) {
            if(lookupList.contains(lookupDto.getLookupName())) {
                ret.add(lookupDto);
            }
        }
        return ret;
    }
}
