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

        if (partvariantsAuth.isDataMissing(createPartVariants.getId())) {
            errors.put("MissingPartVariantId");
        }
        if (partvariantsAuth.isDataMissing(createPartVariants.getName())) {
            errors.put("MissingName");
        }

        if (partvariantsAuth.isDataMissing(createPartVariants.getValue())) {
            errors.put("MissingValue");
        }

        /*
        if (partsAuth.isDataMissing(createParts.getPrice())) {  BIGDECIMAL
            errors.put("MissingPrice");
        }*/

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
        /*if (!partsAuth.isDataMissing(createParts.getPrice()) && !partsAuth.isValidPrice(createParts.getPrice())) {
            errors.put("InvalidCategory");
        }*/
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
            toReturn.put("statusCode", 201);
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
        toReturn.put("parts", partVariantObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getPartVariantsById

}
