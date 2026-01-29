package com.fintech.wallet.infrastructure.adapter;

import com.fintech.wallet.application.port.out.ReportExportPort;
import com.fintech.wallet.domain.valueobject.AccountStatement;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

/**
 * Adapter for exporting reports to PDF and CSV formats.
 */
@Component
public class ReportExportAdapter implements ReportExportPort {

    private static final Logger log = LoggerFactory.getLogger(ReportExportAdapter.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public byte[] exportToPdf(AccountStatement statement) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Title
            document.add(new Paragraph("Account Statement")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            // Account Info
            document.add(new Paragraph(String.format(
                    "Wallet: %s | Currency: %s",
                    statement.walletId(), statement.currency()))
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(String.format(
                    "Period: %s to %s",
                    statement.startDate(), statement.endDate()))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("")); // Spacer

            // Summary
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .setWidth(UnitValue.createPercentValue(60));
            
            summaryTable.addCell(createCell("Opening Balance:", true));
            summaryTable.addCell(createCell(formatMoney(statement.openingBalance()), false));
            summaryTable.addCell(createCell("Closing Balance:", true));
            summaryTable.addCell(createCell(formatMoney(statement.closingBalance()), false));
            summaryTable.addCell(createCell("Total Transactions:", true));
            summaryTable.addCell(createCell(String.valueOf(statement.totalTransactions()), false));

            document.add(summaryTable);
            document.add(new Paragraph("")); // Spacer

            // Transaction Table
            Table transactionTable = new Table(UnitValue.createPercentArray(
                    new float[]{15, 10, 30, 15, 15, 15}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Header
            transactionTable.addHeaderCell(createHeaderCell("Date"));
            transactionTable.addHeaderCell(createHeaderCell("Type"));
            transactionTable.addHeaderCell(createHeaderCell("Description"));
            transactionTable.addHeaderCell(createHeaderCell("Amount"));
            transactionTable.addHeaderCell(createHeaderCell("Balance"));
            transactionTable.addHeaderCell(createHeaderCell("Transaction ID"));

            // Rows
            for (AccountStatement.StatementEntry entry : statement.entries()) {
                transactionTable.addCell(createCell(
                        entry.date().atZone(java.time.ZoneId.systemDefault())
                                .format(DATE_FORMATTER), false));
                transactionTable.addCell(createCell(entry.type(), false));
                transactionTable.addCell(createCell(entry.description(), false));
                transactionTable.addCell(createCell(formatMoney(entry.amount()), false));
                transactionTable.addCell(createCell(formatMoney(entry.runningBalance()), false));
                transactionTable.addCell(createCell(
                        entry.transactionId().substring(0, 8) + "...", false));
            }

            document.add(transactionTable);

            // Footer
            document.add(new Paragraph("")); // Spacer
            document.add(new Paragraph("Generated: " + java.time.Instant.now())
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.RIGHT));

            document.close();
            log.info("PDF statement generated for wallet {}", statement.walletId());
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Failed to generate PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF statement", e);
        }
    }

    @Override
    public byte[] exportToCsv(AccountStatement statement) {
        try (StringWriter sw = new StringWriter()) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader("Date", "Type", "Description", "Amount", "Currency", 
                            "Running Balance", "Transaction ID")
                    .build();

            try (CSVPrinter printer = new CSVPrinter(sw, format)) {
                for (AccountStatement.StatementEntry entry : statement.entries()) {
                    printer.printRecord(
                            entry.date().atZone(java.time.ZoneId.systemDefault())
                                    .format(DATE_FORMATTER),
                            entry.type(),
                            entry.description(),
                            entry.amount().getAmount().toPlainString(),
                            entry.amount().getCurrency().getCode(),
                            entry.runningBalance().getAmount().toPlainString(),
                            entry.transactionId()
                    );
                }
            }

            log.info("CSV statement generated for wallet {}", statement.walletId());
            return sw.toString().getBytes(StandardCharsets.UTF_8);

        } catch (IOException e) {
            log.error("Failed to generate CSV: {}", e.getMessage());
            throw new RuntimeException("Failed to generate CSV statement", e);
        }
    }

    private String formatMoney(com.fintech.wallet.domain.valueobject.Money money) {
        return String.format("%s %s", 
                money.getAmount().toPlainString(), 
                money.getCurrency().getCode());
    }

    private Cell createHeaderCell(String content) {
        return new Cell()
                .add(new Paragraph(content))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
    }

    private Cell createCell(String content, boolean bold) {
        Cell cell = new Cell().add(new Paragraph(content));
        if (bold) {
            cell.setBold();
        }
        return cell;
    }
}
