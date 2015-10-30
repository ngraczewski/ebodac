package org.motechproject.ebodac.web;

import org.apache.commons.io.IOUtils;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EbodacService;
import org.motechproject.mds.util.Constants;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * Controller for EbodacController message and bundle status.
 */
@Controller
public class EbodacController {

    private static final String OK = "OK";

    @Autowired
    private EbodacService ebodacService;

    @Autowired
    @Qualifier("ebodacSettings")
    private SettingsFacade settingsFacade;

    @Autowired
    private ConfigService configService;

    @RequestMapping("/web-api/status")
    @ResponseBody
    public String status() {
        return OK;
    }

    @RequestMapping(value = "/runJob", method = RequestMethod.POST)
    @PreAuthorize(Constants.Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public ResponseEntity<String> runZetesJob() {
        Config config = configService.getConfig();

        try {
            ebodacService.sendUpdatedSubjects(config.getZetesUrl(), config.getZetesUsername(), config.getZetesPassword());
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/mds-databrowser-config", method = RequestMethod.GET)
    @ResponseBody
    public String getCustomUISettings() throws IOException {
        return IOUtils.toString(settingsFacade.getRawConfig(EbodacConstants.UI_CONFIG));
    }
}
