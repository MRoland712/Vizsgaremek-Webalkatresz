/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.UserVehicles;
import com.mycompany.vizsgaremek.model.Users;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class UserVehiclesService {

    private final AuthenticationService.userVehiclesAuth userVehiclesAuth = new AuthenticationService.userVehiclesAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createUserVehicleService(UserVehicles uv) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK
        if (userVehiclesAuth.isDataMissing(uv.getUserId())) {
            errors.put("MissingUserId");
        }
        if (userVehiclesAuth.isDataMissing(uv.getVehicleType())) {
            errors.put("MissingVehicleType");
        }
        if (userVehiclesAuth.isDataMissing(uv.getVehicleId())) {
            errors.put("MissingVehicleId");
        }
        if (userVehiclesAuth.isDataMissing(uv.getYear())) {
            errors.put("MissingYear");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        if (!userVehiclesAuth.isValidUserId(uv.getUserId())) {
            errors.put("InvalidUserId");
        }
        if (!userVehiclesAuth.isValidVehicleType(uv.getVehicleType())) {
            errors.put("InvalidVehicleType");
        }
        if (!userVehiclesAuth.isValidVehicleId(uv.getVehicleId())) {
            errors.put("InvalidVehicleId");
        }
        if (!userVehiclesAuth.isValidYear(uv.getYear())) {
            errors.put("InvalidYear");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // User létezik
        Users existingUser = Users.getUserById(uv.getUserId());
        if (userVehiclesAuth.isDataMissing(existingUser)) {
            errors.put("UserNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        // MODEL HÍVÁS
        if (UserVehicles.createUserVehicle(uv)) {
            toReturn.put("success", true);
            toReturn.put("message", "UserVehicle Created Successfully");
            toReturn.put("statusCode", 201);
            return toReturn;
        } else {
            toReturn.put("success", false);
            toReturn.put("message", "UserVehicle Creation Failed");
            toReturn.put("statusCode", 500);
            return toReturn;
        }
    }

    public JSONObject getUserVehiclesByUserIdService(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK hiányzik
        if (userVehiclesAuth.isDataMissing(userId)) {
            errors.put("MissingUserId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        if (!userVehiclesAuth.isValidUserId(userId)) {
            errors.put("InvalidUserId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        ArrayList<UserVehicles> modelResult = UserVehicles.getUserVehiclesByUserId(userId);

        if (userVehiclesAuth.isDataMissing(modelResult)) {
            errors.put("UserVehiclesNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray uvArray = new JSONArray();
        for (UserVehicles uv : modelResult) {
            JSONObject uvObj = new JSONObject();
            uvObj.put("id", uv.getId());
            uvObj.put("userId", uv.getUserId());
            uvObj.put("vehicleType", uv.getVehicleType());
            uvObj.put("vehicleId", uv.getVehicleId());
            uvObj.put("year", uv.getYear());
            uvObj.put("createdAt", uv.getCreatedAt());
            uvObj.put("isDeleted", uv.getIsDeleted());
            uvArray.put(uvObj);
        }

        toReturn.put("success", true);
        toReturn.put("userVehicles", uvArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);
        return toReturn;
    }

    public JSONObject softDeleteUserVehicleService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK hiányzik
        if (userVehiclesAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        if (!userVehiclesAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Boolean result = UserVehicles.softDeleteUserVehicle(id);

        if (!result) {
            errors.put("ServerError");
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("success", true);
        toReturn.put("message", "UserVehicle Deleted Successfully");
        toReturn.put("statusCode", 200);
        return toReturn;
    }
}
