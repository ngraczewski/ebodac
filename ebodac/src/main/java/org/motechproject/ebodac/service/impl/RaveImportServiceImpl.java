package org.motechproject.ebodac.service.impl;


import org.apache.commons.lang.StringUtils;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.domain.Visit;
import org.motechproject.ebodac.domain.VisitType;
import org.motechproject.ebodac.service.RaveImportService;
import org.motechproject.ebodac.service.SubjectService;
import org.motechproject.ebodac.service.VisitService;
import org.motechproject.ebodac.service.impl.csv.RaveSubjectField;
import org.motechproject.ebodac.service.impl.csv.RaveVisitField;
import org.motechproject.mds.ex.csv.CsvImportException;
import org.motechproject.mds.util.PropertyUtil;
import org.motechproject.mds.util.TypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Map;

@Service("raveImportService")
public class RaveImportServiceImpl implements RaveImportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RaveImportServiceImpl.class);

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private VisitService visitService;

    @Override
    public void importCsv(Reader reader, String filename) {
        try (CsvMapReader csvMapReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE)) {
            Map<String, String> row;
            final String headers[] = csvMapReader.getHeader(true);

            while ((row = csvMapReader.read(headers)) != null) {
                String subjectId = null;
                if (row.containsKey(RaveSubjectField.Subject.name())) {
                    subjectId = row.get(RaveSubjectField.Subject.name());
                    if (csvMapReader.getRowNumber() == 2 && subjectId.matches("\\-+")) {
                        continue;
                    }
                }
                try {
                    importRow(row, csvMapReader.getRowNumber(), subjectId);
                } catch (CsvImportException e) {
                    LOGGER.error(filename + ": Skipping participant with id " + subjectId + ": " + e.getMessage(),  e);
                }
            }
        } catch (IOException e) {
            throw new CsvImportException("IO Error when importing CSV", e);
        }
    }

    private void importRow(Map<String, String> row, Integer rowNumber, String subjectId) {
        Subject subject = new Subject();
        if (subjectId != null) {
            Subject existingSubject = subjectService.findSubjectBySubjectId(subjectId);
            if (existingSubject != null) {
                subject = existingSubject;
            }
        }
        for (RaveSubjectField subjectField : RaveSubjectField.values()) {
            String header = subjectField.name();
            String fieldName = subjectField.getValue();
            String csvValue = getValue(row, header);
            if (subjectField.equals(RaveSubjectField.SEX)) {
                Gender gender = Gender.getByValue(csvValue);
                subject.setGender(gender);
            } else {
                setProperty(subject, fieldName, csvValue);
            }
        }
        Visit visit = new Visit();
        for (RaveVisitField visitField : RaveVisitField.values()) {
            String header = visitField.name();
            String fieldName = visitField.getValue();
            String csvValue = getValue(row, header);
            if (visitField.equals(RaveVisitField.VISIT)) {
                VisitType visitType = VisitType.getByValue(csvValue);
                if (visitType == null) {
                    throw new CsvImportException("Unknown visit type \"" + csvValue + "\"");
                }
                visit.setType(visitType);
            } else {
                setProperty(visit, fieldName, csvValue);
            }
        }
        Subject updatedSubject = subjectService.createOrUpdateForRave(subject);
        if (updatedSubject != null) {
            visit.setSubject(updatedSubject);
            visitService.createOrUpdate(visit);
        } else {
            throw new CsvImportException("Could not store participant imported from row " + row);
        }
    }

    private void setProperty(Object o, String fieldName, String csvValue) {
        try {
            Field f = o.getClass().getDeclaredField(fieldName);
            Object parsedValue = null;
            if (csvValue != null && !csvValue.equalsIgnoreCase("null")) {
                parsedValue = TypeHelper.parse(csvValue, f.getType());
            }
            PropertyUtil.setProperty(o, StringUtils.uncapitalize(f.getName()), parsedValue);
        } catch (Exception e) {
            String msg = String.format("Error when processing field: %s, value in CSV file is %s",
                    fieldName, csvValue);
            throw new CsvImportException(msg, e);
        }
    }

    private String getValue(Map<String, String> row, String header) {
        if (!row.containsKey(header)) {
            throw new CsvImportException("The row " + row + " does not contain required " + header + " property.");
        }
        return row.get(header);
    }

}
