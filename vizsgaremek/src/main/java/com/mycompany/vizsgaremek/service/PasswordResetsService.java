/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.config.Encrypt;
import com.mycompany.vizsgaremek.config.SendEmail;
import com.mycompany.vizsgaremek.model.PasswordResets;
import com.mycompany.vizsgaremek.model.Users;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class PasswordResetsService {

    private final AuthenticationService.passwordResetsAuth passwordResetsAuth = new AuthenticationService.passwordResetsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    // forgotPassword email alapján küld tokent
    public JSONObject createPasswordResetService(String email) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (passwordResetsAuth.isDataMissing(email)) {
            errors.put("MissingEmail");
            return errorAuth.createErrorResponse(errors, 400);
        }

        if (!passwordResetsAuth.isValidEmail(email)) {
            errors.put("InvalidEmail");
            return errorAuth.createErrorResponse(errors, 400);
        }

        Users existingUser = Users.getUserByEmail(email);
        if (existingUser == null) {
            errors.put("UserNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        // Token generálás
        String token = UUID.randomUUID().toString();

        // Mentés adatbázisba
        Boolean result = PasswordResets.createPasswordReset(existingUser.getId(), token);
        if (!result) {
            errors.put("ServerError");
            return errorAuth.createErrorResponse(errors, 500);
        }

        // Email küldés
        try {
            SendEmail.sendPasswordResetEmail(existingUser.getEmail(), existingUser.getUsername(), token);
        } catch (Exception ex) {
            ex.printStackTrace();
            errors.put("EmailSendError");
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("success", true);
        toReturn.put("message", "Password Reset Email Sent Successfully");
        toReturn.put("statusCode", 200);
        return toReturn;
    }

    public JSONObject getPasswordResetByTokenService(String token) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (passwordResetsAuth.isDataMissing(token)) {
            errors.put("MissingToken");
            return errorAuth.createErrorResponse(errors, 400);
        }

        PasswordResets passwordReset = PasswordResets.getPasswordResetByToken(token);
        if (passwordReset == null) {
            errors.put("InvalidOrExpiredToken");
            return errorAuth.createErrorResponse(errors, 404);
        }

        toReturn.put("userId", passwordReset.getUserId().getId());
        toReturn.put("token", passwordReset.getToken());
        toReturn.put("expiresAt", passwordReset.getExpiresAt().toString());
        toReturn.put("statusCode", 200);
        return toReturn;
    }

    // resetPassword token + új jelszó alapján frissít
    public JSONObject updatePasswordResetService(String token, String newPassword) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Hiányzik a token?
        if (passwordResetsAuth.isDataMissing(token)) {
            errors.put("MissingToken");
        }

        // Hiányzik az új jelszó?
        if (passwordResetsAuth.isDataMissing(newPassword)) {
            errors.put("MissingNewPassword");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // Érvényes a jelszó?
        if (!passwordResetsAuth.isValidPassword(newPassword)) {
            errors.put("InvalidPassword");
            return errorAuth.createErrorResponse(errors, 400);
        }

        // Token létezik és nem járt le?
        PasswordResets passwordReset = PasswordResets.getPasswordResetByToken(token);
        if (passwordReset == null) {
            errors.put("InvalidOrExpiredToken");
            return errorAuth.createErrorResponse(errors, 404);
        }

        // User lekérése
        Users existingUser = Users.getUserById(passwordReset.getUserId().getId());
        if (existingUser == null) {
            errors.put("UserNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        // Jelszó titkosítás
        try {
            String encryptedPassword = Encrypt.encrypt(newPassword);
            existingUser.setPassword(encryptedPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
            errors.put("EncryptionError");
            return errorAuth.createErrorResponse(errors, 500);
        }

        // Jelszó frissítése
        Boolean updateResult = Users.updateUser(existingUser);
        if (!updateResult) {
            errors.put("ServerError");
            return errorAuth.createErrorResponse(errors, 500);
        }

        // Token used = 1
        Boolean tokenUsed = PasswordResets.updatePasswordReset(token);
        if (!tokenUsed) {
            errors.put("TokenUpdateError");
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("success", true);
        toReturn.put("message", "Password Reset Successfully");
        toReturn.put("statusCode", 200);
        return toReturn;
    }

    // softDeletePasswordReset - admin funkció
    public JSONObject softDeletePasswordResetService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (passwordResetsAuth.isDataMissing(id)) {
            errors.put("MissingId");
            return errorAuth.createErrorResponse(errors, 400);
        }

        if (!passwordResetsAuth.isValidId(id)) {
            errors.put("InvalidId");
            return errorAuth.createErrorResponse(errors, 400);
        }

        Boolean result = PasswordResets.softDeletePasswordReset(id);
        if (!result) {
            errors.put("ServerError");
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("success", true);
        toReturn.put("message", "Password Reset Deleted Successfully");
        toReturn.put("statusCode", 200);
        return toReturn;
    }

    public JSONObject resetPasswordService(String token, String newPassword) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (passwordResetsAuth.isDataMissing(token)) {
            errors.put("MissingToken");
        }
        if (passwordResetsAuth.isDataMissing(newPassword)) {
            errors.put("MissingNewPassword");
        }
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        if (!passwordResetsAuth.isValidPassword(newPassword)) {
            errors.put("InvalidPassword");
            return errorAuth.createErrorResponse(errors, 400);
        }

        String encryptedPassword;
        try {
            encryptedPassword = Encrypt.encrypt(newPassword);
            System.err.println("encryptedPassword: " + encryptedPassword); // DEBUG
        } catch (Exception ex) {
            ex.printStackTrace();
            errors.put("EncryptionError");
            return errorAuth.createErrorResponse(errors, 500);
        }

        Integer userId = PasswordResets.resetPassword(token, encryptedPassword);
        System.err.println("resetPassword userId: " + userId); // DEBUG

        if (userId == null) {
            errors.put("InvalidOrExpiredToken");
            return errorAuth.createErrorResponse(errors, 404);
        }

        toReturn.put("message", "Password Reset Successfully");
        toReturn.put("statusCode", 200);
        return toReturn;
    }
}
