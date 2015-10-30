package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;

/**
 * Interface for repository that persists simple records and allows CRUD.
 * MotechDataService base class will provide the implementation of this class as well
 * as methods for adding, deleting, saving and finding all instances.  In this class we
 * define and custom lookups we may need.
 */
public interface SubjectDataService extends MotechDataService<Subject> {

    @Lookup(name="Find Participants By Name")
    List<Subject> findSubjectsByName(@LookupField(name = "name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    @Lookup(name="Find unique Participant By ParticipantId")
    Subject findSubjectBySubjectId(@LookupField(name = "subjectId") String subjectId);

    @Lookup(name="Find Participants By Modified")
    List<Subject> findSubjectsByModified(@LookupField(name = "changed") Boolean modified);

    @Lookup(name="Find Participants By Primer Vaccination Date Range")
    List<Subject> findSubjectsByPrimerVaccinationDateRange(@LookupField(name = "primerVaccinationDate")
                                                           Range<LocalDate> dateRange);

    @Lookup(name="Find Participants By Booster Vaccination Date Range")
    List<Subject> findSubjectsByBoosterVaccinationDateRange(@LookupField(name = "boosterVaccinationDate")
                                                            Range<LocalDate> dateRange);

    @Lookup(name="Find Participants By Primer Vaccination Date")
    List<Subject> findSubjectsByPrimerVaccinationDate(@LookupField(name = "primerVaccinationDate") LocalDate dateRange);

    @Lookup(name="Find Participants By Booster Vaccination Date")
    List<Subject> findSubjectsByBoosterVaccinationDate(@LookupField(name = "boosterVaccinationDate") LocalDate dateRange);

    @Lookup(name="Find Participants By Address")
    List<Subject> findSubjectsByAddress(@LookupField(name = "address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address);

    @Lookup(name="Find Participants By ParticipantId")
    List<Subject> findSubjectsByMatchesCaseInsensitiveSubjectId(@LookupField(name = "subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    @Lookup(name="Find Participants By exact Phone Number")
    List<Subject> findSubjectsByPhoneNumber(@LookupField(name = "phoneNumber") String phoneNumber);
}
