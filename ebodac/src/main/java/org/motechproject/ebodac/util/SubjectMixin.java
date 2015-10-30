package org.motechproject.ebodac.util;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

// View definitions:
class Views {
    static class Zetes {
    }
}

abstract class SubjectMixin {

    @JsonView(Views.Zetes.class)
    private String subjectId;

    @JsonView(Views.Zetes.class)
    private String name;

    @JsonView(Views.Zetes.class)
    private String householdName;

    @JsonView(Views.Zetes.class)
    private String headOfHousehold;

    @JsonView(Views.Zetes.class)
    private String phoneNumber;

    @JsonView(Views.Zetes.class)
    private String address;
    
    @JsonView(Views.Zetes.class)
    private String community;

    @JsonProperty("language")
    @JsonView(Views.Zetes.class)
    abstract String getLanguageCode();

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHouseholdName() {
        return householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }

    public String getHeadOfHousehold() {
        return headOfHousehold;
    }

    public void setHeadOfHousehold(String headOfHousehold) {
        this.headOfHousehold = headOfHousehold;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }
}
