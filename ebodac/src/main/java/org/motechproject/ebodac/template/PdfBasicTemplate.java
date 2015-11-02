package org.motechproject.ebodac.template;


import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfFormField;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.motechproject.ebodac.exception.EbodacExportException;

import java.io.IOException;
import java.io.OutputStream;

public abstract class PdfBasicTemplate {

    public static final Font TABLE_FONT = new Font(Font.FontFamily.HELVETICA, 8);
    public static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

    private final PdfReader pdfReader;

    private final PdfStamper pdfStamper;

    private final AcroFields acroFields;

    private final Rectangle nextPageRectangle;

    private final Rectangle firstPageRectangle;

    public PdfBasicTemplate(String templatePath, Rectangle firstPageRectangle, OutputStream outputStream) {
        nextPageRectangle = new Rectangle(20, 36, 580, 762);
        this.firstPageRectangle = firstPageRectangle;

        try {
            pdfReader = new PdfReader(getClass().getResourceAsStream(templatePath));
            pdfStamper = new PdfStamper(pdfReader, outputStream);
            acroFields = pdfStamper.getAcroFields();
        } catch (DocumentException | IOException e) {
            throw new EbodacExportException(e.getMessage(), e);
        }
    }

    public Rectangle getNextPageRectangle() {
        return nextPageRectangle;
    }

    public Rectangle getFirstPageRectangle() {
        return firstPageRectangle;
    }

    protected void setAdditionalCellValue(String cellName, String cellValue) {
        try {
            BaseFont helveticaBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
            acroFields.setField(cellName, cellValue);
            acroFields.setFieldProperty(cellName, "textfont", helveticaBold, null);
            acroFields.setFieldProperty(cellName, "setfflags", PdfFormField.FF_READ_ONLY, null);
        } catch (IOException | DocumentException e) {
            throw new EbodacExportException("No such additional cell: " + cellName, e);
        }
    }

    public PdfStamper getPdfStamper() {
        return this.pdfStamper;
    }

    public Rectangle getTemplatePageSize() {
        return pdfReader.getPageSize(1);
    }

    public void close() {
        try {
            pdfStamper.close();
            pdfReader.close();
        } catch (DocumentException | IOException e) {
            throw new EbodacExportException(e.getMessage(), e);
        }
    }
}
