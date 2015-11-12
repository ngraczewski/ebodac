package org.motechproject.bookingapp.service.impl;

import org.apache.commons.lang.Validate;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.ScreeningDto;
import org.motechproject.bookingapp.domain.Volunteer;
import org.motechproject.bookingapp.repository.ClinicDataService;
import org.motechproject.bookingapp.repository.RoomDataService;
import org.motechproject.bookingapp.repository.ScreeningDataService;
import org.motechproject.bookingapp.repository.SiteDataService;
import org.motechproject.bookingapp.repository.VolunteerDataService;
import org.motechproject.bookingapp.service.ScreeningService;
import org.motechproject.bookingapp.util.ScreeningValidator;
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
    private SiteDataService siteDataService;

    @Autowired
    private ClinicDataService clinicDataService;

    @Autowired
    private RoomDataService roomDataService;

    @Override
    public List<Screening> getScreenings(int page, int pageSize, String sortColumn, String sortDirectory) {

        QueryParams queryParams;

        if (sortColumn != null && sortDirectory != null) {
            queryParams = new QueryParams(page, pageSize, new Order(sortColumn, sortDirectory));
        } else {
            queryParams = new QueryParams(page, pageSize);
        }

        return screeningDataService.retrieveAll(queryParams);
    }

    @Override
    public long getTotalInstancesCount() {
        return screeningDataService.count();
    }

    @Override
    public ScreeningDto addOrUpdate(ScreeningDto screeningDto) {
        if (screeningDto.getId() != null) {
            return update(screeningDto);
        }
        return add(screeningDto);
    }

    @Override
    public ScreeningDto add(ScreeningDto screeningDto) {

        ScreeningValidator.validateForAdd(screeningDto);

        Screening screening = new Screening();
        screening.setVolunteer(volunteerDataService.create(new Volunteer(screeningDto.getVolunteerName())));
        screening.setDate(LocalDate.parse(screeningDto.getDate()));
        screening.setStartTime(Time.valueOf(screeningDto.getStartTime()));
        screening.setEndTime(Time.valueOf(screeningDto.getEndTime()));
        screening.setRoom(roomDataService.findById(Long.parseLong(screeningDto.getRoomId())));

        return screeningDataService.create(screening).toDto();
    }

    @Override
    public ScreeningDto update(ScreeningDto screeningDto) {

        ScreeningValidator.validateForUpdate(screeningDto);

        Long screeningId = Long.parseLong(screeningDto.getId());
        Screening screening = screeningDataService.findById(screeningId);

        Validate.notNull(screening, String.format("Screening with id \"%s\" doesn't exist!", screeningId));

        screening.getVolunteer().setName(screeningDto.getVolunteerName());
        screening.setDate(LocalDate.parse(screeningDto.getDate()));
        screening.setStartTime(Time.valueOf(screeningDto.getStartTime()));
        screening.setEndTime(Time.valueOf(screeningDto.getEndTime()));
        screening.setRoom(roomDataService.findById(Long.parseLong(screeningDto.getRoomId())));

        return screeningDataService.update(screening).toDto();
    }

    @Override
    @Transactional
    public ScreeningDto getScreeningById(Long id) {
        return screeningDataService.findById(id).toDto();
    }
}
