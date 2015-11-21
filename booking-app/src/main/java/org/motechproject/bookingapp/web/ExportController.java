package org.motechproject.bookingapp.web;


import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.bookingapp.constants.BookingAppConstants;
import org.motechproject.bookingapp.domain.DateFilter;
import org.motechproject.bookingapp.domain.PrimeVaccinationScheduleDto;
import org.motechproject.bookingapp.domain.Screening;
import org.motechproject.bookingapp.domain.VisitBookingDetails;
import org.motechproject.bookingapp.template.PdfExportTemplate;
import org.motechproject.bookingapp.template.XlsExportTemplate;
import org.motechproject.bookingapp.web.domain.BookingGridSettings;
import org.motechproject.commons.api.Range;
import org.motechproject.ebodac.exception.EbodacExportException;
import org.motechproject.ebodac.exception.EbodacLookupException;
import org.motechproject.ebodac.service.ExportService;
import org.motechproject.ebodac.template.PdfBasicTemplate;
import org.motechproject.ebodac.template.XlsBasicTemplate;
import org.motechproject.ebodac.util.QueryParamsBuilder;
import org.motechproject.mds.query.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang.CharEncoding.UTF_8;

@Controller
public class ExportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportController.class);

    @Autowired
    private ExportService exportService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/exportInstances/screening", method = RequestMethod.GET)
    public void exportScreening(BookingGridSettings settings, @RequestParam String exportRecords,
                                @RequestParam String outputFormat, HttpServletResponse response) throws IOException {

        if (settings.getDateFilter() != null) {
            DateFilter dateFilter = settings.getDateFilter();
            Map<String, Object> fieldsMap = new HashMap<>();
            Map<String, String> rangeMap = new HashMap<>();

            if (DateFilter.DATE_RANGE.equals(dateFilter)) {
                rangeMap.put("min", settings.getStartDate());
                rangeMap.put("max", settings.getEndDate());
            } else {
                Range<LocalDate> dateRange = dateFilter.getRange();
                rangeMap.put("min", dateRange.getMin().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
                rangeMap.put("max", dateRange.getMax().toString(BookingAppConstants.SIMPLE_DATE_FORMAT));
            }

            fieldsMap.put("date", rangeMap);

            settings.setFields(objectMapper.writeValueAsString(fieldsMap));
            settings.setLookup("Find By Date");
        }

        exportEntity(settings, exportRecords, outputFormat, response, BookingAppConstants.SCREENING_NAME,
                null, Screening.class, BookingAppConstants.SCREENING_FIELDS_MAP);
    }

    @RequestMapping(value = "/exportInstances/primeVaccinationSchedule", method = RequestMethod.GET)
    public void exportPrimeVaccinationSchedule(BookingGridSettings settings, @RequestParam String exportRecords,
                                               @RequestParam String outputFormat, HttpServletResponse response) throws IOException {

        Map<String, String> fieldsMap = new HashMap<>();
        fieldsMap.put("visit.type", "PRIME_VACCINATION_DAY");
        settings.setFields(objectMapper.writeValueAsString(fieldsMap));
        settings.setLookup("Find By Visit Type And Participant Prime Vaccination Date");

        exportEntity(settings, exportRecords, outputFormat, response, BookingAppConstants.PRIME_VACCINATION_SCHEDULE_NAME,
                PrimeVaccinationScheduleDto.class, VisitBookingDetails.class, BookingAppConstants.PRIME_VACCINATION_SCHEDULE_FIELDS_MAP);
    }

    private void exportEntity(BookingGridSettings settings, String exportRecords, String outputFormat, HttpServletResponse response,
                              String fileNameBeginning, Class<?> entityDtoType, Class<?> entityType, Map<String, String> headerMap) throws IOException {

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
        final String fileName = fileNameBeginning + "_" + DateTime.now().toString(dateTimeFormatter);

        if (BookingAppConstants.PDF_EXPORT_FORMAT.equals(outputFormat)) {
            response.setContentType("application/pdf");
        } else if(BookingAppConstants.CSV_EXPORT_FORMAT.equals(outputFormat)) {
            response.setContentType("text/csv");
        } else if(BookingAppConstants.XLS_EXPORT_FORMAT.equals(outputFormat)) {
            response.setContentType("application/vnd.ms-excel");
        } else {
            throw new IllegalArgumentException("Invalid export format: " + outputFormat);
        }
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=" + fileName + "." + outputFormat.toLowerCase());

        QueryParams queryParams = new QueryParams(1, StringUtils.equalsIgnoreCase(exportRecords, "all") ? null : Integer.valueOf(exportRecords),
                QueryParamsBuilder.buildOrderList(settings, getFields(settings)));

        try {
            if (BookingAppConstants.PDF_EXPORT_FORMAT.equals(outputFormat)) {
                PdfBasicTemplate template = new PdfExportTemplate(response.getOutputStream());

                exportService.exportEntityToPDF(template, entityDtoType, entityType, headerMap,
                        settings.getLookup(), settings.getFields(), queryParams);
            } else if(BookingAppConstants.CSV_EXPORT_FORMAT.equals(outputFormat)) {
                exportService.exportEntityToCSV(response.getWriter(), entityDtoType, entityType, headerMap,
                        settings.getLookup(), settings.getFields(), queryParams);
            } else if(BookingAppConstants.XLS_EXPORT_FORMAT.equals(outputFormat)) {
                XlsBasicTemplate template = new XlsExportTemplate(response.getOutputStream());

                exportService.exportEntityToExcel(template, entityDtoType, entityType, headerMap,
                        settings.getLookup(), settings.getFields(), queryParams);
            }
        } catch (IOException | EbodacLookupException | EbodacExportException e) {
            LOGGER.debug(e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Map<String, Object> getFields(BookingGridSettings gridSettings) throws IOException {
        if (gridSettings.getFields() == null) {
            return null;
        } else {
            return objectMapper.readValue(gridSettings.getFields(), new TypeReference<LinkedHashMap>() {});
        }
    }

}
