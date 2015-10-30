package org.motechproject.ebodac.osgi;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.Language;
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
import org.motechproject.ebodac.util.CustomVisitTypeDeserializer;
import org.motechproject.ebodac.utils.VisitUtils;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ReportControllerIT extends BasePaxIT {

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    private VisitDataService visitDataService;

    @Inject
    private ReportPrimerVaccinationDataService primerVaccinationDataService;

    @Inject
    private ReportBoosterVaccinationDataService boosterVaccinationDataService;

    private Subject firstSubject;

    private Subject secondSubject;

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

    private ArrayList<Visit> testVisits = new ArrayList<Visit>();

    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException {
        createAdminUser();
        login();
        Thread.sleep(EbodacConstants.LOGIN_WAIT_TIME);
    }

    @Before
    public void cleanBefore(){
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        primerVaccinationDataService.deleteAll();
        resetTestFields();
    }

    @After
    public void cleanAfter() {
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        primerVaccinationDataService.deleteAll();
    }

    private void resetTestFields() {
        firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001", "chiefdom", "section", "district");

        secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address1", Language.Susu, "community", "B05-SL10001", "chiefdom", "section", "district");

        firstSubject.setDateOfBirth(LocalDate.parse("1967-09-17", formatter));
        firstSubject.setGender(Gender.Male);
        firstSubject.setPrimerVaccinationDate(LocalDate.parse("2014-10-17", formatter));
        firstSubject.setBoosterVaccinationDate(LocalDate.parse("2014-10-20", formatter));

        secondSubject.setDateOfBirth(LocalDate.parse("2005-08-04", formatter));
        secondSubject.setGender(Gender.Male);
        secondSubject.setPrimerVaccinationDate(LocalDate.parse("2014-10-17", formatter));
        secondSubject.setBoosterVaccinationDate(LocalDate.parse("2014-10-20", formatter));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.SCREENING, LocalDate.parse("2014-10-17", formatter),
                LocalDate.parse("2014-10-17", formatter), "owner"));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT, LocalDate.parse("2014-10-19", formatter),
                LocalDate.parse("2014-10-19", formatter), "owner"));

        testVisits.add(VisitUtils.createVisit(secondSubject, VisitType.SCREENING, LocalDate.parse("2014-10-19", formatter),
                LocalDate.parse("2014-10-19", formatter), "owner"));

        testVisits.add(VisitUtils.createVisit(secondSubject, VisitType.PRIME_VACCINATION_FOLLOW_UP_VISIT, LocalDate.parse("2014-10-21", formatter),
                LocalDate.parse("2014-10-21", formatter), "owner"));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.BOOST_VACCINATION_DAY, LocalDate.parse("2014-10-17", formatter),
                LocalDate.parse("2014-10-17", formatter), "owner"));
    }

    @Test
    public void shouldGenerateReports() throws IOException, InterruptedException {
        try {
            fakeNow(newDateTime(2014, 10, 21, 1, 0, 0));
            assertEquals(0, subjectDataService.retrieveAll().size());
            assertEquals(0, visitDataService.retrieveAll().size());
            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

            subjectDataService.create(firstSubject);
            subjectDataService.create(secondSubject);
            assertEquals(2, subjectDataService.retrieveAll().size());

            HttpResponse response = getReports("2014-10-17");
            assertEquals(200, response.getStatusLine().getStatusCode());

            List<ReportPrimerVaccination> primerVaccinationReport =  primerVaccinationDataService.retrieveAll();
            List<ReportBoosterVaccination> boosterVaccinationReport = boosterVaccinationDataService.retrieveAll();

            assertEquals(4,primerVaccinationReport.size());
            assertEquals(4, boosterVaccinationReport.size());

            assertEquals(1,(long)primerVaccinationReport.get(0).getAdultMales());
            assertEquals(1,(long)primerVaccinationReport.get(0).getChildrenFrom6To11());

            assertEquals(1, (long) boosterVaccinationReport.get(3).getAdultMales());
            assertEquals(1, (long) boosterVaccinationReport.get(3).getChildrenFrom6To11());

        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldNotGenerateReportsWhenDateMalformedOrMissing() throws IOException, InterruptedException {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());
        assertEquals(0, primerVaccinationDataService.retrieveAll().size());
        assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

        HttpResponse response = getReports("10-17-2014");
        assertEquals(400, response.getStatusLine().getStatusCode());

        response = getReports("");
        assertEquals(400, response.getStatusLine().getStatusCode());
    }

    @Test
    public void shouldNotGenerateReportsWithFutureDates() throws IOException, InterruptedException {
        try {
            fakeNow(newDateTime(2014, 10, 21, 1, 0, 0));
            assertEquals(0, subjectDataService.retrieveAll().size());
            assertEquals(0, visitDataService.retrieveAll().size());
            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

            subjectDataService.create(firstSubject);
            subjectDataService.create(secondSubject);
            assertEquals(2, subjectDataService.retrieveAll().size());

            HttpResponse response = getReports("2015-10-17");
            assertEquals(200, response.getStatusLine().getStatusCode());

            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGetCorrectNumberOfRows() throws IOException, InterruptedException {
        addTestVisitsToDB();
        String jsonInput = getVisitsWithoutLookup(1, 4);
        List<Visit> visits = deserializeVisits(jsonInput).getRows();

        assertEquals(4, visits.size());

        jsonInput = getVisitsWithoutLookup(2, 4);
        visits = deserializeVisits(jsonInput).getRows();

        assertEquals(1, visits.size());
    }

    @Test
    public void shouldGetVisitsByPlannedDate() throws IOException, InterruptedException {
        addTestVisitsToDB();
        String jsonInput = getVisitsByLookup("{\"motechProjectedDate\":\"201" +
                "4-10-21\"}", "Find Visits By Planned Visit Date", 1, 5);

        List<Visit> visits = deserializeVisits(jsonInput).getRows();
        assertEquals(1, visits.size());

        VisitUtils.checkVisitFields(testVisits.get(3), visits.get(0));
    }

    @Test
    public void shouldGetVisitsByPlannedDateAndType() throws IOException, InterruptedException {
        addTestVisitsToDB();
        String jsonInput = getVisitsByLookup("{\"motechProjectedDate\":\"2014-10-21\",\"type\":\"PRIME_VACCINATION_FOLLOW_UP_VISIT\"}",
                "Find Visits By Planned Visit Date And Type", 1, 5);

        List<Visit> visits = deserializeVisits(jsonInput).getRows();
        assertEquals(1, visits.size());

        VisitUtils.checkVisitFields(testVisits.get(3), visits.get(0));
    }

    @Test
    public void shouldGetVisitsByPlannedDateRange() throws IOException, InterruptedException {
        addTestVisitsToDB();
        String jsonInput = getVisitsByLookup("{\"motechProjectedDate\":{\"min\":\"2014-10-20\",\"max\":\"2014-10-21\"}}",
                "Find Visits By Planned Visit Date Range",1, 5);

        List<Visit> visits = deserializeVisits(jsonInput).getRows();
        assertEquals(1, visits.size());

        VisitUtils.checkVisitFields(testVisits.get(3), visits.get(0));
    }

    @Test
    public void shouldGetVisitsByPlannedDateRangeAndType() throws IOException, InterruptedException {
        addTestVisitsToDB();
        String jsonInput = getVisitsByLookup("{\"motechProjectedDate\":{\"min\":\"2014-10-20\",\"max\":\"2014-10-21\"},\"type\":\"PRIME_VACCINATION_FOLLOW_UP_VISIT\"}",
                "Find Visits By Planned Visit Date Range And Type", 1, 5);

        List<Visit> visits = deserializeVisits(jsonInput).getRows();
        assertEquals(1, visits.size());

        VisitUtils.checkVisitFields(testVisits.get(3), visits.get(0));
    }

    @Test
    public void shouldGetVisitsBySubjectAddress() throws IOException, InterruptedException {
        addTestVisitsToDB();
        String jsonInput = getVisitsByLookup("{\"subject.address\":\"address1\"}", "Find Visits By Participant Address", 1, 5);

        List<Visit> visits = deserializeVisits(jsonInput).getRows();
        assertEquals(2, visits.size());

        assertTrue(testVisits.containsAll(visits));
    }

    @Test
    public void shouldGetVisitsBySubjectName() throws IOException, InterruptedException {
        addTestVisitsToDB();
        String jsonInput = getVisitsByLookup("{\"subject.name\":\"Rafal\"}", "Find Visits By Participant Name", 1, 5);

        List<Visit> visits = deserializeVisits(jsonInput).getRows();
        assertEquals(2, visits.size());

        assertTrue(testVisits.containsAll(visits));
    }

    @Test
    public void shouldGetVisitsBySubjectId() throws IOException, InterruptedException {
        addTestVisitsToDB();
        String jsonInput = getVisitsByLookup("{\"subject.subjectId\":\"1000000162\"}", "Find Visits By Participant Id", 1, 5);

        List<Visit> visits = deserializeVisits(jsonInput).getRows();
        assertEquals(2, visits.size());

        assertTrue(testVisits.containsAll(visits));
    }

    @Test
    public void shouldGetVisitsByType() throws IOException, InterruptedException {
        addTestVisitsToDB();
        String jsonInput = getVisitsByLookup("{\"type\":\"SCREENING\"}", "Find Visits By Type", 1, 5);

        List<Visit> visits = deserializeVisits(jsonInput).getRows();
        assertEquals(2, visits.size());

        assertTrue(testVisits.containsAll(visits));
    }

    private Records<Visit> deserializeVisits(String jsonInput) throws IOException {
        CustomVisitTypeDeserializer deserializer = new CustomVisitTypeDeserializer();

        SimpleModule module = new SimpleModule("VisitTypeDeserializerModule", new Version(1, 0, 0, null));
        module.addDeserializer(VisitType.class, deserializer);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        Records<Visit> records = mapper.readValue(jsonInput, new TypeReference<Records<Visit>>(){});
        return records;
    }

    private String getVisitsWithoutLookup(int page, int rows) throws IOException, InterruptedException {
        return getVisitsByLookup("{}","",page,rows);
    }

    private String getVisitsByLookup(String fields, String lookupType, int page, int rows) throws IOException, InterruptedException {
        HttpPost post;
        post = new HttpPost(String.format("http://localhost:%d/ebodac/getReport/dailyClinicVisitScheduleReport", TestContext.getJettyPort()));
        ArrayList<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("fields", fields));
        nvps.add(new BasicNameValuePair("filter", ""));
        nvps.add(new BasicNameValuePair("lookup", lookupType));
        nvps.add(new BasicNameValuePair("page", Integer.toString(page)));
        nvps.add(new BasicNameValuePair("rows", Integer.toString(rows)));
        nvps.add(new BasicNameValuePair("sortColumn", ""));
        nvps.add(new BasicNameValuePair("sortDirection", "asc"));
        post.setEntity(new UrlEncodedFormEntity(nvps, "UTF8"));
        post.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        String response = getHttpClient().execute(post, new BasicResponseHandler());
        assertNotNull(response);

        return response;
    }

    private HttpResponse getReports(String startDate) throws IOException, InterruptedException {
        HttpPost post = new HttpPost(String.format("http://localhost:%d/ebodac/generateReports", TestContext.getJettyPort()));
        StringEntity dateEntity = new StringEntity(startDate);
        post.setEntity(dateEntity);
        post.setHeader(HttpHeaders.CONTENT_TYPE, "text/plain; charset=ISO-8859-1");

        HttpResponse response = getHttpClient().execute(post);
        assertNotNull(response);

        return response;
    }

    private void addTestVisitsToDB() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        for(Visit visit : testVisits) {
            visitDataService.create(visit);
        }

        assertEquals(2, subjectDataService.retrieveAll().size());
        assertEquals(5, visitDataService.retrieveAll().size());
    }

}
