/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Manufacturers;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblg
 */
public class ManufacturersService {
    
    private final AuthenticationService.manufacturersAuth manufacturersAuth = new AuthenticationService.manufacturersAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();
    
    public JSONObject createManufacturers(Manufacturers createManufacturers) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (manufacturersAuth.isDataMissing(createManufacturers.getName())) {
            errors.put("MissingName");
        }
        
        if (manufacturersAuth.isDataMissing(createManufacturers.getCountry())) {
            errors.put("MissingCountry");
        }


        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (Manufacturers.createManufacturers(createManufacturers)) {  // ← Static metódus hívás!
            toReturn.put("message", "Manufacturers Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Manufacturers Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // createManufacturers Closer
    
    public JSONObject getAllManufacturers() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Manufacturers> modelResult = Manufacturers.getAllManufacturers();

        // VALIDÁCIÓ - If no data in DB
        if (manufacturersAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray manufacturersArray = new JSONArray();

        for (Manufacturers manufacturer : modelResult) {
            JSONObject manufacturersObj = new JSONObject();
            manufacturersObj.put("id", manufacturer.getId());
            manufacturersObj.put("name", manufacturer.getName());
            manufacturersObj.put("country", manufacturer.getCountry());
            manufacturersObj.put("createdAt", manufacturer.getCreatedAt());
            manufacturersArray.put(manufacturersObj);
        }

        toReturn.put("success", true);
        toReturn.put("Manufacturers", manufacturersArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
        
    }//getAllManufacturers close
    
    public JSONObject getManufacturersById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - ID hiányzik
        if (manufacturersAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Manufacturers manufacturer = Manufacturers.getManufacturersById(id);

        // Validáció - Nem található
        if (manufacturersAuth.isDataMissing(manufacturer)) {
            errors.put("AddressNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject manufacturersObj = new JSONObject();
        manufacturersObj.put("id", manufacturer.getId());
        manufacturersObj.put("name", manufacturer.getName());
        manufacturersObj.put("country", manufacturer.getCountry());
        manufacturersObj.put("createdAt", manufacturer.getCreatedAt());

        toReturn.put("success", true);
        toReturn.put("Manufacturer", manufacturersObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getManufacturersById
    
}
