package org.motechproject.bookingapp.web.domain;

import org.motechproject.bookingapp.domain.DateFilter;
import org.motechproject.ebodac.web.domain.GridSettings;

public class BookingGridSettings extends GridSettings {

    private DateFilter dateFilter;
    private String startDate;
    private String endDate;

    public DateFilter getDateFilter() {
        return dateFilter;
    }

    public void setDateFilter(DateFilter dateFilter) {
        this.dateFilter = dateFilter;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
