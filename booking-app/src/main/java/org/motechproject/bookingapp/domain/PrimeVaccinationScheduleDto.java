package org.motechproject.bookingapp.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.bookingapp.util.CustomDateDeserializer;
import org.motechproject.bookingapp.util.CustomDateSerializer;
import org.motechproject.bookingapp.util.CustomTimeSerializer;
import org.motechproject.commons.date.model.Time;
import org.motechproject.ebodac.domain.Gender;

public class PrimeVaccinationScheduleDto {

    private Long visitBookingDetailsId;

    private String location;

    private String participantId;

    private String participantName;

    private Gender participantGender;

    private Boolean femaleChildBearingAge;

    private LocalDate actualScreeningDate;

    private LocalDate date;

    private Time startTime;

    private Time endTime;

    private Long siteId;

    private Long clinicId;

    private Long visitId;

    public Long getVisitBookingDetailsId() {
        return visitBookingDetailsId;
    }

    public void setVisitBookingDetailsId(Long visitBookingDetailsId) {
        this.visitBookingDetailsId = visitBookingDetailsId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public Gender getParticipantGender() {
        return participantGender;
    }

    public void setParticipantGender(Gender participantGender) {
        this.participantGender = participantGender;
    }

    public Boolean getFemaleChildBearingAge() {
        return femaleChildBearingAge;
    }

    public void setFemaleChildBearingAge(Boolean femaleChildBearingAge) {
        this.femaleChildBearingAge = femaleChildBearingAge;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getActualScreeningDate() {
        return actualScreeningDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setActualScreeningDate(LocalDate actualScreeningDate) {
        this.actualScreeningDate = actualScreeningDate;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getDate() {
        return date;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @JsonSerialize(using = CustomTimeSerializer.class)
    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    @JsonSerialize(using = CustomTimeSerializer.class)
    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public Long getClinicId() {
        return clinicId;
    }

    public void setClinicId(Long clinicId) {
        this.clinicId = clinicId;
    }

    public Long getVisitId() {
        return visitId;
    }

    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }
}
