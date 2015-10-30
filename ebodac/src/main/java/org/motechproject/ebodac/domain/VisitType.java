package org.motechproject.ebodac.domain;

public enum VisitType {
    SCREENING("Screening"),
    PRIME_VACCINATION_DAY("Prime Vaccination Day"),
    PRIME_VACCINATION_FOLLOW_UP_VISIT("Prime Vaccination Follow-up visit"),
    BOOST_VACCINATION_DAY("Boost Vaccination Day"),
    BOOST_VACCINATION_FIRST_FOLLOW_UP_VISIT("Boost Vaccination First Follow-up visit"),
    BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT("Boost Vaccination Second Follow-up visit"),
    BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT("Boost Vaccination Third Follow-up visit"),
    FIRST_LONG_TERM_FOLLOW_UP_VISIT("First Long-term Follow-up visit"),
    SECOND_LONG_TERM_FOLLOW_UP_VISIT("Second Long-term Follow-up visit"),
    THIRD_LONG_TERM_FOLLOW_UP_VISIT("Third Long-term Follow-up visit"),
    UNSCHEDULED_VISIT("Unscheduled Visit");

    private String value;

    private VisitType(String value) {
        this.value = value;
    }

    public static VisitType getByValue(String value) {
        if (value != null && value.startsWith(UNSCHEDULED_VISIT.getValue())) {
            return UNSCHEDULED_VISIT;
        }
        for (VisitType visitType : VisitType.values()) {
            if (visitType.getValue().equals(value)) {
                return visitType;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}
