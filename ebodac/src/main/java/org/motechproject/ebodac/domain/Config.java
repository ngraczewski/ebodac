package org.motechproject.ebodac.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ebodac.constants.EbodacConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Config {

    public static final String TIME_PICKER_FORMAT = "HH:mm";

    private String zetesUrl;

    private String zetesUsername;

    private String zetesPassword;

    private String startTime;

    private String ftpsHost;

    private Integer ftpsPort;

    private String ftpsDirectory;

    private String ftpsUsername;

    private String ftpsPassword;

    private String lastCsvUpdate;

    private String emailHost;

    private String email;

    private String emailPassword;

    private Integer emailCheckInterval;

    private String reportCalculationStartTime;

    private String firstCalculationStartDate;

    private String lastCalculationDate;

    private String lastCalculationDateForIvrReports;

    private Boolean generateReports;

    private Boolean showWarnings = true;

    private Set<String> primerVaccinationReportsToUpdate;

    private Set<String> boosterVaccinationReportsToUpdate;

    private List<String> disconVacCampaignsList;

    private List<String> availableLookupsForDailyClinicVisitScheduleReport;

    private List<String> availableLookupsForFollowupsAfterPrimeInjectionReport;

    private List<String> availableLookupsForFollowupsMissedClinicVisitsReport;

    private List<String> availableLookupsForMandEMissedClinicVisitsReport;

    private List<String> availableLookupsForOptsOutOfMotechMessagesReport;

    private List<String> availableLookupsForVisits;

    private List<String> availableLookupsForSubjectEnrollments;

    private Boolean enableZetesJob = false;

    private Boolean enableRaveJob = false;

    private Boolean enableReportJob = false;

    private String district;

    private String chiefdom;

    private Boolean sendIvrCalls = true;

    private String ivrSettingsName;

    private String apiKey;

    private String statusCallbackUrl;

    private Boolean sendSmsIfVoiceFails = true;

    private Boolean detectVoiceMail = true;

    private Integer retryAttempts;

    private Integer retryDelay;

    public Config() {
    }

    public String getZetesUrl() {
        return zetesUrl;
    }

    public void setZetesUrl(String zetesUrl) {
        this.zetesUrl = zetesUrl;
    }

    public String getZetesPassword() {
        return zetesPassword;
    }

    public void setZetesPassword(String zetesPassword) {
        this.zetesPassword = zetesPassword;
    }

    public String getZetesUsername() {
        return zetesUsername;
    }

    public void setZetesUsername(String zetesUsername) {
        this.zetesUsername = zetesUsername;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFtpsHost() {
        return ftpsHost;
    }

    public void setFtpsHost(String ftpsHost) {
        this.ftpsHost = ftpsHost;
    }

    public Integer getFtpsPort() {
        return ftpsPort;
    }

    public void setFtpsPort(Integer ftpsPort) {
        this.ftpsPort = ftpsPort;
    }

    public String getFtpsDirectory() {
        return ftpsDirectory;
    }

    public void setFtpsDirectory(String ftpsDirectory) {
        this.ftpsDirectory = ftpsDirectory;
    }

    public String getFtpsUsername() {
        return ftpsUsername;
    }

    public void setFtpsUsername(String ftpsUsername) {
        this.ftpsUsername = ftpsUsername;
    }

    public String getFtpsPassword() {
        return ftpsPassword;
    }

    public void setFtpsPassword(String ftpsPassword) {
        this.ftpsPassword = ftpsPassword;
    }

    public String getLastCsvUpdate() {
        return lastCsvUpdate;
    }

    public void setLastCsvUpdate(String lastCsvUpdate) {
        this.lastCsvUpdate = lastCsvUpdate;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public Integer getEmailCheckInterval() {
        return emailCheckInterval;
    }

    public void setEmailCheckInterval(Integer emailCheckInterval) {
        this.emailCheckInterval = emailCheckInterval;
    }

    public String getReportCalculationStartTime() {
        if (StringUtils.isBlank(reportCalculationStartTime)) {
            reportCalculationStartTime = EbodacConstants.DAILY_REPORT_EVENT_START_HOUR;
        }
        return reportCalculationStartTime;
    }

    public void setReportCalculationStartTime(String reportCalculationStartTime) {
        this.reportCalculationStartTime = reportCalculationStartTime;
    }

    public String getFirstCalculationStartDate() {
        return firstCalculationStartDate;
    }

    public void setFirstCalculationStartDate(String firstCalculationStartDate) {
        this.firstCalculationStartDate = firstCalculationStartDate;
    }

    public String getLastCalculationDate() {
        return lastCalculationDate;
    }

    public void setLastCalculationDate(String lastCalculationDate) {
        this.lastCalculationDate = lastCalculationDate;
    }

    public String getLastCalculationDateForIvrReports() {
        return lastCalculationDateForIvrReports;
    }

    public void setLastCalculationDateForIvrReports(String lastCalculationDateForIvrReports) {
        this.lastCalculationDateForIvrReports = lastCalculationDateForIvrReports;
    }

    public Boolean getGenerateReports() {
        return generateReports;
    }

    public void setGenerateReports(Boolean generateReports) {
        this.generateReports = generateReports;
    }

    public Boolean getShowWarnings() {
        return showWarnings;
    }

    public void setShowWarnings(Boolean showWarnings) {
        this.showWarnings = showWarnings;
    }

    public Set<String> getPrimerVaccinationReportsToUpdate() {
        if (primerVaccinationReportsToUpdate == null) {
            primerVaccinationReportsToUpdate = new HashSet<>();
        }
        return primerVaccinationReportsToUpdate;
    }

    public void setPrimerVaccinationReportsToUpdate(Set<String> primerVaccinationReportsToUpdate) {
        this.primerVaccinationReportsToUpdate = primerVaccinationReportsToUpdate;
    }

    public Set<String> getBoosterVaccinationReportsToUpdate() {
        if (boosterVaccinationReportsToUpdate == null) {
            boosterVaccinationReportsToUpdate = new HashSet<>();
        }
        return boosterVaccinationReportsToUpdate;
    }

    public void setBoosterVaccinationReportsToUpdate(Set<String> boosterVaccinationReportsToUpdate) {
        this.boosterVaccinationReportsToUpdate = boosterVaccinationReportsToUpdate;
    }

    public List<String> getDisconVacCampaignsList() {
        if (disconVacCampaignsList == null) {
            disconVacCampaignsList = new ArrayList<>();
        }
        return disconVacCampaignsList;
    }

    public void setDisconVacCampaignsList(List<String> disconVacCampaignsList) {
        this.disconVacCampaignsList = disconVacCampaignsList;
    }

    public List<String> getAvailableLookupsForDailyClinicVisitScheduleReport() {
        if (availableLookupsForDailyClinicVisitScheduleReport == null) {
            availableLookupsForDailyClinicVisitScheduleReport = new ArrayList<>();
        }
        return availableLookupsForDailyClinicVisitScheduleReport;
    }

    public void setAvailableLookupsForDailyClinicVisitScheduleReport(List<String> availableLookupsForDailyClinicVisitScheduleReport) {
        this.availableLookupsForDailyClinicVisitScheduleReport = availableLookupsForDailyClinicVisitScheduleReport;
    }

    public List<String> getAvailableLookupsForFollowupsAfterPrimeInjectionReport() {
        if (availableLookupsForFollowupsAfterPrimeInjectionReport == null) {
            availableLookupsForFollowupsAfterPrimeInjectionReport = new ArrayList<>();
        }
        return availableLookupsForFollowupsAfterPrimeInjectionReport;
    }

    public void setAvailableLookupsForFollowupsAfterPrimeInjectionReport(List<String> availableLookupsForFollowupsAfterPrimeInjectionReport) {
        this.availableLookupsForFollowupsAfterPrimeInjectionReport = availableLookupsForFollowupsAfterPrimeInjectionReport;
    }

    public List<String> getAvailableLookupsForFollowupsMissedClinicVisitsReport() {
        if (availableLookupsForFollowupsMissedClinicVisitsReport == null) {
            availableLookupsForFollowupsMissedClinicVisitsReport = new ArrayList<>();
        }
        return availableLookupsForFollowupsMissedClinicVisitsReport;
    }

    public void setAvailableLookupsForFollowupsMissedClinicVisitsReport(List<String> availableLookupsForFollowupsMissedClinicVisitsReport) {
        this.availableLookupsForFollowupsMissedClinicVisitsReport = availableLookupsForFollowupsMissedClinicVisitsReport;
    }


    public List<String> getAvailableLookupsForMandEMissedClinicVisitsReport() {
        if (availableLookupsForMandEMissedClinicVisitsReport == null) {
            availableLookupsForMandEMissedClinicVisitsReport = new ArrayList<>();
        }
        return availableLookupsForMandEMissedClinicVisitsReport;
    }

    public void setAvailableLookupsForMandEMissedClinicVisitsReport(List<String> availableLookupsForMandEMissedClinicVisitsReport) {
        this.availableLookupsForMandEMissedClinicVisitsReport = availableLookupsForMandEMissedClinicVisitsReport;
    }

    public List<String> getAvailableLookupsForOptsOutOfMotechMessagesReport() {
        if (availableLookupsForOptsOutOfMotechMessagesReport == null) {
            availableLookupsForOptsOutOfMotechMessagesReport = new ArrayList<>();
        }
        return availableLookupsForOptsOutOfMotechMessagesReport;
    }

    public void setAvailableLookupsForOptsOutOfMotechMessagesReport(List<String> availableLookupsForOptsOutOfMotechMessagesReport) {
        this.availableLookupsForOptsOutOfMotechMessagesReport = availableLookupsForOptsOutOfMotechMessagesReport;
    }

    public List<String> getAvailableLookupsForVisits() {
        if (availableLookupsForVisits == null) {
            availableLookupsForVisits = new ArrayList<>();
        }
        return availableLookupsForVisits;
    }

    public void setAvailableLookupsForVisits(List<String> availableLookupsForVisits) {
        this.availableLookupsForVisits = availableLookupsForVisits;
    }

    public List<String> getAvailableLookupsForSubjectEnrollments() {
        if (availableLookupsForSubjectEnrollments == null) {
            availableLookupsForSubjectEnrollments = new ArrayList<>();
        }
        return availableLookupsForSubjectEnrollments;
    }

    public void setAvailableLookupsForSubjectEnrollments(List<String> availableLookupsForSubjectEnrollments) {
        this.availableLookupsForSubjectEnrollments = availableLookupsForSubjectEnrollments;
    }

    public Boolean getEnableZetesJob() {
        return enableZetesJob;
    }

    public void setEnableZetesJob(Boolean enableZetesJob) {
        this.enableZetesJob = enableZetesJob;
    }

    public Boolean getEnableRaveJob() {
        return enableRaveJob;
    }

    public void setEnableRaveJob(Boolean enableRaveJob) {
        this.enableRaveJob = enableRaveJob;
    }

    public Boolean getEnableReportJob() {
        return enableReportJob;
    }

    public void setEnableReportJob(Boolean enableReportJob) {
        this.enableReportJob = enableReportJob;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getChiefdom() {
        return chiefdom;
    }

    public void setChiefdom(String chiefdom) {
        this.chiefdom = chiefdom;
    }

    public Boolean getSendIvrCalls() {
        return sendIvrCalls;
    }

    public void setSendIvrCalls(Boolean sendIvrCalls) {
        this.sendIvrCalls = sendIvrCalls;
    }

    public String getIvrSettingsName() {
        return ivrSettingsName;
    }

    public void setIvrSettingsName(String ivrSettingsName) {
        this.ivrSettingsName = ivrSettingsName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getStatusCallbackUrl() {
        return statusCallbackUrl;
    }

    public void setStatusCallbackUrl(String statusCallbackUrl) {
        this.statusCallbackUrl = statusCallbackUrl;
    }

    public Boolean getSendSmsIfVoiceFails() {
        return sendSmsIfVoiceFails;
    }

    public void setSendSmsIfVoiceFails(Boolean sendSmsIfVoiceFails) {
        this.sendSmsIfVoiceFails = sendSmsIfVoiceFails;
    }

    public Boolean getDetectVoiceMail() {
        return detectVoiceMail;
    }

    public void setDetectVoiceMail(Boolean detectVoiceMail) {
        this.detectVoiceMail = detectVoiceMail;
    }

    public Integer getRetryAttempts() {
        return retryAttempts;
    }

    public void setRetryAttempts(Integer retryAttempts) {
        this.retryAttempts = retryAttempts;
    }

    public Integer getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(Integer retryDelay) {
        this.retryDelay = retryDelay;
    }
}
