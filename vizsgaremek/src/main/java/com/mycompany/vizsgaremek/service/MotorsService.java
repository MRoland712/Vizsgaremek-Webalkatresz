/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Motors;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class MotorsService {
    
    private final AuthenticationService.motorsAuth motorsAuth = new AuthenticationService.motorsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createMotors(Motors createMotors) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (motorsAuth.isDataMissing(createMotors.getBrand())) {
            errors.put("MissingBrand");
        }

        if (motorsAuth.isDataMissing(createMotors.getModel())) {
            errors.put("MissingModel");
        }

        if (motorsAuth.isDataMissing(createMotors.getYearFrom())) {
            errors.put("MissingYearFrom");
        }

        if (motorsAuth.isDataMissing(createMotors.getYearTo())) {
            errors.put("MissingYearTo");
        }

        if (!motorsAuth.isDataMissing(createMotors.getBrand()) && !motorsAuth.isValidBrand(createMotors.getBrand())) {
            errors.put("InvalidBrand");
        }

        if (!motorsAuth.isDataMissing(createMotors.getModel()) && !motorsAuth.isValidModel(createMotors.getModel())) {
            errors.put("InvalidModel");
        }

        if (!motorsAuth.isDataMissing(createMotors.getYearFrom()) && !motorsAuth.isValidYearFrom(createMotors.getYearFrom())) {
            errors.put("InvalidYearFrom");
        }

        if (!motorsAuth.isDataMissing(createMotors.getYearTo()) && !motorsAuth.isValidYearTo(createMotors.getYearTo())) {
            errors.put("InvalidYearTo");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (Motors.createMotors(createMotors)) {
            toReturn.put("message", "Motor Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Motor Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // createMotors Closer

    public JSONObject getAllMotors() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Motors> modelResult = Motors.getAllMotors();

        // VALIDÁCIÓ 
        if (motorsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray carsArray = new JSONArray();

        for (Motors motor : modelResult) {
            JSONObject motorsObj = new JSONObject();
            motorsObj.put("id", motor.getId());
            motorsObj.put("Brand", motor.getBrand());
            motorsObj.put("Model", motor.getModel());
            motorsObj.put("YearFrom", motor.getYearFrom());
            motorsObj.put("YearTo", motor.getYearTo());
            motorsObj.put("createdAt", motor.getCreatedAt());
            motorsObj.put("updatedAt", motor.getUpdatedAt());
            motorsObj.put("deletedAt", motor.getDeletedAt());
            motorsObj.put("isDeleted", motor.getIsDeleted());

            carsArray.put(motorsObj);
        }

        toReturn.put("success", true);
        toReturn.put("motors", carsArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllMotors
    
    public JSONObject getMotorsById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - ID hiányzik
        if (motorsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Motors motor = Motors.getMotorsById(id);

        // Validáció nem található
        if (motorsAuth.isDataMissing(motor)) {
            errors.put("MotorNotound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject motorsObj = new JSONObject();
        motorsObj.put("id", motor.getId());
        motorsObj.put("brand", motor.getBrand());
        motorsObj.put("model", motor.getModel());
        motorsObj.put("yearFrom", motor.getYearFrom());
        motorsObj.put("yearTo", motor.getYearTo());
        motorsObj.put("createdAt", motor.getCreatedAt());
        motorsObj.put("updatedAt", motor.getUpdatedAt());
        motorsObj.put("isDeleted", motor.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("motor", motorsObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getMotorsById
    
    public JSONObject getMotorsByBrand(String brand) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - brand hiányzik
        if (motorsAuth.isDataMissing(brand)) {
            errors.put("MissingBrand");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Motors motor = Motors.getMotorsByBrand(brand);

        // Validáció nem található
        if (motorsAuth.isDataMissing(motor)) {
            errors.put("MotorNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject motorsObj = new JSONObject();
        motorsObj.put("id", motor.getId());
        motorsObj.put("brand", motor.getBrand());
        motorsObj.put("model", motor.getModel());
        motorsObj.put("yearFrom", motor.getYearFrom());
        motorsObj.put("yearTo", motor.getYearTo());
        motorsObj.put("createdAt", motor.getCreatedAt());
        motorsObj.put("updatedAt", motor.getUpdatedAt());
        motorsObj.put("isDeleted", motor.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("motor", motorsObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getMotorsByBrand
    
    public JSONObject getMotorsByModel(String model) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - model hiányzik
        if (motorsAuth.isDataMissing(model)) {
            errors.put("MissingModel");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Motors motor = Motors.getMotorsByModel(model);

        // Validáció nem található
        if (motorsAuth.isDataMissing(motor)) {
            errors.put("MotorNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject motorsObj = new JSONObject();
        motorsObj.put("id", motor.getId());
        motorsObj.put("brand", motor.getBrand());
        motorsObj.put("model", motor.getModel());
        motorsObj.put("yearFrom", motor.getYearFrom());
        motorsObj.put("yearTo", motor.getYearTo());
        motorsObj.put("createdAt", motor.getCreatedAt());
        motorsObj.put("updatedAt", motor.getUpdatedAt());
        motorsObj.put("isDeleted", motor.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("motor", motorsObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getMotorsByModel
    
    public JSONObject softDeleteMotors(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (motorsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!motorsAuth.isDataMissing(id) && !motorsAuth.isValidId(id)) {  
            
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Motors modelResult = Motors.getMotorsById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("MotorsNotFound");
        }


        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("MotorIsDeleted");
        }


        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Motors.softDeleteMotors(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Motor Succesfully");
        return toReturn;
    }//softDeleteCars
    
}
