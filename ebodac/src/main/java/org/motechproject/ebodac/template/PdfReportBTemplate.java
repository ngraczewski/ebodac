package org.motechproject.ebodac.template;

import com.itextpdf.text.Rectangle;

import java.io.OutputStream;

public class PdfReportBTemplate extends PdfBasicTemplate {

    private static final String TEMPLATE_PATH = "/report_templateB.pdf";
    private static final Rectangle FIRST_PAGE_RECTANGLE = new Rectangle(20, 36, 580, 475);

    private static final String PDF_TEMPLATE_TITLE = "#titleReport";
    private static final String PDF_TEMPLATE_DISTRICT = "#district";
    private static final String PDF_TEMPLATE_CHIEFDOM = "#chiefdom";
    private static final String PDF_TEMPLATE_FROM = "#from";
    private static final String PDF_TEMPLATE_TO = "#to";

    public PdfReportBTemplate(OutputStream outputStream) {
        super(TEMPLATE_PATH, FIRST_PAGE_RECTANGLE, outputStream);
    }

    public void setAdditionalCellValues(String title, String  distinct, String chiefdom, String from, String to) {
        setAdditionalCellValue(PDF_TEMPLATE_TITLE, title);
        setAdditionalCellValue(PDF_TEMPLATE_DISTRICT, distinct);
        setAdditionalCellValue(PDF_TEMPLATE_CHIEFDOM, chiefdom);
        setAdditionalCellValue(PDF_TEMPLATE_FROM, from);
        setAdditionalCellValue(PDF_TEMPLATE_TO, to);
    }
}
