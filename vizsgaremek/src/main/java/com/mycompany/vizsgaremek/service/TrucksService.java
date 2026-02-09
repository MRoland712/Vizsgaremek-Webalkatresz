/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Trucks;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class TrucksService {

    private final AuthenticationService.trucksAuth trucksAuth = new AuthenticationService.trucksAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createTrucks(Trucks createTrucks) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (trucksAuth.isDataMissing(createTrucks.getBrand())) {
            errors.put("MissingBrand");
        }

        if (trucksAuth.isDataMissing(createTrucks.getModel())) {
            errors.put("MissingModel");
        }

        if (trucksAuth.isDataMissing(createTrucks.getYearFrom())) {
            errors.put("MissingYearFrom");
        }

        if (trucksAuth.isDataMissing(createTrucks.getYearTo())) {
            errors.put("MissingYearTo");
        }

        if (!trucksAuth.isDataMissing(createTrucks.getBrand()) && !trucksAuth.isValidBrand(createTrucks.getBrand())) {
            errors.put("InvalidBrand");
        }

        if (!trucksAuth.isDataMissing(createTrucks.getModel()) && !trucksAuth.isValidModel(createTrucks.getModel())) {
            errors.put("InvalidModel");
        }

        if (!trucksAuth.isDataMissing(createTrucks.getYearFrom()) && !trucksAuth.isValidYearFrom(createTrucks.getYearFrom())) {
            errors.put("InvalidYearFrom");
        }

        if (!trucksAuth.isDataMissing(createTrucks.getYearTo()) && !trucksAuth.isValidYearTo(createTrucks.getYearTo())) {
            errors.put("InvalidYearTo");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (Trucks.createTrucks(createTrucks)) {
            toReturn.put("message", "Truck Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Truck Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // createTrucks Closer

    public JSONObject getAllTrucks() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Trucks> modelResult = Trucks.getAllTrucks();

        // VALIDÁCIÓ 
        if (trucksAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray trucksArray = new JSONArray();

        for (Trucks truck : modelResult) {
            JSONObject trucksObj = new JSONObject();
            trucksObj.put("id", truck.getId());
            trucksObj.put("Brand", truck.getBrand());
            trucksObj.put("Model", truck.getModel());
            trucksObj.put("YearFrom", truck.getYearFrom());
            trucksObj.put("YearTo", truck.getYearTo());
            trucksObj.put("createdAt", truck.getCreatedAt());
            trucksObj.put("updatedAt", truck.getUpdatedAt());
            trucksObj.put("deletedAt", truck.getDeletedAt());
            trucksObj.put("isDeleted", truck.getIsDeleted());

            trucksArray.put(trucksObj);
        }

        toReturn.put("success", true);
        toReturn.put("trucks", trucksArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllTrucks

    public JSONObject getTrucksById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - ID hiányzik
        if (trucksAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Trucks truck = Trucks.getTrucksById(id);

        // Validáció nem található
        if (trucksAuth.isDataMissing(truck)) {
            errors.put("TruckNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject trucksObj = new JSONObject();
        trucksObj.put("id", truck.getId());
        trucksObj.put("brand", truck.getBrand());
        trucksObj.put("model", truck.getModel());
        trucksObj.put("yearFrom", truck.getYearFrom());
        trucksObj.put("yearTo", truck.getYearTo());
        trucksObj.put("createdAt", truck.getCreatedAt());
        trucksObj.put("updatedAt", truck.getUpdatedAt());
        trucksObj.put("isDeleted", truck.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("Truck", trucksObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getTrucksById

    public JSONObject getTrucksByBrand(String brand) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - brand hiányzik
        if (trucksAuth.isDataMissing(brand)) {
            errors.put("MissingBrand");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Trucks truck = Trucks.getTrucksByBrand(brand);

        // Validáció nem található
        if (trucksAuth.isDataMissing(truck)) {
            errors.put("TruckNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject trucksObj = new JSONObject();
        trucksObj.put("id", truck.getId());
        trucksObj.put("brand", truck.getBrand());
        trucksObj.put("model", truck.getModel());
        trucksObj.put("yearFrom", truck.getYearFrom());
        trucksObj.put("yearTo", truck.getYearTo());
        trucksObj.put("createdAt", truck.getCreatedAt());
        trucksObj.put("updatedAt", truck.getUpdatedAt());
        trucksObj.put("isDeleted", truck.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("Truck", trucksObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getTrucksByBrand

    public JSONObject getTrucksByModel(String model) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - model hiányzik
        if (trucksAuth.isDataMissing(model)) {
            errors.put("MissingModel");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Trucks truck = Trucks.getTrucksByModel(model);

        // Validáció nem található
        if (trucksAuth.isDataMissing(truck)) {
            errors.put("TruckNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject trucksObj = new JSONObject();
        trucksObj.put("id", truck.getId());
        trucksObj.put("brand", truck.getBrand());
        trucksObj.put("model", truck.getModel());
        trucksObj.put("yearFrom", truck.getYearFrom());
        trucksObj.put("yearTo", truck.getYearTo());
        trucksObj.put("createdAt", truck.getCreatedAt());
        trucksObj.put("updatedAt", truck.getUpdatedAt());
        trucksObj.put("isDeleted", truck.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("Truck", trucksObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getTrucksByModel

    public JSONObject softDeleteTrucks(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (trucksAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!trucksAuth.isDataMissing(id) && !trucksAuth.isValidId(id)) {

            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Trucks modelResult = Trucks.getTrucksById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("TrucksNotFound");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("TruckIsDeleted");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Trucks.softDeleteTrucks(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Truck Succesfully");
        return toReturn;
    }//softDeleteTrucks

}
