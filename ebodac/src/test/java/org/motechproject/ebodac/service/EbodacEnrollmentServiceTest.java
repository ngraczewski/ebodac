package org.motechproject.ebodac.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ebodac.domain.Enrollment;
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.service.impl.EbodacEnrollmentServiceImpl;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EbodacEnrollmentServiceImpl.class)
public class EbodacEnrollmentServiceTest {

    @InjectMocks
    private EbodacEnrollmentServiceImpl enrollmentService = new EbodacEnrollmentServiceImpl();

    @Mock
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldSetParticipantEnrolledStatus() throws Exception{
        SubjectEnrollments subjectEnrollments = new SubjectEnrollments();

        Enrollment enrollment = new Enrollment("1", "campaign 1");
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 2");
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 3");
        enrollment.setStatus(EnrollmentStatus.INITIAL);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 4");
        enrollment.setStatus(EnrollmentStatus.UNENROLLED);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 5");
        enrollment.setStatus(EnrollmentStatus.WITHDRAWN_FROM_STUDY);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 6");
        enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(6, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertEquals(EnrollmentStatus.ENROLLED, subjectEnrollments.getStatus());
    }

    @Test
    public void shouldSetParticipantUnenrolledStatus() throws Exception{
        SubjectEnrollments subjectEnrollments = new SubjectEnrollments();

        Enrollment enrollment = new Enrollment("1", "campaign 1");
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 2");
        enrollment.setStatus(EnrollmentStatus.INITIAL);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 3");
        enrollment.setStatus(EnrollmentStatus.UNENROLLED);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 4");
        enrollment.setStatus(EnrollmentStatus.WITHDRAWN_FROM_STUDY);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 5");
        enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(5, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());

        enrollment = new Enrollment("1", "campaign 6");
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(6, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.UNENROLLED, subjectEnrollments.getStatus());
    }

    @Test
    public void shouldSetParticipantInitialStatus() throws Exception{
        SubjectEnrollments subjectEnrollments = new SubjectEnrollments();

        Enrollment enrollment = new Enrollment("1", "campaign 1");
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 2");
        enrollment.setStatus(EnrollmentStatus.INITIAL);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 3");
        enrollment.setStatus(EnrollmentStatus.WITHDRAWN_FROM_STUDY);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 4");
        enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(4, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertEquals(EnrollmentStatus.INITIAL, subjectEnrollments.getStatus());

        enrollment = new Enrollment("1", "campaign 5");
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(5, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.INITIAL, subjectEnrollments.getStatus());

        enrollment.setStatus(EnrollmentStatus.UNENROLLED);

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.INITIAL, subjectEnrollments.getStatus());
    }

    @Test
    public void shouldSetParticipantWithdrawnFromStudyStatus() throws Exception{
        SubjectEnrollments subjectEnrollments = new SubjectEnrollments();

        Enrollment enrollment = new Enrollment("1", "campaign 1");
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 2");
        enrollment.setStatus(EnrollmentStatus.WITHDRAWN_FROM_STUDY);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 3");
        enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, subjectEnrollments.getStatus());

        enrollment = new Enrollment("1", "campaign 4");
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(4, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, subjectEnrollments.getStatus());

        enrollment.setStatus(EnrollmentStatus.UNENROLLED);

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, subjectEnrollments.getStatus());

        enrollment.setStatus(EnrollmentStatus.INITIAL);

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.WITHDRAWN_FROM_STUDY, subjectEnrollments.getStatus());
    }

    @Test
    public void shouldSetParticipantCompletedStatus() throws Exception{
        SubjectEnrollments subjectEnrollments = new SubjectEnrollments();

        Enrollment enrollment = new Enrollment("1", "campaign 1");
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 2");
        enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(2, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.getStatus());

        enrollment = new Enrollment("1", "campaign 3");
        enrollment.setStatus(EnrollmentStatus.ENROLLED);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(3, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.getStatus());

        enrollment.setStatus(EnrollmentStatus.UNENROLLED);

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.getStatus());

        enrollment.setStatus(EnrollmentStatus.INITIAL);

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.getStatus());

        enrollment.setStatus(EnrollmentStatus.WITHDRAWN_FROM_STUDY);

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertNotEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.getStatus());
    }

    @Test
    public void shouldNotSetParticipantUnenrolledFormBoosterStatus() throws Exception{
        SubjectEnrollments subjectEnrollments = new SubjectEnrollments();

        Enrollment enrollment = new Enrollment("1", "campaign 1");
        enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
        subjectEnrollments.addEnrolment(enrollment);

        enrollment = new Enrollment("1", "campaign 2");
        enrollment.setStatus(EnrollmentStatus.UNENROLLED_FROM_BOOSTER);
        subjectEnrollments.addEnrolment(enrollment);

        assertEquals(2, subjectEnrollments.getEnrollments().size());

        Whitebox.invokeMethod(enrollmentService, "updateSubjectEnrollments", subjectEnrollments);

        assertEquals(EnrollmentStatus.COMPLETED, subjectEnrollments.getStatus());
    }
}
