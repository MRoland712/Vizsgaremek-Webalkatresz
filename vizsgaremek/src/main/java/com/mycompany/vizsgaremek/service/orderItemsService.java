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
 * @author ddori
 */
public class orderItemsService {
    
    private final AuthenticationService.orderItemsAuth orderItemsAuth = new AuthenticationService.orderItemsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();
    
    public JSONObject getAllOrderItems() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<OrderItems> modelResult = OrderItems.getAllOrderItems();

        // VALIDÁCIÓ - If no data in DB
        if (orderItemsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray orderArray = new JSONArray();

        for (OrderItems order : modelResult) {
            JSONObject orderObj = new JSONObject();
            orderObj.put("id", order.getId());
            orderObj.put("orderId", order.getOrderId().getId());
            orderObj.put("partId", order.getPartId().getId());
            orderObj.put("quantity", order.getQuantity());
            orderObj.put("price", order.getPrice());
            orderObj.put("createdAt", order.getCreatedAt());
            orderObj.put("isDeleted", order.getIsDeleted());
            orderObj.put("deletedAt", order.getDeletedAt());
            
            orderArray.put(orderObj);
        }

        toReturn.put("success", true);
        toReturn.put("result", orderArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllOrders
}
