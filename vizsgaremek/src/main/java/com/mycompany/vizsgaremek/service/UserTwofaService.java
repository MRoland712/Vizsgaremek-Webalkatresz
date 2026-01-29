/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.service.AuthenticationService.errorAuth;
import com.mycompany.vizsgaremek.service.AuthenticationService.userTwofaAuth;
import com.mycompany.vizsgaremek.model.UserTwofa;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.config.TFA;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 *
 * @author ddori
 */
public class UserTwofaService {

    public static JSONObject createUserTwofaService(String email) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (userTwofaAuth.isDataMissing(email)) {
            errors.put("MissingEmail");
        }

        if (!userTwofaAuth.isValidEmail(email)) {
            errors.put("InvalidEmail");
        }

        //error check if datas given are missing or invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Users user = Users.getUserByEmail(email);

        if (userTwofaAuth.isDataMissing(user)) {
            errors.put("UserNotFound");
        }

        //error check if user is missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        Integer userId = user.getId();

        String secretKey = TFA.generateSecretKey();

        StringBuilder recoveryCodes = new StringBuilder();

        //6 pseudorandom recoveryCodes
        for (int i = 0; i < 6; i++) {
            recoveryCodes.append(UUID.randomUUID()).append(";");
        }

        Boolean modelResponse = UserTwofa.createUserTwofa(userId, false, secretKey, recoveryCodes.toString());

        if (!modelResponse) {
            errors.put("modelError");
        }

        //error check if modelError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        ArrayList<String> list = new ArrayList<>();

        String[] parts = recoveryCodes.toString().split(";");

        Collections.addAll(list, parts);

        toReturn.put("secretKey", secretKey);
        toReturn.put("recoveryCodes", list);
        toReturn.put("QR", TFA.generateQRUrl(secretKey, email));

        return errorAuth.createOKResponse(toReturn);
    }

    public static JSONObject getUserTwofaByUserIdService(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        if (userTwofaAuth.isDataMissing(userId)) {
            errors.put("missingUserId");
        }
        
        if (!userTwofaAuth.isValidUserId(userId)) {
            errors.put("invalidUserId");
        }

        //error check if datas given are missing or invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        UserTwofa response = UserTwofa.getUserTwofaByUserId(userId);
        
        if (userTwofaAuth.isDataMissing(response)) {
            errors.put("modelError");
        }
        
        //error check if modelError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }
        
        ArrayList<String> list = new ArrayList<>();

        String[] parts = response.getRecoveryCodes().toString().split(";");

        Collections.addAll(list, parts);
        
        toReturn.put("id",response.getId());
        toReturn.put("userId",response.getUserId().getId());
        toReturn.put("TFASecret",response.getTwofaSecret());
        toReturn.put("TFAEnabled",response.getTwofaEnabled());
        toReturn.put("recoveryCodes",list);
        toReturn.put("createdAt",response.getCreatedAt());
        toReturn.put("updatedAt",response.getUpdatedAt());
        toReturn.put("isDeleted",response.getIsDeleted());
        toReturn.put("deletedAt",response.getDeletedAt());
        
        return errorAuth.createOKResponse(toReturn);
    }
    
    public static JSONObject updateUserTwofaService(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
        
        if (userTwofaAuth.isDataMissing(userId)) {
            errors.put("missingUserId");
        }
        
        if (!userTwofaAuth.isValidUserId(userId)) {
            errors.put("invalidUserId");
        }

        //error check if datas given are missing or invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        UserTwofa response = UserTwofa.getUserTwofaByUserId(userId);
        
        return errorAuth.createOKResponse(toReturn);
    }
}
