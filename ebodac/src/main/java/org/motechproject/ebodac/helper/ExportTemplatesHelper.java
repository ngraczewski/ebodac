package org.motechproject.ebodac.helper;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.ReportVaccinationAbstract;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.exception.EbodacExportException;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.template.PdfBasicTemplate;
import org.motechproject.ebodac.template.PdfReportATemplate;
import org.motechproject.ebodac.template.PdfReportBTemplate;
import org.motechproject.ebodac.template.PdfReportCTemplate;
import org.motechproject.ebodac.template.XlsBasicTemplate;
import org.motechproject.ebodac.template.XlsReportATemplate;
import org.motechproject.ebodac.template.XlsReportBTemplate;
import org.motechproject.ebodac.template.XlsReportCTemplate;
import org.motechproject.ebodac.web.domain.GridSettings;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExportTemplatesHelper {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private ConfigService configService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String FROM = "min";
    private static final String TO = "max";

    public PdfBasicTemplate createTemplateForPdf(String reportName, Class<?> entityType, GridSettings settings,
                                                 String exportRecords, String oldLookupFields, OutputStream outputStream) {
        Config config = configService.getConfig();
        Map<String, String> map = findMinAndMaxDateForReport(reportName, entityType, settings, exportRecords, oldLookupFields);

        String from = map.get(FROM);
        String to = map.get(TO);

        PdfBasicTemplate template;
        if (entityType.getName().equals(ReportPrimerVaccination.class.getName()) || entityType.getName().equals(ReportBoosterVaccination.class.getName())) {
            template = new PdfReportATemplate(outputStream);
            ((PdfReportATemplate) template).setAdditionalCellValues(reportName.replaceAll("([A-Z])", " $1"), "Daily", config.getDistrict(),
                    StringUtils.isBlank(from) ? "" : from, StringUtils.isBlank(to) ? "" : to);
        } else if (reportName.equals("DailyClinicVisitScheduleReport") || reportName.equals("FollowupsMissedClinicVisitsReport") || reportName.equals("FollowupsAfterPrimeInjectionReport")) {
            template = new PdfReportBTemplate(outputStream);
            ((PdfReportBTemplate) template).setAdditionalCellValues(reportName.replaceAll("([A-Z])", " $1"), config.getDistrict(), config.getChiefdom(),
                    StringUtils.isBlank(from) ? "" : from, StringUtils.isBlank(to) ? "" : to);
        } else {
            template = new PdfReportCTemplate(outputStream);
        }
        return template;
    }

    public XlsBasicTemplate createTemplateForXls(String reportName, Class<?> entityType, GridSettings settings,
                                                 String exportRecords, String oldLookupFields, OutputStream outputStream) {
        Config config = configService.getConfig();
        Map<String, String> map = findMinAndMaxDateForReport(reportName, entityType, settings, exportRecords, oldLookupFields);

        String from = map.get(FROM);
        String to = map.get(TO);

        XlsBasicTemplate template;
        if (entityType.getName().equals(ReportPrimerVaccination.class.getName()) || entityType.getName().equals(ReportBoosterVaccination.class.getName())) {
            template = new XlsReportATemplate(outputStream);
            ((XlsReportATemplate) template).setAdditionalCellValues(reportName.replaceAll("([A-Z])", " $1"), "Daily", config.getDistrict(),
                    StringUtils.isBlank(from) ? "" : from, StringUtils.isBlank(to) ? "" : to);
        } else if (reportName.equals("DailyClinicVisitScheduleReport") || reportName.equals("FollowupsMissedClinicVisitsReport") || reportName.equals("FollowupsAfterPrimeInjectionReport")) {
            template = new XlsReportBTemplate(outputStream);
            ((XlsReportBTemplate) template).setAdditionalCellValues(reportName.replaceAll("([A-Z])", " $1"), config.getDistrict(), config.getChiefdom(),
                    StringUtils.isBlank(from) ? "" : from, StringUtils.isBlank(to) ? "" : to);
        } else {
            template = new XlsReportCTemplate(outputStream);
        }
        return template;
    }

    private Map<String, String> findMinAndMaxDateForReport(String reportName, Class<?> entityType, GridSettings settings, String exportRecords, String oldLookupFields) {
        switch (reportName) {
            case EbodacConstants.PRIMER_VACCINATION_REPORT_NAME:
            case EbodacConstants.BOOSTER_VACCINATION_REPORT_REPORT_NAME:
                return findMinAndMaxDate(entityType, settings, exportRecords, oldLookupFields, ReportVaccinationAbstract.DATE_PROPERTY_NAME);
            case EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_NAME:
                return findMinAndMaxDate(entityType, settings, exportRecords, oldLookupFields, Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);
            case EbodacConstants.FOLLOW_UPS_AFTER_PRIME_INJECTION_REPORT_NAME:
                return findMinAndMaxDate(entityType, settings, exportRecords, oldLookupFields, Visit.SUBJECT_PRIME_VACCINATION_DATE_PROPERTY_NAME);
            case EbodacConstants.FOLLOW_UPS_MISSED_CLINIC_VISITS_REPORT_NAME:
                return findMinAndMaxDate(entityType, settings, exportRecords, oldLookupFields, Visit.MOTECH_PROJECTED_DATE_PROPERTY_NAME);
        }

        return new HashMap<>();
    }

    private <T> Map<String, String> findMinAndMaxDate(Class<T> entityType, GridSettings settings, String exportRecords,
                                                      String oldLookupFields, String fieldName) {

        if (StringUtils.isNotBlank(oldLookupFields)) {
            Map<String, Object> fieldsMap = getFields(oldLookupFields);

            if (fieldsMap.containsKey(fieldName)) {
                Object o = fieldsMap.get(fieldName);

                if (o instanceof Map) {
                    return (Map<String, String>) o;
                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put(FROM, (String) o);
                    map.put(TO, (String) o);
                    return map;
                }
            }
        }

        Order order = new Order(fieldName, Order.Direction.ASC);
        QueryParams queryParams = new QueryParams(1, StringUtils.equalsIgnoreCase(exportRecords, "all") ? null : Integer.valueOf(exportRecords), order);

        Records<T> records = lookupService.getEntities(entityType, settings.getLookup(), settings.getFields(), queryParams);
        List<T> entities = records.getRows();

        Map<String, String> map = new HashMap<>();

        if (!entities.isEmpty()) {
            String to = getFieldValue(entities.get(entities.size() - 1), fieldName);

            if (StringUtils.isBlank(to)) {
                map.put(FROM, "");
                map.put(TO, "");
            } else {
                map.put(TO, to);

                for (T entity : entities) {
                    String from = getFieldValue(entity, fieldName);
                    if (StringUtils.isNotBlank(from)) {
                        map.put(FROM, from);
                        return map;
                    }
                }

                map.put(FROM, "");
            }
        }

        return map;
    }

    private <T> String getFieldValue(T entity, String fieldName) {
        Map<String, Object> entityMap;
        try {
            String json = objectMapper.writeValueAsString(entity);
            entityMap = objectMapper.readValue(json, new TypeReference<HashMap>() {});
        } catch (IOException e) {
            throw new EbodacExportException("Error creating headers for report", e);
        }

        String[] fieldPath = fieldName.split("\\.");
        String value;
        if (fieldPath.length == 2) {
            Map<String, Object> objectMap = (Map<String, Object>) entityMap.get(fieldPath[0]);
            value = (String) objectMap.get(fieldPath[1]);
        } else {
            value = (String) entityMap.get(fieldName);
        }

        return value;
    }

    private Map<String, Object> getFields(String lookupFields) {
        try {
            return objectMapper.readValue(lookupFields, new TypeReference<HashMap>() {});
        } catch (IOException e) {
            throw new EbodacExportException("Error creating headers for report", e);
        }
    }
}
