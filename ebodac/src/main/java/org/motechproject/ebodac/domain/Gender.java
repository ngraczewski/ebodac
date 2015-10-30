package org.motechproject.ebodac.domain;

public enum Gender {
    Male("M"),
    Female("F"),
    Unknown("U"),
    Undifferentiated("UN");

    private String value;

    private Gender(String value) {
        this.value = value;
    }

    public static Gender getByValue(String value) {
        for (Gender gender : Gender.values()) {
            if (gender.getValue().equals(value)) {
                return gender;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}