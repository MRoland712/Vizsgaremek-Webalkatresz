/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Addresses;
import com.mycompany.vizsgaremek.service.AuthenticationService;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblg
 */
public class AddressService {

    private final AuthenticationService.addressAuth addressAuth = new AuthenticationService.addressAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

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

    public JSONObject getAddressByUserId(Integer id) {
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

    // test 1
    /*
    public JSONObject updateAddress(Addresses updatedAddress) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if no search parameter (id or street or userId) is given
        if (addressAuth.isDataMissing(updatedAddress.getId())
                && addressAuth.isDataMissing(updatedAddress.getStreet())
                && addressAuth.isDataMissing(updatedAddress.getUserId().getId())) {
            errors.put("MissingSearchParameter");
        }

        //if addressId as a search parameter is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getId()) && !addressAuth.isValidId(updatedAddress.getId())) {
            errors.put("InvalidId");
        }

        //if userid as a search parameter is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getUserId()) && !addressAuth.isValidUserId(updatedAddress.getUserId().getId())) {
            errors.put("InvalidUserId");
        }

        //if street as a search parameter is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getStreet()) && !addressAuth.isValidStreet(updatedAddress.getStreet())) {
            errors.put("InvalidStreet");
        }

        if (!addressAuth.isDataMissing(updatedAddress.getStreet())
                && addressAuth.isDataMissing(updatedAddress.getId())
                && addressAuth.isDataMissing(updatedAddress.getUserId().getId())
                && addressAuth.isDataMissing(updatedAddress.getFirstName())
                && addressAuth.isDataMissing(updatedAddress.getLastName())
                && addressAuth.isDataMissing(updatedAddress.getCompany())
                && addressAuth.isDataMissing(updatedAddress.getTaxNumber())
                && addressAuth.isDataMissing(updatedAddress.getCountry())
                && addressAuth.isDataMissing(updatedAddress.getCity())
                && addressAuth.isDataMissing(updatedAddress.getZipCode())
                && addressAuth.isDataMissing(updatedAddress.getIsDefault())) {
            errors.put("InvalidSearchParameter");
        }

        //tempComment       //if zipcode as a search parameter is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getZipCode()) && !addressAuth.isValidZipCode(updatedAddress.getZipCode())) {
            errors.put("InvalidZipCode");
        }
        //error check if Search Parameters are wrong
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Addresses existingAddress = null;
        Object searchData = null;

        // ID alapján keresés
        if (!addressAuth.isDataMissing(updatedAddress.getId())) {
            existingAddress = Addresses.getAddressById(updatedAddress.getId());
            searchData = updatedAddress.getId();
        } else if (!addressAuth.isDataMissing(updatedAddress.getUserId().getId())) {
            // userId alapján keresés
            existingAddress = Addresses.getAddressByUserId(updatedAddress.getUserId().getId());
            searchData = updatedAddress.getUserId(); // User objektum adása hogy lássa az authservice hogy ez alapján kéne kersni :P
        } else {
            //existingAddress = Addresses.getAddressByStreet(updatedAddress.getStreet()); // ToDo: Megcsinálni a getAddressbyStreet-et
            errors.put("StreetAsSearchParamUnavailable"); //temp error handling
        }

        System.out.println("updateAddress existingAddress: " + existingAddress);
        System.out.println("updateAddress existingAddress: " + existingAddress.getCompany());
        //if Address not found
        if (addressAuth.isDataMissing(existingAddress)) {
            errors.put("AddressNotFound");
        }

        //error check if address not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //if Id is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getId()) && addressAuth.isValidId(updatedAddress.getId())) {
            existingAddress.setId(updatedAddress.getId());
        }

        //if street is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getStreet()) && addressAuth.isValidStreet(updatedAddress.getStreet())) {
            existingAddress.setStreet(updatedAddress.getStreet());
        }

        //if userId is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getUserId().getId()) && addressAuth.isValidUserId(updatedAddress.getUserId().getId())) {
            existingAddress.setUserId(updatedAddress.getUserId()); //ToDo: setUserId Normally!
        }

        //if zipCode is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getZipCode()) && addressAuth.isValidZipCode(updatedAddress.getZipCode())) {
            existingAddress.setZipCode(updatedAddress.getZipCode());
        }

        //if firstName is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getFirstName()) && addressAuth.isValidFirstName(updatedAddress.getFirstName())) {
            existingAddress.setFirstName(updatedAddress.getFirstName());
        }

        //if lastName is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getLastName()) && addressAuth.isValidLastName(updatedAddress.getLastName())) {
            existingAddress.setLastName(updatedAddress.getLastName());
        }

        //if company is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getCompany()) && addressAuth.isValidCompany(updatedAddress.getCompany())) {
            existingAddress.setCompany(updatedAddress.getCompany());
        }

        //if taxnumber is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getTaxNumber()) && addressAuth.isValidTaxNumber(updatedAddress.getTaxNumber())) {
            existingAddress.setTaxNumber(updatedAddress.getTaxNumber());
        }

        //if city is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getCity()) && addressAuth.isValidCity(updatedAddress.getCity())) {
            existingAddress.setCity(updatedAddress.getCity());
        }
        //if country is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getCountry()) && addressAuth.isValidCountry(updatedAddress.getCountry())) {
            existingAddress.setCountry(updatedAddress.getCountry());
        }
        //if isDefault is NOT missing AND IS VALID
        if (!addressAuth.isDataMissing(updatedAddress.getIsDefault()) && addressAuth.isValidIsDefault(updatedAddress.getIsDefault())) {
            existingAddress.setIsDefault(updatedAddress.getIsDefault());
        }

        // INVALID DATAS
        //if zipCode is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getZipCode()) && !addressAuth.isValidZipCode(updatedAddress.getZipCode())) {
            errors.put("InvalidZipCode");
        }

        //if firstName is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getFirstName()) && !addressAuth.isValidFirstName(updatedAddress.getFirstName())) {
            errors.put("InvalidFirstName");
        }

        //if lastName is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getLastName()) && !addressAuth.isValidLastName(updatedAddress.getLastName())) {
            errors.put("InvalidLastName");
        }

        //if company is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getCompany()) && !addressAuth.isValidCompany(updatedAddress.getCompany())) {
            errors.put("InvalidCompany");
        }

        //if taxnumber is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getTaxNumber()) && !addressAuth.isValidTaxNumber(updatedAddress.getTaxNumber())) {
            errors.put("InvalidTaxNumber");
        }

        //if city is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getCity()) && !addressAuth.isValidCity(updatedAddress.getCity())) {
            errors.put("InvalidCity");
        }
        //if country is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getCountry()) && !addressAuth.isValidCountry(updatedAddress.getCountry())) {
            errors.put("InvalidCountry");
        }
        //if isDefault is NOT missing AND IS INVALID
        if (!addressAuth.isDataMissing(updatedAddress.getIsDefault()) && !addressAuth.isValidIsDefault(updatedAddress.getIsDefault())) {
            errors.put("InvalidIsDefault");
        }

        
        // check this 
        if (addressAuth.isDataMissing(existingAddress)) {
            errors.put("AddressNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }
        
        // ---
        
        //error check if datas are invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        try {
            Boolean result = Addresses.updateAddress(updatedAddress);

            if (!result) {
                errors.put("ServerError");
            }
        } catch (Exception ex) {
            errors.put("DatabaseError");
            ex.printStackTrace();
        }
        //error check if there is server error
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        return errorAuth.createOKResponse();
    }*/
    public JSONObject updateAddress(Addresses updatedAddress) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK KERESÉSI PARAMÉTEREK 
        // Ha nincs SEMMILYEN keresési paraméter (id VAGY userId)
        if (addressAuth.isDataMissing(updatedAddress.getId())
                && addressAuth.isDataMissing(updatedAddress.getUserId())) {
            errors.put("MissingSearchParameter");
        }

        // Ha addressId mint keresési paraméter NEM hiányzik ÉS ÉRVÉNYTELEN
        if (!addressAuth.isDataMissing(updatedAddress.getId())
                && !addressAuth.isValidId(updatedAddress.getId())) {
            errors.put("InvalidId");
        }

        // Ha userId mint keresési paraméter NEM hiányzik ÉS ÉRVÉNYTELEN
        if (!addressAuth.isDataMissing(updatedAddress.getUserId())
                && !addressAuth.isDataMissing(updatedAddress.getUserId().getId())
                && !addressAuth.isValidUserId(updatedAddress.getUserId().getId())) {
            errors.put("InvalidUserId");
        }

        // Hiba ellenőrzés - keresési paraméterek
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // CÍM LEKÉRDEZÉSE 
        Addresses existingAddress = null;

        // ID alapján keresés
        if (!addressAuth.isDataMissing(updatedAddress.getId())) {
            existingAddress = Addresses.getAddressById(updatedAddress.getId());

        } else if (!addressAuth.isDataMissing(updatedAddress.getUserId())
                && !addressAuth.isDataMissing(updatedAddress.getUserId().getId())) {
            // userId alapján keresés
            existingAddress = Addresses.getAddressByUserId(updatedAddress.getUserId().getId());
        }

        // Ha nem található a cím
        if (addressAuth.isDataMissing(existingAddress)) {
            errors.put("AddressNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        //  MEZŐK MÓDOSÍTÁSA (csak a megadottak!)
        // firstName - CSAK ha meg van adva!
        if (!addressAuth.isDataMissing(updatedAddress.getFirstName())) {
            if (addressAuth.isValidFirstName(updatedAddress.getFirstName())) {
                existingAddress.setFirstName(updatedAddress.getFirstName());
            } else {
                errors.put("InvalidFirstName");
            }
        }

        // lastName CSAK ha meg van adva!
        if (!addressAuth.isDataMissing(updatedAddress.getLastName())) {
            if (addressAuth.isValidLastName(updatedAddress.getLastName())) {
                existingAddress.setLastName(updatedAddress.getLastName());
            } else {
                errors.put("InvalidLastName");
            }
        }

        // company CSAK ha meg van adva!
        if (!addressAuth.isDataMissing(updatedAddress.getCompany())) {
            if (addressAuth.isValidCompany(updatedAddress.getCompany())) {
                existingAddress.setCompany(updatedAddress.getCompany());
            } else {
                errors.put("InvalidCompany");
            }
        }

        // taxNumber CSAK ha meg van adva!
        if (!addressAuth.isDataMissing(updatedAddress.getTaxNumber())) {
            if (addressAuth.isValidTaxNumber(updatedAddress.getTaxNumber())) {
                existingAddress.setTaxNumber(updatedAddress.getTaxNumber());
            } else {
                errors.put("InvalidTaxNumber");
            }
        }

        // country CSAK ha meg van adva!
        if (!addressAuth.isDataMissing(updatedAddress.getCountry())) {
            if (addressAuth.isValidCountry(updatedAddress.getCountry())) {
                existingAddress.setCountry(updatedAddress.getCountry());
            } else {
                errors.put("InvalidCountry");
            }
        }

        // city CSAK ha meg van adva!
        if (!addressAuth.isDataMissing(updatedAddress.getCity())) {
            if (addressAuth.isValidCity(updatedAddress.getCity())) {
                existingAddress.setCity(updatedAddress.getCity());
            } else {
                errors.put("InvalidCity");
            }
        }

        // zipCode CSAK ha meg van adva!
        if (!addressAuth.isDataMissing(updatedAddress.getZipCode())) {
            if (addressAuth.isValidZipCode(updatedAddress.getZipCode())) {
                existingAddress.setZipCode(updatedAddress.getZipCode());
            } else {
                errors.put("InvalidZipCode");
            }
        }

        // street CSAK ha meg van adva!
        if (!addressAuth.isDataMissing(updatedAddress.getStreet())) {
            if (addressAuth.isValidStreet(updatedAddress.getStreet())) {
                existingAddress.setStreet(updatedAddress.getStreet());
            } else {
                errors.put("InvalidStreet");
            }
        }

        // isDefault CSAK ha meg van adva!
        if (!addressAuth.isDataMissing(updatedAddress.getIsDefault())) {
            if (addressAuth.isValidIsDefault(updatedAddress.getIsDefault())) {
                existingAddress.setIsDefault(updatedAddress.getIsDefault());
            } else {
                errors.put("InvalidIsDefault");
            }
        }

        // Hiba ellenőrzés validációk
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // ADATBÁZIS UPDATE 
        try {
            Boolean result = Addresses.updateAddress(existingAddress);

            if (!result) {
                errors.put("ServerError");
            }

        } catch (Exception ex) {
            errors.put("DatabaseError");
            ex.printStackTrace();
        }

        // Hiba ellenőrzés - adatbázis
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // SIKERES VÁLASZ 
        toReturn.put("success", true);
        toReturn.put("message", "Address updated successfully");
        toReturn.put("statusCode", 200);

        return toReturn;
    }
} // Class Closer

