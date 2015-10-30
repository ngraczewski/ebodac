package org.motechproject.ebodac.domain;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.motechproject.ebodac.util.CustomDateDeserializer;
import org.motechproject.ebodac.util.CustomDateSerializer;
import org.motechproject.ebodac.util.CustomSubjectSerializer;
import org.motechproject.ebodac.util.CustomVisitTypeDeserializer;
import org.motechproject.ebodac.util.CustomVisitTypeSerializer;
import org.motechproject.mds.annotations.Access;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.EnumDisplayName;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.NonEditable;
import org.motechproject.mds.util.SecurityMode;


@Access(value = SecurityMode.PERMISSIONS, members = {"manageEbodac"})
@Entity(recordHistory = true)
public class Visit {

    public static final String MOTECH_PROJECTED_DATE_PROPERTY_NAME = "motechProjectedDate";
    public static final String ACTUAL_VISIT_DATE_PROPERTY_NAME = "date";
    public static final String VISIT_TYPE_PROPERTY_NAME = "type";
    public static final String SUBJECT_PHONE_NUMBER_PROPERTY_NAME = "subject.phoneNumber";
    public static final String SUBJECT_ADDRESS_PROPERTY_NAME = "subject.address";
    public static final String SUBJECT_PRIME_VACCINATION_DATE_PROPERTY_NAME = "subject.primerVaccinationDate";

    @NonEditable
    @Field(displayName = "Participant")
    private Subject subject;

    @NonEditable
    @Field(displayName = "Visit Type")
    @EnumDisplayName(enumField="value")
    private VisitType type;

    @NonEditable
    @Field(displayName = "Actual Visit Date")
    private LocalDate date;

    @NonEditable(display = false)
    @Field(displayName = "RAVE Planned Date")
    private LocalDate dateProjected;

    @Field(displayName = "Planned Visit Date")
    private LocalDate motechProjectedDate;

    @NonEditable(display = false)
    @Field
    private String owner;

    @JsonSerialize(using = CustomSubjectSerializer.class)
    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    @JsonSerialize(using = CustomVisitTypeSerializer.class)
    public VisitType getType() {
        return type;
    }

    @JsonDeserialize(using = CustomVisitTypeDeserializer.class)
    public void setType(VisitType type) {
        this.type = type;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getDate() {
        return date;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getDateProjected() {
        return dateProjected;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setDateProjected(LocalDate dateProjected) {
        this.dateProjected = dateProjected;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getMotechProjectedDate() {
        return motechProjectedDate;
    }

    @JsonDeserialize(using = CustomDateDeserializer.class)
    public void setMotechProjectedDate(LocalDate motechProjectedDate) {
        this.motechProjectedDate = motechProjectedDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override //NO CHECKSTYLE CyclomaticComplexity
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Visit visit = (Visit) o;

        if (subject != null && visit.getSubject() != null) {
            if (!subject.getSubjectId().equals(visit.getSubject().getSubjectId())) {
                return false;
            }
        } else if (subject != null || visit.getSubject() != null) {
            return false;
        }
        if (getType() != null && getType().equals(VisitType.UNSCHEDULED_VISIT)
                && visit.getType() != null && visit.getType().equals(getType())) {
            return (getDate() != null && visit.getDate() != null
                    && getDate().equals(visit.getDate()))
                    || (getDate() == null && visit.getDate() == null);
        }
        return getType() != null && getType().equals(visit.getType());
    }

    @Override
    public int hashCode() {
        int result = subject != null ? subject.getSubjectId().hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public boolean visitDatesChanged(Visit visit) {
        if (date != null ? !date.equals(visit.date) : visit.date != null) {
            return true;
        }
        if (dateProjected != null ? !dateProjected.equals(visit.dateProjected) : visit.dateProjected != null) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return type.getValue() +
                (getDateProjected() != null ? " / Planned Date: " + getDateProjected().toString() : "") +
                (getDate() != null ? " / Actual Date: " + getDate().toString() : "");
    }
}
