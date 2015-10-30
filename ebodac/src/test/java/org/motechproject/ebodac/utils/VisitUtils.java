package org.motechproject.ebodac.utils;


import org.joda.time.LocalDate;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;

import static junit.framework.Assert.assertEquals;

public class VisitUtils {

    public static Visit createVisit(Subject subject, VisitType type, LocalDate date,
                              LocalDate projectedDate, String owner) {
        Visit ret = new Visit();
        ret.setSubject(subject);
        ret.setType(type);
        ret.setDate(date);
        ret.setDateProjected(projectedDate);
        ret.setMotechProjectedDate(projectedDate);
        ret.setOwner(owner);

        return ret;
    }

    public static void checkVisitFields(Visit expected, Visit actual) {
        assertEquals(expected.getSubject().getSubjectId(), actual.getSubject().getSubjectId());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getMotechProjectedDate(), actual.getMotechProjectedDate());
        assertEquals(expected.getOwner(), actual.getOwner());
    }

}
