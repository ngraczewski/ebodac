package org.motechproject.ebodac.osgi;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.ebodac.domain.Language;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.exception.EbodacEnrollmentException;
import org.motechproject.ebodac.repository.EnrollmentDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EbodacEnrollmentServiceIT extends BasePaxIT {

    @Inject
    private BundleContext bundleContext;

    @Inject
    private ConfigService configService;

    @Inject
    EbodacEnrollmentService ebodacEnrollmentService;

    @Inject
    SubjectService subjectService;

    @Inject
    VisitService visitService;

    @Inject
    EnrollmentDataService enrollmentDataService;

    @Inject
    SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    RaveImportService raveImportService;

    Scheduler scheduler;

    private Config savedConfig;

    @Before
    public void cleanBefore() throws SchedulerException {
        scheduler = (Scheduler) getQuartzScheduler(bundleContext);
        clearJobs();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectService.deleteAll();
        savedConfig = configService.getConfig();
    }

    @After
    public void cleanAfter() throws SchedulerException {
        clearJobs();
        subjectEnrollmentsDataService.deleteAll();
        enrollmentDataService.deleteAll();
        subjectService.deleteAll();
        configService.updateConfig(savedConfig);
    }

    @Test
    public void shouldEnrollAllVisitTypes() throws IOException, SchedulerException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
        inputStream.close();

        subject = subjectService.findSubjectBySubjectId("1");
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

        assertEquals(10, subjectEnrollments.getEnrollments().size());

        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + ".1" + runonce;
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            assertEquals(new LocalDate(2115, 9, 10), enrollment.getReferenceDate());
        }
    }

    @Test
    public void shouldEnrollWhenGetZetesDataAfterRave() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        Subject subject = subjectService.findSubjectBySubjectId("1");
        List<Enrollment> enrollmentList = enrollmentDataService.retrieveAll();
        assertEquals(0, enrollmentList.size());

        subject.setPhoneNumber("123456789");
        subject.setLanguage(Language.English);
        subjectService.createOrUpdateForZetes(subject);

        subject = subjectService.findSubjectBySubjectId("1");
        enrollmentList = enrollmentDataService.retrieveAll();
        assertEquals(3, enrollmentList.size());

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

    @Test
    public void shouldNotReenrollWhenMotechProjectedDateWasChanged() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        inputStream = getClass().getResourceAsStream("/enrollChangeProjectedDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollChangeProjectedDate.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        }

        subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertEquals(3, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

    @Test
    public void shouldNotEnrollWhenSubjectDoesntHaveLanguageOrPhoneNumber() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        Subject subject = subjectService.findSubjectBySubjectId("1");

        subject.setPhoneNumber("123456789");
        subject.setLanguage(null);
        subjectService.createOrUpdateForZetes(subject);

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

        assertNull(subjectEnrollments);

        subject.setLanguage(Language.English);
        subject.setPhoneNumber(null);
        subjectService.createOrUpdateForZetes(subject);

        subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

        assertNull(subjectEnrollments);
    }

    @Ignore
    @Test
    public void shouldEnrollScreening() throws IOException {
        Subject subject = createSubjectWithRequireData("1");
        subjectService.createOrUpdateForZetes(subject);

        List<Enrollment> enrollmentList = enrollmentDataService.retrieveAll();
        assertEquals(1, enrollmentList.size());
    }

    @Test
    public void shouldEnrollBoostVaccinationForSpecificDay() throws IOException {
        for (int i = 0; i < 7; i++) {
            createSubjectWithRequireData(Integer.toString(i + 1));
        }

        InputStream inputStream = getClass().getResourceAsStream("/enrollBoostVaccination.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBoostVaccination.csv");
        inputStream.close();

        int i = 0;
        for (Subject subject : subjectService.getAll()) {

            SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
            assertEquals(1, subjectEnrollments.getEnrollments().size());
            Enrollment enrollment = subjectEnrollments.getEnrollments().iterator().next();
            assertEquals(VisitType.BOOST_VACCINATION_DAY.getValue() + " " + EbodacConstants.DAYS_OF_WEEK.get(i), enrollment.getCampaignName());
            assertEquals(new LocalDate(2115, 9, 22 + i), subjectEnrollments.getEnrollments().iterator().next().getReferenceDate());
            i++;
        }
    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldReenrollOnlyEnrolledCampaigns() {
        Subject subject = createSubjectWithRequireData("1");

        Visit visit = new Visit();
        visit.setDateProjected(new LocalDate(2115, 8, 17));
        visit.setType(VisitType.BOOST_VACCINATION_DAY);
        visit.setSubject(subject);

        ebodacEnrollmentService.reenrollSubject(visit);
    }

    @Test
    public void shouldCompleteCampaignWhenVisitAlreadyTookPlace() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        inputStream = getClass().getResourceAsStream("/enrollActualDate.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollActualDate.csv");
        inputStream.close();

        enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");

        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus());
        }
    }

    @Test
    public void shouldCompleteCampaignWhenSubjectHaveDisconStdDate() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        subject = subjectService.findSubjectBySubjectId("1");
        subject.setDateOfDisconStd(new LocalDate(2115, 8, 8));
        subjectService.createOrUpdateForRave(subject);

        enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.COMPLETED, enrollment.getStatus());
        }
    }

    @Test
    public void shouldCompleteCampaignWhenSubjectHaveDisconVacDate() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        List<String> campaignList = new ArrayList<>();
        campaignList.add("Boost Vaccination Second Follow-up visit");
        Config config = new Config();
        config.setDisconVacCampaignsList(campaignList);
        configService.updateConfig(config);

        subject = subjectService.findSubjectBySubjectId("1");
        subject.setDateOfDisconVac(new LocalDate(2115, 8, 8));
        subjectService.createOrUpdateForRave(subject);

        enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(0).getStatus());
        assertEquals(EnrollmentStatus.COMPLETED, enrollmentList.get(1).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(2).getStatus());
    }

    @Test
    public void shouldEnrollSubject() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        ebodacEnrollmentService.unenrollSubject(subjectEnrollments.getSubject().getSubjectId());

        subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }

        ebodacEnrollmentService.enrollSubject(subject.getSubjectId());

        subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertEquals(3, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

    @Test
    public void shouldEnrollSubjectToCampaign() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }
        ebodacEnrollmentService.enrollSubjectToCampaign(subject.getSubjectId(), VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue());

        enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        assertEquals(EnrollmentStatus.UNENROLLED, enrollmentList.get(0).getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, enrollmentList.get(1).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(2).getStatus());
    }

    @Test
    public void shouldEnrollSubjectToCampaignWithNewDate() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
            assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        }
        ebodacEnrollmentService.enrollSubjectToCampaignWithNewDate(subject.getSubjectId(), VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue(), new LocalDate(2115, 12, 1));

        enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(2).getStatus());
        assertEquals(new LocalDate(2115, 12, 1), enrollmentList.get(2).getReferenceDate());
    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldNotEnrollSubjectWithEnrolledStatus() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.enrollSubject(subjectEnrollments.getSubject().getSubjectId());
    }

    @Test
    public void shouldUnenrollSubject() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollBasic.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollBasic.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());

        enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.UNENROLLED, enrollment.getStatus());
        }
    }

    @Test
    public void shouldUnenrollSubjectFromCampaign() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue());

        enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(0).getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, enrollmentList.get(1).getStatus());
        assertEquals(EnrollmentStatus.UNENROLLED, enrollmentList.get(2).getStatus());

    }

    @Test(expected = EbodacEnrollmentException.class)
    public void shouldNotUnenrollSubjectWithUnenrolledStatus() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
    }

    @Test
    public void shouldReenrollSubject() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        }

        subject = subjectService.findSubjectBySubjectId("1");
        for (Visit visit : subject.getVisits()) {
            visit.setMotechProjectedDate(new LocalDate(2115, 10, 11));
            ebodacEnrollmentService.reenrollSubject(visit);
        }
        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId("1");
        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(new LocalDate(2115, 10, 11), enrollment.getReferenceDate());
        }
    }

    @Test
    public void shouldReenrollSubjectToCampaignWithNewDate() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        List<Enrollment> enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");
        for (Enrollment enrollment : enrollmentList) {
            assertEquals(new LocalDate(2115, 10, 10), enrollment.getReferenceDate());
        }

        ebodacEnrollmentService.reenrollSubjectWithNewDate(subject.getSubjectId(), VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue(), new LocalDate(2115, 12, 12));
        enrollmentList = enrollmentDataService.findEnrollmentsBySubjectId("1");

        assertEquals(new LocalDate(2115, 10, 10), enrollmentList.get(0).getReferenceDate());
        assertEquals(new LocalDate(2115, 10, 10), enrollmentList.get(1).getReferenceDate());
        assertEquals(new LocalDate(2115, 12, 12), enrollmentList.get(2).getReferenceDate());
    }

    @Test
    public void shouldCorrectlySetSubjectEnrollmentStatus() throws IOException {
        Subject subject = createSubjectWithRequireData("1");

        InputStream inputStream = getClass().getResourceAsStream("/enrollSimple.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollSimple.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT.getValue()).getCampaignName());
        subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), subjectEnrollments.findEnrolmentByCampaignName(VisitType.BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT.getValue()).getCampaignName());
        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId(), subjectEnrollments.findEnrolmentByCampaignName("Boost Vaccination Day Thursday").getCampaignName());

        subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());

        ebodacEnrollmentService.enrollSubject(subjectEnrollments.getSubject().getSubjectId());

        subject = subjectService.findSubjectBySubjectId("1");
        Visit visit = subject.getVisits().get(0);
        visit.setDate(new LocalDate(2115, 8, 8));
        visitService.createOrUpdate(visit);

        subject = subjectService.findSubjectBySubjectId("1");
        subject.setDateOfDisconStd(new LocalDate(2115, 8, 8));
        subjectService.createOrUpdateForRave(subject);
        subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());

        assertEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.getStatus());
    }

    @Test
    public void shouldCheckIfSubjectIsEnrolled() throws IOException {
        Subject subject = createSubjectWithRequireData("1");
        subject.setPrimerVaccinationDate(new LocalDate(2115, 10, 11));
        subjectService.update(subject);

        Visit visit = new Visit();
        visit.setType(VisitType.BOOST_VACCINATION_DAY);
        visit.setMotechProjectedDate(new LocalDate(2115, 10, 10));
        visit.setSubject(subject);
        assertFalse(ebodacEnrollmentService.isEnrolled(visit));

        visitService.create(visit);
        subject = subjectService.findSubjectBySubjectId("1");
        ebodacEnrollmentService.enrollSubject(subject);

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertTrue(ebodacEnrollmentService.isEnrolled(visit));

        ebodacEnrollmentService.unenrollSubject(subject.getSubjectId());
        assertFalse(ebodacEnrollmentService.isEnrolled(visit));
    }

    @Test
    public void shouldNotCreateJobsForDuplicatedEnrollments() throws IOException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        Subject subject1 = createSubjectWithRequireData("1");
        Subject subject2 = createSubjectWithRequireData("2");
        Subject subject3 = createSubjectWithRequireData("3");
        Subject subject4 = createSubjectWithRequireData("4");
        Subject subject5 = createSubjectWithRequireData("5");

        InputStream inputStream = getClass().getResourceAsStream("/enrollDuplicated.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/enrollDuplicated.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        SubjectEnrollments subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());
        SubjectEnrollments subjectEnrollments3 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject3.getSubjectId());
        SubjectEnrollments subjectEnrollments4 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject4.getSubjectId());
        SubjectEnrollments subjectEnrollments5 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject5.getSubjectId());

        assertEquals(10, subjectEnrollments1.getEnrollments().size());
        assertEquals(10, subjectEnrollments2.getEnrollments().size());
        assertEquals(10, subjectEnrollments3.getEnrollments().size());
        assertEquals(10, subjectEnrollments4.getEnrollments().size());
        assertEquals(10, subjectEnrollments5.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments1.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
            assertEquals(4, enrollment.getDuplicatedEnrollments().size());
        }

        for (Enrollment enrollment : subjectEnrollments2.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        for (Enrollment enrollment : subjectEnrollments3.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        for (Enrollment enrollment : subjectEnrollments4.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }

        for (Enrollment enrollment : subjectEnrollments5.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
            String triggerKeyString = campaignCompletedString + enrollment.getCampaignName() + "." + enrollment.getExternalId() + runonce;
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString)));
        }
    }

    @Test
    public void shouldFindNewParentWhenOldUnenrolled() throws IOException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        Subject subject1 = createSubjectWithRequireData("1");
        Subject subject2 = createSubjectWithRequireData("2");
        Subject subject3 = createSubjectWithRequireData("3");

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
        assertEquals(2, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(enrollment1, enrollment3.getParentEnrollment());

        ebodacEnrollmentService.unenrollSubject(subject1.getSubjectId());

        subjectEnrollments1 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject1.getSubjectId());
        subjectEnrollments2 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject2.getSubjectId());
        subjectEnrollments3 = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject3.getSubjectId());

        assertEquals(1, subjectEnrollments1.getEnrollments().size());
        assertEquals(1, subjectEnrollments2.getEnrollments().size());
        assertEquals(1, subjectEnrollments3.getEnrollments().size());

        enrollment1 = subjectEnrollments1.getEnrollments().iterator().next();
        enrollment2 = subjectEnrollments2.getEnrollments().iterator().next();
        enrollment3 = subjectEnrollments3.getEnrollments().iterator().next();

        assertEquals(EnrollmentStatus.UNENROLLED, enrollment1.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString1)));
        assertEquals(0, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());

        if (enrollment2.getParentEnrollment() == null) {
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
            assertEquals(enrollment2, enrollment3.getParentEnrollment());
        } else if (enrollment3.getParentEnrollment() == null) {
            assertTrue(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
            assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
            assertEquals(enrollment3, enrollment2.getParentEnrollment());
        } else {
            fail();
        }
    }

    @Test
    public void shouldUnenrollDuplicatedEnrollment() throws IOException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        Subject subject1 = createSubjectWithRequireData("1");
        Subject subject2 = createSubjectWithRequireData("2");
        Subject subject3 = createSubjectWithRequireData("3");

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
        assertEquals(2, enrollment1.getDuplicatedEnrollments().size());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment2.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString2)));
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.ENROLLED, enrollment3.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
        assertEquals(enrollment1, enrollment3.getParentEnrollment());

        ebodacEnrollmentService.unenrollSubject(subject3.getSubjectId());

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
        assertEquals(enrollment1, enrollment2.getParentEnrollment());

        assertEquals(EnrollmentStatus.UNENROLLED, enrollment3.getStatus());
        assertFalse(scheduler.checkExists(TriggerKey.triggerKey(triggerKeyString3)));
    }

    @Test
    public void shouldUpdateGroupsWhenSubjectReenrolled() throws IOException, SchedulerException {
        final String campaignCompletedString = "org.motechproject.messagecampaign.campaign-completed-EndOfCampaignJob.";
        final String runonce = "-runonce";

        Subject subject1 = createSubjectWithRequireData("1");
        Subject subject2 = createSubjectWithRequireData("2");

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

        ebodacEnrollmentService.reenrollSubjectWithNewDate(enrollment2.getExternalId(), enrollment2.getCampaignName(), new LocalDate(2115, 9, 22));

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
    public void shouldEnrollWhenDataAreComingInTwoParts() throws IOException {
        Subject subject = createSubjectWithRequireData("1020000111");

        InputStream inputStream = getClass().getResourceAsStream("/motech_20151002_154600.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/motech_20151002_154600.csv");
        inputStream.close();

        SubjectEnrollments subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertNull(subjectEnrollments);

        inputStream = getClass().getResourceAsStream("/motech_20151002_160010.csv");
        raveImportService.importCsv(new InputStreamReader(inputStream), "/motech_20151002_160010.csv");
        inputStream.close();

        subjectEnrollments = subjectEnrollmentsDataService.findEnrollmentBySubjectId(subject.getSubjectId());
        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
        assertEquals(10, subjectEnrollments.getEnrollments().size());

        for (Enrollment enrollment : subjectEnrollments.getEnrollments()) {
            assertEquals(EnrollmentStatus.ENROLLED, enrollment.getStatus());
        }
    }

    private void clearJobs() throws SchedulerException {
        NameMatcher<JobKey> nameMatcher = NameMatcher.jobNameStartsWith("org.motechproject.messagecampaign");
        Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.jobGroupStartsWith("default"));
        for (JobKey jobKey : jobKeys) {
            if (nameMatcher.isMatch(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        }
    }

    private Subject createSubjectWithRequireData(String subjectId) {
        Subject subject = new Subject();
        subject.setSubjectId(subjectId);
        subject.setLanguage(Language.English);
        subject.setPhoneNumber("123456789");
        subject.setSiteId("asdas");
        subjectService.create(subject);
        return subject;
    }
}