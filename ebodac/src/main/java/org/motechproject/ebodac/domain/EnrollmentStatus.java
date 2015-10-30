package org.motechproject.ebodac.domain;

public enum EnrollmentStatus {
    INITIAL("Initial"),
    ENROLLED("Enrolled"),
    UNENROLLED("Unenrolled"),
    COMPLETED("Completed"),
    UNENROLLED_FROM_BOOSTER("Unenrolled from Booster"),
    WITHDRAWN_FROM_STUDY("Withdrawn from Study");

    private String value;

    EnrollmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
