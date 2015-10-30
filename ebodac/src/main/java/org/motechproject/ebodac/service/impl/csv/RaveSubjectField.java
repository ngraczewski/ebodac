package org.motechproject.ebodac.service.impl.csv;


public enum RaveSubjectField {
    SiteNumber("siteId"),
    Subject("subjectId"),
    BRTHDT("dateOfBirth"),
    SEX("gender"),
    STAGE("stageId"),
    PRMDT("primerVaccinationDate"),
    BOOSTDT("boosterVaccinationDate"),
    VACDSDT("dateOfDisconVac"),
    TRDSDT("dateOfDisconStd");

    private String value;

    private RaveSubjectField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}