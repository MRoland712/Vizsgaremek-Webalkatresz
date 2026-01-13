/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.PartVariants;
import com.mycompany.vizsgaremek.service.AuthenticationService.partvariantsAuth;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblg
 */
public class PartVariantsService {

    private final AuthenticationService.partvariantsAuth partvariantsAuth = new AuthenticationService.partvariantsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createPartVariants(PartVariants createPartVariants) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (partvariantsAuth.isDataMissing(createPartVariants.getPartId().getId())) {
            errors.put("MissingPartId");
        }
        if (partvariantsAuth.isDataMissing(createPartVariants.getName())) {
            errors.put("MissingName");
        }

        if (partvariantsAuth.isDataMissing(createPartVariants.getValue())) {
            errors.put("MissingValue");
        }

        if (partvariantsAuth.isDataMissing(createPartVariants.getAdditionalPrice())) {
            errors.put("MissingPrice");
        }

        if (!partvariantsAuth.isDataMissing(createPartVariants.getId()) && !partvariantsAuth.isValidPartsId(createPartVariants.getId())) {
            errors.put("InvalidId");
        }

        if (!partvariantsAuth.isDataMissing(createPartVariants.getName()) && !partvariantsAuth.isValidName(createPartVariants.getName())) {
            errors.put("InvalidName");
        }

        if (!partvariantsAuth.isDataMissing(createPartVariants.getValue()) && !partvariantsAuth.isValidValue(createPartVariants.getValue())) {
            errors.put("InvalidValue");
        }

        //BigDecimal
        if (!partvariantsAuth.isDataMissing(createPartVariants.getAdditionalPrice()) && !partvariantsAuth.isValidAdditionalPrice(createPartVariants.getAdditionalPrice())) {
            errors.put("InvalidAdditionalPrice");
        }
        //Integer
        /*if (!partsAuth.isDataMissing(createParts.getStock()) && !partsAuth.isValidStock(createParts.getStock())) {
            errors.put("InvalidCategory");
        }*/

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (PartVariants.createPartVariants(createPartVariants)) {
            toReturn.put("message", "PartVariant Created Successfully");
            toReturn.put("statusCode", 200);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "PartVariant Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // createParts Closer

    public JSONObject getAllPartVariants() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        ArrayList<PartVariants> modelResult = PartVariants.getAllPartVariants();

        if (partvariantsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        JSONArray PartVarintArray = new JSONArray();

        for (PartVariants partVariant : modelResult) {
            JSONObject partVariantsObj = new JSONObject();
            partVariantsObj.put("id", partVariant.getId());
            partVariantsObj.put("partId", partVariant.getPartId().getId());
            partVariantsObj.put("name", partVariant.getName());
            partVariantsObj.put("value", partVariant.getValue());
            partVariantsObj.put("additionalPrice", partVariant.getAdditionalPrice());
            partVariantsObj.put("createdAt", partVariant.getCreatedAt());

            PartVarintArray.put(partVariantsObj);
        }

        toReturn.put("success", true);
        toReturn.put("partVariants", PartVarintArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllPartVariants

    public JSONObject getPartVariantsById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (partvariantsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        PartVariants partvariant = PartVariants.getPartVariantsById(id);

        if (partvariantsAuth.isDataMissing(partvariant)) {
            errors.put("PartVariantsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject partVariantObj = new JSONObject();
        partVariantObj.put("id", partvariant.getId());
        partVariantObj.put("partId", partvariant.getPartId().getId());
        partVariantObj.put("name", partvariant.getName());
        partVariantObj.put("value", partvariant.getValue());
        partVariantObj.put("additionalPrice", partvariant.getAdditionalPrice());
        partVariantObj.put("createdAt", partvariant.getCreatedAt());

        toReturn.put("success", true);
        toReturn.put("partsVariants", partVariantObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPartVariantsById

    public JSONObject softDeletePartVariants(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (partvariantsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!partvariantsAuth.isDataMissing(id) && !partvariantsAuth.isValidId(id)) {  // Csak ha NEM missing!
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        PartVariants modelResult = PartVariants.getPartVariantsById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("PartVariantsNotFound");
        }

        //if parts not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("PartVariantIsSoftDeleted");
        }

        //if parts is soft deleted
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = PartVariants.softDeletePartVariants(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted partVariants Succesfully");
        return toReturn;
    }//softDeletePartVariants

    public JSONObject updatePartVariants(PartVariants updatedPartVariants) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK KERESÉSI PARAMÉTEREK 
        // Ha nincs SEMMILYEN keresési paraméter (id)
        if (partvariantsAuth.isDataMissing(updatedPartVariants.getId())){
            errors.put("MissingSearchParameter");
        }

        // Ha partVariantsId mint keresési paraméter NEM hiányzik ÉS ÉRVÉNYTELEN
        if (!partvariantsAuth.isDataMissing(updatedPartVariants.getId())
                && !partvariantsAuth.isValidId(updatedPartVariants.getId())) {
            errors.put("InvalidId");
        }

        // Hiba ellenőrzés - keresési paraméterek
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // CÍM LEKÉRDEZÉSE 
        PartVariants existingPartVariants = null;

        // ID alapján keresés
        if (!partvariantsAuth.isDataMissing(updatedPartVariants.getId())) {
            existingPartVariants = PartVariants.getPartVariantsById(updatedPartVariants.getId());

        }

        // Ha nem található a cím
        if (partvariantsAuth.isDataMissing(existingPartVariants)) {
            errors.put("PartsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        //  MEZŐK MÓDOSÍTÁSA (csak a megadottak!)
        // name - CSAK ha meg van adva!
        if (!partvariantsAuth.isDataMissing(updatedPartVariants.getName())) {
            if (partvariantsAuth.isValidName(updatedPartVariants.getName())) {
                existingPartVariants.setName(updatedPartVariants.getName());
            } else {
                errors.put("InvalidName");
            }
        }

        // value CSAK ha meg van adva!
        if (!partvariantsAuth.isDataMissing(updatedPartVariants.getValue())) {
            if (partvariantsAuth.isValidValue(updatedPartVariants.getValue())) {
                existingPartVariants.setValue(updatedPartVariants.getValue());
            } else {
                errors.put("InvalidValue");
            }
        }

        // additionalPrice CSAK ha meg van adva!
        if (!partvariantsAuth.isDataMissing(updatedPartVariants.getAdditionalPrice())) {
            if (partvariantsAuth.isValidAdditionalPrice(updatedPartVariants.getAdditionalPrice())) {
                existingPartVariants.setAdditionalPrice(updatedPartVariants.getAdditionalPrice());
            } else {
                errors.put("InvalidAdditionalPrice");
            }
        }

        /*
        // isDeleted CSAK ha meg van adva!
        if (!partsAuth.isDataMissing(updatedParts.getIsDeleted())) {
            if (partsAuth.isValidIsDeleted(updatedParts.getIsDeleted())) {
                existingParts.setIsDeleted(updatedParts.getIsDeleted());
            } else {
                errors.put("InvalidIsDefault");
            }
        }*/
        // Hiba ellenőrzés validációk
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // ADATBÁZIS UPDATE 
        try {
            Boolean result = PartVariants.updatePartVariants(existingPartVariants);

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
        toReturn.put("message", "PartVariants updated successfully");
        toReturn.put("statusCode", 200);

        return toReturn;
    }//updateParts
    
    public JSONObject getPartVariantsByName(String name) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (partvariantsAuth.isDataMissing(name)) {
            errors.put("MissingName");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        PartVariants partvariant = PartVariants.getPartVariantsByName(name);

        if (partvariantsAuth.isDataMissing(partvariant)) {
            errors.put("PartVariantsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject partVariantObj = new JSONObject();
        partVariantObj.put("id", partvariant.getId());
        partVariantObj.put("partId", partvariant.getPartId().getId());
        partVariantObj.put("name", partvariant.getName());
        partVariantObj.put("value", partvariant.getValue());
        partVariantObj.put("additionalPrice", partvariant.getAdditionalPrice());
        partVariantObj.put("createdAt", partvariant.getCreatedAt());

        toReturn.put("success", true);
        toReturn.put("partsVariants", partVariantObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPartVariantsByName
    
    public JSONObject getPartVariantsByValue(String value) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (partvariantsAuth.isDataMissing(value)) {
            errors.put("MissingValue");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        PartVariants partvariant = PartVariants.getPartVariantsByValue(value);

        if (partvariantsAuth.isDataMissing(partvariant)) {
            errors.put("PartVariantsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject partVariantObj = new JSONObject();
        partVariantObj.put("id", partvariant.getId());
        partVariantObj.put("partId", partvariant.getPartId().getId());
        partVariantObj.put("name", partvariant.getName());
        partVariantObj.put("value", partvariant.getValue());
        partVariantObj.put("additionalPrice", partvariant.getAdditionalPrice());
        partVariantObj.put("createdAt", partvariant.getCreatedAt());

        toReturn.put("success", true);
        toReturn.put("partsVariants", partVariantObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPartVariantsByValue

}
