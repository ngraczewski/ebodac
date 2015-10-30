package org.motechproject.ebodac.scheduler;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EbodacScheduler {

    private MotechSchedulerService motechSchedulerService;

    @Autowired
    public EbodacScheduler(MotechSchedulerService motechSchedulerService) {
        this.motechSchedulerService = motechSchedulerService;
    }

    public void scheduleZetesUpdateJob(Date startDate, String zetesUrl, String zetesUsername, String zetesPassword) {
        Map<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(EbodacConstants.ZETES_URL, zetesUrl);
        eventParameters.put(EbodacConstants.ZETES_USERNAME, zetesUsername);
        eventParameters.put(EbodacConstants.ZETES_PASSWORD, zetesPassword);

        MotechEvent event = new MotechEvent(EbodacConstants.ZETES_UPDATE_EVENT, eventParameters);

        Period period = Period.days(1);

        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate, null, period, true);
        motechSchedulerService.safeScheduleRepeatingPeriodJob(job);
    }

    public void scheduleEmailCheckJob(Integer interval) {
        MotechEvent event = new MotechEvent(EbodacConstants.EMAIL_CHECK_EVENT);

        Period period = Period.minutes(interval);

        DateTime startDate = DateTime.now().plusMinutes(1);
        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate.toDate(), null, period, true);
        motechSchedulerService.safeScheduleRepeatingPeriodJob(job);
    }

    public void unscheduleZetesUpdateJob() {
        motechSchedulerService.safeUnscheduleAllJobs(EbodacConstants.ZETES_UPDATE_EVENT);
    }

    public void unscheduleEmailCheckJob() {
        motechSchedulerService.safeUnscheduleAllJobs(EbodacConstants.EMAIL_CHECK_EVENT);
    }

    public void scheduleDailyReportJob(DateTime startDate) {
        Period period = Period.days(1);

        Map<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(EbodacConstants.DAILY_REPORT_EVENT_START_DATE, startDate);

        MotechEvent event = new MotechEvent(EbodacConstants.DAILY_REPORT_EVENT, eventParameters);

        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate.toDate(), null, period, true);
        motechSchedulerService.safeScheduleRepeatingPeriodJob(job);
    }

    public void unscheduleDailyReportJob() {
        motechSchedulerService.safeUnscheduleAllJobs(EbodacConstants.DAILY_REPORT_EVENT);
    }
}
