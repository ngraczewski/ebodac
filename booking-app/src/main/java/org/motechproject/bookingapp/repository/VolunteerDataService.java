package org.motechproject.bookingapp.repository;

import org.motechproject.bookingapp.domain.Volunteer;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.Date;
import java.util.List;

public interface VolunteerDataService extends MotechDataService<Volunteer> {

    @Lookup(name="Find by Clinic ID")
    List<Volunteer> findVolunteerByClinicId(@LookupField(name = "clinicId") String clinicId);

    @Lookup(name="Find by Participant Name")
    List<Volunteer> findVolunteersByName(@LookupField(name = "name") String name);

    @Lookup(name="Find by Screening Date")
    List<Volunteer> findVolunteersByScreeningDate(@LookupField(name = "screeningDate") Date screeningDate);
}
