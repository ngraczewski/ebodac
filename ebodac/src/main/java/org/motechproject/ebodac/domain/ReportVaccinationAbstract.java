package org.motechproject.ebodac.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.ebodac.util.CustomDateDeserializer;
import org.motechproject.ebodac.util.CustomDateSerializer;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.annotations.UIDisplayable;

import javax.jdo.annotations.Unique;

@Entity
public abstract class ReportVaccinationAbstract {

    public static final String DATE_PROPERTY_NAME = "date";

    @NonEditable
    @Unique
    @UIDisplayable(position = 0)
    @Field
    private LocalDate date;

    @NonEditable
    @UIDisplayable(position = 2)
    @Field
    private Integer adultMales;

    @NonEditable
    @UIDisplayable(position = 3)
    @Field
    private Integer adultFemales;

    @NonEditable
    @UIDisplayable(position = 8)
    @Field(displayName = "Children 1-5", name = "children_1_5")
    private Integer childrenFrom1To5;

    @NonEditable
    @UIDisplayable(position = 7)
    @Field(displayName = "Children 6-11", name = "children_6_11")
    private Integer childrenFrom6To11;

    @NonEditable
    @UIDisplayable(position = 6)
    @Field(displayName = "Children 12-17", name = "children_12_17")
    private Integer childrenFrom12To17;

    @NonEditable
    @UIDisplayable(position = 4)
    @Field
    private Integer adultUnidentified;

    @NonEditable
    @UIDisplayable(position = 5)
    @Field
    private Integer adultUndifferentiated;

    @NonEditable(display = false)
    @Field
    private String owner;

    public ReportVaccinationAbstract() {
    }

    public ReportVaccinationAbstract(LocalDate date, Integer adultMales, Integer adultFemales, Integer childrenFrom1To5, Integer childrenFrom6To11,
                                     Integer childrenFrom12To17, Integer adultUnidentified, Integer adultUndifferentiated) {
        this.date = date;
        this.adultMales = adultMales;
        this.adultFemales = adultFemales;
        this.childrenFrom1To5 = childrenFrom1To5;
        this.childrenFrom6To11 = childrenFrom6To11;
        this.childrenFrom12To17 = childrenFrom12To17;
        this.adultUnidentified = adultUnidentified;
        this.adultUndifferentiated = adultUndifferentiated;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getDate() {
        return date;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getAdultMales() {
        return adultMales;
    }

    public void setAdultMales(Integer adultMales) {
        this.adultMales = adultMales;
    }

    public Integer getAdultFemales() {
        return adultFemales;
    }

    public void setAdultFemales(Integer adultFemales) {
        this.adultFemales = adultFemales;
    }

    public Integer getChildrenFrom1To5() {
        return childrenFrom1To5;
    }

    public void setChildrenFrom1To5(Integer childrenFrom1To5) {
        this.childrenFrom1To5 = childrenFrom1To5;
    }

    public Integer getChildrenFrom6To11() {
        return childrenFrom6To11;
    }

    public void setChildrenFrom6To11(Integer childrenFrom6To11) {
        this.childrenFrom6To11 = childrenFrom6To11;
    }

    public Integer getChildrenFrom12To17() {
        return childrenFrom12To17;
    }

    public void setChildrenFrom12To17(Integer childrenFrom12To17) {
        this.childrenFrom12To17 = childrenFrom12To17;
    }

    public Integer getAdultUnidentified() {
        return adultUnidentified;
    }

    public void setAdultUnidentified(Integer adultUnidentified) {
        this.adultUnidentified = adultUnidentified;
    }

    public Integer getAdultUndifferentiated() {
        return adultUndifferentiated;
    }

    public void setAdultUndifferentiated(Integer adultUndifferentiated) {
        this.adultUndifferentiated = adultUndifferentiated;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void updateReportData(Integer adultMales, Integer adultFemales, Integer childrenFrom1To5, Integer childrenFrom6To11,
                                 Integer childrenFrom12To17, Integer adultUnidentified, Integer adultUndifferentiated) {
        this.adultMales = adultMales;
        this.adultFemales = adultFemales;
        this.childrenFrom1To5 = childrenFrom1To5;
        this.childrenFrom6To11 = childrenFrom6To11;
        this.childrenFrom12To17 = childrenFrom12To17;
        this.adultUnidentified = adultUnidentified;
        this.adultUndifferentiated = adultUndifferentiated;
    }
}
