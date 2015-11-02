package org.motechproject.bookingapp.web;

import org.motechproject.bookingapp.domain.Volunteer;
import org.motechproject.bookingapp.service.VolunteerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class VolunteerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VolunteerController.class);

    @Autowired
    private VolunteerService volunteerService;

    @RequestMapping(value = "/volunteers", method = RequestMethod.GET)
    public List<Volunteer> getVolunteers() {
        return volunteerService.getVolunteers();
    }
}
