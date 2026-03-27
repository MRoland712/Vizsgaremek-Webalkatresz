/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.model.EmailVerifications;
import com.mycompany.vizsgaremek.config.SendEmail;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ddori
 */
public class EmailVerificationsService {

    private final AuthenticationService.emailVerificationsAuth emailAuth = new AuthenticationService.emailVerificationsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createEmailVerificationAndSendEmailService(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (emailAuth.isDataMissing(userId)) {
            errors.put("MissingUserId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        if (!emailAuth.isValidUserId(userId)) {
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
            return errorAuth.createErrorResponse(errors, 404);
        }

        String token = UUID.randomUUID().toString();

        EmailVerifications createdEmailVerification = new EmailVerifications();

        Users userIdUserObject = new Users(userData.getId());

        createdEmailVerification.setUserId(userIdUserObject);
        createdEmailVerification.setToken(token);

        // MODEL HÍVÁS
        if (EmailVerifications.createEmailVerification(createdEmailVerification)) {
            System.out.println("Created Email Verification For " + userData.getUsername());

        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Email Verification Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }

        try {
            SendEmail.sendEmailVerificationEmail(userData.getEmail(), userData.getLastName(), token);
            toReturn.put("message", "Email Verification has been created and email was sent successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
        } catch (MessagingException ex) {
            System.err.println("Failed to send VerificationEmail:" + ex);

            JSONObject error = new JSONObject();
            error.put("message", "Failed to send Verification Email");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
        return toReturn;

    }

    public JSONObject verifyEmailVerificationService(String Token) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (emailAuth.isDataMissing(Token)) {
            errors.put("MissingToken");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }
        
        Boolean result = EmailVerifications.verifyEmailVerification(Token);

        if (result) {
            toReturn.put("statusCode", 200);
            toReturn.put("success", true);
        } else {
            toReturn.put("statusCode", 409);
            toReturn.put("success", true);
        }
        
        return toReturn;
    }
}
