package org.motechproject.ebodac.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 * Verify that EbodacSettingsService is present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class EbodacConfigServiceIT extends BasePaxIT {

    @Inject
    private ConfigService configService;

    @Test
    public void testEbodacSettingsServicePresent() throws Exception {
        assertNotNull(configService.getConfig());
    }
}
