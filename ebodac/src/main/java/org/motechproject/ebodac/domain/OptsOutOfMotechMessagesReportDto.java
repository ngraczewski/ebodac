package org.motechproject.ebodac.domain;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.motechproject.ebodac.util.CustomDateSerializer;
import org.motechproject.ebodac.util.CustomSubjectSerializer;

@JsonAutoDetect
public class OptsOutOfMotechMessagesReportDto {

    @JsonProperty
    private Subject subject;

    @JsonProperty
    private int age;

    @JsonProperty
    private LocalDate dateOfUnenrollment;

    public OptsOutOfMotechMessagesReportDto(SubjectEnrollments subjectEnrollments) {
        subject = subjectEnrollments.getSubject();
        if (subject.getDateOfBirth() == null) {
            age = 0;
        } else {
            age = Years.yearsBetween(subject.getDateOfBirth(), LocalDate.now()).getYears();
        }
        dateOfUnenrollment = subjectEnrollments.getDateOfUnenrollment();
    }

    @JsonSerialize(using = CustomSubjectSerializer.class)
    public Subject getSubject() {
        return subject;
    }

    public int getAge() {
        return age;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getDateOfUnenrollment() {
        return dateOfUnenrollment;
    }
}
