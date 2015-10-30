package org.motechproject.ebodac.web;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.ebodac.client.EbodacEmailClient;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.service.EbodacService;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RaveControllerTest {

    @InjectMocks
    private RaveController raveController = new RaveController();

    @Mock
    private EbodacService ebodacService;

    @Mock
    private EbodacEmailClient ebodacEmailClient;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldFetchCsvFormStartDate() throws IOException {
        raveController.fetchCsv("2015-10-10");

        verify(ebodacService).fetchCSVUpdates(DateTimeFormat.forPattern(EbodacConstants.FETCH_CSV_START_DATE_FORMAT).parseDateTime("2015-10-10"));
    }

    @Test
    public void shouldFetchCsvWithoutStartDate() throws IOException {
        raveController.fetchCsv("");

        verify(ebodacService).fetchCSVUpdates();
    }

    @Test
    public void shouldNotFetchCsvWhenStartDateMalformed() throws IOException {
        raveController.fetchCsv("asdg");
        raveController.fetchCsv("2015-20-10");

        verify(ebodacService, never()).fetchCSVUpdates();
        verify(ebodacService, never()).fetchCSVUpdates(any(DateTime.class));
    }
}
