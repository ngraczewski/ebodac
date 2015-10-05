package org.motechproject.ebodac.osgi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.utils.VisitUtils;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.NameMatcher;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EnrollmentControllerIT extends BasePaxIT {
    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private VisitDataService visitDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    private EnrollmentDataService enrollmentDataService;

    @Inject
    private EbodacEnrollmentService ebodacEnrollmentService;

    @Inject
    RaveImportService raveImportService;

    @Inject
    private BundleContext bundleContext;

    private Scheduler scheduler;

    private Subject firstSubject;

    private Subject secondSubject;

    private ArrayList<Visit> testVisits = new ArrayList<Visit>();

    private DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

    private ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        @Override
        public String handleResponse(final HttpResponse response) throws IOException {
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new ClientProtocolException("Response contains no content");
            }
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            return Integer.toString(response.getStatusLine().getStatusCode()) + "__" + EntityUtils.toString(entity, charset);
        }
    };

    @BeforeClass
    public static void beforeClass() throws IOException, InterruptedException {
        createAdminUser();
        login();
        Thread.sleep(EbodacConstants.LOGIN_WAIT_TIME);
    }

    @Before
    public void cleanBefore() throws SchedulerException {
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectDataService.deleteAll();
        clearJobs();
        resetTestFields();
        addTestVisitsToDB();
    }

    @After
    public void cleanAfter() throws SchedulerException {
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectDataService.deleteAll();
        clearJobs();
    }

    private void resetTestFields() {
        firstSubject = new Subject("1000000161", "Michal", "Abacki", "Cabacki",
                "729402018364", "address", Language.English, "community", "B05-SL10001", "chiefdom", "section", "district");

        secondSubject = new Subject("1000000162", "Rafal", "Dabacki", "Ebacki",
                "44443333222", "address1", Language.Susu, "community", "B05-SL10001", "chiefdom", "section", "district");
        secondSubject.setPrimerVaccinationDate(new LocalDate(2115, 10, 10));

        testVisits.add(VisitUtils.createVisit(null, VisitType.SCREENING, LocalDate.parse("2014-10-17", formatter),
                null, "owner"));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.SCREENING, LocalDate.parse("2014-10-17", formatter),
                null, "owner"));

        testVisits.add(VisitUtils.createVisit(firstSubject, VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT, null,
                LocalDate.parse("2115-10-19", formatter), "owner"));

        testVisits.add(VisitUtils.createVisit(secondSubject, VisitType.FIRST_LONG_TERM_FOLLOW_UP_VISIT, null,
                LocalDate.parse("2115-10-19", formatter), "owner"));

        Visit visit = testVisits.get(2);
        visit.setMotechProjectedDate(LocalDate.parse("2115-10-10", formatter));
    }

    private void addTestVisitsToDB() {
        assertEquals(0, subjectDataService.retrieveAll().size());
        assertEquals(0, visitDataService.retrieveAll().size());

        for (Visit visit : testVisits) {
            visitDataService.create(visit);
        }

        assertEquals(2, subjectDataService.retrieveAll().size());
        assertEquals(4, visitDataService.retrieveAll().size());
    }

    private void clearJobs() throws SchedulerException {
        scheduler = (Scheduler) getQuartzScheduler(bundleContext);
        NameMatcher<JobKey> nameMatcher = NameMatcher.jobNameStartsWith("org.motechproject.messagecampaign");
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupStartsWith("default"));
        for (JobKey jobKey : jobKeys) {
            if (nameMatcher.isMatch(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        }
    }

    @Test
    public void shouldNotReenrollVisit() throws IOException, InterruptedException {
        checkResponse(400, "ebodac.enrollment.error.nullVisit", reenrollVisit(null, 400));

        checkResponse(400, "ebodac.enrollment.error.noSubject", reenrollVisit(testVisits.get(0), 400));

        Visit visit = testVisits.get(1);
        visit.setMotechProjectedDate(null);
        checkResponse(400, "ebodac.enrollment.error.EmptyPlannedDate",
                reenrollVisit(visit, 400));

        visit = testVisits.get(1);
        visit.setType(VisitType.THIRD_LONG_TERM_FOLLOW_UP_VISIT);
        visit.setMotechProjectedDate(LocalDate.parse("2115-10-10", formatter));
        checkResponse(500, "ebodac.enrollment.error.noVisitInDB", reenrollVisit(visit, 500));


        ebodacEnrollmentService.enrollSubject(secondSubject);
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(secondSubject.getSubjectId());
        visit = testVisits.get(3);

        visit.setMotechProjectedDate(LocalDate.parse("2010-10-19", formatter));
        checkResponse(400, "ebodac.enrollment.error.plannedDateInPast",
                reenrollVisit(visit, 400));

        visit.setMotechProjectedDate(LocalDate.parse("2115-10-19", formatter));
        checkResponse(400, "ebodac.enrollment.error.plannedDateNotChanged",
                reenrollVisit(testVisits.get(3), 400));
    }

    @Test
    public void shouldReenrollVisit() throws IOException, InterruptedException {
        assertEquals(0, enrollmentDataService.retrieveAll().size());
        Visit visit = testVisits.get(3);

        ebodacEnrollmentService.enrollSubject(secondSubject);
        visit.setMotechProjectedDate(LocalDate.parse("2115-10-11", formatter));
        checkResponse(200, "", reenrollVisit(visit, 200));

        List<Enrollment> enrollments = enrollmentDataService.retrieveAll();
        assertEquals(1, enrollments.size());

        checkCampaignEnrollment("1000000162", VisitType.FIRST_LONG_TERM_FOLLOW_UP_VISIT.getValue(),
                LocalDate.parse("2115-10-11", formatter), enrollments.get(0));
    }

    @Test
    public void shouldUnerollSubjectWhenPhoneNumberOrLanguageRemoved() throws IOException, InterruptedException {
        assertEquals(0, enrollmentDataService.retrieveAll().size());

        Subject subject1 = new Subject();
        subject1.setSubjectId("1");
        subject1.setSiteId("SiteId");
        subject1.setLanguage(Language.English);
        subject1.setPhoneNumber("123456789");
        subjectDataService.create(subject1);

        Subject subject2 = new Subject();
        subject2.setSubjectId("2");
        subject2.setSiteId("SiteId");
        subject2.setLanguage(Language.English);
        subject2.setPhoneNumber("1111111");
        subjectDataService.create(subject2);

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());

        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments1.getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments2.getStatus());

        subject1.setLanguage(null);
        subject2.setPhoneNumber(null);

        checkResponse(200, "", updateSubject(subject1, 200));
        checkResponse(200, "", updateSubject(subject2, 200));

        subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());

        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments1.getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments2.getStatus());
    }

    @Test
    public void shouldReturnOkIfSubjectNoEnrolled() throws IOException, InterruptedException {
        Subject subject = new Subject();
        subject.setSubjectId("1");
        subject.setSiteId("SiteId");
        subject.setLanguage(Language.English);
        subjectDataService.create(subject);

        checkResponse(200, "", updateSubject(subject, 200));
    }

    @Test
    public void shouldScheduleJobsForDuplicatedEnrolmentWhenSubjectPhoneNumberChangedForThisEnrollment() throws IOException, InterruptedException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        assertEquals(0, enrollmentDataService.retrieveAll().size());

        Subject subject1 = new Subject();
        subject1.setSubjectId("1");
        subject1.setSiteId("SiteId");
        subject1.setLanguage(Language.English);
        subject1.setPhoneNumber("123456789");
        subjectDataService.create(subject1);

        Subject subject2 = new Subject();
        subject2.setSubjectId("2");
        subject2.setSiteId("SiteId");
        subject2.setLanguage(Language.English);
        subject2.setPhoneNumber("123456789");
        subjectDataService.create(subject2);

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());

        Enrollment enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        Enrollment enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignName() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignName() + "." + enrollment2.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(1, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        subject2.setPhoneNumber("987654321");
        checkResponse(200, "", updateSubject(subject2, 200));

        subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(null, enrollment2.getParentEnrollment());
    }

    @Test
    public void shouldFindNewParentWhenSubjectPhoneNumberChangedForOldParent() throws IOException, InterruptedException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        assertEquals(0, enrollmentDataService.retrieveAll().size());

        Subject subject1 = new Subject();
        subject1.setSubjectId("1");
        subject1.setSiteId("SiteId");
        subject1.setLanguage(Language.English);
        subject1.setPhoneNumber("123456789");
        subjectDataService.create(subject1);

        Subject subject2 = new Subject();
        subject2.setSubjectId("2");
        subject2.setSiteId("SiteId");
        subject2.setLanguage(Language.English);
        subject2.setPhoneNumber("123456789");
        subjectDataService.create(subject2);

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());

        Enrollment enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        Enrollment enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignName() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignName() + "." + enrollment2.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(1, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        subject1.setPhoneNumber("987654321");
        checkResponse(200, "", updateSubject(subject1, 200));

        subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(null, enrollment2.getParentEnrollment());
    }

    @Test
    public void shouldChangeGroupForDuplicatedEnrolmentWhenSubjectPhoneNumberChanged() throws IOException, InterruptedException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        assertEquals(0, enrollmentDataService.retrieveAll().size());

        Subject subject1 = new Subject();
        subject1.setSubjectId("1");
        subject1.setSiteId("SiteId");
        subject1.setLanguage(Language.English);
        subject1.setPhoneNumber("123456789");
        subjectDataService.create(subject1);

        Subject subject2 = new Subject();
        subject2.setSubjectId("2");
        subject2.setSiteId("SiteId");
        subject2.setLanguage(Language.English);
        subject2.setPhoneNumber("123456789");
        subjectDataService.create(subject2);

        Subject subject3 = new Subject();
        subject3.setSubjectId("3");
        subject3.setSiteId("SiteId");
        subject3.setLanguage(Language.English);
        subject3.setPhoneNumber("987654321");
        subjectDataService.create(subject3);

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());
        SubjectEnrollments subjectEnrollments3 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        Enrollment enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        Enrollment enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        Enrollment enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignName() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignName() + "." + enrollment2.getExternalId() + runonce;
        String triggerKeyString3 = campaignCompletedString + enrollment3.getCampaignName() + "." + enrollment3.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(1, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(0, enrollment3.getDuplicatedEnrollments().size());

        subject2.setPhoneNumber("987654321");
        checkResponse(200, "", updateSubject(subject2, 200));

        subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());
        subjectEnrollments3 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment3, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(1, enrollment3.getDuplicatedEnrollments().size());
    }

    @Test
    public void shouldFindNewParentAndUnscheduleJobsForOldParenWhenParentAddedToDifferentGroupAfterSubjectPhoneNumberChanged() throws IOException, InterruptedException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        assertEquals(0, enrollmentDataService.retrieveAll().size());

        Subject subject1 = new Subject();
        subject1.setSubjectId("1");
        subject1.setSiteId("SiteId");
        subject1.setLanguage(Language.English);
        subject1.setPhoneNumber("123456789");
        subjectDataService.create(subject1);

        Subject subject2 = new Subject();
        subject2.setSubjectId("2");
        subject2.setSiteId("SiteId");
        subject2.setLanguage(Language.English);
        subject2.setPhoneNumber("123456789");
        subjectDataService.create(subject2);

        Subject subject3 = new Subject();
        subject3.setSubjectId("3");
        subject3.setSiteId("SiteId");
        subject3.setLanguage(Language.English);
        subject3.setPhoneNumber("987654321");
        subjectDataService.create(subject3);

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicatedSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicatedSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());
        SubjectEnrollments subjectEnrollments3 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        Enrollment enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        Enrollment enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        Enrollment enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        String triggerKeyString1 = campaignCompletedString + enrollment1.getCampaignName() + "." + enrollment1.getExternalId() + runonce;
        String triggerKeyString2 = campaignCompletedString + enrollment2.getCampaignName() + "." + enrollment2.getExternalId() + runonce;
        String triggerKeyString3 = campaignCompletedString + enrollment3.getCampaignName() + "." + enrollment3.getExternalId() + runonce;

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(1, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(0, enrollment3.getDuplicatedEnrollments().size());

        subject1.setPhoneNumber("987654321");
        checkResponse(200, "", updateSubject(subject1, 200));

        subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());
        subjectEnrollments3 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.ENROLLED, enrollment1.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(enrollment3, enrollment1.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(null, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(1, enrollment3.getDuplicatedEnrollments().size());
    }

    @Test
    public void shouldCreateEnrollmentRecordsForSubject() throws IOException, InterruptedException {

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        Subject subject = subjectDataService.findSubjectBySubjectId("1");
        assertNotNull(subject);

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertNull(subjectEnrollments);

        subject.setLanguage(Language.English);
        subject.setPhoneNumber("123456789");

        checkResponse(200, "", updateSubject(subject, 200));

        subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertNotNull(subjectEnrollments);
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());
        assertEquals(3, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment: subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }
    }

    private String updateSubject(Subject subject, int errorCode) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(subject);
        HttpPost post = new HttpPost(String.format("http://localhost:%d/ebodac/subjectDataChanged", TestContext.getJettyPort()));
        StringEntity jsonEntity;
        jsonEntity = new StringEntity(json);
        post.setEntity(jsonEntity);
        post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");

        String response = getHttpClient().execute(post, responseHandler, errorCode);
        assertNotNull(response);

        return response;
    }

    private String reenrollVisit(Visit visit, int errorCode) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(visit);
        HttpPost post = new HttpPost(String.format("http://localhost:%d/ebodac/reenrollSubject", TestContext.getJettyPort()));
        StringEntity jsonEntity;
        jsonEntity = new StringEntity(json);
        post.setEntity(jsonEntity);
        post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=utf-8");

        String response = getHttpClient().execute(post, responseHandler, errorCode);
        assertNotNull(response);

        return response;
    }

    private void checkResponse(int code, String message, String response) {
        String[] codeAndMessage = response.split("__");
        if (code != 200) {
            assertEquals(2, codeAndMessage.length);
            assertEquals(code, Integer.parseInt(codeAndMessage[0]));
            assertEquals(message, codeAndMessage[1]);
        } else {
            assertEquals(code, Integer.parseInt(codeAndMessage[0]));
        }
    }

    private void checkCampaignEnrollment(String externalId, String campaignName,
                                         LocalDate referenceDate, Enrollment enrollment) {
        assertEquals(externalId, enrollment.getExternalId());
        assertEquals(campaignName, enrollment.getCampaignName());
        assertEquals(referenceDate, enrollment.getReferenceDate());
    }

}
