package org.motechproject.bookingapp.domain;

import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.motechproject.mds.domain.MdsEntity;

@Entity(maxFetchDepth = 3)
public class Screening extends MdsEntity {

    @Field
    private Room room;

    @Field(required = true)
    private Volunteer volunteer;

    @Field(required = true)
    private LocalDate date;

    @Field(required = true)
    private Time startTime;

    @Field(required = true)
    private Time endTime;

    public ScreeningDto toDto() {
        ScreeningDto dto = new ScreeningDto();
        dto.setId(getId().toString());
        dto.setRoomId(room.getId().toString());
        dto.setClinicId(getRoom().getClinic().getId().toString());
        dto.setSiteId(getClinic().getSite().getId().toString());
        dto.setVolunteerId(volunteer.getId().toString());
        dto.setVolunteerName(volunteer.getName());
        dto.setDate(date.toString());
        dto.setStartTime(startTime.toString());
        dto.setEndTime(endTime.toString());
        return dto;
    }

    @Ignore
    public Clinic getClinic() {
        if (room != null) {
            return room.getClinic();
        }
        return null;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
