package org.motechproject.ebodac.domain;

/**
 * Represents type of phone Subject want to register
 */
public enum PhoneType {
    PERSONAL("Personal"),
    SHARED("Shared");

    private String value;

    private PhoneType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
