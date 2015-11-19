package org.motechproject.bookingapp.service.impl;

import org.apache.commons.lang.Validate;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.Clinic;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.ScreeningDto;
import org.motechproject.bookingapp.domain.Volunteer;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.ScreeningDataService;
import org.motechproject.bookingapp.repository.VolunteerDataService;
import org.motechproject.bookingapp.service.ScreeningService;
import org.motechproject.bookingapp.util.ScreeningValidator;
import org.motechproject.commons.api.Range;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("screeningService")
public class ScreeningServiceImpl implements ScreeningService {

    @Autowired
    private ScreeningDataService screeningDataService;

    @Autowired
    private VolunteerDataService volunteerDataService;

    @Autowired
    private ClinicDataService clinicDataService;

    @Override
    @Transactional
    public List<Screening> getScreenings(int page, int pageSize, String sortColumn, String sortDirection, Range<LocalDate> dateRange) {

        QueryParams queryParams;

        if (sortColumn != null && sortDirection != null) {
            queryParams = new QueryParams(page, pageSize, new Order(sortColumn, sortDirection));
        } else {
            queryParams = new QueryParams(page, pageSize);
        }

        return screeningDataService.findByDate(dateRange, queryParams);
    }

    @Override
    public long countScreeningsForDateRange(Range<LocalDate> range) {
        return screeningDataService.countFindByDate(range);
    }

    @Override
    public Screening addOrUpdate(ScreeningDto screeningDto) {
        if (screeningDto.getId() != null) {
            return update(screeningDto);
        }
        return add(screeningDto);
    }

    @Override
    public Screening add(ScreeningDto screeningDto) {

        ScreeningValidator.validateForAdd(screeningDto);

        Screening screening = new Screening();
        screening.setVolunteer(volunteerDataService.create(new Volunteer(screeningDto.getVolunteerName())));
        checkNumberOfPatientsAndSetScreeningData(screeningDto, screening);

        return screeningDataService.create(screening);
    }

    @Override
    public Screening update(ScreeningDto screeningDto) {

        ScreeningValidator.validateForUpdate(screeningDto);

        Long screeningId = Long.parseLong(screeningDto.getId());
        Screening screening = screeningDataService.findById(screeningId);

        Validate.notNull(screening, String.format("Screening with id \"%s\" doesn't exist!", screeningId));

        screening.getVolunteer().setName(screeningDto.getVolunteerName());
        checkNumberOfPatientsAndSetScreeningData(screeningDto, screening);

        return screeningDataService.update(screening);
    }

    @Override
    @Transactional
    public ScreeningDto getScreeningById(Long id) {
        return screeningDataService.findById(id).toDto();
    }

    private void checkNumberOfPatientsAndSetScreeningData(ScreeningDto screeningDto, Screening screening) {
        Clinic clinic = clinicDataService.findById(Long.parseLong(screeningDto.getClinicId()));
        LocalDate date = LocalDate.parse(screeningDto.getDate());
        Time startTime = Time.valueOf(screeningDto.getStartTime());
        Time endTime = Time.valueOf(screeningDto.getEndTime());

        List<Screening> screeningList = screeningDataService.findByDateAndClinicId(date, clinic.getId());

        if (screeningList != null) {
            int numberOfRooms = clinic.getNumberOfRooms();
            int maxVisits = clinic.getMaxScreeningVisits() * numberOfRooms;
            int patients = 0;

            for (Screening s : screeningList) {
                if (s.getId().equals(screening.getId())) {
                    maxVisits++;
                } else {
                    if (startTime.isBefore(s.getStartTime())) {
                        if (s.getStartTime().isBefore(endTime)) {
                            patients++;
                        }
                    } else {
                        if (startTime.isBefore(s.getEndTime())) {
                            patients++;
                        }
                    }
                }
            }

            Validate.isTrue(screeningList.size() < maxVisits, "Maximum amount of Screening Visits exceeded for this day");
            Validate.isTrue(patients < numberOfRooms, "Too many Patients at the same time");
        }

        screening.setDate(date);
        screening.setStartTime(startTime);
        screening.setEndTime(endTime);
        screening.setClinic(clinic);
    }
}
