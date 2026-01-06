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
        if (AuthenticationService.isDataMissing(createParts.getManufacturerId())) {
            errors.put("MissingManufacturerId");
        }
        if (AuthenticationService.isDataMissing(createParts.getSku())) {
            errors.put("MissingSku");
        }

        if (AuthenticationService.isDataMissing(createParts.getName())) {
            errors.put("MissingName");
        }

        if (AuthenticationService.isDataMissing(createParts.getCategory())) {
            errors.put("MissingCategory");
        }

        /*
        if (AuthenticationService.isDataMissing(createParts.getPrice())) {  BIGDECIMAL
            errors.put("MissingPrice");
        }*/
        if (AuthenticationService.isDataMissing(createParts.getStock())) {
            errors.put("MissingStock");
        }

        if (AuthenticationService.isDataMissing(createParts.getStatus())) {
            errors.put("MissingStatus");
        }

        if (!AuthenticationService.isDataMissing(createParts.getManufacturerId()) && !partsAuth.isValidManufacturerId(createParts.getManufacturerId())) {
            errors.put("InvalidManufacturerId");
        }

        if (!AuthenticationService.isDataMissing(createParts.getSku()) && !partsAuth.isValidSku(createParts.getSku())) {
            errors.put("InvalidSku");
        }

        if (!AuthenticationService.isDataMissing(createParts.getName()) && !partsAuth.isValidName(createParts.getName())) {
            errors.put("InvalidName");
        }

        if (!AuthenticationService.isDataMissing(createParts.getCategory()) && !partsAuth.isValidCategory(createParts.getCategory())) {
            errors.put("InvalidCategory");
        }

        //BigDecimal
        /*if (!AuthenticationService.isDataMissing(createParts.getPrice()) && !partsAuth.isValidPrice(createParts.getPrice())) {
            errors.put("InvalidCategory");
        }*/
        //Integer
        /*if (!AuthenticationService.isDataMissing(createParts.getStock()) && !partsAuth.isValidStock(createParts.getStock())) {
            errors.put("InvalidCategory");
        }*/
        if (!AuthenticationService.isDataMissing(createParts.getStatus()) && !partsAuth.isValidStatus(createParts.getStatus())) {
            errors.put("InvalidStatus");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (Parts.createParts(createParts)) {  // ← Static metódus hívás!
            toReturn.put("message", "Part Created Successfully");
            return errorAuth.createOKResponse(toReturn);
        } else {
            errors.put("ModelError");
            return errorAuth.createErrorResponse(errors, 500);
        }
    } // createParts Closer

    public JSONObject getAllParts() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Parts> modelResult = Parts.getAllParts();

        // VALIDÁCIÓ - If no data in DB
        if (AuthenticationService.isDataMissing(modelResult)) {
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

        if (AuthenticationService.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Parts part = Parts.getPartsById(id);

        if (AuthenticationService.isDataMissing(part)) {
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

        if (AuthenticationService.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Parts part = Parts.getPartsById(id);

        if (AuthenticationService.isDataMissing(part)) {
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
        if (AuthenticationService.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!AuthenticationService.isDataMissing(id) && !partsAuth.isValidId(id)) {  // Csak ha NEM missing!
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

}
