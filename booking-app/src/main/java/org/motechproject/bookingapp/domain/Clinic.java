package org.motechproject.bookingapp.domain;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.domain.MdsEntity;

import java.util.List;

@Entity
public class Clinic extends MdsEntity {

    @Field(required = true)
    @JsonBackReference
    private Site site;

    @Field(required = true)
    private String location;

    @Field
    private List<Room> rooms;

    @Field
    private Integer maxScreeningVisits;

    @Field
    private Integer maxPrimeVisits;

    @Field
    private Integer maxPrimeFollowUpVisits;

    @Field
    private Integer maxBoosterVisits;

    @Field
    private Integer maxBoosterFollowUpVisits;

    @Field
    private Integer maxBoosterSecondFollowUpVisits;

    @Field
    private Integer maxBoosterThirdFollowUpVisits;

    @Field
    private Integer maxLongTermFollowUpVisits;

    @Field
    private Integer maxLongTermSecondFollowUpVisits;

    @Field
    private Integer maxLongTestThirdFollowUpVisits;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public Integer getMaxScreeningVisits() {
        return maxScreeningVisits;
    }

    public void setMaxScreeningVisits(Integer maxScreeningVisits) {
        this.maxScreeningVisits = maxScreeningVisits;
    }

    public Integer getMaxPrimeVisits() {
        return maxPrimeVisits;
    }

    public void setMaxPrimeVisits(Integer maxPrimeVisits) {
        this.maxPrimeVisits = maxPrimeVisits;
    }

    public Integer getMaxPrimeFollowUpVisits() {
        return maxPrimeFollowUpVisits;
    }

    public void setMaxPrimeFollowUpVisits(Integer maxPrimeFollowUpVisits) {
        this.maxPrimeFollowUpVisits = maxPrimeFollowUpVisits;
    }

    public Integer getMaxBoosterVisits() {
        return maxBoosterVisits;
    }

    public void setMaxBoosterVisits(Integer maxBoosterVisits) {
        this.maxBoosterVisits = maxBoosterVisits;
    }

    public Integer getMaxBoosterFollowUpVisits() {
        return maxBoosterFollowUpVisits;
    }

    public void setMaxBoosterFollowUpVisits(Integer maxBoosterFollowUpVisits) {
        this.maxBoosterFollowUpVisits = maxBoosterFollowUpVisits;
    }

    public Integer getMaxBoosterSecondFollowUpVisits() {
        return maxBoosterSecondFollowUpVisits;
    }

    public void setMaxBoosterSecondFollowUpVisits(Integer maxBoosterSecondFollowUpVisits) {
        this.maxBoosterSecondFollowUpVisits = maxBoosterSecondFollowUpVisits;
    }

    public Integer getMaxBoosterThirdFollowUpVisits() {
        return maxBoosterThirdFollowUpVisits;
    }

    public void setMaxBoosterThirdFollowUpVisits(Integer maxBoosterThirdFollowUpVisits) {
        this.maxBoosterThirdFollowUpVisits = maxBoosterThirdFollowUpVisits;
    }

    public Integer getMaxLongTermFollowUpVisits() {
        return maxLongTermFollowUpVisits;
    }

    public void setMaxLongTermFollowUpVisits(Integer maxLongTermFollowUpVisits) {
        this.maxLongTermFollowUpVisits = maxLongTermFollowUpVisits;
    }

    public Integer getMaxLongTermSecondFollowUpVisits() {
        return maxLongTermSecondFollowUpVisits;
    }

    public void setMaxLongTermSecondFollowUpVisits(Integer maxLongTermSecondFollowUpVisits) {
        this.maxLongTermSecondFollowUpVisits = maxLongTermSecondFollowUpVisits;
    }

    public Integer getMaxLongTestThirdFollowUpVisits() {
        return maxLongTestThirdFollowUpVisits;
    }

    public void setMaxLongTestThirdFollowUpVisits(Integer maxLongTestThirdFollowUpVisits) {
        this.maxLongTestThirdFollowUpVisits = maxLongTestThirdFollowUpVisits;
    }
}

