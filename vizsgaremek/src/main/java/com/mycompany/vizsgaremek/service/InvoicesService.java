/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Invoices;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

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

        // KONVERZIÓ ArrayList --> JSONArray
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
}
