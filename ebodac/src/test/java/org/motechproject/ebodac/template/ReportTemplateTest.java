package org.motechproject.ebodac.template;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ReportTemplateTest {

    @Test
    public void shouldCreatePdfReportATemplateAndSetAdditionalCellValues() throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfReportATemplate pdfReportATemplate = new PdfReportATemplate(outputStream);
        pdfReportATemplate.setAdditionalCellValues("title", "type", "district", "from", "to");
        pdfReportATemplate.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        PdfReader pdfReader = new PdfReader(inputStream);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, null);
        AcroFields acroFields = pdfStamper.getAcroFields();

        assertEquals("title", acroFields.getField("#titleReport"));
        assertEquals("type", acroFields.getField("#type"));
        assertEquals("district", acroFields.getField("#district"));
        assertEquals("from", acroFields.getField("#from"));
        assertEquals("to", acroFields.getField("#to"));

        pdfReader.close();
        inputStream.close();
        outputStream.close();
    }

    @Test
    public void shouldCreatePdfReportBTemplateAndSetAdditionalCellValues() throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfReportBTemplate pdfReportBTemplate = new PdfReportBTemplate(outputStream);
        pdfReportBTemplate.setAdditionalCellValues("title", "district", "chiefdom", "from", "to");
        pdfReportBTemplate.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        PdfReader pdfReader = new PdfReader(inputStream);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        AcroFields acroFields = pdfStamper.getAcroFields();

        assertEquals("title", acroFields.getField("#titleReport"));
        assertEquals("district", acroFields.getField("#district"));
        assertEquals("chiefdom", acroFields.getField("#chiefdom"));
        assertEquals("from", acroFields.getField("#from"));
        assertEquals("to", acroFields.getField("#to"));

        pdfReader.close();
        inputStream.close();
        outputStream.close();
    }

    @Test
    public void shouldCreateXlsReportATemplateAndSetAdditionalCellValues() throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        XlsReportATemplate xlsReportATemplate = new XlsReportATemplate(outputStream);
        xlsReportATemplate.setAdditionalCellValues("title", "type", "district", "from", "to");
        xlsReportATemplate.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        Workbook workbook = new HSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        CellReference cr = new CellReference("B10");
        Row row = sheet.getRow(cr.getRow());
        Cell cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("title", cell.getStringCellValue());

        cr = new CellReference("A14");
        row = sheet.getRow(cr.getRow());
        cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("type", cell.getStringCellValue());

        cr = new CellReference("B14");
        row = sheet.getRow(cr.getRow());
        cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("district", cell.getStringCellValue());

        cr = new CellReference("C14");
        row = sheet.getRow(cr.getRow());
        cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("from", cell.getStringCellValue());

        cr = new CellReference("D14");
        row = sheet.getRow(cr.getRow());
        cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("to", cell.getStringCellValue());

        inputStream.close();
        outputStream.close();
    }

    @Test
    public void shouldCreateXlsReportBTemplateAndSetAdditionalCellValues() throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        XlsReportBTemplate xlsReportBTemplate = new XlsReportBTemplate(outputStream);
        xlsReportBTemplate.setAdditionalCellValues("title", "district", "chiefdom", "from", "to");
        xlsReportBTemplate.close();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        Workbook workbook = new HSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        CellReference cr = new CellReference("B10");
        Row row = sheet.getRow(cr.getRow());
        Cell cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("title", cell.getStringCellValue());

        cr = new CellReference("A14");
        row = sheet.getRow(cr.getRow());
        cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("district", cell.getStringCellValue());

        cr = new CellReference("B14");
        row = sheet.getRow(cr.getRow());
        cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("chiefdom", cell.getStringCellValue());

        cr = new CellReference("C14");
        row = sheet.getRow(cr.getRow());
        cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("from", cell.getStringCellValue());

        cr = new CellReference("D14");
        row = sheet.getRow(cr.getRow());
        cell = row.getCell(cr.getCol());
        cell.getStringCellValue();
        assertEquals("to", cell.getStringCellValue());

        inputStream.close();
        outputStream.close();
    }
}
