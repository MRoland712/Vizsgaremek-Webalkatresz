/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Addresses;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblg
 */
public class AddressService {

    /**
     * commonly used error codes 400 - Bad request (validation error / client
     * sends wrong data) 401 - Unauthorised (Authentication error ex: Wrong
     * password entered) 404 - Missing (ex: Couldnt find user with given data)
     * 409 - Conflict (something is the same as in db ex: Email is same as in
     * DB) 500 - Internal Server Error (Missing required data in DB ex:
     * isDeleted == null)
     */
    //ToDo: checkolni hogy kell-e break a kód közben (úgy mint a updateUser-ben error check)
    /*public JSONObject createAddress(Addresses createAddress) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //IF REQUIRED DATA IS MISSING
        //if email is missing
        
        if (userAuth.isDataMissing(createdUser.getEmail())) {
            errors.put("MissingEmail");
        }

        //if Username is missing
        if (userAuth.isDataMissing(createdUser.getUsername())) {
            errors.put("MissingUsername");
        }

        //if Password is missing
        if (userAuth.isDataMissing(createdUser.getPassword())) {
            errors.put("MissingPassword");
        }

        //if firstName is missing
        if (userAuth.isDataMissing(createdUser.getFirstName())) {
            errors.put("MissingFirstName");
        }

        //if lastName is missing
        if (userAuth.isDataMissing(createdUser.getLastName())) {
            errors.put("MissingLastName");
        }

        //if phone is missing
        if (userAuth.isDataMissing(createdUser.getPhone())) {
            errors.put("MissingPhone");
        }

        //IF DATAS ARE INVALID
        //if email is invalid
        if (!userAuth.isDataMissing(createdUser.getEmail()) && !userAuth.isValidEmail(createdUser.getEmail())) {
            errors.put("InvalidEmail");
        }

        //if Username is invalid
        if (!userAuth.isDataMissing(createdUser.getUsername()) && !userAuth.isValidUsername(createdUser.getUsername())) {
            errors.put("InvalidUsername");
        }

        //if Password is invalid
        if (!userAuth.isDataMissing(createdUser.getPassword()) && !userAuth.isValidPassword(createdUser.getPassword())) {
            errors.put("InvalidPassword");
        }

        //if firstName is invalid
        if (!userAuth.isDataMissing(createdUser.getFirstName()) && !userAuth.isValidFirstName(createdUser.getFirstName())) {
            errors.put("InvalidFirstName");
        }

        //if lastName is invalid
        if (!userAuth.isDataMissing(createdUser.getLastName()) && !userAuth.isValidLastName(createdUser.getLastName())) {
            errors.put("InvalidLastName");
        }

        //if phone is invalid
        if (!userAuth.isDataMissing(createdUser.getPhone()) && !userAuth.isValidPhone(createdUser.getPhone())) {
            errors.put("InvalidPhone");
        }

        //error check if datas given are missing or invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //if the email is existing in DB
        if (userAuth.isEmailSame(createdUser.getEmail())) {
            errors.put("EmailIsSameAsDB");
        }

        //if the username is existing in DB
        if (userAuth.isUsernameSame(createdUser.getUsername())) {
            errors.put("UsernameIsSameAsDB");
        }

        //if the phone number is existing in DB
        if (userAuth.isPhoneSame(createdUser.getPhone())) {
            errors.put("PhoneIsSameAsDB");
        }

        //error check if email is existing in db
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        try {
            // set Password to Encrypted version of entered password
            String encryptedPassword = Encrypt.encrypt(createdUser.getPassword());
            createdUser.setPassword(encryptedPassword);

            //Create OTP
            Random random = new Random();
            createdUser.setAuthSecret(
                    Integer.toString(random.nextInt(900000) + 100000)
            );

            //set role to null for default creation of user
            createdUser.setRole(null);

            String token = UUID.randomUUID().toString();
            createdUser.setRegistrationToken(token);

            Boolean modelResult = Users.createUser(createdUser);

            if (!modelResult) {
                errors.put("ModelError");
            }

        } catch (Exception e) {
            e.printStackTrace();
            errors.put("InternalServerError");
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("JWTToken", JwtUtil.generateToken(createdUser.getId(), createdUser.getEmail(), createdUser.getRole(), createdUser.getUsername()));
        toReturn.put("message", "User Created Succesfully");
        return errorAuth.createOKResponse(toReturn);
    }*/

}
