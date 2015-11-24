package org.motechproject.bookingapp.domain;

import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity(maxFetchDepth = 3)
public class VisitBookingDetails {

    public static final String VISIT_TYPE_PROPERTY_NAME = "visit.type";
    public static final String SUBJECT_PRIME_VACCINATION_DATE_PROPERTY_NAME = "subject.primerVaccinationDate";

    @Field
    private Long id;

    @Field
    private LocalDate bookingPlannedDate;

    @Field
    private LocalDate bookingActualDate;

    @Field
    private Time startTime;

    @Field
    private Time endTime;

    @Field
    private Boolean femaleChildBearingAge;

    @Field
    private Clinic clinic;

    @Field(required = true)
    private Visit visit;

    @Field(required = true)
    private Subject subject;

    public VisitBookingDetails() {
    }

    public VisitBookingDetails(LocalDate bookingPlannedDate, Visit visit) {
        this.bookingPlannedDate = bookingPlannedDate;
        this.visit = visit;
        this.subject = visit.getSubject();
    }

    public VisitBookingDetails(Visit visit, LocalDate bookingActualDate) {
        this.visit = visit;
        this.bookingActualDate = bookingActualDate;
        this.subject = visit.getSubject();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean getFemaleChildBearingAge() {
        return femaleChildBearingAge;
    }

    public void setFemaleChildBearingAge(Boolean femaleChildBearingAge) {
        this.femaleChildBearingAge = femaleChildBearingAge;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
        if (visit != null) {
            this.subject = visit.getSubject();
        }
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
