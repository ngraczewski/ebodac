package org.motechproject.ebodac.osgi;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.MissedVisitsReportDto;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.service.ReportService;
import org.motechproject.ebodac.utils.VisitUtils;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.dto.LookupDto;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class LookupServiceIT extends BasePaxIT{

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    @Inject
    private LookupService lookupService;

    @Inject
    private ReportService reportService;

    @Inject
    private  ReportBoosterVaccinationDataService reportBoosterVaccinationDataService;

    @Inject
    private  ReportPrimerVaccinationDataService reportPrimerVaccinationDataService;

    @Inject
    ReportPrimerVaccinationDataService primerVaccinationDataService;

    @Inject
    ReportBoosterVaccinationDataService boosterVaccinationDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    private Subject firstSubject;

    private Subject secondSubject;

    private ArrayList<Visit> testVisits = new ArrayList<Visit>();

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

    @Before
    public void cleanBefore() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
        reportBoosterVaccinationDataService.deleteAll();
        reportPrimerVaccinationDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        resetTestFields();
    }

    @Before
    public void cleanAfter() {
        visitDataService.deleteAll();
        subjectDataService.deleteAll();
        reportPrimerVaccinationDataService.deleteAll();
        reportBoosterVaccinationDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
    }

    private void resetTestFields() {
        firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001", "chiefdom", "section", "district");

        secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address1", Language.Susu, "community", "B05-SL10001", "chiefdom", "section", "district");

        firstSubject.setDateOfBirth(LocalDate.parse("1967-09-17", formatter));
        firstSubject.setGender(Gender.Male);
        firstSubject.setPrimerVaccinationDate(LocalDate.parse("2014-10-16", formatter));
        firstSubject.setBoosterVaccinationDate(LocalDate.parse("2014-10-16", formatter));

        secondSubject.setDateOfBirth(LocalDate.parse("2005-08-04", formatter));
        secondSubject.setGender(Gender.Male);
        secondSubject.setPrimerVaccinationDate(LocalDate.parse("2014-10-17", formatter));
        secondSubject.setBoosterVaccinationDate(LocalDate.parse("2014-10-17", formatter));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.SCREENING, LocalDate.parse("2014-10-17", formatter),
                LocalDate.parse("2014-10-21", formatter), "owner"));

        testVisits.add(VisitUtils.createVisit(secondSubject, VisitType.SCREENING, LocalDate.parse("2014-10-19", formatter),
                LocalDate.parse("2014-10-21", formatter), "owner"));

        testVisits.add(VisitUtils.createVisit(secondSubject, VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT, LocalDate.parse("2014-10-21", formatter),
                LocalDate.parse("2014-10-23", formatter), "owner"));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.BOOST_VACCINATION_DAY, LocalDate.parse("2014-10-22", formatter),
                LocalDate.parse("2014-10-24", formatter), "owner"));
    }

    private void addTestVisitsToDB() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        for(Visit visit : testVisits) {
            visitDataService.create(visit);
        }

        assertEquals(2, subjectDataService.retrieveAll().size());
        assertEquals(4, visitDataService.retrieveAll().size());
    }

    @Test
    public void shouldGetVisitEntitiesFromLookup() throws IOException {
        addTestVisitsToDB();
        String []fields = {
                "{\"type\":\"SCREENING\"}",
                "{\"subject.subjectId\":\"1000000162\"}",
                "{\"subject.address\":\"address\"}",
                "{\"subject.name\":\"Michal\"}",
                "{\"motechProjectedDate\":{\"min\":\"2014-10-18\",\"max\":\"2014-10-21\"}, \"type\":\"SCREENING\"}",
                "{\"motechProjectedDate\":{\"min\":\"2014-10-16\",\"max\":\"2014-10-23\"}}",
                "{\"motechProjectedDate\":\"2014-10-21\", \"type\":\"SCREENING\"}",
                "{\"motechProjectedDate\":\"2014-10-21\"}"

        };
        String []lookups = {
                "Find Visits By Type",
                "Find Visits By Participant Id",
                "Find Visits By Participant Address",
                "Find Visits By Participant Name",
                "Find Visits By Planned Visit Date Range And Type",
                "Find Visits By Planned Visit Date Range",
                "Find Visits By Planned Visit Date And Type",
                "Find Visits By Planned Visit Date"
        };
        int []expectedResults = { 2, 2, 4, 2, 2, 3, 2, 2};

        List<LookupDto> lookupDtos= lookupService.getAvailableLookups(Visit.class.getName());

        List<Visit> visitList1 = visitDataService.retrieveAll();

        QueryParams queryParams = new QueryParams(1, null);
        for( int i = 0 ; i < lookups.length; i ++) {
            Records<Visit> records = lookupService.getEntities(Visit.class, lookups[i], fields[i], queryParams);
            List<Visit> visitList = records.getRows();
            assertEquals(expectedResults[i], visitList.size());
        }
    }

    @Test
    public void shouldGetSubjectEntitiesFromLookup() throws IOException {
        addTestVisitsToDB();
        String []fields = {
                "{\"primerVaccinationDate\":\"2014-10-16\"}",
                "{\"boosterVaccinationDate\":{\"min\":\"2014-10-15\",\"max\":\"2014-10-18\"}}",
                "{\"name\":\"Michal\"}",
                "{\"boosterVaccinationDate\":\"2014-10-16\"}",
                "{\"changed\":\"false\"}",
                "{\"address\":\"address\"}"
        };
        String []lookups = {
                "Find Participants By Primer Vaccination Date",
                "Find Participants By Booster Vaccination Date Range",
                "Find Participants By Name",
                "Find Participants By Booster Vaccination Date",
                "Find Participants By Modified",
                "Find Participants By Address",
        };
        int []expectedResults = { 1, 2, 1, 1, 2, 2};

        List<Subject> subject = subjectDataService.retrieveAll();
        QueryParams queryParams = new QueryParams(1, null);
        for( int i = 0 ; i < lookups.length; i ++) {
            Records<Subject> records = lookupService.getEntities(Subject.class, lookups[i], fields[i], queryParams);
            List<Subject> subjectList = records.getRows();
            assertEquals(expectedResults[i], subjectList.size());
        }
    }

    @Test
    public void shouldGetReportPrimerEntitiesFromLookup() throws IOException {
        try {
            fakeNow(newDateTime(2014, 10, 21, 1, 0, 0));
            reportService.generateDailyReportsFromDate(new LocalDate(2014, 10, 15));

            String[] fields = {
                    "{\"date\":{\"min\":\"2014-10-15\",\"max\":\"2014-10-18\"}}",
                    "{\"date\":\"2014-10-16\"}",
            };
            String[] lookups = {
                    "Find Reports By Date Range",
                    "Find Report By Date"
            };
            int[] expectedResults = {4, 1};

            QueryParams queryParams = new QueryParams(1, null);
            for (int i = 0; i < lookups.length; i++) {
                Records<ReportPrimerVaccination> records = lookupService.getEntities(ReportPrimerVaccination.class, lookups[i], fields[i], queryParams);
                List<ReportPrimerVaccination> reportPrimerVaccinations = records.getRows();
                assertEquals(expectedResults[i], reportPrimerVaccinations.size());
            }
        } finally {
            stopFakingTime();
        }
    }


    @Test
    public void shouldGetReportBoosterEntitiesFromLookup() throws IOException {
        try {
            fakeNow(newDateTime(2014, 10, 21, 1, 0, 0));
            reportService.generateDailyReportsFromDate(new LocalDate(2014, 10, 15));
            String[] fields = {
                    "{\"date\":{\"min\":\"2014-10-16\",\"max\":\"2014-10-18\"}}",
                    "{\"date\":\"2014-10-19\"}",
            };
            String[] lookups = {
                    "Find Reports By Date Range",
                    "Find Report By Date"
            };
            int[] expectedResults = {3, 1};

            QueryParams queryParams = new QueryParams(1, null);
            for (int i = 0; i < lookups.length; i++) {
                Records<ReportBoosterVaccination> records = lookupService.getEntities(ReportBoosterVaccination.class, lookups[i], fields[i], queryParams);
                List<ReportBoosterVaccination> reportBoosterVaccinations = records.getRows();
                assertEquals(expectedResults[i], reportBoosterVaccinations.size());
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGetMissedVisitReportEntitiesFromLookup() throws IOException {
        addTestVisitsToDB();
        String []fields = {
                "{\"subject.name\":\"Michal\"}",
                "{\"subject.address\":\"address\"}",
                "{\"motechProjectedDate\":{\"min\":\"2014-10-18\",\"max\":\"2014-10-21\"}, \"type\":\"SCREENING\"}",
                "{\"motechProjectedDate\":{\"min\":\"2014-10-16\",\"max\":\"2014-10-23\"}}",
                "{\"motechProjectedDate\":\"2014-10-21\", \"type\":\"SCREENING\"}",
                "{\"motechProjectedDate\":\"2014-10-21\"}",
                "{\"subject.community\":\"community\"}"
        };
        String []lookups = {
                "Find Visits By Participant Name",
                "Find Visits By Participant Address",
                "Find Visits By Planned Visit Date Range And Type",
                "Find Visits By Planned Visit Date Range",
                "Find Visits By Planned Visit Date And Type",
                "Find Visits By Planned Visit Date",
                "Find Visits By Participant Community"
        };
        int []expectedResults = { 2, 4, 2, 3, 2, 2, 4};

        QueryParams queryParams = new QueryParams(1, null);
        for( int i = 0 ; i < lookups.length; i ++) {
            Records<?> records = lookupService.getEntities(MissedVisitsReportDto.class, Visit.class, lookups[i], fields[i], queryParams);
            List<?> missedVisitsReportDtos = records.getRows();
            assertEquals(expectedResults[i], missedVisitsReportDtos.size());
        }
    }


}
