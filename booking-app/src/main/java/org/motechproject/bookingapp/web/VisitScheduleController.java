package org.motechproject.bookingapp.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.exception.VisitScheduleException;
import org.motechproject.bookingapp.service.VisitScheduleService;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.VisitDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/schedule")
public class VisitScheduleController {

    @Autowired
    private VisitDataService visitDataService;

    @Autowired
    private VisitScheduleService visitScheduleService;

    @RequestMapping(value = "/getScreeningVisits", method = RequestMethod.GET)
    @ResponseBody
    public List<Visit> getScreeningVisits() {
        return visitDataService.findByVisitTypeAndActualDateLess(VisitType.SCREENING, LocalDate.now());
    }

    @RequestMapping(value = "/getPrimeVacDate/{subjectId}", method = RequestMethod.GET)
    @ResponseBody
    public String getPrimeVacDate(@PathVariable String subjectId) {
        if (StringUtils.isBlank(subjectId)) {
            return "";
        }

        LocalDate date = visitScheduleService.getPrimeVaccinationDate(subjectId);

        if (date != null) {
            return date.toString(BookingAppConstants.SIMPLE_DATE_FORMAT);
        } else {
            return "";
        }
    }

    @RequestMapping(value = "/getPlannedDates/{subjectId}/{plannedDate}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getPlannedDates(@PathVariable String subjectId, @PathVariable String plannedDate) {
        LocalDate date = null;

        if (StringUtils.isBlank(subjectId)) {
            return new HashMap<>();
        }

        if (StringUtils.isNotBlank(plannedDate)) {
            date = LocalDate.parse(plannedDate, DateTimeFormat.forPattern(BookingAppConstants.SIMPLE_DATE_FORMAT));
        }

        return visitScheduleService.calculatePlannedVisitDates(subjectId, date);
    }

    @RequestMapping(value = "/savePlannedDates/{subjectId}/{plannedDate}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> savePlannedDates(@PathVariable String subjectId, @PathVariable String plannedDate) {
        LocalDate date;

        if (StringUtils.isBlank(subjectId)) {
            return new ResponseEntity<>("Cannot save Planned Dates, because Participant Id is empty", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isNotBlank(plannedDate)) {
            date = LocalDate.parse(plannedDate, DateTimeFormat.forPattern(BookingAppConstants.SIMPLE_DATE_FORMAT));
        } else {
            return new ResponseEntity<>("Cannot save Planned Dates, because Prime Vaccination Date is empty", HttpStatus.BAD_REQUEST);
        }

        visitScheduleService.savePlannedVisitDates(subjectId, date);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(VisitScheduleException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleBadRequest(Exception e) {
        return e.getMessage();
    }
}
