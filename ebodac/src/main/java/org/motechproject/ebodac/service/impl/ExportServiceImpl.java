package org.motechproject.ebodac.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.ebodac.service.ExportService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.util.ExcelTableWriter;
import org.motechproject.ebodac.template.PdfBasicTemplate;
import org.motechproject.ebodac.util.PdfTableWriter;
import org.motechproject.ebodac.template.XlsBasicTemplate;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.impl.csv.writer.CsvTableWriter;
import org.motechproject.mds.service.impl.csv.writer.TableWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("exportService")
public class ExportServiceImpl implements ExportService {

    @Autowired
    private LookupService lookupService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void exportEntityToPDF(PdfBasicTemplate template, Class<?> entityDtoType, Class<?> entityType, Map<String, String> headerMap,
                                  String lookup, String lookupFields, QueryParams queryParams) throws IOException {
        PdfTableWriter tableWriter = new PdfTableWriter(template);
        exportEntity(entityDtoType, entityType, headerMap, tableWriter, lookup, lookupFields, queryParams);
    }

    @Override
    public void exportEntityToCSV(Writer writer, Class<?> entityDtoType, Class<?> entityType, Map<String, String> headerMap,
                                  String lookup, String lookupFields, QueryParams queryParams)throws IOException {
        CsvTableWriter tableWriter = new CsvTableWriter(writer);
        exportEntity(entityDtoType, entityType, headerMap, tableWriter, lookup, lookupFields, queryParams);
    }

    @Override
    public void exportEntityToExcel(XlsBasicTemplate template, Class<?> entityDtoType, Class<?> entityType, Map<String, String> headerMap,
                                    String lookup, String lookupFields, QueryParams queryParams) throws IOException {
        ExcelTableWriter tableWriter = new ExcelTableWriter(template);
        exportEntity(entityDtoType, entityType, headerMap, tableWriter, lookup, lookupFields, queryParams);
    }

    private <T> void exportEntity(Class<?> entityDtoType, Class<T> entityType, Map<String, String> headerMap, TableWriter tableWriter, String lookup,
                                                      String lookupFields, QueryParams queryParams) throws IOException {
        Records<T> records;
        if(entityDtoType != null) {
            records = (Records<T>) lookupService.getEntities(entityDtoType, entityType, lookup, lookupFields, queryParams);
        } else {
            records = lookupService.getEntities(entityType, lookup, lookupFields, queryParams);
        }
        List<T> entities = records.getRows();
        Set<String> keys = headerMap.keySet();
        String[] fields = keys.toArray(new String[keys.size()]);
        try {
            tableWriter.writeHeader(fields);
            for (T entity : entities) {
                Map<String, String> row = buildRow(entity, headerMap);
                tableWriter.writeRow(row, fields);
            }
        } catch (IOException e) {
            throw new IOException("IO Error when writing data", e);
        } finally {
            tableWriter.close();
        }
    }

    private <T> Map<String, String> buildRow(T entity, Map<String, String> headerMap) throws IOException {
        String json = objectMapper.writeValueAsString(entity);
        Map<String, Object> entityMap = objectMapper.readValue(json, new TypeReference<HashMap>() { });
        Map<String, String> row = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            String fieldName = entry.getValue();
            if (fieldName == null) {
                row.put(entry.getKey(), null);
                continue;
            }
            String[] fieldPath = fieldName.split("\\.");
            String value = null;
            if (fieldPath.length == 2) {
                Map<String, Object> objectMap = (Map<String, Object>) entityMap.get(fieldPath[0]);
                Object fieldValue = objectMap.get(fieldPath[1]);
                if(fieldValue != null) {
                    value = fieldValue.toString();
                }
            } else {
                Object entryValue = entityMap.get(entry.getValue());
                if(entryValue != null) {
                    value = entryValue.toString();
                }
            }
            row.put(entry.getKey(), value);
        }
        return  row;
    }
}
