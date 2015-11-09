package org.motechproject.bookingapp.service.impl;

import org.motechproject.bookingapp.domain.Site;
import org.motechproject.bookingapp.repository.SiteDataService;
import org.motechproject.bookingapp.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {

    @Autowired
    private SiteDataService siteDataService;


    @Override
    public List<Site> getSites() {
        return siteDataService.retrieveAll();
    }
}
