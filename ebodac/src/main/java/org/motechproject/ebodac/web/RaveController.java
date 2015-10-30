package org.motechproject.ebodac.web;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ebodac.client.EbodacEmailClient;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.service.EbodacService;
import org.motechproject.mds.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class RaveController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RaveController.class);

    @Autowired
    private EbodacService ebodacService;

    @Autowired
    private EbodacEmailClient ebodacEmailClient;

    @RequestMapping(value = "/web-api/fetch-csv", method = RequestMethod.POST)
    @PreAuthorize(Constants.Roles.HAS_DATA_ACCESS)
    @ResponseBody
    public ResponseEntity<String> fetchCsv(@RequestBody String startDate) throws IOException {
        if (StringUtils.isNotBlank(startDate)) {
            try {
                ebodacService.fetchCSVUpdates(DateTimeFormat.forPattern(EbodacConstants.FETCH_CSV_START_DATE_FORMAT).parseDateTime(startDate));
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid date format", e);
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        } else {
            ebodacService.fetchCSVUpdates();
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
