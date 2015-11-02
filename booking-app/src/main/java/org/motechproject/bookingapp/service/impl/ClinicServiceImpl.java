package org.motechproject.bookingapp.service.impl;

import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("clinicService")
public class ClinicServiceImpl implements ClinicService {

    @Autowired
    private ClinicDataService clinicDataService;

    @Override
    public List<Clinic> getClinics() {
        return clinicDataService.retrieveAll();
    }
}
