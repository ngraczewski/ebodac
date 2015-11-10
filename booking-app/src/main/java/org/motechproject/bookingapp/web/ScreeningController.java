package org.motechproject.bookingapp.web;

import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.ScreeningDto;
import org.motechproject.bookingapp.service.ScreeningService;
import org.motechproject.bookingapp.web.domain.GridSettings;
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

    @Autowired
    private ScreeningService screeningService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Records<Screening> getScreenings(GridSettings settings) {

        int page = settings.getPage();
        int pageSize = settings.getRows();
        String sortColumn = settings.getSidx();
        String sortOrder = settings.getSort();

        List<Screening> screenings = screeningService.getScreenings(page, pageSize, sortColumn, sortOrder);
        long recordCount = screeningService.getTotalInstancesCount();


        int rowCount = (int) Math.ceil(recordCount / (double) pageSize);

        return new Records<>(page, rowCount, (int) recordCount, screenings);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ScreeningDto getScreeningById(@PathVariable Long id) {
        return screeningService.getScreeningById(id);
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST, consumes = "application/json")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void addOrUpdateScreening(@RequestBody ScreeningDto screening) {
        screeningService.addOrUpdate(screening);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleBadRequest(Exception e) {
        LOGGER.error("Error while add or updating screening", e);
        return e.getMessage();
    }
}
