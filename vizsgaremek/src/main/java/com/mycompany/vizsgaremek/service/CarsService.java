/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Cars;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class CarsService {

    private final AuthenticationService.carsAuth carsAuth = new AuthenticationService.carsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createCars(Cars createCars) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (carsAuth.isDataMissing(createCars.getBrand())) {
            errors.put("MissingBrand");
        }

        if (carsAuth.isDataMissing(createCars.getModel())) {
            errors.put("MissingModel");
        }

        if (carsAuth.isDataMissing(createCars.getYearFrom())) {
            errors.put("MissingYearFrom");
        }

        if (carsAuth.isDataMissing(createCars.getYearTo())) {
            errors.put("MissingYearTo");
        }

        if (!carsAuth.isDataMissing(createCars.getBrand()) && !carsAuth.isValidBrand(createCars.getBrand())) {
            errors.put("InvalidBrand");
        }

        if (!carsAuth.isDataMissing(createCars.getModel()) && !carsAuth.isValidModel(createCars.getModel())) {
            errors.put("InvalidModel");
        }

        if (!carsAuth.isDataMissing(createCars.getYearFrom()) && !carsAuth.isValidYearFrom(createCars.getYearFrom())) {
            errors.put("InvalidYearFrom");
        }

        if (!carsAuth.isDataMissing(createCars.getYearTo()) && !carsAuth.isValidYearTo(createCars.getYearTo())) {
            errors.put("InvalidYearTo");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (Cars.createCars(createCars)) {
            toReturn.put("message", "Car Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Car Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // createCars Closer

    public JSONObject getAllCars() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Cars> modelResult = Cars.getAllCars();

        // VALIDÁCIÓ 
        if (carsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray carsArray = new JSONArray();

        for (Cars car : modelResult) {
            JSONObject carsObj = new JSONObject();
            carsObj.put("id", car.getId());
            carsObj.put("Brand", car.getBrand());
            carsObj.put("Model", car.getModel());
            carsObj.put("YearFrom", car.getYearFrom());
            carsObj.put("YearTo", car.getYearTo());
            carsObj.put("createdAt", car.getCreatedAt());
            carsObj.put("updatedAt", car.getUpdatedAt());
            carsObj.put("deletedAt", car.getDeletedAt());
            carsObj.put("isDeleted", car.getIsDeleted());

            carsArray.put(carsObj);
        }

        toReturn.put("success", true);
        toReturn.put("cars", carsArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllCars
    
    public JSONObject getCarsById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - ID hiányzik
        if (carsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Cars car = Cars.getCarsById(id);

        // Validáció nem található
        if (carsAuth.isDataMissing(car)) {
            errors.put("CarNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject carsObj = new JSONObject();
        carsObj.put("id", car.getId());
        carsObj.put("brand", car.getBrand());
        carsObj.put("model", car.getModel());
        carsObj.put("yearFrom", car.getYearFrom());
        carsObj.put("yearTo", car.getYearTo());
        carsObj.put("createdAt", car.getCreatedAt());
        carsObj.put("updatedAt", car.getUpdatedAt());
        carsObj.put("isDeleted", car.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("Car", carsObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getCarsById
    
    public JSONObject getCarsByBrand(String brand) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - brand hiányzik
        if (carsAuth.isDataMissing(brand)) {
            errors.put("MissingBrand");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Cars car = Cars.getCarsByBrand(brand);

        // Validáció nem található
        if (carsAuth.isDataMissing(car)) {
            errors.put("CarNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject carsObj = new JSONObject();
        carsObj.put("id", car.getId());
        carsObj.put("brand", car.getBrand());
        carsObj.put("model", car.getModel());
        carsObj.put("yearFrom", car.getYearFrom());
        carsObj.put("yearTo", car.getYearTo());
        carsObj.put("createdAt", car.getCreatedAt());
        carsObj.put("updatedAt", car.getUpdatedAt());
        carsObj.put("isDeleted", car.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("Car", carsObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getCarsByBrand
    
    public JSONObject getCarsByModel(String model) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - model hiányzik
        if (carsAuth.isDataMissing(model)) {
            errors.put("MissingModel");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Cars car = Cars.getCarsByModel(model);

        // Validáció nem található
        if (carsAuth.isDataMissing(car)) {
            errors.put("CarNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject carsObj = new JSONObject();
        carsObj.put("id", car.getId());
        carsObj.put("brand", car.getBrand());
        carsObj.put("model", car.getModel());
        carsObj.put("yearFrom", car.getYearFrom());
        carsObj.put("yearTo", car.getYearTo());
        carsObj.put("createdAt", car.getCreatedAt());
        carsObj.put("updatedAt", car.getUpdatedAt());
        carsObj.put("isDeleted", car.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("Car", carsObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getCarsByModel
    
    public JSONObject softDeleteCars(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (carsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!carsAuth.isDataMissing(id) && !carsAuth.isValidId(id)) {  
            
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Cars modelResult = Cars.getCarsById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("CarsNotFound");
        }


        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("CarIsDeleted");
        }


        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Cars.softDeleteCars(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Car Succesfully");
        return toReturn;
    }//softDeleteCars
}


