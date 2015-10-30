package org.motechproject.ebodac.template;

import java.io.OutputStream;

public class XlsReportCTemplate extends XlsBasicTemplate {

    private static final String XLS_TEMPLATE_PATH = "/report_templateC.xls";
    private static final int INDEX_OF_HEADER_ROW = 1;

    public XlsReportCTemplate(OutputStream outputStream) {
        super(XLS_TEMPLATE_PATH, INDEX_OF_HEADER_ROW, outputStream);
    }
}
