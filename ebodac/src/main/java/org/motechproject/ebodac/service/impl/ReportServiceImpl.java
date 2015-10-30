package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.IvrAndSmsStatisticReport;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.exception.EbodacReportException;
import org.motechproject.ebodac.repository.IvrAndSmsStatisticReportDataService;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.ReportService;
import org.motechproject.ebodac.service.ReportUpdateService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.repository.CallDetailRecordDataService;
import org.motechproject.mds.query.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    private static final DateTimeFormatter SIMPLE_DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd");

    private ReportPrimerVaccinationDataService primerVaccinationDataService;

    private ReportBoosterVaccinationDataService boosterVaccinationDataService;

    private ConfigService configService;

    private SubjectService subjectService;

    private ReportUpdateService reportUpdateService;

    private CallDetailRecordDataService callDetailRecordDataService;

    private IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService;

    @Override
    public void generateDailyReports() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

        Config config = configService.getConfig();

        if (config.getGenerateReports() != null && config.getGenerateReports()) {
            String lastCalculationDate = config.getLastCalculationDate();
            String calculationStartDate = config.getFirstCalculationStartDate();

            LocalDate startDate;

            if (StringUtils.isNotBlank(lastCalculationDate)) {
                startDate = LocalDate.parse(lastCalculationDate, formatter).plusDays(1);
            } else if (StringUtils.isNotBlank(calculationStartDate)) {
                startDate = LocalDate.parse(calculationStartDate, formatter);
            } else {
                startDate = subjectService.findOldestPrimerVaccinationDate();
            }

            updateBoosterVaccinationReportsForDates(reportUpdateService.getBoosterVaccinationReportsToUpdate());
            updatePrimerVaccinationReportsForDates(reportUpdateService.getPrimerVaccinationReportsToUpdate());

            generateDailyReportsFromDate(startDate);

            config = configService.getConfig();
            config.setGenerateReports(false);
            config.setLastCalculationDate(DateUtil.now().minusDays(1).toString(formatter));
            configService.updateConfig(config);
        }
    }

    @Override
    public void generateDailyReportsFromDate(LocalDate startDate) {
        LocalDate now = DateUtil.now().toLocalDate();

        for(LocalDate date = startDate; date.isBefore(now); date = date.plusDays(1)) {
            generateOrUpdatePrimerVaccinationReport(subjectService.findSubjectsPrimerVaccinatedAtDay(date), date);
            generateOrUpdateBoosterVaccinationReport(subjectService.findSubjectsBoosterVaccinatedAtDay(date), date);
        }
    }

    @Override
    public void generateIvrAndSmsStatisticReports() {
        Config config = configService.getConfig();

        if (StringUtils.isNotBlank(config.getLastCalculationDateForIvrReports())) {
            LocalDate startDate = SIMPLE_DATE_FORMATTER.parseLocalDate(config.getLastCalculationDateForIvrReports());
            generateIvrAndSmsStatisticReportsFromDate(startDate);
        } else {
            generateIvrAndSmsStatisticReportsFromDate(null);
        }

        config.setLastCalculationDateForIvrReports(LocalDate.now().toString(SIMPLE_DATE_FORMATTER));
        configService.updateConfig(config);
    }

    @Override
    public void generateIvrAndSmsStatisticReportsFromDate(LocalDate startDate) {
        List<CallDetailRecord> callDetailRecords = new ArrayList<>();

        if (startDate == null) {
            callDetailRecords = callDetailRecordDataService.findByCallStatus(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED);
        } else {
            LocalDate now = DateUtil.now().toLocalDate();

            for(LocalDate date = startDate; date.isBefore(now); date = date.plusDays(1)) {
                String dateString = SIMPLE_DATE_FORMATTER.print(date);
                callDetailRecords.addAll(callDetailRecordDataService.findByMotechTimestampAndCallStatus(dateString, EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_INITIATED));
            }
        }

        for (CallDetailRecord callDetailRecord : callDetailRecords) {
            try {
                createIvrAndSmsStatisticReport(callDetailRecord);
            } catch (EbodacReportException e) {
                LOGGER.warn(e.getMessage());
            }
        }
    }

    private void updateBoosterVaccinationReportsForDates(Set<String> dates) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

        for (String dateString : dates) {
            LocalDate date = LocalDate.parse(dateString, formatter);
            generateOrUpdateBoosterVaccinationReport(subjectService.findSubjectsBoosterVaccinatedAtDay(date), date);
        }
    }

    private void updatePrimerVaccinationReportsForDates(Set<String> dates) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

        for (String dateString : dates) {
            LocalDate date = LocalDate.parse(dateString, formatter);
            generateOrUpdatePrimerVaccinationReport(subjectService.findSubjectsPrimerVaccinatedAtDay(date), date);
        }
    }

    private void generateOrUpdateBoosterVaccinationReport(List<Subject> subjects, LocalDate date) {
        LocalDate age6 = date.minusYears(6);
        LocalDate age12 = date.minusYears(12);
        LocalDate age18 = date.minusYears(18);

        int childrenFrom1To5 = 0;
        int childrenFrom6To11 = 0;
        int childrenFrom12To17 = 0;
        int adultMales = 0;
        int adultFemales = 0;
        int adultUndifferentiated = 0;
        int adultUnidentified = 0;

        for (Subject subject : subjects) {
            if (subject.getDateOfBirth() == null) {
                LOGGER.warn("Subject with id: {} has no birth date", subject.getSubjectId());
            } else if (subject.getDateOfBirth().isAfter(age6)) {
                childrenFrom1To5++;
            } else if (subject.getDateOfBirth().isAfter(age12)) {
                childrenFrom6To11++;
            } else if (subject.getDateOfBirth().isAfter(age18)) {
                childrenFrom12To17++;
            } else if (Gender.Male.equals(subject.getGender())) {
                adultMales++;
            } else if (Gender.Female.equals(subject.getGender())) {
                adultFemales++;
            } else if (Gender.Undifferentiated.equals(subject.getGender())) {
                adultUndifferentiated++;
            } else {
                adultUnidentified++;
            }
        }

        int peopleBoostered = childrenFrom1To5 + childrenFrom6To11 + childrenFrom12To17 + adultMales + adultFemales;

        ReportBoosterVaccination existingReport = boosterVaccinationDataService.findReportByDate(date);

        if (existingReport != null) {
            existingReport.updateReportData(adultMales, adultFemales, childrenFrom1To5, childrenFrom6To11, childrenFrom12To17,
                    adultUnidentified, adultUndifferentiated, peopleBoostered);
            boosterVaccinationDataService.update(existingReport);

            LOGGER.debug("Booster Vaccination Daily Report for date: {} updated",
                    date.toString(DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT)));
        } else {
            ReportBoosterVaccination reportBoosterVaccination = new ReportBoosterVaccination(date, adultMales, adultFemales,
                    childrenFrom1To5, childrenFrom6To11, childrenFrom12To17, adultUnidentified, adultUndifferentiated, peopleBoostered);

            boosterVaccinationDataService.create(reportBoosterVaccination);

            LOGGER.debug("Booster Vaccination Daily Report for date: {} created",
                    date.toString(DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT)));
        }
    }

    private void generateOrUpdatePrimerVaccinationReport(List<Subject> subjects, LocalDate date) {
        LocalDate age6 = date.minusYears(6);
        LocalDate age12 = date.minusYears(12);
        LocalDate age18 = date.minusYears(18);

        int childrenFrom1To5 = 0;
        int childrenFrom6To11 = 0;
        int childrenFrom12To17 = 0;
        int adultMales = 0;
        int adultFemales = 0;
        int adultUndifferentiated = 0;
        int adultUnidentified = 0;

        for (Subject subject : subjects) {
            if (subject.getDateOfBirth() == null) {
                LOGGER.warn("Subject with id: {} has no birth date", subject.getSubjectId());
            } else if (subject.getDateOfBirth().isAfter(age6)) {
                childrenFrom1To5++;
            } else if (subject.getDateOfBirth().isAfter(age12)) {
                childrenFrom6To11++;
            } else if (subject.getDateOfBirth().isAfter(age18)) {
                childrenFrom12To17++;
            } else if (Gender.Male.equals(subject.getGender())) {
                adultMales++;
            } else if (Gender.Female.equals(subject.getGender())) {
                adultFemales++;
            } else if (Gender.Undifferentiated.equals(subject.getGender())) {
                adultUndifferentiated++;
            } else {
                adultUnidentified++;
            }
        }

        int peopleVaccinated = childrenFrom1To5 + childrenFrom6To11 + childrenFrom12To17 + adultMales + adultFemales;

        ReportPrimerVaccination existingReport = primerVaccinationDataService.findReportByDate(date);

        if (existingReport != null) {
            existingReport.updateReportData(adultMales, adultFemales, childrenFrom1To5, childrenFrom6To11, childrenFrom12To17,
                    adultUnidentified, adultUndifferentiated, peopleVaccinated);
            primerVaccinationDataService.update(existingReport);

            LOGGER.debug("Primer Vaccination Daily Report for date: {} updated",
                    date.toString(DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT)));
        } else {
            ReportPrimerVaccination reportPrimerVaccination = new ReportPrimerVaccination(date, adultMales, adultFemales,
                    childrenFrom1To5, childrenFrom6To11, childrenFrom12To17, adultUnidentified, adultUndifferentiated, peopleVaccinated);

            primerVaccinationDataService.create(reportPrimerVaccination);

            LOGGER.debug("Primer Vaccination Daily Report for date: {} created",
                    date.toString(DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT)));
        }
    }

    private void createIvrAndSmsStatisticReport(CallDetailRecord initialRecord) { //NO CHECKSTYLE CyclomaticComplexity
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.IVR_CALL_DETAIL_RECORD_TIME_FORMAT);

        String providerCallId = initialRecord.getProviderCallId();
        Map<String, String> providerExtraData = initialRecord.getProviderExtraData();

        if (StringUtils.isBlank(providerCallId)) {
            throw new EbodacReportException("Cannot generate report for Call Detail Record with Motech Call Id: %s, because Provider Call Id is empty",
                    "", initialRecord.getMotechCallId());
        }
        if (providerExtraData == null || providerExtraData.isEmpty()) {
            throw new EbodacReportException("Cannot generate report for Call Detail Record with Motech Call Id: %s, because Provider Extra Data Map is empty",
                    "", initialRecord.getMotechCallId());
        }

        String subjectIds = providerExtraData.get(EbodacConstants.SUBJECT_IDS);
        List<Subject> subjects = new ArrayList<>();

        if (StringUtils.isBlank(subjectIds)) {
            throw new EbodacReportException("Cannot generate report for Call Detail Record with Motech Call Id: %s, because No Participant Id found In Provider Extra Data Map",
                    "", initialRecord.getMotechCallId());
        }

        for (String subjectId : subjectIds.split(",")) {
            Subject subject = subjectService.findSubjectBySubjectId(subjectId.trim());
            subjects.add(subject);
        }

        if (subjects.isEmpty()) {
            throw new EbodacReportException("Cannot generate report for Call Detail Record with Motech Call Id: %s, because No Participants found with Ids: %s",
                    "", initialRecord.getMotechCallId(), subjectIds);
        }

        List<CallDetailRecord> callDetailRecords = callDetailRecordDataService.findByExactProviderCallId(providerCallId,
                QueryParams.ascOrder(EbodacConstants.IVR_CALL_DETAIL_RECORD_MOTECH_TIMESTAMP_FIELD));

        List<CallDetailRecord> failed = new ArrayList<>();
        List<CallDetailRecord> finished = new ArrayList<>();
        boolean sms = false;
        String messageId = providerExtraData.get(EbodacConstants.MESSAGE_ID);
        DateTime sendDate = DateTime.parse(initialRecord.getMotechTimestamp(), formatter);
        int attempts = 0;
        DateTime receivedDate = null;
        DateTime smsReceivedDate = null;
        double expectedDuration = 0;
        double timeListenedTo = 0;

        for (CallDetailRecord callDetailRecord : callDetailRecords) {
            if (callDetailRecord.getCallStatus() == null) {
                continue;
            }
            if (callDetailRecord.getCallStatus().contains(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_SUBMITTED)) {
                sms = true;
                smsReceivedDate = DateTime.parse(callDetailRecord.getMotechTimestamp(), formatter);
            } else if (callDetailRecord.getCallStatus().contains(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FINISHED)) {
                finished.add(callDetailRecord);
            } else if (callDetailRecord.getCallStatus().contains(EbodacConstants.IVR_CALL_DETAIL_RECORD_STATUS_FAILED)) {
                failed.add(callDetailRecord);
            }
        }

        Integer recordsCount = failed.size() + finished.size();
        CallDetailRecord callRecord;

        if (sms) {
            if (failed.isEmpty()) {
                throw new EbodacReportException("Cannot generate report for Call Detail Record with Provider Call Id: %s for Providers with Ids %s, because SMS was sent but no failed record found for the Call",
                        "", providerCallId, subjectIds);
            }
            if (recordsCount > 2) {
                throw new EbodacReportException("Cannot generate report for Call Detail Record with Provider Call Id: %s for Providers with Ids %s, because there is too much records with failed/finished status (%s)",
                        "", providerCallId, subjectIds, recordsCount.toString());
            }
            if (failed.size() == 2) {
                sms = false;
                smsReceivedDate = null;
                LOGGER.warn("Failed to sent SMS for Call Detail Record with Provider Call Id: {} for Providers with Ids {}", providerCallId, subjectIds);
            }
            if (finished.isEmpty()) {
                LOGGER.warn("SMS is sent but not yet received for Call Detail Record with Provider Call Id: {} for Providers with Ids {}", providerCallId, subjectIds);
            }

            callRecord = failed.get(0);
        } else {
            if (recordsCount > 1) {
                throw new EbodacReportException("Cannot generate report for Call Detail Record with Provider Call Id: %s for Providers with Ids %s, because there is too much records with failed/finished status (%s)",
                        "", providerCallId, subjectIds, recordsCount.toString());
            }
            if (finished.isEmpty()) {
                throw new EbodacReportException("Cannot generate report for Call Detail Record with Provider Call Id: %s for Providers with Ids %s, because no SMS was sent but there is no record with finished status",
                        "", providerCallId, subjectIds);
            }

            callRecord = finished.get(0);
            receivedDate = DateTime.parse(callRecord.getMotechTimestamp(), formatter);

            if (StringUtils.isNotBlank(callRecord.getCallDuration())) {
                timeListenedTo = Double.parseDouble(callRecord.getCallDuration());
                if (StringUtils.isNotBlank(callRecord.getMessagePercentListened())) {
                    double messagePercentListened = Double.parseDouble(callRecord.getMessagePercentListened());
                    expectedDuration = timeListenedTo * 100 / messagePercentListened;
                }
            }
        }

        String attemptsString = callRecord.getProviderExtraData().get(EbodacConstants.IVR_CALL_DETAIL_RECORD_NUMBER_OF_ATTEMPTS);
        if (StringUtils.isNotBlank(attemptsString)) {
            attempts = Integer.parseInt(attemptsString);
        }

        for(Subject subject : subjects) {
            IvrAndSmsStatisticReport ivrAndSmsStatisticReport = new IvrAndSmsStatisticReport(subject, messageId, sendDate,
                    expectedDuration, timeListenedTo, receivedDate, attempts, sms, smsReceivedDate);
            ivrAndSmsStatisticReportDataService.create(ivrAndSmsStatisticReport);
        }
    }

    @Autowired
    public void setPrimerVaccinationDataService(ReportPrimerVaccinationDataService primerVaccinationDataService) {
        this.primerVaccinationDataService = primerVaccinationDataService;
    }

    @Autowired
    public void setBoosterVaccinationDataService(ReportBoosterVaccinationDataService boosterVaccinationDataService) {
        this.boosterVaccinationDataService = boosterVaccinationDataService;
    }

    @Autowired
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Autowired
    public void setReportUpdateService(ReportUpdateService reportUpdateService) {
        this.reportUpdateService = reportUpdateService;
    }

    @Autowired
    public void setCallDetailRecordDataService(CallDetailRecordDataService callDetailRecordDataService) {
        this.callDetailRecordDataService = callDetailRecordDataService;
    }

    @Autowired
    public void setIvrAndSmsStatisticReportDataService(IvrAndSmsStatisticReportDataService ivrAndSmsStatisticReportDataService) {
        this.ivrAndSmsStatisticReportDataService = ivrAndSmsStatisticReportDataService;
    }
}
