package org.motechproject.ebodac.template;

import java.io.OutputStream;

public class XlsReportBTemplate extends XlsBasicTemplate {

    private static final String XLS_TEMPLATE_PATH = "/report_templateB.xls";
    private static final int INDEX_OF_HEADER_ROW = 15;

    private static final String XLS_TEMPLATE_TITLE = "B10";
    private static final String XLS_TEMPLATE_DISTRICT = "A14";
    private static final String XLS_TEMPLATE_CHIEFDOM = "B14";
    private static final String XLS_TEMPLATE_FROM = "C14";
    private static final String XLS_TEMPLATE_TO = "D14";

    public XlsReportBTemplate(OutputStream outputStream) {
        super(XLS_TEMPLATE_PATH, INDEX_OF_HEADER_ROW, outputStream);
    }

    public void setAdditionalCellValues(String title, String  district, String chiefdom, String from, String to) {
        setAdditionalCellValue(XLS_TEMPLATE_TITLE, title);
        setAdditionalCellValue(XLS_TEMPLATE_DISTRICT, district);
        setAdditionalCellValue(XLS_TEMPLATE_CHIEFDOM, chiefdom);
        setAdditionalCellValue(XLS_TEMPLATE_FROM, from);
        setAdditionalCellValue(XLS_TEMPLATE_TO, to);
    }
}
