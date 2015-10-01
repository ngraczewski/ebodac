package org.motechproject.ebodac.web;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.commons.date.model.Time;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.scheduler.EbodacScheduler;
import org.motechproject.ebodac.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.File;
import java.io.IOException;
import java.util.Date;

    /**
     *  Sends & receives configs to/from the UI.
     */
@Controller
public class ConfigController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigController.class);

    private ConfigService configService;
    private EbodacScheduler ebodacScheduler;

    @Autowired
    public ConfigController(@Qualifier("configService") ConfigService configService, EbodacScheduler ebodacScheduler) {
        this.configService = configService;
        this.ebodacScheduler = ebodacScheduler;
    }

    @RequestMapping(value = "/ebodac-config", method = RequestMethod.GET)
    @ResponseBody
    public Config getConfig() {
        return configService.getConfig();
    }

    @RequestMapping(value = "/ebodac-config", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Config updateConfig(@RequestBody Config config) {
        String directory = config.getFtpsDirectory();
        if (directory != null && !directory.endsWith(File.separator)) {
            directory += File.separator;
        }
        config.setFtpsDirectory(directory);

        configService.updateConfig(config);

        ebodacScheduler.unscheduleZetesUpdateJob();
        ebodacScheduler.unscheduleEmailCheckJob();
        ebodacScheduler.unscheduleDailyReportJob();
        scheduleJobs();

        return configService.getConfig();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        LOGGER.error("Error while updating configs", e);
        return e.getMessage();
    }

    private void scheduleJobs() {

        if(configService.getConfig().getEnableZetesJob()) {
            String zetesUrl = configService.getConfig().getZetesUrl();
            String zetesUsername = configService.getConfig().getZetesUsername();
            String zetesPassword = configService.getConfig().getZetesPassword();

            LocalTime startTime = LocalTime.parse(
                    configService.getConfig().getStartTime(),
                    DateTimeFormat.forPattern(Config.TIME_PICKER_FORMAT)
            );
            Date startDate = startTime.toDateTimeToday().toDate();
            ebodacScheduler.scheduleZetesUpdateJob(startDate, zetesUrl, zetesUsername, zetesPassword);
        }

        if(configService.getConfig().getEnableRaveJob()) {
            Integer interval = configService.getConfig().getEmailCheckInterval();
            ebodacScheduler.scheduleEmailCheckJob(interval);
        }

        if(configService.getConfig().getEnableReportJob()) {
            DateTime reportStartDate = DateUtil.newDateTime(LocalDate.now(), Time.parseTime(configService.getConfig().getReportCalculationStartTime(), ":"));
            if (reportStartDate.isBeforeNow()) {
                reportStartDate = reportStartDate.plusDays(1);
            }
            ebodacScheduler.scheduleDailyReportJob(reportStartDate);
        }
    }
}
