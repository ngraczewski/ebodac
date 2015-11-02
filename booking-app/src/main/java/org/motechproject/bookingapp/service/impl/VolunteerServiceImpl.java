package org.motechproject.bookingapp.service.impl;

import org.motechproject.bookingapp.domain.Volunteer;
import org.motechproject.bookingapp.repository.VolunteerDataService;
import org.motechproject.bookingapp.service.VolunteerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("volunteerService")
public class VolunteerServiceImpl implements VolunteerService {

    @Autowired
    private VolunteerDataService volunteerDataService;

    @Override
    public List<Volunteer> getVolunteers() {
        return volunteerDataService.retrieveAll();
    }
}
