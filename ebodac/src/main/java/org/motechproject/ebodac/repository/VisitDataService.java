package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
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
public interface VisitDataService extends MotechDataService<Visit> {

    @Lookup
    List<Visit> findVisitsByActualDate(@LookupField(name = "date") LocalDate date);

    @Lookup
    List<Visit> findVisitsByActualDateAndType(@LookupField(name = "date") LocalDate date,
                                              @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByActualDateRange(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByActualDateRangeAndType(@LookupField(name = "date") Range<LocalDate> dateRange,
                                                   @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByType(@LookupField(name = "type") VisitType visitType);

    @Lookup(name = "Find Visits By Participant Id")
    List<Visit> findVisitsBySubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    @Lookup(name = "Find Visits By Participant Name")
    List<Visit> findVisitsBySubjectName(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name);

    @Lookup(name = "Find Visits By Participant Community")
    List<Visit> findVisitsBySubjectCommunity(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community);

    @Lookup(name = "Find Visits By Participant Address")
    List<Visit> findVisitsBySubjectAddress(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address);

    @Lookup(name = "Find Visits By Participant Primer Vaccination Date")
    List<Visit> findVisitsBySubjectPrimerVaccinationDate(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate);

    @Lookup(name = "Find Visits By Participant Primer Vaccination Date Range")
    List<Visit> findVisitsBySubjectPrimerVaccinationDateRange(@LookupField(name = "subject.primerVaccinationDate")  Range<LocalDate> primerVaccinationDateRange);

    @Lookup(name = "Find Visits By Participant Booster Vaccination Date")
    List<Visit> findVisitsBySubjectBoosterVaccinationDate(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate);

    @Lookup(name = "Find Visits By Participant Booster Vaccination Date Range")
    List<Visit> findVisitsBySubjectBoosterVaccinationDateRange(@LookupField(name = "subject.boosterVaccinationDate")  Range<LocalDate> boosterVaccinationDateRange);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDate(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateAndType(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                    @LookupField(name = "type") VisitType visitType);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRange(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeAndType(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                         @LookupField(name = "type") VisitType visitType);

    /**
     *  Followups After Prime Injection Report Lookups
     */

    @Lookup
    List<Visit> findVisitsByTypePhoneNumberAndAddress(@LookupField(name = "type") VisitType visitType,
                                                      @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                      @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visits By Participant Address Phone Number And Type")
    List<Visit> findVisitsBySubjectAddressPhoneNumberAndType(@LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address,
                                                             @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                             @LookupField(name = "type") VisitType visitType);

    @Lookup(name = "Find Visits By Participant Name Type Phone Number And Address")
    List<Visit> findVisitsBySubjectNameTypePhoneNumberAndAddress(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                 @LookupField(name = "type") VisitType visitType,
                                                                 @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                 @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visits By Participant Community Type Phone Number And Address")
    List<Visit> findVisitsBySubjectCommunityTypePhoneNumberAndAddress(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                                      @LookupField(name = "type") VisitType visitType,
                                                                      @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                      @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visits By Participant Primer Vaccination Date Type Phone Number And Address")
    List<Visit> findVisitsBySubjectPrimerVaccinationDateTypePhoneNumberAndAddress(@LookupField(name = "subject.primerVaccinationDate") LocalDate primerVaccinationDate,
                                                                                  @LookupField(name = "type") VisitType visitType,
                                                                                  @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                                  @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visits By Participant Primer Vaccination Date Range Type Phone Number And Address")
    List<Visit> findVisitsBySubjectPrimerVaccinationDateRangeTypePhoneNumberAndAddress(@LookupField(name = "subject.primerVaccinationDate") Range<LocalDate> primerVaccinationDateRange,
                                                                                       @LookupField(name = "type") VisitType visitType,
                                                                                       @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                                       @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visits By Participant Booster Vaccination Date Type Phone Number And Address")
    List<Visit> findVisitsBySubjectBoosterVaccinationDateTypePhoneNumberAndAddress(@LookupField(name = "subject.boosterVaccinationDate") LocalDate boosterVaccinationDate,
                                                                                   @LookupField(name = "type") VisitType visitType,
                                                                                   @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                                   @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    @Lookup(name = "Find Visits By Participant Booster Vaccination Date Range Type Phone Number And Address")
    List<Visit> findVisitsBySubjectBoosterVaccinationDateRangeTypePhoneNumberAndAddress(@LookupField(name = "subject.boosterVaccinationDate") Range<LocalDate> boosterVaccinationDateRange,
                                                                                        @LookupField(name = "type") VisitType visitType,
                                                                                        @LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                                        @LookupField(name = "subject.address", customOperator = Constants.Operators.NEQ) String address);

    /**
    *  Followups Missed Clinic Visits Report Lookups
    */

    @Lookup
    List<Visit> findVisitsByPlannedDateLessAndActualDateEqAndSubjectPhoneNumberEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                                                   @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                                   @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup(name = "Find Visits By Participant Name Less")
    List<Visit> findVisitsBySubjectNameLess(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                            @LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                            @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                            @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup(name = "Find Visits By Participant Community Less")
    List<Visit> findVisitsBySubjectCommunityLess(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                 @LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                 @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                 @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup(name = "Find Visits By Participant Address Less")
    List<Visit> findVisitsBySubjectAddressLess(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                               @LookupField(name = "subject.address",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address,
                                               @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                               @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup
    List<Visit> findVisitsByPlannedVisitDateEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                               @LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                               @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup
    List<Visit> findVisitsByPlannedVisitDateAndTypeEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                      @LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                      @LookupField(name = "type") VisitType visitType,
                                                      @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                    @LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange,
                                                    @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    // TODO: rename this accordingly to new phone number parameter
    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeAndTypeEq(@LookupField(name = "subject.phoneNumber", customOperator = Constants.Operators.EQ) String phoneNumber,
                                                           @LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                           @LookupField(name = "type") VisitType visitType,
                                                           @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);


    /**
     *  M&E Missed Clinic Visits Report Lookups
     */

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateLessAndActualVisitDate(@LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                   @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find Visits By Participant Id And Planned Visit Date And Actual Visit Date")
    List<Visit> findVisitsBySubjectIdAndPlannedVisitDateAndActualVisitDate(@LookupField(name = "subject.subjectId", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                                           @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                           @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find Visits By Participant Name And Planned Visit Date And Actual Visit Date")
    List<Visit> findVisitsBySubjectNameAndPlannedVisitDateAndActualVisitDate(@LookupField(name = "subject.name", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                             @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                             @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find Visits By Participant Community And Planned Visit Date And Actual Visit Date")
    List<Visit> findVisitsBySubjectCommunityAndPlannedVisitDateAndActualVisitDate(@LookupField(name = "subject.community", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community,
                                                                                  @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                                  @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup(name = "Find Visits By Participant Address And Planned Visit Date And Actual Visit Date")
    List<Visit> findVisitsBySubjectAddressAndPlannedVisitDateAndActualVisitDate(@LookupField(name = "subject.address", customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String address,
                                                                                @LookupField(name = "motechProjectedDate", customOperator = Constants.Operators.LT) LocalDate motechProjectedDate,
                                                                                @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateAndActualVisitDate(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                               @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateAndTypeAndActualVisitDate(@LookupField(name = "motechProjectedDate") LocalDate motechProjectedDate,
                                                                      @LookupField(name = "type") VisitType visitType,
                                                                      @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeAndActualVisitDate(@LookupField(name = "motechProjectedDate") Range<LocalDate> dateRange,
                                                                    @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    @Lookup
    List<Visit> findVisitsByPlannedVisitDateRangeAndTypeAndActualVisitDate(@LookupField(name = "motechProjectedDate") Range<LocalDate> motechProjectedDateRange,
                                                                           @LookupField(name = "type") VisitType visitType,
                                                                           @LookupField(name = "date", customOperator = Constants.Operators.EQ) LocalDate date);

    /**
     *  Booking App Lookups
     */

    @Lookup
    List<Visit> findVisitsByTypeDateAndPrimerVaccinationDate(@LookupField(name = "type") VisitType visitType,
                                                             @LookupField(name = "date", customOperator = Constants.Operators.NEQ) LocalDate date,
                                                             @LookupField(name = "subject.primerVaccinationDate", customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate);

    @Lookup(name = "Find Visits By Participant Id Type Date And Primer Vaccination Date")
    List<Visit> findVisitsBySubjectIdTypeDateAndPrimerVaccinationDate(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId,
                                                                      @LookupField(name = "type") VisitType visitType,
                                                                      @LookupField(name = "date",
                                                                              customOperator = Constants.Operators.NEQ) LocalDate date,
                                                                      @LookupField(name = "subject.primerVaccinationDate",
                                                                              customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate);

    @Lookup(name = "Find Visits By Participant Name Type Date And Primer Vaccination Date")
    List<Visit> findVisitsBySubjectNameTypeDateAndPrimerVaccinationDate(@LookupField(name = "subject.name",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String name,
                                                                        @LookupField(name = "type") VisitType visitType,
                                                                        @LookupField(name = "date",
                                                                                customOperator = Constants.Operators.NEQ) LocalDate date,
                                                                        @LookupField(name = "subject.primerVaccinationDate",
                                                                                customOperator = Constants.Operators.EQ) LocalDate primerVaccinationDate);

    @Lookup
    List<Visit> findByVisitTypeAndActualDateLess(@LookupField(name = "type") VisitType type,
                                                 @LookupField(name = "date", customOperator = Constants.Operators.LT) LocalDate date);
}
