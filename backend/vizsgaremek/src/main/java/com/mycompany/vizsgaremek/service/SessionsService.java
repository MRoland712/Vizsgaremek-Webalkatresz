/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Sessions;
import com.mycompany.vizsgaremek.model.Users;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ddori
 */
public class SessionsService {
    
    private final AuthenticationService.sessionsAuth sessionsAuth = new AuthenticationService.sessionsAuth();
    private final AuthenticationService.userAuth userAuth = new AuthenticationService.userAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createSessionService(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        /*if (sessionsAuth.isDataMissing(userId)) {
            errors.put("MissingUserId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }*/
        
        System.out.println("userId " + userId);
        
        if (userId < 1) {
             errors.put("InvalidUserId");
        }
        
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        Users userData = Users.getUserById(userId);
        
        if (userData == null) {
            errors.put("UserNotFound");
        }
        
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        
        String token = java.util.UUID.randomUUID().toString();
        
        Sessions createdSession = new Sessions(
                token,
                userData
        );
        
        Boolean modelResponse = Sessions.createSession(createdSession);
        
        // MODEL HÍVÁS
        if (modelResponse) {
            toReturn.put("message", "Session Token Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);

            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Session Token Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            
            return errorAuth.createErrorResponse(errors, 500);
        }
    } // createSession Closer
    
    public JSONObject getSessionTokenByUserIdService(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (sessionsAuth.isDataMissing(userId)) {
            errors.put("MissingUserId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Sessions session = Sessions.getSessionTokenByUserId(userId);

        if (sessionsAuth.isDataMissing(session)) {
            errors.put("NoActiveSession");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject SessionObj = new JSONObject();
        SessionObj.put("id", session.getId());
        SessionObj.put("userId", session.getUserId().getId());
        SessionObj.put("token", session.getToken());
        SessionObj.put("expiresAt", session.getExpiresAt());
        SessionObj.put("createdAt", session.getCreatedAt());
        SessionObj.put("revoked", session.getRevoked());

        toReturn.put("success", true);
        toReturn.put("result", SessionObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getSessionTokenByUserId closer
}
