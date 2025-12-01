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

        if (userLogsAuth.isDataMissing(userId)) {
            errors.put("MissingUserId");
        }
        if (userLogsAuth.isDataMissing(createdUserLog.getAction())) {
            errors.put("MissingAction");
        }
        if (userLogsAuth.isDataMissing(createdUserLog.getDetails())) {
            toReturn.put("message", "Details Is Missing");
        }

        if (!userLogsAuth.isDataMissing(userId) && !userLogsAuth.isValidUserId(userId)) {
            errors.put("InvalidId");
        }
        if (!userLogsAuth.isDataMissing(createdUserLog.getAction()) && !userLogsAuth.isValidAction(createdUserLog.getAction())) {
            errors.put("InvalidAction");
        }
        if (!userLogsAuth.isDataMissing(createdUserLog.getDetails()) && !userLogsAuth.isValidDetail(createdUserLog.getDetails())) {
            errors.put("InvalidDetails");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        Boolean result = UserLogs.createUserLogs(createdUserLog, userId);
        
        if (userLogsAuth.isDataMissing(result)) {
            errors.put("ModelError");
        }
        
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }
        
        if (!errorAuth.isDataMissing(toReturn)) {
            return errorAuth.createOKResponse(toReturn);
        } else {
            return errorAuth.createOKResponse();
        }
    }
}
