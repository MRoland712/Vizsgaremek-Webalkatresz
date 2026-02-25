/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Invoices;
import com.mycompany.vizsgaremek.model.OrderItems;
import com.mycompany.vizsgaremek.model.Orders;
import com.mycompany.vizsgaremek.model.Payments;
import com.mycompany.vizsgaremek.model.Users;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;

/**
 *
 * @author neblgergo
 */
public class InvoicesService {

    private final AuthenticationService.invoicesAuth invoicesAuth = new AuthenticationService.invoicesAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createInvoiceService(Invoices createInvoice) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (invoicesAuth.isDataMissing(createInvoice.getOrderId())) {
            errors.put("MissingOrderId");
        }
        if (invoicesAuth.isDataMissing(createInvoice.getPdfUrl())) {
            errors.put("MissingPdfUrl");
        }

        if (!invoicesAuth.isDataMissing(createInvoice.getOrderId()) && !invoicesAuth.isValidOrderId(createInvoice.getOrderId())) {
            errors.put("InvalidOrderId");
        }

        if (!invoicesAuth.isDataMissing(createInvoice.getPdfUrl()) && !invoicesAuth.isValidPdfUrl(createInvoice.getPdfUrl())) {
            errors.put("InvalidPdfUrl");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Invoices existingOrders = Invoices.getInvoiceById(createInvoice.getId());
        if (existingOrders != null) {
            errors.put("InvoicesAlreadyExist");
            return errorAuth.createErrorResponse(errors, 409);
        }

        // MODEL HÍVÁS
        if (Invoices.createInvoice(createInvoice)) {
            toReturn.put("message", "Invoices Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Invoices Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }

    }

    public JSONObject getAllInvoicesService() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Invoices> modelResult = Invoices.getAllInvoices();

        // VALIDÁCIÓ If no data in DB
        if (invoicesAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList
        JSONArray invociesArray = new JSONArray();

        for (Invoices invoice : modelResult) {
            JSONObject invoicesObj = new JSONObject();
            invoicesObj.put("id", invoice.getId());
            invoicesObj.put("orderId", invoice.getOrderId().getId());
            invoicesObj.put("pdfUrl", invoice.getPdfUrl());
            invoicesObj.put("createdAt", invoice.getCreatedAt());
            invoicesObj.put("isDeleted", invoice.getIsDeleted());
            invoicesObj.put("deletedAt", invoice.getDeletedAt());

            invociesArray.put(invoicesObj);
        }

        toReturn.put("success", true);
        toReturn.put("invoices", invociesArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllOrders

    public JSONObject getInvoiceByIdService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (invoicesAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Invoices invoice = Invoices.getInvoiceById(id);

        if (invoicesAuth.isDataMissing(invoice)) {
            errors.put("InvoiceNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject invoicesObj = new JSONObject();
        invoicesObj.put("id", invoice.getId());
        invoicesObj.put("orderId", invoice.getOrderId().getId());
        invoicesObj.put("pdfUrl", invoice.getPdfUrl());
        invoicesObj.put("createdAt", invoice.getCreatedAt());
        invoicesObj.put("isDeleted", invoice.getIsDeleted());
        invoicesObj.put("deletedAt", invoice.getDeletedAt());

        toReturn.put("success", true);
        toReturn.put("orders", invoicesObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getOrdersById

    public JSONObject getInvoicesByOrderIdService(Integer orderId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (invoicesAuth.isDataMissing(orderId)) {
            errors.put("MissingOrderId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<Invoices> invoicesList = Invoices.getInvoicesByOrderId(orderId);

        if (invoicesAuth.isDataMissing(invoicesList)) {
            errors.put("InvoiceNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray invoicesArray = new JSONArray();
        for (Invoices invoice : invoicesList) {
            JSONObject invoiceObj = new JSONObject();
            invoiceObj.put("id", invoice.getId());
            invoiceObj.put("orderId", invoice.getOrderId().getId());
            invoiceObj.put("pdfUrl", invoice.getPdfUrl());
            invoiceObj.put("createdAt", invoice.getCreatedAt());
            invoiceObj.put("isDeleted", invoice.getIsDeleted());
            invoiceObj.put("deletedAt", invoice.getDeletedAt());
            invoicesArray.put(invoiceObj);
        }

        toReturn.put("success", true);
        toReturn.put("invoices", invoicesArray);
        toReturn.put("count", invoicesList.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getInvoicesByOrderId

    public JSONObject softDeleteInvoiceService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (invoicesAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!invoicesAuth.isDataMissing(id) && !invoicesAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Invoices modelResult = Invoices.getInvoiceById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("InvoicesNotFound");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("InvoicesIsSoftDeleted");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Invoices.softDeleteInvoice(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Invoices Succesfully");
        return toReturn;
    }

    public JSONObject updateInvoiceService(Invoices updatedInvoice) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK KERESÉSI PARAMÉTEREK 
        if (invoicesAuth.isDataMissing(updatedInvoice.getId())) {
            errors.put("MissingSearchParameter");
        }

        if (!invoicesAuth.isDataMissing(updatedInvoice.getId())
                && !invoicesAuth.isValidId(updatedInvoice.getId())) {
            errors.put("InvalidId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // CÍM LEKÉRDEZÉSE 
        Invoices existingInvoices = null;

        if (!invoicesAuth.isDataMissing(updatedInvoice.getId())) {
            existingInvoices = Invoices.getInvoiceById(updatedInvoice.getId());
        }

        if (invoicesAuth.isDataMissing(existingInvoices)) {
            errors.put("InvoicesNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (!invoicesAuth.isDataMissing(updatedInvoice.getPdfUrl())) {
            if (invoicesAuth.isValidPdfUrl(updatedInvoice.getPdfUrl())) {
                existingInvoices.setPdfUrl(updatedInvoice.getPdfUrl());
            } else {
                errors.put("InvalidPdfUrl");
            }
        }

        // isDeleted CSAK ha meg van adva!
        if (!invoicesAuth.isDataMissing(updatedInvoice.getIsDeleted())) {
            if (invoicesAuth.isInvoicesDeleted(updatedInvoice.getIsDeleted())) {
                existingInvoices.setIsDeleted(updatedInvoice.getIsDeleted());
            } else {
                errors.put("InvalidIsDeleted");
            }
        }

        // Hiba ellenőrzés validációk
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // ADATBÁZIS UPDATE 
        try {
            Boolean result = Invoices.updateInvoice(existingInvoices);

            if (!result) {
                errors.put("ServerError");
            }

        } catch (Exception ex) {
            errors.put("DatabaseError");
            ex.printStackTrace();
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // SIKERES VÁLASZ 
        toReturn.put("success", true);
        toReturn.put("message", "Invoices updated successfully");
        toReturn.put("statusCode", 200);

        return toReturn;
    }//updateParts

    /**
     * Generate HTML invoice
     */
    public static String generateInvoiceHtml(
            Integer orderId,
            Orders order,
            Payments payment,
            Users user,
            ArrayList<OrderItems> orderItems) throws Exception {

        // Dátum formázás
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String invoiceDate = dateFormat.format(payment.getPaidAt() != null ? payment.getPaidAt() : new Date());

        // Számla szám
        String invoiceNumber = "INV-" + orderId + "-" + invoiceDate.replace("-", "");

        // Összeg formázás
        String formattedTotal = String.format("%,.2f Ft", payment.getAmount());

        // HTML tartalom
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"hu\">");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\" />");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />");
        html.append("<title>Számla - ").append(invoiceNumber).append("</title>");
        html.append("<style>");
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }");
        html.append("body { font-family: Arial, sans-serif; padding: 40px; background: #f5f5f5; }");
        html.append(".invoice { max-width: 800px; margin: 0 auto; background: white; padding: 40px; }");
        html.append(".header { border-bottom: 3px solid #4CAF50; padding-bottom: 20px; margin-bottom: 30px; }");
        html.append(".header h1 { color: #333; font-size: 32px; }");
        html.append(".header .invoice-number { color: #666; font-size: 14px; margin-top: 5px; }");
        html.append(".company-info, .customer-info { margin-bottom: 30px; }");
        html.append(".company-info h3, .customer-info h3 { color: #4CAF50; margin-bottom: 10px; font-size: 14px; text-transform: uppercase; }");
        html.append(".info-line { color: #333; margin: 5px 0; font-size: 14px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 30px 0; }");
        html.append("th { background: #4CAF50; color: white; padding: 12px; text-align: left; font-weight: 600; }");
        html.append("td { padding: 12px; border-bottom: 1px solid #ddd; }");
        html.append(".text-right { text-align: right; }");
        html.append(".total-row { font-weight: bold; font-size: 18px; background: #f5f5f5; }");
        html.append(".footer { margin-top: 40px; padding-top: 20px; border-top: 2px solid #eee; text-align: center; color: #666; font-size: 12px; }");
        html.append(".payment-info { background: #f0f9ff; padding: 15px; border-left: 4px solid #4CAF50; margin: 20px 0; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class=\"invoice\">");

        // Header
        html.append("<div class=\"header\">");
        html.append("<h1>SZÁMLA</h1>");
        html.append("<div class=\"invoice-number\">Számlaszám: ").append(invoiceNumber).append("</div>");
        html.append("<div class=\"invoice-number\">Kiállítás dátuma: ").append(invoiceDate).append("</div>");
        html.append("</div>");

        // Company info
        html.append("<div class=\"company-info\">");
        html.append("<h3>Eladó (Szolgáltató)</h3>");
        html.append("<div class=\"info-line\"><strong>CarComps Kft.</strong></div>");
        html.append("<div class=\"info-line\">7621 Pécs, Fő utca 12.</div>");
        html.append("<div class=\"info-line\">Adószám: 12345678-2-02</div>");
        html.append("<div class=\"info-line\">Email: info@carcomps.hu</div>");
        html.append("<div class=\"info-line\">Telefon: +36 30 123 4567</div>");
        html.append("</div>");

        // Customer info
        html.append("<div class=\"customer-info\">");
        html.append("<h3>Vevő</h3>");
        html.append("<div class=\"info-line\"><strong>").append(user.getFirstName()).append(" ").append(user.getLastName()).append("</strong></div>");
        html.append("<div class=\"info-line\">Email: ").append(user.getEmail()).append("</div>");
        if (user.getPhone() != null) {
            html.append("<div class=\"info-line\">Telefon: ").append(user.getPhone()).append("</div>");
        }
        html.append("</div>");

        // Payment info
        html.append("<div class=\"payment-info\">");
        html.append("<strong>Fizetési információ:</strong><br />");
        html.append("Fizetési mód: ").append(getPaymentMethod(payment.getMethod())).append("<br />");
        html.append("Fizetés dátuma: ").append(invoiceDate).append("<br />");
        html.append("Státusz: Fizetve");
        html.append("</div>");

        // Items table
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th>Megnevezés</th>");
        html.append("<th class=\"text-right\">Mennyiség</th>");
        html.append("<th class=\"text-right\">Egységár</th>");
        html.append("<th class=\"text-right\">Összesen</th>");
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");

        // Order items
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItems item : orderItems) {
            BigDecimal itemTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            html.append("<tr>");
            html.append("<td>").append(item.getPartId().getName()).append("</td>");
            html.append("<td class=\"text-right\">").append(item.getQuantity()).append(" db</td>");
            html.append("<td class=\"text-right\">").append(String.format("%,.2f Ft", item.getPrice())).append("</td>");
            html.append("<td class=\"text-right\">").append(String.format("%,.2f Ft", itemTotal)).append("</td>");
            html.append("</tr>");
        }

        // Total
        html.append("<tr class=\"total-row\">");
        html.append("<td colspan=\"3\" class=\"text-right\">Végösszeg:</td>");
        html.append("<td class=\"text-right\">").append(formattedTotal).append("</td>");
        html.append("</tr>");
        html.append("</tbody>");
        html.append("</table>");

        // Footer
        html.append("<div class=\"footer\">");
        html.append("<p>Köszönjük a vásárlást!</p>");
        html.append("<p>© 2025 CarComps Kft. - Minden jog fenntartva.</p>");
        html.append("<p>Ez egy elektronikus számla, aláírás és pecsét nélkül is érvényes.</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    private static String getPaymentMethod(String method) {
        switch (method) {
            case "credit_card":
                return "Bankkártya";
            case "debit_card":
                return "Betéti kártya";
            case "paypal":
                return "PayPal";
            case "cash_on_delivery":
                return "Utánvét";
            case "bank_transfer":
                return "Banki átutalás";
            default:
                return method;
        }
    }

    /**
     * Save invoice as PDF file works on Mac, Windows, Linux
     */
    public static String saveInvoicePdf(String html, Integer orderId) throws Exception {
        String fileName = "invoice_" + orderId + ".pdf";

        // Automatikus környezet detektálás
        String os = System.getProperty("os.name").toLowerCase();
        String baseDir;

        if (os.contains("mac") || os.contains("darwin")) {
            // Mac Development
            baseDir = System.getProperty("user.home") + "/Desktop/carcomps_invoices/";
        } else if (os.contains("win")) {
            // Windows Production
            baseDir = "C:/carcomps/invoices/";
        } else {
            // Linux  
            baseDir = "/var/www/carcomps/invoices/";
        }

        String filePath = baseDir + fileName;

        // Create directory
        Files.createDirectories(Paths.get(baseDir));

        // HTML to PDF conversion
        FileOutputStream fos = new FileOutputStream(filePath);  
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(fos);  
        fos.close();  

        System.out.println("PDF saved: " + filePath);

        return "https://carcomps.hu/invoices/" + fileName;
    }

    /**
     * Generate PDF as byte array for email attachment
     */
    public static byte[] generateInvoicePdfBytes(String html) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(os);
        os.close();

        System.out.println("PDF byte array generated: " + os.size() + " bytes");

        return os.toByteArray();
    }
}
