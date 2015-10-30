package org.motechproject.ebodac.osgi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;

import javax.inject.Inject;

import static junit.framework.Assert.assertTrue;
import static org.quartz.TriggerKey.triggerKey;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class JobImporterIT extends BasePaxIT {

    @Inject
    private BundleContext bundleContext;

    private Scheduler scheduler;

    private TriggerKey triggerKey = triggerKey("daily_report_event-null-period", "default");

    @Before
    public void setup() throws SchedulerException {
        scheduler = (Scheduler) getQuartzScheduler(bundleContext);
    }

    @Test
    public void shouldImportDailyReportJob() throws Exception {
        assertTrue(scheduler.checkExists(triggerKey));
    }
}
