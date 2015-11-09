package org.motechproject.bookingapp.web;

import org.motechproject.bookingapp.domain.Site;
import org.motechproject.bookingapp.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller("/sites")
public class SiteController {

    @Autowired
    private SiteService siteService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    private List<Site> getSites() {
        return siteService.getSites();
    }

}
