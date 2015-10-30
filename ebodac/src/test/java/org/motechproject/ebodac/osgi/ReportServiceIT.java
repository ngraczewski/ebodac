package org.motechproject.ebodac.osgi;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.repository.SubjectDataService;
import org.motechproject.ebodac.repository.SubjectEnrollmentsDataService;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.service.ReportService;
import org.motechproject.ebodac.service.ReportUpdateService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ReportServiceIT extends BasePaxIT{

    @Inject
    private SubjectDataService subjectDataService;

    @Inject
    private SubjectEnrollmentsDataService subjectEnrollmentsDataService;

    @Inject
    private VisitDataService visitDataService;

    @Inject
    private ConfigService configService;

    @Inject
    private ReportPrimerVaccinationDataService primerVaccinationDataService;

    @Inject
    private ReportBoosterVaccinationDataService boosterVaccinationDataService;

    @Inject
    private RaveImportService raveImportService;

    @Inject
    private ReportUpdateService reportUpdateService;

    @Inject
    private ReportService reportService;

    private Config savedConfig;

    private Config config;

    @Before
    public void setUp() throws Exception {
        savedConfig = configService.getConfig();
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        primerVaccinationDataService.deleteAll();
        reportUpdateService.setConfigService(configService);
    }

    @After
    public void tearDown() throws Exception {
        configService.updateConfig(savedConfig);
        visitDataService.deleteAll();
        subjectEnrollmentsDataService.deleteAll();
        subjectDataService.deleteAll();
        boosterVaccinationDataService.deleteAll();
        primerVaccinationDataService.deleteAll();
    }

    @Test
    public void shouldGenerateDailyReports() throws IOException {
        try {
            fakeNow(newDateTime(2015, 6, 29, 10, 0, 0));

            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

            assertEquals(0, subjectDataService.retrieveAll().size());
            assertEquals(0, visitDataService.retrieveAll().size());

            DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
            LocalDate startDate = LocalDate.parse("2015-06-27", formatter);

            InputStream in = getClass().getResourceAsStream("/report.csv");
            assertNotNull(in);
            raveImportService.importCsv(new InputStreamReader(in), "/report.csv");
            in.close();

            assertEquals(98, subjectDataService.retrieveAll().size());
            assertEquals(98, visitDataService.retrieveAll().size());

            reportService.generateDailyReportsFromDate(startDate);

            LocalDate now = LocalDate.parse("2015-06-29",formatter);
            for (LocalDate date = startDate; date.isBefore(now); date = date.plusDays(1)) {
                checkUpdateBoosterVaccinationReportsForDates(date);
                checkUpdatePrimerVaccinationReportsForDates(date);
            }
        }finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldGenerateNewReports() throws IOException {
        try {
            fakeNow(newDateTime(2015, 7, 6, 1, 0, 0));
            DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);
            assertEquals(0, primerVaccinationDataService.retrieveAll().size());
            assertEquals(0, boosterVaccinationDataService.retrieveAll().size());

            config = configService.getConfig();
            config.setLastCalculationDate(DateUtil.now().minusDays(5).toString(formatter));
            config.setGenerateReports(true);
            configService.updateConfig(config);

            reportService.generateDailyReports();
            assertEquals(5, primerVaccinationDataService.retrieveAll().size());
            assertEquals(5, boosterVaccinationDataService.retrieveAll().size());
            for(int i=2;i<6;i++) {
                assertNotNull(primerVaccinationDataService.findReportByDate(new LocalDate(2015, 7, i)));
                assertNotNull(boosterVaccinationDataService.findReportByDate(new LocalDate(2015, 7, i)));
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldUpdateDailyReports() throws IOException {
        try {
            fakeNow(newDateTime(2015, 7, 6, 12, 0, 0));
            DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

            config = configService.getConfig();
            config.setLastCalculationDate(DateUtil.now().minusDays(5).toString(formatter));
            config.setGenerateReports(true);
            config.setPrimerVaccinationReportsToUpdate(null);
            config.setBoosterVaccinationReportsToUpdate(null);
            configService.updateConfig(config);

            assertEquals(0, config.getPrimerVaccinationReportsToUpdate().size());
            assertEquals(0, config.getBoosterVaccinationReportsToUpdate().size());

            InputStream in = getClass().getResourceAsStream("/sample2.csv");
            raveImportService.importCsv(new InputStreamReader(in), "/sample2.csv");
            in.close();

            config=configService.getConfig();
            assertEquals(2, config.getPrimerVaccinationReportsToUpdate().size());
            assertEquals(3, config.getBoosterVaccinationReportsToUpdate().size());

            reportService.generateDailyReports();
            config=configService.getConfig();
            assertEquals(0, config.getPrimerVaccinationReportsToUpdate().size());
            assertEquals(0, config.getBoosterVaccinationReportsToUpdate().size());

            for(int i = 2; i < 6; i++) {
                assertNotNull(primerVaccinationDataService.findReportByDate(new LocalDate(2015, 7, i)));
                assertNotNull(boosterVaccinationDataService.findReportByDate(new LocalDate(2015, 7, i)));
            }
            assertNotNull(primerVaccinationDataService.findReportByDate(new LocalDate(2015, 6, 21)));
            assertNotNull(primerVaccinationDataService.findReportByDate(new LocalDate(2015, 6, 29)));
            assertNotNull(boosterVaccinationDataService.findReportByDate(new LocalDate(2015, 6, 19)));
            assertNotNull(boosterVaccinationDataService.findReportByDate(new LocalDate(2015, 6, 21)));
            assertNotNull(boosterVaccinationDataService.findReportByDate(new LocalDate(2015, 6, 27)));
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldStartGenerateFromOldestVaccinationDay() throws IOException {
        try {
            fakeNow(newDateTime(2015, 6, 28, 12, 0, 0));

            config = new Config();
            config.setGenerateReports(true);
            configService.updateConfig(config);

            assertNull(config.getLastCalculationDate());
            assertNull(config.getFirstCalculationStartDate());

            InputStream in = getClass().getResourceAsStream("/sample2.csv");
            raveImportService.importCsv(new InputStreamReader(in), "/sample2.csv");
            in.close();

            reportService.generateDailyReports();
            assertEquals(7, primerVaccinationDataService.retrieveAll().size());
            assertEquals(7, boosterVaccinationDataService.retrieveAll().size());

            for(int i = 21; i < 28; i++) {
                assertNotNull(primerVaccinationDataService.findReportByDate(new LocalDate(2015, 6, i)));
                assertNotNull(boosterVaccinationDataService.findReportByDate(new LocalDate(2015, 6, i)));
            }
        } finally {
            stopFakingTime();
        }
    }

    @Test
    public void shouldUseReportStartDateIfLastReportDateIsNull() throws IOException {
        try {
            fakeNow(newDateTime(2015, 6, 28, 12, 0, 0));

            config = new Config();
            config.setFirstCalculationStartDate("2015-06-25");
            config.setGenerateReports(true);
            configService.updateConfig(config);

            assertNull(config.getLastCalculationDate());

            InputStream in = getClass().getResourceAsStream("/sample2.csv");
            raveImportService.importCsv(new InputStreamReader(in), "/sample2.csv");
            in.close();

            reportService.generateDailyReports();
            assertEquals(3, primerVaccinationDataService.retrieveAll().size());
            assertEquals(3, boosterVaccinationDataService.retrieveAll().size());

            for(int i = 25; i < 28; i++) {
                assertNotNull(primerVaccinationDataService.findReportByDate(new LocalDate(2015, 6, i)));
                assertNotNull(boosterVaccinationDataService.findReportByDate(new LocalDate(2015, 6, i)));
            }
        } finally {
            stopFakingTime();
        }
    }

    private void checkUpdateBoosterVaccinationReportsForDates(LocalDate date) {
        ReportBoosterVaccination existingBoosterReport = boosterVaccinationDataService.findReportByDate(date);

        assertEquals(7, (int) existingBoosterReport.getChildrenFrom1To5());
        assertEquals(7, (int) existingBoosterReport.getChildrenFrom6To11());
        assertEquals(7, (int) existingBoosterReport.getChildrenFrom12To17());
        assertEquals(7, (int) existingBoosterReport.getAdultFemales());
        assertEquals(7, (int) existingBoosterReport.getAdultMales());
        assertEquals(7, (int) existingBoosterReport.getAdultUndifferentiated());
        assertEquals(7, (int) existingBoosterReport.getAdultUnidentified());

        assertEquals(35, (int) existingBoosterReport.getPeopleBoostered());
    }

    private void checkUpdatePrimerVaccinationReportsForDates(LocalDate date) {
        ReportPrimerVaccination existingPrimerReport = primerVaccinationDataService.findReportByDate(date);

        assertEquals(7, (int) existingPrimerReport.getChildrenFrom1To5());
        assertEquals(7, (int) existingPrimerReport.getChildrenFrom6To11());
        assertEquals(7, (int) existingPrimerReport.getChildrenFrom12To17());
        assertEquals(7, (int) existingPrimerReport.getAdultFemales());
        assertEquals(7, (int) existingPrimerReport.getAdultMales());
        assertEquals(7, (int) existingPrimerReport.getAdultUndifferentiated());
        assertEquals(7, (int) existingPrimerReport.getAdultUnidentified());

        assertEquals(35, (int) existingPrimerReport.getPeopleVaccinated());
    }
}

