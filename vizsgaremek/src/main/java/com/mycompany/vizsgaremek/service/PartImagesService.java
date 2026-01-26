/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.PartImages;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblg
 */
public class PartImagesService {
    private final AuthenticationService.partImagesAuth partImagesAuth = new AuthenticationService.partImagesAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createPartImages(PartImages createPartImages) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (partImagesAuth.isDataMissing(createPartImages.getPartId())) {
            errors.put("MissingPartId");
        }
        if (partImagesAuth.isDataMissing(createPartImages.getUrl())) {
            errors.put("MissingUrl");
        }

        if (!partImagesAuth.isDataMissing(createPartImages.getPartId()) && !partImagesAuth.isValidPartId(createPartImages.getPartId())) {
            errors.put("InvalidPartId");
        }

        if (!partImagesAuth.isDataMissing(createPartImages.getUrl()) && !partImagesAuth.isValidUrl(createPartImages.getUrl())) {
            errors.put("InvalidUrl");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (PartImages.createPartImages(createPartImages)) {  // ← Static metódus hívás!
            toReturn.put("message", "PartImages Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "PartImages Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // createParts Closer
    
    public JSONObject getAllPartImages() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<PartImages> modelResult = PartImages.getAllPartImages();

        // VALIDÁCIÓ 
        if (partImagesAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        JSONArray partArray = new JSONArray();

        for (PartImages partImage : modelResult) {
            JSONObject partImageObj = new JSONObject();
            partImageObj.put("id", partImage.getId());
            partImageObj.put("partId", partImage.getPartId().getId());
            partImageObj.put("url", partImage.getUrl());
            partImageObj.put("isPrimary", partImage.getIsPrimary());
            partImageObj.put("createdAt", partImage.getCreatedAt());
            partImageObj.put("isDeleted", partImage.getIsDeleted());
            partImageObj.put("deletedAt", partImage.getDeletedAt());

            partArray.put(partImageObj);
        }

        toReturn.put("success", true);
        toReturn.put("partImages", partArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllPartImages
}
