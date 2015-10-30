package org.motechproject.ebodac.domain;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.ebodac.util.CustomDateSerializer;
import org.motechproject.ebodac.util.CustomSubjectSerializer;
import org.motechproject.ebodac.util.CustomVisitTypeSerializer;

@JsonAutoDetect
public class MissedVisitsReportDto{

    @JsonProperty
    private int noOfDaysExceededVisit;

    @JsonProperty
    private Subject subject;

    @JsonProperty
    private VisitType type;

    @JsonProperty
    private LocalDate motechProjectedDate;

    public MissedVisitsReportDto(Visit entityObject) {
        motechProjectedDate = entityObject.getMotechProjectedDate();
        if(motechProjectedDate == null) {
            noOfDaysExceededVisit = 0;
        } else {
            noOfDaysExceededVisit = Days.daysBetween(motechProjectedDate, LocalDate.now()).getDays();
        }

        subject = entityObject.getSubject();
        type = entityObject.getType();
        motechProjectedDate = entityObject.getMotechProjectedDate();
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getPlanedVisitDate() {
        return motechProjectedDate;
    }

    public Integer getNoOfDaysExceededVisit() {
        return noOfDaysExceededVisit;
    }

    @JsonSerialize(using = CustomSubjectSerializer.class)
    public Subject getSubject() {
        return subject;
    }

    @JsonSerialize(using = CustomVisitTypeSerializer.class)
    public VisitType getType() {
        return type;
    }

    @JsonSerialize(using = CustomDateSerializer.class)
    public LocalDate getMotechProjectedDate() {
        return motechProjectedDate;
    }
}
