/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Orders;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class OrdersService {

    private final AuthenticationService.ordersAuth ordersAuth = new AuthenticationService.ordersAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createOrders(Orders createOrders) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (ordersAuth.isDataMissing(createOrders.getUserId())) {
            errors.put("MissingUserId");
        }
        if (ordersAuth.isDataMissing(createOrders.getStatus())) {
            errors.put("MissingStatus");
        }

        if (!ordersAuth.isDataMissing(createOrders.getUserId()) && !ordersAuth.isValidUserId(createOrders.getUserId())) {
            errors.put("InvalidUserId");
        }

        if (!ordersAuth.isDataMissing(createOrders.getStatus()) && !ordersAuth.isValidStatus(createOrders.getStatus())) {
            errors.put("InvalidStatus");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Orders existingOrders = Orders.getOrdersById(createOrders.getId());
        if (existingOrders != null) {
            errors.put("OrderAlreadyExist");
            return errorAuth.createErrorResponse(errors, 409);
        }

        // MODEL HÍVÁS
        if (Orders.createOrders(createOrders)) {
            toReturn.put("message", "Orders Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Orders Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }

    }

    public JSONObject getAllOrders() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Orders> modelResult = Orders.getAllOrders();

        // VALIDÁCIÓ If no data in DB
        if (ordersAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray orderArray = new JSONArray();

        for (Orders order : modelResult) {
            JSONObject orderObj = new JSONObject();
            orderObj.put("id", order.getId());
            orderObj.put("userId", order.getUserId().getId());
            orderObj.put("status", order.getStatus());
            orderObj.put("createdAt", order.getCreatedAt());
            orderObj.put("updatedAt", order.getUpdatedAt());

            orderArray.put(orderObj);
        }

        toReturn.put("success", true);
        toReturn.put("orders", orderArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllOrders

    public JSONObject getOrdersById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (ordersAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Orders order = Orders.getOrdersById(id);

        if (ordersAuth.isDataMissing(order)) {
            errors.put("OrderNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject orderObj = new JSONObject();
        orderObj.put("id", order.getId());
        orderObj.put("userId", order.getUserId().getId());
        orderObj.put("status", order.getStatus());
        orderObj.put("createdAt", order.getCreatedAt());
        orderObj.put("updatedAt", order.getUpdatedAt());

        toReturn.put("success", true);
        toReturn.put("orders", orderObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getOrdersById

    public JSONObject getOrdersByUserId(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (ordersAuth.isDataMissing(userId)) {
            errors.put("MissingUserId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<Orders> ordersList = Orders.getOrdersByUserId(userId); 

        if (ordersAuth.isDataMissing(ordersList)) { 
            errors.put("OrdersNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray ordersArray = new JSONArray();
        for (Orders order : ordersList) {
            JSONObject orderObj = new JSONObject();
            orderObj.put("id", order.getId());
            orderObj.put("userId", order.getUserId().getId());
            orderObj.put("status", order.getStatus());
            orderObj.put("createdAt", order.getCreatedAt());
            orderObj.put("updatedAt", order.getUpdatedAt());
            orderObj.put("isDeleted", order.getIsDeleted());
            orderObj.put("deletedAt", order.getDeletedAt());
            ordersArray.put(orderObj);
        }

        toReturn.put("success", true);
        toReturn.put("orders", ordersArray);  
        toReturn.put("count", ordersList.size()); 
        toReturn.put("statusCode", 200);
        return toReturn;
    }

    public JSONObject softDeleteOrders(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (ordersAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!ordersAuth.isDataMissing(id) && !ordersAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Orders modelResult = Orders.getOrdersById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("OrdersNotFound");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("OrdersIsSoftDeleted");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Orders.softDeleteOrders(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Orders Succesfully");
        return toReturn;
    }

    public JSONObject updateOrders(Orders updatedOrders) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK KERESÉSI PARAMÉTEREK 
        if (ordersAuth.isDataMissing(updatedOrders.getId())) {
            errors.put("MissingSearchParameter");
        }

        if (!ordersAuth.isDataMissing(updatedOrders.getId())
                && !ordersAuth.isValidId(updatedOrders.getId())) {
            errors.put("InvalidId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // CÍM LEKÉRDEZÉSE 
        Orders existingOrders = null;

        if (!ordersAuth.isDataMissing(updatedOrders.getId())) {
            existingOrders = Orders.getOrdersById(updatedOrders.getId());
        }

        if (ordersAuth.isDataMissing(existingOrders)) {
            errors.put("OrdersNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (!ordersAuth.isDataMissing(updatedOrders.getStatus())) {
            if (ordersAuth.isValidStatus(updatedOrders.getStatus())) {
                existingOrders.setStatus(updatedOrders.getStatus());
            } else {
                errors.put("InvalidStatus");
            }
        }

        // isDeleted CSAK ha meg van adva!
        if (!ordersAuth.isDataMissing(updatedOrders.getIsDeleted())) {
            if (ordersAuth.isOrdersDeleted(updatedOrders.getIsDeleted())) {
                existingOrders.setIsDeleted(updatedOrders.getIsDeleted());
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
            Boolean result = Orders.updateOrders(existingOrders);

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
        toReturn.put("message", "Orders updated successfully");
        toReturn.put("statusCode", 200);

        return toReturn;
    }//updateParts

}
