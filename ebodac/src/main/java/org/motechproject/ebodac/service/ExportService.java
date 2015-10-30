package org.motechproject.ebodac.service;


import org.motechproject.ebodac.template.PdfBasicTemplate;
import org.motechproject.ebodac.template.XlsBasicTemplate;
import org.motechproject.mds.query.QueryParams;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public interface ExportService {

    void  exportEntityToPDF(PdfBasicTemplate template, Class<?> entityDtoType, Class<?> entityType, Map<String, String> headerMap,
                            String lookup, String lookupFields, QueryParams queryParams) throws IOException;

    void  exportEntityToCSV(Writer writer, Class<?> entityDtoType, Class<?> entityType, Map<String, String> headerMap,
                            String lookup, String lookupFields, QueryParams queryParams) throws IOException;

    void  exportEntityToExcel(XlsBasicTemplate template, Class<?> entityDtoType, Class<?> entityType, Map<String, String> headerMap,
                              String lookup, String lookupFields, QueryParams queryParams) throws IOException;
}
