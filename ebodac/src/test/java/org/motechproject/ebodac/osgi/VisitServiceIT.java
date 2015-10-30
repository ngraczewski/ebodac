package org.motechproject.ebodac.osgi;


import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.ebodac.utils.VisitUtils;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class VisitServiceIT extends BasePaxIT {

    @Inject
    private VisitService visitService;

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    private VisitDataService visitDataService;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

    @Before
    public void cleanBefore() {
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @After
    public void cleanAfter() {
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
    }

    @Test
    public void shouldCreateOrUpdate() {
        Subject firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001", "chiefdom", "section", "district");

        Subject secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address", Language.Susu, "community", "B05-SL10001", "chiefdom", "section", "district");

        Visit firstVisit = VisitUtils.createVisit(firstSubject, VisitType.SCREENING, LocalDate.parse("2014-10-17", formatter),
                LocalDate.parse("2014-10-18", formatter), "owner");

        Visit secondVisit = VisitUtils.createVisit(firstSubject, VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT, LocalDate.parse("2014-10-19", formatter),
                LocalDate.parse("2014-10-20", formatter), "owner");


        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        visitService.create(firstVisit);
        List<Visit> visits = visitDataService.retrieveAll();
        assertEquals(1, visits.size());

        firstVisit = visits.get(0);
        firstVisit.setDateProjected(LocalDate.parse("2014-10-20", formatter));
        firstVisit.setSubject(firstSubject);

        visitService.createOrUpdate(firstVisit); //should update
        visits = visitDataService.retrieveAll();
        assertEquals(1, visits.size());

        VisitUtils.checkVisitFields(firstVisit, visits.get(0));

        visitService.createOrUpdate(secondVisit); // should create
        visits = visitDataService.retrieveAll();
        assertEquals(2, visits.size());

        visits = visitDataService.findVisitsByActualDate(LocalDate.parse("2014-10-19", formatter));
        assertEquals(1, visits.size());

        VisitUtils.checkVisitFields(secondVisit, visits.get(0));

        secondVisit.setDate(LocalDate.parse("2014-10-21", formatter));
        secondVisit.setSubject(secondSubject);

        visitService.update(secondVisit);
        visits = visitDataService.retrieveAll();
        assertEquals(2, visits.size());

        visits = visitDataService.findVisitsByActualDate(LocalDate.parse("2014-10-21", formatter));
        assertEquals(1, visits.size());

        VisitUtils.checkVisitFields(secondVisit, visits.get(0));
    }

    @Test
    public void shouldFetchWithNullPhone() {
        Subject subject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                null , "address", Language.English, "community", "B05-SL10001", "chiefdom", "section", "district");

        Visit visit = VisitUtils.createVisit(subject, VisitType.SCREENING, null,
                LocalDate.parse("2014-10-17", formatter), "owner");

        visitService.create(visit);

        List<Visit> visits = visitDataService.findVisitsByPlannedDateLessAndActualDateEqAndSubjectPhoneNumberEq(null,
                new LocalDate("2015-10-19"), null);

        assertEquals(1, visits.size());

    }

    @Test
    public void shouldDelete() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        Subject firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001", "chiefdom", "section", "district");

        Visit firstVisit = VisitUtils.createVisit(firstSubject, VisitType.SCREENING, LocalDate.parse("2014-10-17", formatter),
                LocalDate.parse("2014-10-18", formatter), "owner");

        Visit secondVisit = VisitUtils.createVisit(firstSubject, VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT, LocalDate.parse("2014-10-19", formatter),
                LocalDate.parse("2014-10-20", formatter), "owner");


        visitService.createOrUpdate(firstVisit);
        visitService.createOrUpdate(secondVisit);

        assertEquals(2, visitDataService.retrieveAll().size());

        visitService.delete(firstVisit);
        List<Visit> visits = visitDataService.retrieveAll();
        assertEquals(1, visits.size());
    }

}
