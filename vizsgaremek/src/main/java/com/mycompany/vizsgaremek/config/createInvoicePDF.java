package com.mycompany.vizsgaremek.config;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class createInvoicePDF {

    /**
     * Számla generálás
     */
    public static byte[] generateInvoice(
        String invoiceNumber,
        String customerName,
        String customerAddress,
        String[] items,
        double[] prices,
        double totalAmount
    ) throws IOException {
        
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Cég adatok (fejléc)
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("CarComps Kft.");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(50, 730);
        contentStream.showText("1011 Budapest, Fo utca 1.");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 715);
        contentStream.showText("Adoszam: 12345678-1-23");
        contentStream.endText();

        // Számla szám és dátum
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.newLineAtOffset(50, 680);
        contentStream.showText("SZAMLA");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(50, 660);
        contentStream.showText("Szamlaszam: " + invoiceNumber);
        contentStream.endText();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 645);
        contentStream.showText("Kelt: " + sdf.format(new Date()));
        contentStream.endText();

        // Vevő adatok
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(50, 610);
        contentStream.showText("Vevo:");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(50, 595);
        contentStream.showText(customerName);
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 580);
        contentStream.showText(customerAddress);
        contentStream.endText();

        // Tételek fejléc
        float yPosition = 540;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Megnevezes");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(400, yPosition);
        contentStream.showText("Ar (Ft)");
        contentStream.endText();

        // Vonal
        contentStream.moveTo(50, yPosition - 5);
        contentStream.lineTo(550, yPosition - 5);
        contentStream.stroke();

        // Tételek
        yPosition -= 20;
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        
        for (int i = 0; i < items.length; i++) {
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText(items[i]);
            contentStream.endText();

            contentStream.beginText();
            contentStream.newLineAtOffset(400, yPosition);
            contentStream.showText(String.format("%.0f Ft", prices[i]));
            contentStream.endText();

            yPosition -= 20;
        }

        // Végösszeg
        yPosition -= 20;
        contentStream.moveTo(50, yPosition);
        contentStream.lineTo(550, yPosition);
        contentStream.stroke();

        yPosition -= 20;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(50, yPosition);
        contentStream.showText("Vegosszeg:");
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(400, yPosition);
        contentStream.showText(String.format("%.0f Ft", totalAmount));
        contentStream.endText();

        contentStream.close();

        // PDF byte array-é alakítás
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }

    /**
     * PDF mentése fájlba
     */
    public static void savePdfToFile(byte[] pdfData, String filePath) throws IOException {
        java.nio.file.Files.write(
            java.nio.file.Paths.get(filePath), 
            pdfData
        );
    }
    /*
    public static void main(String[] args) {
        java.nio.file.Files.write(
            java.nio.file.Paths.get(filePath), 
            pdfData
        );
    }*/
}