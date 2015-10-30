package org.motechproject.ebodac.scheduler;

import org.joda.time.DateTime;
import org.motechproject.ebodac.client.EbodacEmailClient;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.helper.IvrCallHelper;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacEnrollmentService;
import org.motechproject.ebodac.service.EbodacService;
import org.motechproject.ebodac.service.ReportService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.messagecampaign.EventKeys;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EbodacEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(EbodacEventListener.class);

    @Autowired
    private EbodacService ebodacService;

    @Autowired
    private EbodacEmailClient ebodacEmailClient;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private EbodacEnrollmentService enrollmentService;

    @Autowired
    private IvrCallHelper ivrCallHelper;

    @MotechListener(subjects = {EbodacConstants.ZETES_UPDATE_EVENT})
    public void zetesUpdate(MotechEvent event) {
        Object zetesUrl = event.getParameters().get(EbodacConstants.ZETES_URL);
        Object username = event.getParameters().get(EbodacConstants.ZETES_USERNAME);
        Object password = event.getParameters().get(EbodacConstants.ZETES_PASSWORD);
        ebodacService.sendUpdatedSubjects(zetesUrl.toString(), username.toString(), password.toString());
    }

    @MotechListener(subjects = {EbodacConstants.EMAIL_CHECK_EVENT})
    public void emailCheck(MotechEvent event) {
        Config config = configService.getConfig();
        String host = config.getEmailHost();
        String user = config.getEmail();
        String password = config.getEmailPassword();

        if (ebodacEmailClient.hasNewJobCompletionMessage(host, user, password)) {
            ebodacService.fetchCSVUpdates();
        }
    }

    @MotechListener(subjects = {EbodacConstants.DAILY_REPORT_EVENT})
    public void generateDailyReport(MotechEvent event) {
        DateTime startDate = (DateTime) event.getParameters().get(EbodacConstants.DAILY_REPORT_EVENT_START_DATE);
        LOGGER.info("Started generation of daily reports...");
        reportService.generateDailyReports();
        reportService.generateIvrAndSmsStatisticReports();
        LOGGER.info("Daily Reports generation completed");
    }

    @MotechListener(subjects = EventKeys.CAMPAIGN_COMPLETED)
    public void completeCampaign(MotechEvent event) throws SchedulerException {
        String campaignName = (String) event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
        String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);

        enrollmentService.completeCampaign(externalId, campaignName);
    }

    @MotechListener(subjects = EventKeys.SEND_MESSAGE)
    public void initiateIvrCall(MotechEvent event) {
        LOGGER.debug("Handling Motech event {}: {}", event.getSubject(), event.getParameters().toString());

        String campaignName = (String) event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
        String messageKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);
        String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);

        ivrCallHelper.initiateIvrCall(campaignName, messageKey, externalId);
    }
}
