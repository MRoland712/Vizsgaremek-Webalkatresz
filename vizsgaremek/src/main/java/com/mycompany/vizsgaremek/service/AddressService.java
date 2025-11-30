/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Addresses;
import com.mycompany.vizsgaremek.service.AuthenticationService.addressAuth;
import com.mycompany.vizsgaremek.service.AuthenticationService.errorAuth;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblg
 */
public class AddressService {

    private final addressAuth addressAuth = new addressAuth();

    /**
     * commonly used error codes 400 - Bad request (validation error / client
     * sends wrong data) 401 - Unauthorised (Authentication error ex: Wrong
     * password entered) 404 - Missing (ex: Couldnt find user with given data)
     * 409 - Conflict (something is the same as in db ex: Email is same as in
     * DB) 500 - Internal Server Error (Missing required data in DB ex:
     * isDeleted == null)
     */
    //ToDo: checkolni hogy kell-e break a kód közben (úgy mint a updateUser-ben error check)
// AddressService.java
    public JSONObject createAddress(Addresses createAddress) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (addressAuth.isDataMissing(createAddress.getFirstName())) {
            errors.put("MissingFirstName");
        }

        if (addressAuth.isDataMissing(createAddress.getLastName())) {
            errors.put("MissingLastName");
        }

        if (!addressAuth.isDataMissing(createAddress.getFirstName()) && !addressAuth.isValidFirstName(createAddress.getFirstName())) {
            errors.put("InvalidFirstName");
        }

        if (!addressAuth.isDataMissing(createAddress.getLastName()) && !addressAuth.isValidLastName(createAddress.getLastName())) {
            errors.put("InvalidLastName");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (Addresses.createAddress(createAddress)) {  // ← Static metódus hívás!
            toReturn.put("message", "Address Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Address Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // createAddress Closer
} // Class Closer

