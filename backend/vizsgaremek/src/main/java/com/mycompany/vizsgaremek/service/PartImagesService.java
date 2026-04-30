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

        Integer newId = PartImages.createPartImages(createPartImages);  // Integer

        if (newId != null && newId > 0) {  //  Sikeres
            toReturn.put("success", true);
            toReturn.put("id", newId);  // ID hozzáadása
            toReturn.put("statusCode", 201);
        } else {  // newId <= 0 vagy null
            toReturn.put("success", false);
            toReturn.put("statusCode", 500);
        }

        return toReturn;
    } // createPartImages Closer

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

    public JSONObject getPartImagesById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (partImagesAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        PartImages partImages = PartImages.getPartImagesById(id);

        if (partImagesAuth.isDataMissing(partImages)) {
            errors.put("PartImagesNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject partObj = new JSONObject();
        partObj.put("id", partImages.getId());
        partObj.put("partId", partImages.getPartId().getId());
        partObj.put("url", partImages.getUrl());
        partObj.put("IsPrimary", partImages.getIsPrimary());
        partObj.put("createdAt", partImages.getCreatedAt());

        toReturn.put("success", true);
        toReturn.put("partImages", partObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPartImagesById

    public JSONObject getPartImagesByPartId(Integer partId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (partImagesAuth.isDataMissing(partId)) {
            errors.put("MissingPartId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<PartImages> modelResult = PartImages.getPartImagesByPartId(partId);

        if (partImagesAuth.isDataMissing(modelResult)) {
            errors.put("PartImagesNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        // ArrayList<PartImages> van, akkor végig kell iterálni!
        JSONArray partImagesArray = new JSONArray();
        for (PartImages img : modelResult) {
            JSONObject partImagesObj = new JSONObject();
            partImagesObj.put("id", img.getId());
            partImagesObj.put("partId", img.getPartId().getId());
            partImagesObj.put("url", img.getUrl());
            partImagesObj.put("isPrimary", img.getIsPrimary());
            partImagesObj.put("createdAt", img.getCreatedAt());
            partImagesArray.put(partImagesObj);
        }

        toReturn.put("success", true);
        toReturn.put("partImages", partImagesArray);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPartImagesByPartId

    public JSONObject getPartImagesByUrl(String url) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (partImagesAuth.isDataMissing(url)) {
            errors.put("MissingUrl");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        PartImages partImages = PartImages.getPartImagesByUrl(url);

        if (partImagesAuth.isDataMissing(partImages)) {
            errors.put("PartImagesNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject partObj = new JSONObject();
        partObj.put("id", partImages.getId());
        partObj.put("partId", partImages.getPartId().getId());
        partObj.put("url", partImages.getUrl());
        partObj.put("IsPrimary", partImages.getIsPrimary());
        partObj.put("createdAt", partImages.getCreatedAt());

        toReturn.put("success", true);
        toReturn.put("partImages", partObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPartImagesByUrl

    public JSONObject softDeletePartImages(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (partImagesAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!partImagesAuth.isDataMissing(id) && !partImagesAuth.isValidId(id)) {  // Csak ha NEM missing!
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        PartImages modelResult = PartImages.getPartImagesById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("PartImagesNotFound");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("PartImagesIsSoftDeleted");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = PartImages.softDeletePartImages(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted PartImages Succesfully");
        return toReturn;
    }//softDeletePartImages

    public JSONObject updatePartImages(PartImages updatedPartImages) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK KERESÉSI PARAMÉTEREK 
        // Ha nincs SEMMILYEN keresési paraméter (id VAGY url)
        if (partImagesAuth.isDataMissing(updatedPartImages.getId())
                && partImagesAuth.isDataMissing(updatedPartImages.getUrl())) {
            errors.put("MissingSearchParameter");
        }

        // Ha id mint keresési paraméter NEM hiányzik ÉS ÉRVÉNYTELEN
        if (!partImagesAuth.isDataMissing(updatedPartImages.getId())
                && !partImagesAuth.isValidId(updatedPartImages.getId())) {
            errors.put("InvalidId");
        }

        // Ha url mint keresési paraméter NEM hiányzik ÉS ÉRVÉNYTELEN
        if (!partImagesAuth.isDataMissing(updatedPartImages.getUrl())
                && !partImagesAuth.isDataMissing(updatedPartImages.getUrl())
                && !partImagesAuth.isValidUrl(updatedPartImages.getUrl())) {
            errors.put("InvalidUrl");
        }

        // Hiba ellenőrzés - keresési paraméterek
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // CÍM LEKÉRDEZÉSE 
        PartImages existingParts = null;

        // ID alapján keresés
        if (!partImagesAuth.isDataMissing(updatedPartImages.getId())) {
            existingParts = PartImages.getPartImagesById(updatedPartImages.getId());

        } else if (!partImagesAuth.isDataMissing(updatedPartImages.getUrl())
                && !partImagesAuth.isDataMissing(updatedPartImages.getUrl())) {
            // Url alapján keresés
            existingParts = PartImages.getPartImagesByUrl(updatedPartImages.getUrl());
        }

        // Ha nem található a cím
        if (partImagesAuth.isDataMissing(existingParts)) {
            errors.put("PartsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        //  MEZŐK MÓDOSÍTÁSA (csak a megadottak!)
        // url CSAK ha meg van adva!
        if (!partImagesAuth.isDataMissing(updatedPartImages.getUrl())) {
            if (partImagesAuth.isValidUrl(updatedPartImages.getUrl())) {
                existingParts.setUrl(updatedPartImages.getUrl());
            } else {
                errors.put("InvalidUrl");
            }
        }

        // isPrimary CSAK ha meg van adva!
        if (!partImagesAuth.isDataMissing(updatedPartImages.getIsPrimary())) {
            if (partImagesAuth.isPartImagePrimary(updatedPartImages.getIsPrimary())) {
                existingParts.setIsPrimary(updatedPartImages.getIsPrimary());
            } else {
                errors.put("InvalidIsPrimary");
            }
        }

        // isDeleted CSAK ha meg van adva!
        if (!partImagesAuth.isDataMissing(updatedPartImages.getIsDeleted())) {
            if (partImagesAuth.isPartImagesDeleted(updatedPartImages.getIsDeleted())) {
                existingParts.setIsDeleted(updatedPartImages.getIsDeleted());
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
            Boolean result = PartImages.updatePartImages(existingParts);

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
        toReturn.put("message", "PartImages updated successfully");
        toReturn.put("statusCode", 200);

        return toReturn;
    }//updatePartImages
}
