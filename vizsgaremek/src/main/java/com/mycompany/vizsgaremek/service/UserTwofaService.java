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

        //error check if datas given are missing or invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        if (!userTwofaAuth.isValidEmail(email) && !userTwofaAuth.isDataMissing(email)) {
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

        //System.out.println("UserTwofa.getUserTwofaByUserId(user.getId())" + UserTwofa.getUserTwofaByUserId(user.getId()).getId());
        UserTwofa userTwofaData = UserTwofa.getUserTwofaByUserId(user.getId());
        Boolean canCreateUserTwoFa = true;

        //error check if user has active TFA
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        if (userTwofaData == null) {
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
        } else {
            Integer userId = user.getId();

            String secretKey = TFA.generateSecretKey();

            StringBuilder recoveryCodes = new StringBuilder();

            //6 pseudorandom recoveryCodes
            for (int i = 0; i < 6; i++) {
                recoveryCodes.append(UUID.randomUUID()).append(";");
            }

            UserTwofa updatedData = new UserTwofa();

            updatedData.setId(userTwofaData.getId());
            updatedData.setTwofaSecret(secretKey);
            updatedData.setRecoveryCodes(recoveryCodes.toString());
            updatedData.setIsDeleted(false);
            updatedData.setTwofaEnabled(false);

            Boolean modelResponse = UserTwofa.updateUserTwofa(updatedData);

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
            toReturn.put("message", "Updated user's TFA");

            return errorAuth.createOKResponse(toReturn);
        }
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

        toReturn.put("id", response.getId());
        toReturn.put("userId", response.getUserId().getId());
        toReturn.put("TFASecret", response.getTwofaSecret());
        toReturn.put("TFAEnabled", response.getTwofaEnabled());
        toReturn.put("recoveryCodes", list);
        toReturn.put("createdAt", response.getCreatedAt());
        toReturn.put("updatedAt", response.getUpdatedAt());
        toReturn.put("isDeleted", response.getIsDeleted());
        toReturn.put("deletedAt", response.getDeletedAt());

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

        //UserTwofa response = UserTwofa.updateUserTwofa(userId);
        return errorAuth.createOKResponse(toReturn);
    }

    public JSONObject getAllUserTwoFaService() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<UserTwofa> modelResult = UserTwofa.getAllUserTwoFa();

        // VALIDÁCIÓ - If no data in DB
        if (userTwofaAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray userTwoFaArray = new JSONArray();

        for (UserTwofa userTwoFa : modelResult) {
            JSONObject twoFaObj = new JSONObject();
            twoFaObj.put("id", userTwoFa.getId());
            twoFaObj.put("userId", userTwoFa.getUserId().getId());
            twoFaObj.put("twofaEnabled", userTwoFa.getTwofaEnabled());
            twoFaObj.put("twofaSecret", userTwoFa.getTwofaSecret());
            twoFaObj.put("category", userTwoFa.getRecoveryCodes());
            twoFaObj.put("createdAt", userTwoFa.getCreatedAt());
            twoFaObj.put("updatedAt", userTwoFa.getUpdatedAt());
            twoFaObj.put("isDeleted", userTwoFa.getIsDeleted());
            twoFaObj.put("deltedAt", userTwoFa.getDeletedAt());

            userTwoFaArray.put(twoFaObj);
        }

        toReturn.put("success", true);
        toReturn.put("UserTwoFa", userTwoFaArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllUserTwoFa

    public static JSONObject getUserTwofaByIdService(Integer idIN) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (userTwofaAuth.isDataMissing(idIN)) {
            errors.put("missingUserId");
        }

        if (!userTwofaAuth.isValidUserId(idIN)) {
            errors.put("invalidUserId");
        }

        //error check if datas given are missing or invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        UserTwofa response = UserTwofa.getUserTwofaById(idIN);

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

        toReturn.put("id", response.getId());
        toReturn.put("userId", response.getUserId().getId());
        toReturn.put("TFASecret", response.getTwofaSecret());
        toReturn.put("TFAEnabled", response.getTwofaEnabled());
        toReturn.put("recoveryCodes", list);
        toReturn.put("createdAt", response.getCreatedAt());
        toReturn.put("updatedAt", response.getUpdatedAt());
        toReturn.put("isDeleted", response.getIsDeleted());
        toReturn.put("deletedAt", response.getDeletedAt());

        return errorAuth.createOKResponse(toReturn);
    }//getUserTwofaByIdService

    public JSONObject softDeleteUserTwofaService(Integer idIN) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓ - ID ellenőrzés
        if (userTwofaAuth.isDataMissing(idIN)) {
            errors.put("TwofaIdMissing");
        }

        if (idIN != null && idIN <= 0) {
            errors.put("InvalidTwofaId");
        }

        // Ha van validációs hiba
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // Ellenőrizzük, hogy létezike a 2FA
        UserTwofa existingTwofa = UserTwofa.getUserTwofaById(idIN);
        if (userTwofaAuth.isDataMissing(existingTwofa)) {
            errors.put("TwofaNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        // Ellenőrizzük, hogy már törölve van-e
        if (existingTwofa.getIsDeleted()) {
            errors.put("TwofaAlreadyDeleted");
            return errorAuth.createErrorResponse(errors, 409);
        }

        // MODEL HÍVÁS 
        Boolean deleteResult = UserTwofa.softDeleteUserTwofa(idIN);

        // Törlés sikerességének ellenőrzése
        if (deleteResult == null || !deleteResult) {
            errors.put("DeleteFailed");
            return errorAuth.createErrorResponse(errors, 500);
        }

        // Sikeres törlés
        toReturn.put("success", true);
        toReturn.put("message", "User TwoFA deleted successfully");
        toReturn.put("twofaId", idIN);
        toReturn.put("statusCode", 200);

        return toReturn;
    } // softDeleteUserTwofaService

    public JSONObject checkUserTwoFaEnabledService(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓ 
        if (userTwofaAuth.isDataMissing(userId)) {
            errors.put("UserIdMissing");
        }

        if (userId != null && !userTwofaAuth.isValidUserId(userId)) {
            errors.put("InvalidUserId");
        }

        // Ha van validációs hiba
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        UserTwofa modelResult = UserTwofa.checkUserTwoFaEnabled(userId);

        // Ha nincs aktív 2FA
        if (userTwofaAuth.isDataMissing(modelResult)) {
            toReturn.put("success", false);
            toReturn.put("twofaEnabled", false);
            toReturn.put("message", "2FA is not enabled or user not found");
            toReturn.put("statusCode", 404);
            return toReturn;
        }

        JSONObject twofaData = new JSONObject();
        twofaData.put("userId", userId);
        twofaData.put("twofaEnabled", modelResult.getTwofaEnabled());
        twofaData.put("twofaSecret", modelResult.getTwofaSecret());

        toReturn.put("success", true);
        toReturn.put("twofaData", twofaData);
        toReturn.put("message", "2FA is enabled");
        toReturn.put("statusCode", 200);

        return toReturn;
    } // checkUserTwoFaEnabledService
}
