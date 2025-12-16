/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Manufacturers;
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
    
}
