package org.motechproject.bookingapp.web;


import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.DateFilter;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.ScreeningDto;
import org.motechproject.bookingapp.exception.LimitationExceededException;
import org.motechproject.bookingapp.service.ScreeningService;
import org.motechproject.bookingapp.web.domain.GridSettings;
import org.motechproject.bookingapp.web.domain.StringResponse;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.web.domain.Records;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
@RequestMapping("/screenings")
public class ScreeningController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScreeningController.class);

    private DateFilter defaultDateFilter = DateFilter.TODAY;

    @Autowired
    private ScreeningService screeningService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Records<Screening> getScreenings(GridSettings settings) {

        int page = settings.getPage();
        int pageSize = settings.getRows();
        String sortColumn = settings.getSortColumn();
        String sortDirection = settings.getSortDirection();

        if (settings.getDateFilter() != null) {
            defaultDateFilter = settings.getDateFilter();
        }

        Range<LocalDate> dateRange = defaultDateFilter.getRange();

        if (defaultDateFilter == DateFilter.DATE_RANGE) {
            LocalDate startDate = null;
            LocalDate endDate = null;

            if (StringUtils.isNotBlank(settings.getStartDate())) {
                startDate = LocalDate.parse(settings.getStartDate());
            }

            if (StringUtils.isNotBlank(settings.getEndDate())) {
                endDate = LocalDate.parse(settings.getEndDate());
            }

            dateRange = new Range<>(startDate, endDate);
        }

        List<Screening> screenings = screeningService.getScreenings(page, pageSize, sortColumn, sortDirection, dateRange);

        long recordCount = screeningService.countScreeningsForDateRange(dateRange);
        int rowCount = (int) Math.ceil(recordCount / (double) pageSize);

        return new Records<>(page, rowCount, (int) recordCount, screenings);
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

    @RequestMapping(value = "/getDefaultDateFilter")
    @ResponseBody
    public StringResponse getDefaultDateFilter() {
        return new StringResponse(defaultDateFilter.toString());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleBadRequest(Exception e) {
        LOGGER.debug("Error while add or updating screening", e);
        return e.getMessage();
    }
}
