package org.motechproject.ebodac.template;

import com.itextpdf.text.Rectangle;

import java.io.OutputStream;

public class PdfReportATemplate extends PdfBasicTemplate {

    private static final String TEMPLATE_PATH = "/report_templateA.pdf";
    private static final Rectangle FIRST_PAGE_RECTANGLE = new Rectangle(20, 36, 580, 475);

    private static final String PDF_TEMPLATE_TITLE = "#titleReport";
    private static final String PDF_TEMPLATE_TYPE = "#type";
    private static final String PDF_TEMPLATE_DISTRICT = "#district";
    private static final String PDF_TEMPLATE_FROM = "#from";
    private static final String PDF_TEMPLATE_TO = "#to";

    public PdfReportATemplate(OutputStream outputStream) {
        super(TEMPLATE_PATH, FIRST_PAGE_RECTANGLE, outputStream);
    }

    public void setAdditionalCellValues(String title, String type, String district, String from, String to) {
        setAdditionalCellValue(PDF_TEMPLATE_TITLE, title);
        setAdditionalCellValue(PDF_TEMPLATE_TYPE, type);
        setAdditionalCellValue(PDF_TEMPLATE_DISTRICT, district);
        setAdditionalCellValue(PDF_TEMPLATE_FROM, from);
        setAdditionalCellValue(PDF_TEMPLATE_TO, to);
    }
}
