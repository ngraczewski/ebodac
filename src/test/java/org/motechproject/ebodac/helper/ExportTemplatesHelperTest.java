package org.motechproject.ebodac.helper;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.template.PdfBasicTemplate;
import org.motechproject.ebodac.template.PdfReportATemplate;
import org.motechproject.ebodac.template.PdfReportBTemplate;
import org.motechproject.ebodac.template.XlsBasicTemplate;
import org.motechproject.ebodac.template.XlsReportATemplate;
import org.motechproject.ebodac.template.XlsReportBTemplate;
import org.motechproject.ebodac.web.domain.GridSettings;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ExportTemplatesHelper.class)
public class ExportTemplatesHelperTest {

    @InjectMocks
    private ExportTemplatesHelper exportTemplatesHelper = new ExportTemplatesHelper();

    @Mock
    private LookupService lookupService;

    @Mock
    private ConfigService configService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    private static final String FIRST_DATE = "2015-09-15";
    private static final String LAST_DATE = "2015-09-30";

    @Test
    public void shouldSetFromAndToHeadersFromLookupByDate() throws Throwable {
        GridSettings settings = new GridSettings();

        Config config = new Config();
        config.setChiefdom("Chiefdom");
        config.setDistrict("District");

        when(configService.getConfig()).thenReturn(config);

        PdfReportBTemplate pdfReportBTemplate = mock(PdfReportBTemplate.class);
        PowerMockito.whenNew(PdfReportBTemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportBTemplate);

        PdfBasicTemplate pdfTemplateB = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME,
                Visit.class, settings, "all", "{\"motechProjectedDate\":\"2015-09-15\"}", null);

        verify((PdfReportBTemplate) pdfTemplateB).setAdditionalCellValues(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", FIRST_DATE, FIRST_DATE);


        PdfReportATemplate pdfReportATemplate = mock(PdfReportATemplate.class);
        PowerMockito.whenNew(PdfReportATemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportATemplate);

        PdfBasicTemplate pdfTemplateA = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "{\"date\":\"2015-09-15\"}", null);

        verify((PdfReportATemplate) pdfTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", FIRST_DATE, FIRST_DATE);


        XlsReportBTemplate xlsReportBTemplate = mock(XlsReportBTemplate.class);
        PowerMockito.whenNew(XlsReportBTemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportBTemplate);

        XlsBasicTemplate xlsTemplateB = exportTemplatesHelper.createTemplateForXls(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME,
                Visit.class, settings, "all", "{\"motechProjectedDate\":\"2015-09-15\"}", null);

        verify((XlsReportBTemplate) xlsTemplateB).setAdditionalCellValues(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", FIRST_DATE, FIRST_DATE);


        XlsReportATemplate xlsReportATemplate = mock(XlsReportATemplate.class);
        PowerMockito.whenNew(XlsReportATemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportATemplate);

        XlsBasicTemplate xlsTemplateA = exportTemplatesHelper.createTemplateForXls(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "{\"date\":\"2015-09-15\"}", null);

        verify((XlsReportATemplate) xlsTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", FIRST_DATE, FIRST_DATE);
    }

    @Test
    public void shouldSetFromAndToHeadersFromLookupByDateRange() throws Throwable {
        GridSettings settings = new GridSettings();

        Config config = new Config();
        config.setChiefdom("Chiefdom");
        config.setDistrict("District");

        when(configService.getConfig()).thenReturn(config);

        PdfReportBTemplate pdfReportBTemplate = mock(PdfReportBTemplate.class);
        PowerMockito.whenNew(PdfReportBTemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportBTemplate);

        PdfBasicTemplate pdfTemplateB = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME,
                Visit.class, settings, "all", "{\"motechProjectedDate\":{\"min\":\"2015-09-15\",\"max\":\"2015-09-30\"}}", null);

        verify((PdfReportBTemplate) pdfTemplateB).setAdditionalCellValues(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", FIRST_DATE, LAST_DATE);


        PdfReportATemplate pdfReportATemplate = mock(PdfReportATemplate.class);
        PowerMockito.whenNew(PdfReportATemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportATemplate);

        PdfBasicTemplate pdfTemplateA = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "{\"date\":{\"min\":\"2015-09-15\",\"max\":\"2015-09-30\"}}", null);

        verify((PdfReportATemplate) pdfTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", FIRST_DATE, LAST_DATE);


        XlsReportBTemplate xlsReportBTemplate = mock(XlsReportBTemplate.class);
        PowerMockito.whenNew(XlsReportBTemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportBTemplate);

        XlsBasicTemplate xlsTemplateB = exportTemplatesHelper.createTemplateForXls(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME,
                Visit.class, settings, "all", "{\"motechProjectedDate\":{\"min\":\"2015-09-15\",\"max\":\"2015-09-30\"}}", null);

        verify((XlsReportBTemplate) xlsTemplateB).setAdditionalCellValues(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", FIRST_DATE, LAST_DATE);


        XlsReportATemplate xlsReportATemplate = mock(XlsReportATemplate.class);
        PowerMockito.whenNew(XlsReportATemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportATemplate);

        XlsBasicTemplate xlsTemplateA = exportTemplatesHelper.createTemplateForXls(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "{\"date\":{\"min\":\"2015-09-15\",\"max\":\"2015-09-30\"}}", null);

        verify((XlsReportATemplate) xlsTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", FIRST_DATE, LAST_DATE);
    }

    @Test
    public void shouldSetFromAndToHeadersEmptyWhenNoRecordsExported() throws Throwable {
        GridSettings settings = new GridSettings();

        Config config = new Config();
        config.setChiefdom("Chiefdom");
        config.setDistrict("District");

        List<Visit> visits = new ArrayList<>();
        Records<Visit> visitRecords = new Records<>(visits);

        List<ReportPrimerVaccination> reportPrimerVaccinations = new ArrayList<>();
        Records<ReportPrimerVaccination> records = new Records<>(reportPrimerVaccinations);

        when(lookupService.getEntities(Visit.class, null, null, null)).thenReturn(visitRecords);
        when(lookupService.getEntities(ReportPrimerVaccination.class, null, null, null)).thenReturn(records);
        when(configService.getConfig()).thenReturn(config);

        PowerMockito.whenNew(QueryParams.class).withArguments(Mockito.any()).thenReturn(null);

        PdfReportBTemplate pdfReportBTemplate = mock(PdfReportBTemplate.class);
        PowerMockito.whenNew(PdfReportBTemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportBTemplate);

        PdfBasicTemplate pdfTemplateB = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME,
                Visit.class, settings, "all", "", null);

        verify((PdfReportBTemplate) pdfTemplateB).setAdditionalCellValues(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", "", "");


        PdfReportATemplate pdfReportATemplate = mock(PdfReportATemplate.class);
        PowerMockito.whenNew(PdfReportATemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportATemplate);

        PdfBasicTemplate pdfTemplateA = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "", null);

        verify((PdfReportATemplate) pdfTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", "", "");


        XlsReportBTemplate xlsReportBTemplate = mock(XlsReportBTemplate.class);
        PowerMockito.whenNew(XlsReportBTemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportBTemplate);

        XlsBasicTemplate xlsTemplateB = exportTemplatesHelper.createTemplateForXls(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME,
                Visit.class, settings, "all", "", null);

        verify((XlsReportBTemplate) xlsTemplateB).setAdditionalCellValues(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", "", "");


        XlsReportATemplate xlsReportATemplate = mock(XlsReportATemplate.class);
        PowerMockito.whenNew(XlsReportATemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportATemplate);

        XlsBasicTemplate xlsTemplateA = exportTemplatesHelper.createTemplateForXls(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "", null);

        verify((XlsReportATemplate) xlsTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", "", "");
    }

    @Test
    public void shouldSetFromAndToHeadersEmptyWhenAllDatesEmpty() throws Throwable {
        GridSettings settings = new GridSettings();

        Config config = new Config();
        config.setChiefdom("Chiefdom");
        config.setDistrict("District");

        List<Visit> visits = new ArrayList<>();
        visits.add(new Visit());
        visits.add(new Visit());
        visits.add(new Visit());
        Records<Visit> visitRecords = new Records<>(visits);

        List<ReportPrimerVaccination> reportPrimerVaccinations = new ArrayList<>();
        reportPrimerVaccinations.add(new ReportPrimerVaccination());
        reportPrimerVaccinations.add(new ReportPrimerVaccination());
        reportPrimerVaccinations.add(new ReportPrimerVaccination());
        Records<ReportPrimerVaccination> records = new Records<>(reportPrimerVaccinations);

        when(lookupService.getEntities(Visit.class, null, null, null)).thenReturn(visitRecords);
        when(lookupService.getEntities(ReportPrimerVaccination.class, null, null, null)).thenReturn(records);
        when(configService.getConfig()).thenReturn(config);

        PowerMockito.whenNew(QueryParams.class).withArguments(Mockito.any()).thenReturn(null);

        PdfReportBTemplate pdfReportBTemplate = mock(PdfReportBTemplate.class);
        PowerMockito.whenNew(PdfReportBTemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportBTemplate);

        PdfBasicTemplate pdfTemplateB = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME,
                Visit.class, settings, "all", "", null);

        verify((PdfReportBTemplate) pdfTemplateB).setAdditionalCellValues(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", "", "");


        PdfReportATemplate pdfReportATemplate = mock(PdfReportATemplate.class);
        PowerMockito.whenNew(PdfReportATemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportATemplate);

        PdfBasicTemplate pdfTemplateA = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "", null);

        verify((PdfReportATemplate) pdfTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", "", "");


        XlsReportBTemplate xlsReportBTemplate = mock(XlsReportBTemplate.class);
        PowerMockito.whenNew(XlsReportBTemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportBTemplate);

        XlsBasicTemplate xlsTemplateB = exportTemplatesHelper.createTemplateForXls(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME,
                Visit.class, settings, "all", "", null);

        verify((XlsReportBTemplate) xlsTemplateB).setAdditionalCellValues(EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", "", "");


        XlsReportATemplate xlsReportATemplate = mock(XlsReportATemplate.class);
        PowerMockito.whenNew(XlsReportATemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportATemplate);

        XlsBasicTemplate xlsTemplateA = exportTemplatesHelper.createTemplateForXls(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "", null);

        verify((XlsReportATemplate) xlsTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", "", "");
    }

    @Test
    public void shouldSetFromHeaderToSmallestNotEmptyDate() throws Throwable {
        GridSettings settings = new GridSettings();

        Config config = new Config();
        config.setChiefdom("Chiefdom");
        config.setDistrict("District");

        List<Visit> visits = new ArrayList<>();
        Visit visit = new Visit();
        Subject subject = new Subject();
        visit.setSubject(subject);
        visits.add(visit);

        visit = new Visit();
        subject = new Subject();
        subject.setPrimerVaccinationDate(new LocalDate(2015, 9, 15));
        visit.setSubject(subject);
        visits.add(visit);

        visit = new Visit();
        subject = new Subject();
        subject.setPrimerVaccinationDate(new LocalDate(2015, 9, 30));
        visit.setSubject(subject);
        visits.add(visit);
        Records<Visit> visitRecords = new Records<>(visits);

        List<ReportPrimerVaccination> reportPrimerVaccinations = new ArrayList<>();
        reportPrimerVaccinations.add(new ReportPrimerVaccination());
        reportPrimerVaccinations.add(new ReportPrimerVaccination(new LocalDate(2015, 9, 15), 0, 0, 0, 0, 0, 0, 0, 0));
        reportPrimerVaccinations.add(new ReportPrimerVaccination(new LocalDate(2015, 9, 30), 0, 0, 0, 0, 0, 0, 0, 0));
        Records<ReportPrimerVaccination> records = new Records<>(reportPrimerVaccinations);

        when(lookupService.getEntities(Visit.class, null, null, null)).thenReturn(visitRecords);
        when(lookupService.getEntities(ReportPrimerVaccination.class, null, null, null)).thenReturn(records);
        when(configService.getConfig()).thenReturn(config);

        PowerMockito.whenNew(QueryParams.class).withArguments(Mockito.any()).thenReturn(null);

        PdfReportBTemplate pdfReportBTemplate = mock(PdfReportBTemplate.class);
        PowerMockito.whenNew(PdfReportBTemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportBTemplate);

        PdfBasicTemplate pdfTemplateB = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT_NAME,
                Visit.class, settings, "all", "", null);

        verify((PdfReportBTemplate) pdfTemplateB).setAdditionalCellValues(EbodacConstants.FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", FIRST_DATE, LAST_DATE);


        PdfReportATemplate pdfReportATemplate = mock(PdfReportATemplate.class);
        PowerMockito.whenNew(PdfReportATemplate.class).withArguments(Mockito.any()).thenReturn(pdfReportATemplate);

        PdfBasicTemplate pdfTemplateA = exportTemplatesHelper.createTemplateForPdf(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "", null);

        verify((PdfReportATemplate) pdfTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", FIRST_DATE, LAST_DATE);


        XlsReportBTemplate xlsReportBTemplate = mock(XlsReportBTemplate.class);
        PowerMockito.whenNew(XlsReportBTemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportBTemplate);

        XlsBasicTemplate xlsTemplateB = exportTemplatesHelper.createTemplateForXls(EbodacConstants.FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT_NAME,
                Visit.class, settings, "all", "", null);

        verify((XlsReportBTemplate) xlsTemplateB).setAdditionalCellValues(EbodacConstants.FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "District", "Chiefdom", FIRST_DATE, LAST_DATE);


        XlsReportATemplate xlsReportATemplate = mock(XlsReportATemplate.class);
        PowerMockito.whenNew(XlsReportATemplate.class).withArguments(Mockito.any()).thenReturn(xlsReportATemplate);

        XlsBasicTemplate xlsTemplateA = exportTemplatesHelper.createTemplateForXls(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME,
                ReportPrimerVaccination.class, settings, "all", "", null);

        verify((XlsReportATemplate) xlsTemplateA).setAdditionalCellValues(EbodacConstants.PRIMER_VACCINATION_REPORT_NAME.replaceAll("([A-Z])", " $1"),
                "Daily", "District", FIRST_DATE, LAST_DATE);
    }
}
