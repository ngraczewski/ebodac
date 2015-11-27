package org.motechproject.bookingapp.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static org.motechproject.bookingapp.constants.BookingAppConstants.ADVANCED_SETTINGS_TAB_PERMISSION;
import static org.motechproject.bookingapp.constants.BookingAppConstants.CLINIC_VISIT_SCHEDULE_TAB_PERMISSION;
import static org.motechproject.bookingapp.constants.BookingAppConstants.PRIME_VAC_TAB_PERMISSION;
import static org.motechproject.bookingapp.constants.BookingAppConstants.SCREENING_TAB_PERMISSION;

@Controller
public class TabAccessController {

    @RequestMapping(value = "/available/bookingTabs", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getAvailableTabs() {
        List<String> availableTabs = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(SCREENING_TAB_PERMISSION))) {
            availableTabs.add("screening");
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(PRIME_VAC_TAB_PERMISSION))) {
            availableTabs.add("primeVaccination");
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(CLINIC_VISIT_SCHEDULE_TAB_PERMISSION))) {
            availableTabs.add("clinicVisitSchedule");
        }

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority(ADVANCED_SETTINGS_TAB_PERMISSION))) {
            availableTabs.add("visitLimitation");
        }

        return availableTabs;
    }

}
