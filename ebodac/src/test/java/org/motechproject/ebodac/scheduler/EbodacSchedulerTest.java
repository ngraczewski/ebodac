package org.motechproject.ebodac.scheduler;


import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduler.contract.RepeatingPeriodSchedulableJob;
import org.motechproject.scheduler.service.MotechSchedulerService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class EbodacSchedulerTest {

    @Mock
    private MotechSchedulerService motechSchedulerService;

    private EbodacScheduler ebodacScheduler;

    @Before
    public void setUp(){
        initMocks(this);
        ebodacScheduler = new EbodacScheduler(motechSchedulerService);
    }

    @After
    public void cleanup() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    @Test
    public void shouldScheduleZetesUpdateJob() {
        Date startDate = LocalDate.now().toDate();
        String zetesUrl = "zetesUrl";
        String zetesUsername = "username";
        String zetesPassword = "password";

        ebodacScheduler.scheduleZetesUpdateJob(startDate,zetesUrl,zetesUsername,zetesPassword);

        Map<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(EbodacConstants.ZETES_URL, zetesUrl);
        eventParameters.put(EbodacConstants.ZETES_USERNAME, zetesUsername);
        eventParameters.put(EbodacConstants.ZETES_PASSWORD, zetesPassword);
        MotechEvent event = new MotechEvent(EbodacConstants.ZETES_UPDATE_EVENT, eventParameters);
        Period period = Period.days(1);
        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate, null, period, true);

        verify(motechSchedulerService).safeScheduleRepeatingPeriodJob(job);
    }

    @Test
    public void shouldUnscheduleZetesUpdateJob() {
        ebodacScheduler.unscheduleZetesUpdateJob();
        verify(motechSchedulerService).safeUnscheduleAllJobs(EbodacConstants.ZETES_UPDATE_EVENT);
    }

    @Test
    public void shouldScheduleEmailCheckJob() {
        Integer interval = 500;
        DateTimeUtils.setCurrentMillisFixed(10L);
        DateTime startDate = DateTime.now().plusMinutes(1);

        ebodacScheduler.scheduleEmailCheckJob(interval);

        MotechEvent event = new MotechEvent(EbodacConstants.EMAIL_CHECK_EVENT);
        Period period = Period.minutes(interval);

        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate.toDate(), null, period, true);

        verify(motechSchedulerService).safeScheduleRepeatingPeriodJob(job);
    }

    @Test
    public void shouldUnscheduleEmailCheckJob() {
        ebodacScheduler.unscheduleEmailCheckJob();
        verify(motechSchedulerService).safeUnscheduleAllJobs(EbodacConstants.EMAIL_CHECK_EVENT);
    }

    @Test
    public void shouldScheduleDailyReportJob() {
        DateTime startDate = DateUtil.newDateTime(LocalDate.now().plusDays(1), Time.parseTime(EbodacConstants.DAILY_REPORT_EVENT_START_HOUR, ":"));

        ebodacScheduler.scheduleDailyReportJob(startDate);

        Period period = Period.days(1);
        Map<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(EbodacConstants.DAILY_REPORT_EVENT_START_DATE, startDate);
        MotechEvent event = new MotechEvent(EbodacConstants.DAILY_REPORT_EVENT, eventParameters);
        RepeatingPeriodSchedulableJob job = new RepeatingPeriodSchedulableJob(event, startDate.toDate(), null, period, true);

        verify(motechSchedulerService).safeScheduleRepeatingPeriodJob(job);
    }

    @Test
    public void shouldUnscheduleDailyReportJob() {
        ebodacScheduler.unscheduleDailyReportJob();
        verify(motechSchedulerService).safeUnscheduleAllJobs(EbodacConstants.DAILY_REPORT_EVENT);
    }

}
