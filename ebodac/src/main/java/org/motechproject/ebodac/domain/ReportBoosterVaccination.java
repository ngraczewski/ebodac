package org.motechproject.ebodac.domain;

import org.joda.time.LocalDate;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.UIDisplayable;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.util.SecurityMode;

@Access(value = SecurityMode.PERMISSIONS, members = {"manageEbodac"})
@Entity
public class ReportBoosterVaccination extends ReportVaccinationAbstract {

    @NonEditable
    @UIDisplayable(position = 1)
    @Field
    private Integer peopleBoostered;

    public ReportBoosterVaccination() {
    }

    public ReportBoosterVaccination(LocalDate date, Integer adultMales, Integer adultFemales, Integer childrenFrom1To5, Integer childrenFrom6To11,
                                    Integer childrenFrom12To17, Integer adultUnidentified, Integer adultUndifferentiated, Integer peopleBoostered) {
        super(date, adultMales, adultFemales, childrenFrom1To5, childrenFrom6To11, childrenFrom12To17, adultUnidentified, adultUndifferentiated);
        this.peopleBoostered = peopleBoostered;
    }

    public Integer getPeopleBoostered() {
        return peopleBoostered;
    }

    public void setPeopleBoostered(Integer peopleBoostered) {
        this.peopleBoostered = peopleBoostered;
    }

    public void updateReportData(Integer adultMales, Integer adultFemales, Integer childrenFrom1To5, Integer childrenFrom6To11,
                                 Integer childrenFrom12To17, Integer adultUnidentified, Integer adultUndifferentiated, Integer peopleBoostered) {
        updateReportData(adultMales, adultFemales, childrenFrom1To5, childrenFrom6To11, childrenFrom12To17, adultUnidentified, adultUndifferentiated);
        this.peopleBoostered = peopleBoostered;
    }
}
