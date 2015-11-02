package org.motechproject.bookingapp.web;

import org.motechproject.bookingapp.service.ClinicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/clinics")
public class ClinicController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClinicController.class);

    @Autowired
    private ClinicService clinicService;


}
