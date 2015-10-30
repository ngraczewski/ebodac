package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.EnrollmentStatus;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.SubjectEnrollments;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;

public interface SubjectEnrollmentsDataService extends MotechDataService<SubjectEnrollments> {

    @Lookup(name="Find unique Enrollment By Participant Id")
    SubjectEnrollments findEnrollmentBySubjectId(@LookupField(name = "subject.subjectId") String subjectId);

    @Lookup(name="Find Enrollments By Participant Id")
    List<SubjectEnrollments> findEnrollmentsByMatchesCaseInsensitiveSubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    @Lookup(name="Find Enrollments By Participant Name")
    List<SubjectEnrollments> findEnrollmentsBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    @Lookup(name="Find Enrollments By Participant Gender")
    List<SubjectEnrollments> findEnrollmentsBySubjectGender(@LookupField(name = "subject.gender") Gender gender);

    @Lookup
    List<SubjectEnrollments> findEnrollmentsByDateOfUnenrollment(@LookupField(name = "dateOfUnenrollment") Range<LocalDate> dateOfUnenrollment);

    @Lookup
    List<SubjectEnrollments> findEnrollmentsByStatus(@LookupField(name = "status") EnrollmentStatus status);

    @Lookup(name="Find Enrollments By Participant Id And Status")
    List<SubjectEnrollments> findEnrollmentsBySubjectIdAndStatus(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                                 @LookupField(name = "status") EnrollmentStatus status);

    @Lookup(name="Find Enrollments By Participant Gender And Status")
    List<SubjectEnrollments> findEnrollmentsBySubjectGenderAndStatus(@LookupField(name = "subject.gender") Gender gender,
                                                                     @LookupField(name = "status") EnrollmentStatus status);

    @Lookup
    List<SubjectEnrollments> findEnrollmentsByDateOfUnenrollmentAndStatus(@LookupField(name = "dateOfUnenrollment") Range<LocalDate> dateOfUnenrollment,
                                                                          @LookupField(name = "status") EnrollmentStatus status);

    @Lookup(name="Find Enrollments By Participant Date Of Birth Range And Status")
    List<SubjectEnrollments> findEnrollmentsBySubjectDateOfBirthRangeAndStatus(@LookupField(name = "subject.dateOfBirth") Range<LocalDate> dateOfBirth,
                                                                               @LookupField(name = "status") EnrollmentStatus status);
}
