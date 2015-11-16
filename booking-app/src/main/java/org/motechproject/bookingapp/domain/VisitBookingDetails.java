package org.motechproject.bookingapp.domain;

import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class VisitBookingDetails {

    @Field
    private LocalDate bookingPlannedDate;

    @Field
    private LocalDate bookingActualDate;

    @Field
    private Time startTime;

    @Field
    private Time endTime;

    @Field
    private Room room;

    @Field(required = true)
    private Visit visit;

    public LocalDate getBookingPlannedDate() {
        return bookingPlannedDate;
    }

    public void setBookingPlannedDate(LocalDate bookingPlannedDate) {
        this.bookingPlannedDate = bookingPlannedDate;
    }

    public LocalDate getBookingActualDate() {
        return bookingActualDate;
    }

    public void setBookingActualDate(LocalDate bookingActualDate) {
        this.bookingActualDate = bookingActualDate;
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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }
}
