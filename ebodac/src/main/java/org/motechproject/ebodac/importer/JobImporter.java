package org.motechproject.ebodac.importer;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.scheduler.EbodacScheduler;
import org.motechproject.scheduler.service.MotechSchedulerService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JobImporter implements OsgiServiceLifecycleListener {

    private MotechSchedulerService motechSchedulerService;

    @Override
    public void bind(Object o, Map map) throws Exception {
        this.motechSchedulerService = (MotechSchedulerService) o;
        importDailyReportJob();
    }

    @Override
    public void unbind(Object o, Map map) throws Exception {
        this.motechSchedulerService = null;
    }

    private void importDailyReportJob() {
        DateTime startDate =  DateUtil.newDateTime(LocalDate.now().plusDays(1), Time.parseTime(EbodacConstants.DAILY_REPORT_EVENT_START_HOUR, ":"));

        EbodacScheduler ebodacScheduler = new EbodacScheduler(motechSchedulerService);
        ebodacScheduler.scheduleDailyReportJob(startDate);
    }
}
