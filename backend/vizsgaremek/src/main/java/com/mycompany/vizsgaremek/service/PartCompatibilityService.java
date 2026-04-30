package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.PartCompatibility;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class PartCompatibilityService {
    
    private final AuthenticationService.partCompatibilityAuth partCompatibilityAuth = new AuthenticationService.partCompatibilityAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();
    
    public JSONObject createPartCompatibilityService(PartCompatibility createPartCompatibility) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        // VALIDÁCIÓK - Hiányzó mezők
        if (partCompatibilityAuth.isDataMissing(createPartCompatibility.getPartId())) {
            errors.put("MissingPartId");
        }
        if (partCompatibilityAuth.isDataMissing(createPartCompatibility.getVehicleType())) {
            errors.put("MissingVehicleType");
        }
        if (partCompatibilityAuth.isDataMissing(createPartCompatibility.getVehicleId())) {
            errors.put("MissingVehicleId");
        }
        
        // VALIDÁCIÓK - Érvénytelen mezők
        if (!partCompatibilityAuth.isDataMissing(createPartCompatibility.getPartId()) 
            && !partCompatibilityAuth.isValidPartId(createPartCompatibility.getPartId())) {
            errors.put("InvalidPartId");
        }
        if (!partCompatibilityAuth.isDataMissing(createPartCompatibility.getVehicleType()) 
            && !partCompatibilityAuth.isValidVehicleType(createPartCompatibility.getVehicleType())) {
            errors.put("InvalidVehicleType");
        }
        if (!partCompatibilityAuth.isDataMissing(createPartCompatibility.getVehicleId()) 
            && !partCompatibilityAuth.isValidVehicleId(createPartCompatibility.getVehicleId())) {
            errors.put("InvalidVehicleId");
        }
        
        // Hiba ellenőrzés
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        // MODEL HÍVÁS
        if (PartCompatibility.createPartCompatibility(createPartCompatibility)) {
            toReturn.put("message", "PartCompatibility Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "PartCompatibility Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    }
    
    public JSONObject getAllPartCompatibilityService() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        // MODEL HÍVÁS
        ArrayList<PartCompatibility> modelResult = PartCompatibility.getAllPartCompatibility();
        
        // VALIDÁCIÓ - If no data in DB
        if (partCompatibilityAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }
        
        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }
        
        // KONVERZIÓ ArrayList → JSONArray
        JSONArray partCompatibilityArray = new JSONArray();
        for (PartCompatibility pc : modelResult) {
            JSONObject pcObj = new JSONObject();
            pcObj.put("id", pc.getId());
            pcObj.put("partId", pc.getPartId().getId());
            pcObj.put("vehicleType", pc.getVehicleType());
            pcObj.put("vehicleId", pc.getVehicleId());
            pcObj.put("createdAt", pc.getCreatedAt());
            pcObj.put("updatedAt", pc.getUpdatedAt());
            pcObj.put("isDeleted", pc.getIsDeleted());
            pcObj.put("deletedAt", pc.getDeletedAt());
            partCompatibilityArray.put(pcObj);
        }
        
        toReturn.put("success", true);
        toReturn.put("partCompatibility", partCompatibilityArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);
        
        return toReturn;
    }
    
    public JSONObject getPartCompatibilityByIdService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        // VALIDÁCIÓ
        if (partCompatibilityAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }
        
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        // MODEL HÍVÁS
        PartCompatibility pc = PartCompatibility.getPartCompatibilityById(id);
        
        // Ha nem található
        if (partCompatibilityAuth.isDataMissing(pc)) {
            errors.put("PartCompatibilityNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }
        
        // KONVERZIÓ
        JSONObject pcObj = new JSONObject();
        pcObj.put("id", pc.getId());
        pcObj.put("partId", pc.getPartId().getId());
        pcObj.put("vehicleType", pc.getVehicleType());
        pcObj.put("vehicleId", pc.getVehicleId());
        pcObj.put("createdAt", pc.getCreatedAt());
        pcObj.put("updatedAt", pc.getUpdatedAt());
        pcObj.put("isDeleted", pc.getIsDeleted());
        pcObj.put("deletedAt", pc.getDeletedAt());
        
        toReturn.put("success", true);
        toReturn.put("partCompatibility", pcObj);
        toReturn.put("statusCode", 200);
        
        return toReturn;
    }
    
    public JSONObject getPartCompatibilityByVehicleTypeService(String vehicleType) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        // VALIDÁCIÓ
        if (partCompatibilityAuth.isDataMissing(vehicleType)) {
            errors.put("MissingVehicleType");
        }
        
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        // MODEL HÍVÁS
        ArrayList<PartCompatibility> pcList = PartCompatibility.getPartCompatibilityByVehicleType(vehicleType);
        
        // Ha nem található
        if (partCompatibilityAuth.isDataMissing(pcList)) {
            errors.put("PartCompatibilityNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }
        
        // KONVERZIÓ ArrayList  JSONArray
        JSONArray partCompatibilityArray = new JSONArray();
        for (PartCompatibility pc : pcList) {
            JSONObject pcObj = new JSONObject();
            pcObj.put("id", pc.getId());
            pcObj.put("partId", pc.getPartId().getId());
            pcObj.put("vehicleType", pc.getVehicleType());
            pcObj.put("vehicleId", pc.getVehicleId());
            pcObj.put("createdAt", pc.getCreatedAt());
            pcObj.put("updatedAt", pc.getUpdatedAt());
            pcObj.put("isDeleted", pc.getIsDeleted());
            pcObj.put("deletedAt", pc.getDeletedAt());
            partCompatibilityArray.put(pcObj);
        }
        
        toReturn.put("success", true);
        toReturn.put("partCompatibility", partCompatibilityArray);
        toReturn.put("count", pcList.size());
        toReturn.put("statusCode", 200);
        
        return toReturn;
    }
    
    public JSONObject getPartCompatibilityByVehicleIdService(Integer vehicleId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        // VALIDÁCIÓ
        if (partCompatibilityAuth.isDataMissing(vehicleId)) {
            errors.put("MissingVehicleId");
        }
        
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        // MODEL HÍVÁS
        ArrayList<PartCompatibility> pcList = PartCompatibility.getPartCompatibilityByVehicleId(vehicleId);
        
        // Ha nem található
        if (partCompatibilityAuth.isDataMissing(pcList)) {
            errors.put("PartCompatibilityNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }
        
        // KONVERZIÓ ArrayList → JSONArray
        JSONArray partCompatibilityArray = new JSONArray();
        for (PartCompatibility pc : pcList) {
            JSONObject pcObj = new JSONObject();
            pcObj.put("id", pc.getId());
            pcObj.put("partId", pc.getPartId().getId());
            pcObj.put("vehicleType", pc.getVehicleType());
            pcObj.put("vehicleId", pc.getVehicleId());
            pcObj.put("createdAt", pc.getCreatedAt());
            pcObj.put("updatedAt", pc.getUpdatedAt());
            pcObj.put("isDeleted", pc.getIsDeleted());
            pcObj.put("deletedAt", pc.getDeletedAt());
            partCompatibilityArray.put(pcObj);
        }
        
        toReturn.put("success", true);
        toReturn.put("partCompatibility", partCompatibilityArray);
        toReturn.put("count", pcList.size());
        toReturn.put("statusCode", 200);
        
        return toReturn;
    }
    
    public JSONObject softDeletePartCompatibilityService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        // If id is Missing
        if (partCompatibilityAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }
        
        // If id is Invalid
        if (!partCompatibilityAuth.isDataMissing(id) && !partCompatibilityAuth.isValidId(id)) {
            errors.put("InvalidId");
        }
        
        // if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        // get data from spq
        PartCompatibility modelResult = PartCompatibility.getPartCompatibilityById(id);
        
        // if spq gives null data
        if (modelResult == null) {
            errors.put("PartCompatibilityNotFound");
        }
        
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }
        
        if (modelResult.getIsDeleted() == true) {
            errors.put("PartCompatibilityIsSoftDeleted");
        }
        
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }
        
        Boolean result = PartCompatibility.softDeletePartCompatibility(id);
        
        if (!result) {
            errors.put("ServerError");
        }
        
        // if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }
        
        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("message", "Deleted PartCompatibility Successfully");
        return toReturn;
    }
    
    public JSONObject updatePartCompatibilityService(PartCompatibility updatedPartCompatibility) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        // VALIDÁCIÓK KERESÉSI PARAMÉTEREK
        if (partCompatibilityAuth.isDataMissing(updatedPartCompatibility.getId())) {
            errors.put("MissingSearchParameter");
        }
        
        if (!partCompatibilityAuth.isDataMissing(updatedPartCompatibility.getId())
                && !partCompatibilityAuth.isValidId(updatedPartCompatibility.getId())) {
            errors.put("InvalidId");
        }
        
        // Hiba ellenőrzés - keresési paraméterek
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        // LEKÉRDEZÉS
        PartCompatibility existingPartCompatibility = null;
        
        if (!partCompatibilityAuth.isDataMissing(updatedPartCompatibility.getId())) {
            existingPartCompatibility = PartCompatibility.getPartCompatibilityById(updatedPartCompatibility.getId());
        }
        
        // Ha nem található
        if (partCompatibilityAuth.isDataMissing(existingPartCompatibility)) {
            errors.put("PartCompatibilityNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }
        
        // MEZŐK MÓDOSÍTÁSA (csak a megadottak!)
        if (!partCompatibilityAuth.isDataMissing(updatedPartCompatibility.getVehicleType())) {
            if (partCompatibilityAuth.isValidVehicleType(updatedPartCompatibility.getVehicleType())) {
                existingPartCompatibility.setVehicleType(updatedPartCompatibility.getVehicleType());
            } else {
                errors.put("InvalidVehicleType");
            }
        }
        
        if (!partCompatibilityAuth.isDataMissing(updatedPartCompatibility.getVehicleId())) {
            if (partCompatibilityAuth.isValidVehicleId(updatedPartCompatibility.getVehicleId())) {
                existingPartCompatibility.setVehicleId(updatedPartCompatibility.getVehicleId());
            } else {
                errors.put("InvalidVehicleId");
            }
        }
        
        // isDeleted CSAK ha meg van adva!
        if (!partCompatibilityAuth.isDataMissing(updatedPartCompatibility.getIsDeleted())) {
            existingPartCompatibility.setIsDeleted(updatedPartCompatibility.getIsDeleted());
        }
        
        // Hiba ellenőrzés validációk
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        // ADATBÁZIS UPDATE
        try {
            Boolean result = PartCompatibility.updatePartCompatibility(existingPartCompatibility);
            
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
        toReturn.put("message", "PartCompatibility updated successfully");
        toReturn.put("statusCode", 200);
        
        return toReturn;
    }
}