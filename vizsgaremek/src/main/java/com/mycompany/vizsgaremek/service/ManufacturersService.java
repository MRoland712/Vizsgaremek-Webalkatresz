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
        
        Manufacturers existingManufacturer = Manufacturers.getManufacturersByName(createManufacturers.getName());
        if(existingManufacturer != null){
            errors.put("nameAlreadyExist");
            return errorAuth.createErrorResponse(errors, 409);
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
            errors.put("ManufacturersNotFound");
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

    public JSONObject softDeleteManufacturers(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (manufacturersAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!manufacturersAuth.isDataMissing(id) && !manufacturersAuth.isValidId(id)) {  // Csak ha NEM missing!
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Manufacturers modelResult = Manufacturers.getManufacturersById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("ManufacturersNotFound");
        }

        //if manufacturers not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("ManufacturersIsDeleted");
        }

        //if manufacturers is soft deleted
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Manufacturers.softDeleteManufacturers(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Manufacturer Succesfully");
        return toReturn;
    }//softDeleteManufacturers

    public JSONObject updateManufacturers(Manufacturers updatedManufacturers) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK KERESÉSI PARAMÉTER
        // Ha nincs ID
        if (manufacturersAuth.isDataMissing(updatedManufacturers.getId())) {
            errors.put("MissingId");
        }

        // Ha ID érvénytelen
        if (!manufacturersAuth.isDataMissing(updatedManufacturers.getId())
                && !manufacturersAuth.isValidId(updatedManufacturers.getId())) {
            errors.put("InvalidId");
        }

        // Hiba ellenőrzés keresési paraméter
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //  GYÁRTÓ LEKÉRDEZÉSE
        Manufacturers existingManufacturers = Manufacturers.getManufacturersById(updatedManufacturers.getId());

        // Ha nem található a gyártó
        if (manufacturersAuth.isDataMissing(existingManufacturers)) {
            errors.put("ManufacturerNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        // MEZŐK MÓDOSÍTÁSA (csak a megadottak!)
        // name CSAK ha meg van adva!
        if (!manufacturersAuth.isDataMissing(updatedManufacturers.getName())) {
            if (manufacturersAuth.isValidName(updatedManufacturers.getName())) {
                existingManufacturers.setName(updatedManufacturers.getName());
            } else {
                errors.put("InvalidName");
            }
        }

        // country CSAK ha meg van adva!
        if (!manufacturersAuth.isDataMissing(updatedManufacturers.getCountry())) {
            if (manufacturersAuth.isValidCountry(updatedManufacturers.getCountry())) {
                existingManufacturers.setCountry(updatedManufacturers.getCountry());
            } else {
                errors.put("InvalidCountry");
            }
        }

        // Hiba ellenőrzés validációk
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // Adatbázis error
        try {
            Boolean result = Manufacturers.updateManufacturers(existingManufacturers);

            if (!result) {
                errors.put("ServerError");
            }

        } catch (Exception ex) {
            errors.put("DatabaseError");
            ex.printStackTrace();
        }

        // Hiba ellenőrzés - adatbázis
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("success", true);
        toReturn.put("message", "Manufacturer updated successfully");
        toReturn.put("statusCode", 200);

        return toReturn;
        
    }//updateManufacturers
    
    public JSONObject getManufacturersByName(String name) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - ID hiányzik
        if (manufacturersAuth.isDataMissing(name)) {
            errors.put("MissingName");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Manufacturers manufacturer = Manufacturers.getManufacturersByName(name);

        // Validáció - Nem található
        if (manufacturersAuth.isDataMissing(manufacturer)) {
            errors.put("ManufacturersNotFound");
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
    }//getManufacturersByName

}
