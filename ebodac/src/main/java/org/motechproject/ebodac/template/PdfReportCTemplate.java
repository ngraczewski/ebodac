package org.motechproject.ebodac.template;

import com.itextpdf.text.Rectangle;

import java.io.OutputStream;

public class PdfReportCTemplate extends PdfBasicTemplate {

    private static final String TEMPLATE_PATH = "/report_templateC.pdf";
    private static final Rectangle FIRST_PAGE_RECTANGLE = new Rectangle(20, 36, 580, 762);

    public PdfReportCTemplate(OutputStream outputStream) {
        super(TEMPLATE_PATH, FIRST_PAGE_RECTANGLE, outputStream);
    }

}
