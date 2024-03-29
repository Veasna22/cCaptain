package io.veasna.ccaptain.report;

import io.veasna.ccaptain.domain.Customer;
import io.veasna.ccaptain.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static java.util.stream.IntStream.range;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 27/3/24 10:52
 */
@Slf4j

public class CustomerReport {
    public CustomerReport(List<Customer> customers) {
        this.customers = customers;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Customers");
        setHeaders();
    }

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Customer> customers;
    private static String[] HEADERS = {"ID","Name","Email","Type","Status","Address","Phone","Created At"};

    private void setHeaders(){
        Row headerRow = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);
        range(0,HEADERS.length).forEach(index->{
            Cell cell = headerRow.createCell(index);
            cell.setCellValue(HEADERS[index]);
        });
    }

    public InputStreamResource export(){
        return generateReport();
    }

    private InputStreamResource generateReport(){
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontHeight(10);
            style.setFont(font);
            int rowIndex = 1;
            for (Customer customer : customers) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(customer.getId());
                row.createCell(1).setCellValue(customer.getName());
                row.createCell(2).setCellValue(customer.getEmail());
                row.createCell(3).setCellValue(customer.getType());
                row.createCell(4).setCellValue(customer.getStatus());
                row.createCell(5).setCellValue(customer.getAddress());
                row.createCell(6).setCellValue(customer.getPhone());
                row.createCell(7).setCellValue(DateFormatUtils.format(customer.getCreatedAt(), "yyyy-MM-dd HH:mm:ss"));
            }
            workbook.write(out);
            return new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
        }catch (Exception e){

            log.error("Error generating report",e);
             throw new ApiException("Unable to export report files");
        }
    }


}
