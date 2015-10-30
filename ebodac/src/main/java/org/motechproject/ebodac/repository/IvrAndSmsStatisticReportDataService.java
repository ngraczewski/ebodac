package org.motechproject.ebodac.repository;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.IvrAndSmsStatisticReport;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.Constants;

import java.util.List;

public interface IvrAndSmsStatisticReportDataService extends MotechDataService<IvrAndSmsStatisticReport> {

    @Lookup(name="Find Reports By ParticipantId")
    List<IvrAndSmsStatisticReport> findReportsBySubjectId(@LookupField(name = "subject.subjectId",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String subjectId);

    @Lookup(name="Find Reports By Participant Phone Number")
    List<IvrAndSmsStatisticReport> findReportsSubjectPhoneNumber(@LookupField(name = "subject.phoneNumber",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String phoneNumber);

    @Lookup(name="Find Reports By Participant Community")
    List<IvrAndSmsStatisticReport> findReportsBySubjectCommunity(@LookupField(name = "subject.community",
            customOperator = Constants.Operators.MATCHES_CASE_INSENSITIVE) String community);

    @Lookup
    List<IvrAndSmsStatisticReport> findReportsBySendDate(@LookupField(name = "sendDate") Range<DateTime> sendDate);

    @Lookup
    List<IvrAndSmsStatisticReport> findReportsByReceivedDate(@LookupField(name = "receivedDate") Range<DateTime> receivedDate);
}
