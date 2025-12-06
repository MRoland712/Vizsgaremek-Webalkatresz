/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Parts;
import com.mycompany.vizsgaremek.service.AuthenticationService.addressAuth;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblg
 */
public class PartsService {

    private final AuthenticationService.partsAuth partsAuth = new AuthenticationService.partsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createParts(Parts createParts) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (partsAuth.isDataMissing(createParts.getManufacturerId())) {
            errors.put("MissingManufacturerId");
        }
        if (partsAuth.isDataMissing(createParts.getSku())) {
            errors.put("MissingSku");
        }

        if (partsAuth.isDataMissing(createParts.getName())) {
            errors.put("MissingName");
        }

        if (partsAuth.isDataMissing(createParts.getCategory())) {
            errors.put("MissingCategory");
        }

        /*
        if (partsAuth.isDataMissing(createParts.getPrice())) {  BIGDECIMAL
            errors.put("MissingPrice");
        }*/
        if (partsAuth.isDataMissing(createParts.getStock())) {
            errors.put("MissingStock");
        }

        if (partsAuth.isDataMissing(createParts.getStatus())) {
            errors.put("MissingStatus");
        }

        if (!partsAuth.isDataMissing(createParts.getManufacturerId()) && !partsAuth.isValidManufacturerId(createParts.getManufacturerId())) {
            errors.put("InvalidManufacturerId");
        }

        if (!partsAuth.isDataMissing(createParts.getSku()) && !partsAuth.isValidSku(createParts.getSku())) {
            errors.put("InvalidSku");
        }

        if (!partsAuth.isDataMissing(createParts.getName()) && !partsAuth.isValidName(createParts.getName())) {
            errors.put("InvalidName");
        }

        if (!partsAuth.isDataMissing(createParts.getCategory()) && !partsAuth.isValidCategory(createParts.getCategory())) {
            errors.put("InvalidCategory");
        }
        
        //BigDecimal
        /*if (!partsAuth.isDataMissing(createParts.getPrice()) && !partsAuth.isValidPrice(createParts.getPrice())) {
            errors.put("InvalidCategory");
        }*/
        
        //Integer
        /*if (!partsAuth.isDataMissing(createParts.getStock()) && !partsAuth.isValidStock(createParts.getStock())) {
            errors.put("InvalidCategory");
        }*/
        
        if (!partsAuth.isDataMissing(createParts.getStatus()) && !partsAuth.isValidStatus(createParts.getStatus())) {
            errors.put("InvalidStatus");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (Parts.createParts(createParts)) {  // ← Static metódus hívás!
            toReturn.put("message", "Part Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Part Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // createParts Closer
    
    public JSONObject getAllParts() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Parts> modelResult = Parts.getAllParts();

        // VALIDÁCIÓ - If no data in DB
        if (partsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray partArray = new JSONArray();

        for (Parts part : modelResult) {
            JSONObject partObj = new JSONObject();
            partObj.put("id", part.getId());
            partObj.put("manufacturerId", part.getManufacturerId().getId());
            partObj.put("sku", part.getSku());
            partObj.put("name", part.getName());
            partObj.put("category", part.getCategory());
            partObj.put("price", part.getPrice());
            partObj.put("stock", part.getStock());
            partObj.put("status", part.getStatus());
            partObj.put("isActive", part.getIsActive());
            partObj.put("createdAt", part.getCreatedAt());
            partObj.put("updatedAt", part.getUpdatedAt());

            partArray.put(partObj);
        }

        toReturn.put("success", true);
        toReturn.put("parts", partArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllParts

}
