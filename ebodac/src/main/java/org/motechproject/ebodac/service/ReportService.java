package org.motechproject.ebodac.service;

import org.joda.time.LocalDate;

public interface ReportService {

    void generateDailyReports();

    void generateDailyReportsFromDate(LocalDate startDate);

    void generateIvrAndSmsStatisticReports();

    void generateIvrAndSmsStatisticReportsFromDate(LocalDate startDate);
}
