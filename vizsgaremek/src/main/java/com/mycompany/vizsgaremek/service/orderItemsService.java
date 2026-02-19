/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.OrderItems;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class orderItemsService {
    
    private final AuthenticationService.orderItemsAuth orderItemsAuth = new AuthenticationService.orderItemsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createOrderItemsService(OrderItems createOrderItems) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (orderItemsAuth.isDataMissing(createOrderItems.getOrderId())) {
            errors.put("MissingOrderId");
        }
        
        if (orderItemsAuth.isDataMissing(createOrderItems.getPartId())) {
            errors.put("MissingPartId");
        }
        
        if (orderItemsAuth.isDataMissing(createOrderItems.getQuantity())) {
            errors.put("MissingQuantity");
        }
        
        if (orderItemsAuth.isDataMissing(createOrderItems.getPrice())) {
            errors.put("MissingPrice");
        }

        if (!orderItemsAuth.isDataMissing(createOrderItems.getOrderId()) && !orderItemsAuth.isValidOrderId(createOrderItems.getOrderId())) {
            errors.put("InvalidOrderId");
        }
        
        if (!orderItemsAuth.isDataMissing(createOrderItems.getPartId()) && !orderItemsAuth.isValidPartId(createOrderItems.getPartId())) {
            errors.put("InvalidPartId");
        }

        if (!orderItemsAuth.isDataMissing(createOrderItems.getQuantity()) && !orderItemsAuth.isValidQuantity(createOrderItems.getQuantity())) {
            errors.put("InvalidQuantity");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        OrderItems existingOrderItems = OrderItems.getOrderItemById(createOrderItems.getId());
        if (existingOrderItems != null) {
            errors.put("OrderItemIsAlreadyExist");
            return errorAuth.createErrorResponse(errors, 409);
        }

        // MODEL HÍVÁS
        if (OrderItems.createOrderItems(createOrderItems)) {
            toReturn.put("message", "OrderItem Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "OrderItem Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }

    }
    
    public JSONObject getAllOrderItemsAdminService() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<OrderItems> modelResult = OrderItems.getAllOrderItemsAdmin();

        // VALIDÁCIÓ If no data in DB
        if (orderItemsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList 
        JSONArray orderItemsArray = new JSONArray();

        for (OrderItems orderItem : modelResult) {
            JSONObject orderItemObj = new JSONObject();
            orderItemObj.put("id", orderItem.getId());
            orderItemObj.put("orderId", orderItem.getOrderId().getId());
            orderItemObj.put("partId", orderItem.getPartId().getId());
            orderItemObj.put("quantity", orderItem.getQuantity());
            orderItemObj.put("price", orderItem.getPrice());
            orderItemObj.put("createdAt", orderItem.getCreatedAt());
            orderItemObj.put("isDeleted", orderItem.getIsDeleted());
            orderItemObj.put("deletedAt", orderItem.getDeletedAt());

            orderItemsArray.put(orderItemObj);
        }

        toReturn.put("success", true);
        toReturn.put("OrderItems", orderItemsArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllOrderItemAdmin

    public JSONObject getAllOrderItemsService() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<OrderItems> modelResult = OrderItems.getAllOrderItems();

        // VALIDÁCIÓ If no data in DB
        if (orderItemsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList 
        JSONArray orderItemsArray = new JSONArray();

        for (OrderItems orderItem : modelResult) {
            JSONObject orderItemObj = new JSONObject();
            orderItemObj.put("id", orderItem.getId());
            orderItemObj.put("orderId", orderItem.getOrderId().getId());
            orderItemObj.put("partId", orderItem.getPartId().getId());
            orderItemObj.put("quantity", orderItem.getQuantity());
            orderItemObj.put("price", orderItem.getPrice());
            orderItemObj.put("createdAt", orderItem.getCreatedAt());
            orderItemObj.put("isDeleted", orderItem.getIsDeleted());
            orderItemObj.put("deletedAt", orderItem.getDeletedAt());

            orderItemsArray.put(orderItemObj);
        }

        toReturn.put("success", true);
        toReturn.put("OrderItems", orderItemsArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllOrderItem

    public JSONObject getOrderItemByIdService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (orderItemsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        OrderItems orderItem = OrderItems.getOrderItemById(id);

        if (orderItemsAuth.isDataMissing(orderItem)) {
            errors.put("OrderItemNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject orderItemObj = new JSONObject();
        orderItemObj.put("id", orderItem.getId());
        orderItemObj.put("orderId", orderItem.getOrderId().getId());
        orderItemObj.put("partId", orderItem.getPartId().getId());
        orderItemObj.put("quantity", orderItem.getQuantity());
        orderItemObj.put("price", orderItem.getPrice());
        orderItemObj.put("createdAt", orderItem.getCreatedAt());
        orderItemObj.put("isDeleted", orderItem.getIsDeleted());
        orderItemObj.put("deletedAt", orderItem.getDeletedAt());

        toReturn.put("success", true);
        toReturn.put("OrderItem", orderItemObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getOrderItemById

    public JSONObject getOrderItemsByOrderIdService(Integer orderId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (orderItemsAuth.isDataMissing(orderId)) {
            errors.put("MissingOrderId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<OrderItems> orderItemsList = OrderItems.getOrderItemsByOrderId(orderId);

        if (orderItemsAuth.isDataMissing(orderItemsList)) {
            errors.put("OrderItemNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray orderItemsArray = new JSONArray();
        for (OrderItems orderItem : orderItemsList) {
            JSONObject orderItemObj = new JSONObject();
            orderItemObj.put("id", orderItem.getId());
            orderItemObj.put("orderId", orderItem.getOrderId().getId());
            orderItemObj.put("partId", orderItem.getPartId().getId());
            orderItemObj.put("quantity", orderItem.getQuantity());
            orderItemObj.put("price", orderItem.getPrice());
            orderItemObj.put("createdAt", orderItem.getCreatedAt());
            orderItemObj.put("isDeleted", orderItem.getIsDeleted());
            orderItemObj.put("deletedAt", orderItem.getDeletedAt());
            orderItemsArray.put(orderItemObj);
        }

        toReturn.put("success", true);
        toReturn.put("orderItem", orderItemsArray);
        toReturn.put("count", orderItemsList.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getOrderItemsByOrderId
    
    public JSONObject getOrderItemsByPartIdService(Integer partId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (orderItemsAuth.isDataMissing(partId)) {
            errors.put("MissingPartId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<OrderItems> orderItemsList = OrderItems.getOrderItemsByPartId(partId);

        if (orderItemsAuth.isDataMissing(orderItemsList)) {
            errors.put("OrderItemNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray orderItemsArray = new JSONArray();
        for (OrderItems orderItem : orderItemsList) {
            JSONObject orderItemObj = new JSONObject();
            orderItemObj.put("id", orderItem.getId());
            orderItemObj.put("orderId", orderItem.getOrderId().getId());
            orderItemObj.put("partId", orderItem.getPartId().getId());
            orderItemObj.put("quantity", orderItem.getQuantity());
            orderItemObj.put("price", orderItem.getPrice());
            orderItemObj.put("createdAt", orderItem.getCreatedAt());
            orderItemObj.put("isDeleted", orderItem.getIsDeleted());
            orderItemObj.put("deletedAt", orderItem.getDeletedAt());
            orderItemsArray.put(orderItemObj);
        }

        toReturn.put("success", true);
        toReturn.put("orderItem", orderItemsArray);
        toReturn.put("count", orderItemsList.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getOrderItemsByOrderId

    public JSONObject softDeleteOrderItemService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (orderItemsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!orderItemsAuth.isDataMissing(id) && !orderItemsAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        OrderItems modelResult = OrderItems.getOrderItemById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("OrderItemNotFound");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("OrderItemIsSoftDeleted");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = OrderItems.softDeleteOrderItem(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted OrderItem Succesfully");
        return toReturn;
    }

    public JSONObject updateOrderItemService(OrderItems updatedOrderItem) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK KERESÉSI PARAMÉTEREK 
        if (orderItemsAuth.isDataMissing(updatedOrderItem.getId())) {
            errors.put("MissingSearchParameter");
        }

        if (!orderItemsAuth.isDataMissing(updatedOrderItem.getId())
                && !orderItemsAuth.isValidId(updatedOrderItem.getId())) {
            errors.put("InvalidId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // CÍM LEKÉRDEZÉSE 
        OrderItems existingOrderItems = null;

        if (!orderItemsAuth.isDataMissing(updatedOrderItem.getId())) {
            existingOrderItems = OrderItems.getOrderItemById(updatedOrderItem.getId());
        }

        if (orderItemsAuth.isDataMissing(existingOrderItems)) {
            errors.put("OrderItemNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (!orderItemsAuth.isDataMissing(updatedOrderItem.getQuantity())) {
            if (orderItemsAuth.isValidQuantity(updatedOrderItem.getQuantity())) {
                existingOrderItems.setQuantity(updatedOrderItem.getQuantity());
            } else {
                errors.put("InvalidQuantity");
            }
        }
        
        if (!orderItemsAuth.isDataMissing(updatedOrderItem.getPrice())) {
            if (orderItemsAuth.isValidPrice(updatedOrderItem.getPrice())) {
                existingOrderItems.setPrice(updatedOrderItem.getPrice());
            } else {
                errors.put("InvalidPrice");
            }
        }

        // isDeleted CSAK ha meg van adva!
        if (!orderItemsAuth.isDataMissing(updatedOrderItem.getIsDeleted())) {
            if (orderItemsAuth.isOrderItemsDeleted(updatedOrderItem.getIsDeleted())) {
                existingOrderItems.setIsDeleted(updatedOrderItem.getIsDeleted());
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
            Boolean result = OrderItems.updateOrderItem(existingOrderItems);

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
        toReturn.put("message", "OrderItem updated successfully");
        toReturn.put("statusCode", 200);

        return toReturn;
    }//updateOrderItem
    
}
