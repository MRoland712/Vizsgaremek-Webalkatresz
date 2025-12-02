/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Addresses;
import com.mycompany.vizsgaremek.service.AuthenticationService.addressAuth;
import com.mycompany.vizsgaremek.service.AuthenticationService.errorAuth;
import java.util.ArrayList;
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

    public JSONObject getAllAddresses() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Addresses> modelResult = Addresses.getAllAddresses();

        // VALIDÁCIÓ - If no data in DB
        if (addressAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray addressArray = new JSONArray();

        for (Addresses address : modelResult) {
            JSONObject addressObj = new JSONObject();
            addressObj.put("id", address.getId());
            addressObj.put("userId", address.getUserId().getId());
            addressObj.put("firstName", address.getFirstName());
            addressObj.put("lastName", address.getLastName());
            addressObj.put("company", address.getCompany());
            addressObj.put("taxNumber", address.getTaxNumber());
            addressObj.put("country", address.getCountry());
            addressObj.put("city", address.getCity());
            addressObj.put("zipCode", address.getZipCode());
            addressObj.put("street", address.getStreet());
            addressObj.put("isDefault", address.getIsDefault());
            addressObj.put("createdAt", address.getCreatedAt());
            addressObj.put("updatedAt", address.getUpdatedAt());

            addressArray.put(addressObj);
        }

        toReturn.put("success", true);
        toReturn.put("addresses", addressArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }

    public JSONObject getAddressById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - ID hiányzik
        if (addressAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        Addresses address = Addresses.getAddressById(id);

        // Validáció - Nem található
        if (addressAuth.isDataMissing(address)) {
            errors.put("AddressNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        // KONVERZIÓ - Addresses → JSONObject
        JSONObject addressObj = new JSONObject();
        addressObj.put("id", address.getId());
        addressObj.put("userId", address.getUserId().getId());
        addressObj.put("firstName", address.getFirstName());
        addressObj.put("lastName", address.getLastName());
        addressObj.put("company", address.getCompany());
        addressObj.put("taxNumber", address.getTaxNumber());
        addressObj.put("country", address.getCountry());
        addressObj.put("city", address.getCity());
        addressObj.put("zipCode", address.getZipCode());
        addressObj.put("street", address.getStreet());
        addressObj.put("isDefault", address.getIsDefault());
        addressObj.put("createdAt", address.getCreatedAt());
        addressObj.put("updatedAt", address.getUpdatedAt());

        toReturn.put("success", true);
        toReturn.put("address", addressObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }
    
    public JSONObject softDeleteAddress(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (addressAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!addressAuth.isDataMissing(id) && !addressAuth.isValidId(id)) {  // Csak ha NEM missing!
        errors.put("InvalidId");
    }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Addresses modelResult = Addresses.getAddressById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("AddressNotFound");
        }

        //if address not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("UserIsSoftDeleted");
        }

        //if address is soft deleted
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Addresses.softDeleteAddress(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Address Succesfully");
        return toReturn;
    }
    
    /*public JSONObject updateAddress(Addresses updatedAddress) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if no search parameter (id or street) is given
        if (addressAuth.isDataMissing(updatedAddress.getId()) && addressAuth.isDataMissing(updatedAddress.getEmail())) {
            errors.put("MissingSearchParameter");
        }

        //if userid as a search parameter is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getId()) && !addressAuth.isValidId(updatedAddress.getId())) {
            errors.put("InvalidId");
        }

        //if street as a search parameter is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getEmail()) && !addressAuth.isValidEmail(updatedAddress.getEmail())) {
            errors.put("InvalidStreet");
        }
        
        //if zipcode as a search parameter is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getEmail()) && !addressAuth.isValidEmail(updatedAddress.getEmail())) {
            errors.put("InvalidZipCode");
        }

        //error check if Search Parameters are wrong
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Addresses existingUser = null;
        Object searchData = null;

        //if id or street or zipCode is set in the queryparam, set it as the search parameter and find the user via the given search parameter
        if (!addressAuth.isDataMissing(updatedAddress.getId())) {
            existingUser = Addresses.getUserById(updatedAddress.getId());
            searchData = updatedAddress.getId();
        } else {
            existingUser = Addresses.getUserByEmail(updatedAddress.getEmail());
            searchData = updatedAddress.getEmail();
        }

        //if Address not found
        if (addressAuth.isDataMissing(existingUser)) {
            errors.put("UserNotFound");
        }

        //error check if user not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        //if email is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getEmail()) && addressAuth.isValidEmail(updatedAddress.getEmail())) {
            existingUser.setEmail(updatedAddress.getEmail());
        }

        //if username is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getUsername()) && addressAuth.isValidUsername(updatedAddress.getUsername())) {
            existingUser.setUsername(updatedAddress.getUsername());
        }

        //if firstName is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getFirstName()) && addressAuth.isValidFirstName(updatedAddress.getFirstName())) {
            existingUser.setFirstName(updatedAddress.getFirstName());
        }

        //if lastName is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getLastName()) && addressAuth.isValidFirstName(updatedAddress.getLastName())) {
            existingUser.setLastName(updatedAddress.getLastName());
        }

        //if phone is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getPhone()) && addressAuth.isValidPhone(updatedAddress.getPhone())) {
            existingUser.setPhone(updatedAddress.getPhone());
        }

        //if isActive is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getIsActive()) && userAuth.isValidIsActive(updatedUser.getIsActive())) {
            existingUser.setIsActive(updatedAddress.getIsActive());
        }

        //if authSecret is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getAuthSecret()) && userAuth.isValidAuthSecret(updatedUser.getAuthSecret())) {
            existingUser.setAuthSecret(updatedAddress.getAuthSecret());
        }

        //if registrationToken is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getRegistrationToken()) && userAuth.isValidRegistrationToken(updatedUser.getRegistrationToken())) {
            existingUser.setRegistrationToken(updatedAddress.getRegistrationToken());
        }

        //if username is NOT missing AND is NOT VALID
        if (!addressAuth.isDataMissing(updatedAddress.getUsername()) && !userAuth.isValidUsername(updatedUser.getUsername())) {
            errors.put("InvalidUsername");
        }

        //if firstName is NOT missing AND is NOT VALID
        if (!addressAuth.isDataMissing(updatedAddress.getFirstName()) && !userAuth.isValidFirstName(updatedUser.getFirstName())) {
            errors.put("InvalidFirstName");
        }

        //if lastName is NOT missing AND is NOT VALID
        if (!addressAuth.isDataMissing(updatedAddress.getLastName()) && !userAuth.isValidLastName(updatedUser.getLastName())) {
            errors.put("InvalidLastName");
        }

        //if phone is NOT missing AND is NOT VALID
        if (!addressAuth.isDataMissing(updatedAddress.getPhone()) && !userAuth.isValidPhone(updatedUser.getPhone())) {
            errors.put("InvalidPhone");
        }

        //if isActive is NOT missing AND is NOT VALID
        if (!addressAuth.isDataMissing(updatedAddress.getIsActive()) && !userAuth.isValidIsActive(updatedUser.getIsActive())) {
            errors.put("InvalidIsActive");
        }

        //if authSecret is NOT missing AND is NOT VALID
        if (!addressAuth.isDataMissing(updatedAddress.getAuthSecret()) && !userAuth.isValidAuthSecret(updatedUser.getAuthSecret())) {
            errors.put("InvalidAuthSecret");
        }

        //error check if datas are invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        return errorAuth.createOKResponse();
    }*/
} // Class Closer

