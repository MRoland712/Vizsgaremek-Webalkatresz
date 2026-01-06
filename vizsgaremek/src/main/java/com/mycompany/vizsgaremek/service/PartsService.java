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

        
        if (partsAuth.isDataMissing(createParts.getPrice())) {  
            errors.put("MissingPrice");
        }
        
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
        if (!partsAuth.isDataMissing(createParts.getPrice()) && !partsAuth.isValidPrice(createParts.getPrice())) {
            errors.put("InvalidPrice");
        }
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

    public JSONObject getPartsById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (partsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Parts part = Parts.getPartsById(id);

        if (partsAuth.isDataMissing(part)) {
            errors.put("PartsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

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

        toReturn.put("success", true);
        toReturn.put("parts", partObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPartsById
    
    public JSONObject getPartsByManufacturerId(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (partsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Parts part = Parts.getPartsById(id);

        if (partsAuth.isDataMissing(part)) {
            errors.put("PartsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

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

        toReturn.put("success", true);
        toReturn.put("parts", partObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPartsByManufacturerId
    
    public JSONObject softDeleteParts(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (partsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!partsAuth.isDataMissing(id) && !partsAuth.isValidId(id)) {  // Csak ha NEM missing!
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Parts modelResult = Parts.getPartsById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("PartsNotFound");
        }

        //if parts not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("PartsIsSoftDeleted");
        }

        //if parts is soft deleted
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Parts.softDeleteParts(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Parts Succesfully");
        return toReturn;
    }
    
    public JSONObject getPartsByCategory() {
    JSONObject toReturn = new JSONObject();
    JSONArray errors = new JSONArray();
    
    // MODEL HÍVÁS (ArrayList<String>!)
    ArrayList<String> modelResult = Parts.getPartsByCategory();
    
    // VALIDÁCIÓ
    if (modelResult == null || modelResult.isEmpty()) {
        errors.put("NoCategoriesFound");
        return errorAuth.createErrorResponse(errors, 404);
    }
    
    // ArrayList<String> → JSONArray konverzió
    JSONArray categoryArray = new JSONArray();
    for (String category : modelResult) {
        categoryArray.put(category);
    }
    
    toReturn.put("success", true);
    toReturn.put("categories", categoryArray);
    toReturn.put("count", modelResult.size());
    toReturn.put("statusCode", 200);
    
    return toReturn;
}

}
