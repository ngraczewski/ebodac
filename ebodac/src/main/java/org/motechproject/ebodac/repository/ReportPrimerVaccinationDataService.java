package org.motechproject.ebodac.repository;

import org.joda.time.LocalDate;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

public interface ReportPrimerVaccinationDataService extends MotechDataService<ReportPrimerVaccination> {

    @Lookup
    List<ReportPrimerVaccination> findReportsByDateRange(@LookupField(name = "date") Range<LocalDate> dateRange);

    @Lookup
    ReportPrimerVaccination findReportByDate(@LookupField(name = "date") LocalDate date);
}
