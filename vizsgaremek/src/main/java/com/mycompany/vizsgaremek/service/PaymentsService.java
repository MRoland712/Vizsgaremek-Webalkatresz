/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Payments;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class PaymentsService {

    private final AuthenticationService.paymentsAuth paymentsAuth = new AuthenticationService.paymentsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createPaymentsService(Payments createPayments) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (paymentsAuth.isDataMissing(createPayments.getOrderId())) {
            errors.put("MissingOrderId");
        }

        if (paymentsAuth.isDataMissing(createPayments.getAmount())) {
            errors.put("MissingAmount");
        }

        if (paymentsAuth.isDataMissing(createPayments.getMethod())) {
            errors.put("MissingMethod");
        }

        if (paymentsAuth.isDataMissing(createPayments.getStatus())) {
            errors.put("MissingStatus");
        }

        if (!paymentsAuth.isDataMissing(createPayments.getOrderId()) && !paymentsAuth.isValidOrderId(createPayments.getOrderId())) {
            errors.put("InvalidOrderId");
        }

        if (!paymentsAuth.isDataMissing(createPayments.getAmount()) && !paymentsAuth.isValidAmount(createPayments.getAmount())) {
            errors.put("InvalidAmount");
        }

        if (!paymentsAuth.isDataMissing(createPayments.getMethod()) && !paymentsAuth.isValidMethod(createPayments.getMethod())) {
            errors.put("InvalidMethod");
        }

        if (!paymentsAuth.isDataMissing(createPayments.getStatus()) && !paymentsAuth.isValidStatus(createPayments.getStatus())) {
            errors.put("InvalidStatus");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (Payments.createPayments(createPayments)) {
            toReturn.put("message", "Payment Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Payment Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }

    }
// createPayments Closer

    public JSONObject getAllPaymentsService() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Payments> modelResult = Payments.getAllPayments();

        // VALIDÁCIÓ 
        if (paymentsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        JSONArray paymentsArray = new JSONArray();

        for (Payments payments : modelResult) {
            JSONObject paymentObj = new JSONObject();
            paymentObj.put("id", payments.getId());
            paymentObj.put("orderId", payments.getOrderId().getId());
            paymentObj.put("amount", payments.getAmount());
            paymentObj.put("method", payments.getMethod());
            paymentObj.put("status", payments.getStatus());
            paymentObj.put("isDeleted", payments.getIsDeleted());

            paymentsArray.put(paymentObj);
        }

        toReturn.put("success", true);
        toReturn.put("Payments", paymentsArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllPayments

    public JSONObject getPaymentByIdService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (paymentsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Payments payments = Payments.getPaymentById(id);

        if (paymentsAuth.isDataMissing(payments)) {
            errors.put("PaymentNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject paymentObj = new JSONObject();
        paymentObj.put("id", payments.getId());
        paymentObj.put("orderId", payments.getOrderId().getId());
        paymentObj.put("amount", payments.getAmount());
        paymentObj.put("method", payments.getMethod());
        paymentObj.put("status", payments.getStatus());
        paymentObj.put("isDeleted", payments.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("Payments", paymentObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPaymentById

    public JSONObject getPaymentsByOrderIdService(Integer orderId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (paymentsAuth.isDataMissing(orderId)) {
            errors.put("MissingOrderId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Payments payments = Payments.getPaymentsByOrderId(orderId);

        if (paymentsAuth.isDataMissing(payments)) {
            errors.put("PaymentsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject paymentObj = new JSONObject();
        paymentObj.put("id", payments.getId());
        paymentObj.put("orderId", payments.getOrderId().getId());
        paymentObj.put("amount", payments.getAmount());
        paymentObj.put("method", payments.getMethod());
        paymentObj.put("status", payments.getStatus());
        paymentObj.put("isDeleted", payments.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("Payments", paymentObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPaymentsByOrderId

    public JSONObject softDeletePaymentService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (paymentsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!paymentsAuth.isDataMissing(id) && !paymentsAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Payments modelResult = Payments.getPaymentById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("PaymentNotFound");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("PaymentIsSoftDeleted");
        }

        //if parts is soft deleted
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Payments.softDeletePayment(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Payments Succesfully");
        return toReturn;
    }//softDeletePayment

    public JSONObject updatePaymentService(Payments updatedPayment) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK KERESÉSI PARAMÉTEREK 
        // Ha nincs SEMMILYEN keresési paraméter id 
        if (paymentsAuth.isDataMissing(updatedPayment.getId())) {
            errors.put("MissingSearchParameter");
        }

        // Ha id mint keresési paraméter NEM hiányzik ÉS ÉRVÉNYTELEN
        if (!paymentsAuth.isDataMissing(updatedPayment.getId())
                && !paymentsAuth.isValidId(updatedPayment.getId())) {
            errors.put("InvalidId");
        }

        // Hiba ellenőrzés keresési paraméterek
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // CÍM LEKÉRDEZÉSE 
        Payments existingPayment = null;

        // ID alapján keresés
        if (!paymentsAuth.isDataMissing(updatedPayment.getId())) {
            existingPayment = Payments.getPaymentById(updatedPayment.getId());
        }

        if (paymentsAuth.isDataMissing(existingPayment)) {
            errors.put("PaymentNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        //  MEZŐK MÓDOSÍTÁSA (csak a megadottak!)
        if (!paymentsAuth.isDataMissing(updatedPayment.getAmount())) {
            if (paymentsAuth.isValidAmount(updatedPayment.getAmount())) {
                existingPayment.setAmount(updatedPayment.getAmount());
            } else {
                errors.put("InvalidAmount");
            }
        }
        
        if (!paymentsAuth.isDataMissing(updatedPayment.getMethod())) {
            if (paymentsAuth.isValidMethod(updatedPayment.getMethod())) {
                existingPayment.setMethod(updatedPayment.getMethod());
            } else {
                errors.put("InvalidMethod");
            }
        }
        
        if (!paymentsAuth.isDataMissing(updatedPayment.getStatus())) {
            if (paymentsAuth.isValidStatus(updatedPayment.getStatus())) {
                existingPayment.setStatus(updatedPayment.getStatus());
            } else {
                errors.put("InvalidStatus");
            }
        }

        if (!paymentsAuth.isDataMissing(updatedPayment.getIsDeleted())) {
            if (paymentsAuth.isPaymentsDeleted(updatedPayment.getIsDeleted())) {
                existingPayment.setIsDeleted(updatedPayment.getIsDeleted());
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
            Boolean result = Payments.updatePayment(existingPayment);

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
        toReturn.put("message", "Payments updated successfully");
        toReturn.put("statusCode", 200);

        return toReturn;
    }//updatePayments

}
