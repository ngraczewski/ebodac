package org.motechproject.bookingapp.domain;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.UIDisplayable;

@Entity(maxFetchDepth = 2)
public class Clinic {

    @Field
    private Long id;

    @UIDisplayable(position = 0)
    @Field(required = true)
    @JsonBackReference
    private Site site;

    @UIDisplayable(position = 1)
    @Field(required = true)
    private String location;

    @UIDisplayable(position = 2)
    @Field(displayName = "Amount of Rooms", required = true)
    private Integer numberOfRooms;

    @UIDisplayable(position = 3)
    @Field
    private Integer maxScreeningVisits;

    @UIDisplayable(position = 4)
    @Field
    private Integer maxPrimeVisits;

    @UIDisplayable(position = 5)
    @Field
    private Integer maxPrimeFollowUpVisits;

    @UIDisplayable(position = 6)
    @Field
    private Integer maxBoosterVisits;

    @UIDisplayable(position = 7)
    @Field
    private Integer maxBoosterFirstFollowUpVisits;

    @UIDisplayable(position = 8)
    @Field
    private Integer maxBoosterSecondFollowUpVisits;

    @UIDisplayable(position = 9)
    @Field
    private Integer maxBoosterThirdFollowUpVisits;

    @UIDisplayable(position = 10)
    @Field
    private Integer maxFirstLongTermFollowUpVisits;

    @UIDisplayable(position = 11)
    @Field
    private Integer maxSecondLongTermFollowUpVisits;

    @UIDisplayable(position = 12)
    @Field
    private Integer maxThirdLongTermFollowUpVisits;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
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

    public Integer getMaxBoosterFirstFollowUpVisits() {
        return maxBoosterFirstFollowUpVisits;
    }

    public void setMaxBoosterFirstFollowUpVisits(Integer maxBoosterFirstFollowUpVisits) {
        this.maxBoosterFirstFollowUpVisits = maxBoosterFirstFollowUpVisits;
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

    public Integer getMaxFirstLongTermFollowUpVisits() {
        return maxFirstLongTermFollowUpVisits;
    }

    public void setMaxFirstLongTermFollowUpVisits(Integer maxFirstLongTermFollowUpVisits) {
        this.maxFirstLongTermFollowUpVisits = maxFirstLongTermFollowUpVisits;
    }

    public Integer getMaxSecondLongTermFollowUpVisits() {
        return maxSecondLongTermFollowUpVisits;
    }

    public void setMaxSecondLongTermFollowUpVisits(Integer maxSecondLongTermFollowUpVisits) {
        this.maxSecondLongTermFollowUpVisits = maxSecondLongTermFollowUpVisits;
    }

    public Integer getMaxThirdLongTermFollowUpVisits() {
        return maxThirdLongTermFollowUpVisits;
    }

    public void setMaxThirdLongTermFollowUpVisits(Integer maxThirdLongTermFollowUpVisits) {
        this.maxThirdLongTermFollowUpVisits = maxThirdLongTermFollowUpVisits;
    }
}

