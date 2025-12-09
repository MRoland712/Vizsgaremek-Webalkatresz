/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.UserLogs;
import com.mycompany.vizsgaremek.service.AuthenticationService;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ddori
 */
public class UserLogsService {

    private final AuthenticationService.userLogsAuth userLogsAuth = new AuthenticationService.userLogsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createUserLogs(UserLogs createdUserLog, Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (AuthenticationService.isDataMissing(userId)) {
            errors.put("MissingUserId");
        }
        if (AuthenticationService.isDataMissing(createdUserLog.getAction())) {
            errors.put("MissingAction");
        }
        if (AuthenticationService.isDataMissing(createdUserLog.getDetails())) {
            toReturn.put("message", "Details Is Missing");
        }

        if (!AuthenticationService.isDataMissing(userId) && !userLogsAuth.isValidUserId(userId)) {
            errors.put("InvalidId");
        }
        if (!AuthenticationService.isDataMissing(createdUserLog.getAction()) && !userLogsAuth.isValidAction(createdUserLog.getAction())) {
            errors.put("InvalidAction");
        }
        if (!AuthenticationService.isDataMissing(createdUserLog.getDetails()) && !userLogsAuth.isValidDetail(createdUserLog.getDetails())) {
            errors.put("InvalidDetails");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Boolean result = UserLogs.createUserLogs(createdUserLog, userId);

        if (AuthenticationService.isDataMissing(result)) {
            errors.put("ModelError");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        if (!AuthenticationService.isDataMissing(toReturn)) {
            return errorAuth.createOKResponse(toReturn);
        } else {
            return errorAuth.createOKResponse();
        }
    }

    /*public JSONObject updateUserLogs(UserLogs updatedUserLogs, Integer id, Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (AuthenticationService.isDataMissing(id)) {
            errors.put("MissingId");
        }

        if (!AuthenticationService.isDataMissing(id) && !userLogsAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        if (!AuthenticationService.isDataMissing(updatedUserLogs.getAction()) && !userLogsAuth.isValidAction(updatedUserLogs.getAction())) {
            errors.put("InvalidAction");
        }

        if (!AuthenticationService.isDataMissing(updatedUserLogs.getDetails()) && !userLogsAuth.isValidDetail(updatedUserLogs.getDetails())) {
            errors.put("InvalidDetails");
        }

        if (!AuthenticationService.isDataMissing(updatedUserLogs.getUserId().getId())
                && !userLogsAuth.isValidUserId(updatedUserLogs.getUserId().getId())) {
            errors.put("InvalidUserId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        UserLogs existingUserLog = UserLogs.getUserLogById(id);

        if (AuthenticationService.isDataMissing(existingUserLog)) {
            errors.put("UserNotFound");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (!AuthenticationService.isDataMissing(updatedUserLogs.getAction())) {
            existingUserLog.setAction(updatedUserLogs.getAction());
        }

        if (!AuthenticationService.isDataMissing(updatedUserLogs.getDetails())) {
            existingUserLog.setDetails(updatedUserLogs.getDetails());
        }

        if (!AuthenticationService.isDataMissing(updatedUserLogs.getUserId())) {
            existingUserLog.setUserId(updatedUserLogs.getUserId());
        }

        Boolean result = UserLogs.updateUserLogs(existingUserLog, id);

        if (AuthenticationService.isDataMissing(result)) {
            errors.put("ModelError");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        if (!AuthenticationService.isDataMissing(toReturn)) {
            return errorAuth.createOKResponse(toReturn);
        } else {
            return errorAuth.createOKResponse();
        }
    }

    public JSONObject getUserLogById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if id is missing
        if (AuthenticationService.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //if id is invalid
        if (!userLogsAuth.isValidId(id) && !AuthenticationService.isDataMissing(id)) {
            errors.put("InvalidId");
        }

        //error check if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        UserLogs modelResult = UserLogs.getUserLogById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("UserNotFound");
        }

        //error check if user not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject result = new JSONObject();

        result.put("id", modelResult.getId());
        result.put("user_id", modelResult.getUserId().getId());
        result.put("action", modelResult.getAction());
        result.put("details", modelResult.getDetails());
        result.put("created_at", modelResult.getCreatedAt());

        return errorAuth.createOKResponse(result);

    }*/
}
