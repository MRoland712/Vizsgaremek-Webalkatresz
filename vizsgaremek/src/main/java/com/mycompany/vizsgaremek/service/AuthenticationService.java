/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.config.Encrypt;
import com.mycompany.vizsgaremek.model.Addresses;
import com.mycompany.vizsgaremek.model.Manufacturers;
import com.mycompany.vizsgaremek.model.PartVariants;
import com.mycompany.vizsgaremek.model.Parts;
import io.jsonwebtoken.Claims;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.mail.Address;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ddori
 */
public class AuthenticationService {

    public static class errorAuth {

        /**
         * Checks if given JSONArray has data and returns a Boolean
         *
         * @param errors The JSONArray that needs to be checked
         * @return Boolean true or false based on if the errors JSONArray has
         * data
         */
        public static boolean hasErrors(JSONArray errors) {
            return errors.length() > 0;
        }

        /**
         * Creates an JSONObject error response with the given JSONArray errors
         * and status code
         *
         * @param errors The JSONArray that contains the errors
         *
         * @param statusCode An integer value with a given status code, see
         * common error codes: 400 - Bad request (validation error / client
         * sends wrong data) 401 - Unauthorised (Authentication error ex: Wrong
         * password entered) 404 - Missing (ex:Couldnt find user with given
         * data) 409 - Conflict (something is the same as in db ex: Email is
         * same as in DB) 500 - Internal Server Error (Missing required data in
         * DB ex: isDeleted == null)
         *
         * @return a JSONObject with the errors array, a status of "failed" and
         * the given status code in this format: { "errors": [#errors#],
         * "status": "failed", "statusCode": #Given status code# }
         *
         */
        public static JSONObject createErrorResponse(JSONArray errors, int statusCode) {
            JSONObject response = new JSONObject();
            response.put("errors", errors);
            response.put("status", "failed");
            response.put("statusCode", statusCode);
            return response;
        }
        
        /**
         * Creates an JSONArray OK response with the given JSONArray result data
         *
         * @param result The JSONArray that contains the result data
         *
         * @return a JSONArray with the result JSONArray as a "result" a status
         * of "success" and a "statusCode" of 200 in this format { "result": [ {
         * #result data# } ], "status": "success", "statusCode": 200 }
         */
        public static JSONObject createOKResponse(JSONArray result) {
            JSONObject response = new JSONObject();
            response.put("result", result);
            response.put("status", "success");
            response.put("statusCode", 200);
            return response;
        }

        /**
         * Creates an JSONObject OK response with the given JSONObject result
         * data
         *
         * @param result The JSONObject that contains the result data
         *
         * @return a JSONObject with the result JSONObject as a "result" a
         * status of "success" and a "statusCode" of 200 in this format {
         * "result": [ { #result data# } ], "status": "success", "statusCode":
         * 200 }
         */
        public static JSONObject createOKResponse(JSONObject result) {
            JSONObject response = new JSONObject();
            response.put("result", result);
            response.put("status", "success");
            response.put("statusCode", 200);
            return response;
        }
        
        /**
         * Creates an JSONObject OK response with the given JSONObject result
         * data
         *
         * @param result The JSONObject that contains the result data
         *
         * @return a JSONObject with the result JSONObject as a "result" a
         * status of "success" and a "statusCode" of 200 in this format {
         * "result": [ { #result data# } ], "status": "success", "statusCode":
         * 200 }
         */
        public static JSONObject createOKResponse(Claims result) {
            JSONObject response = new JSONObject();
            response.put("result", result);
            response.put("status", "success");
            response.put("statusCode", 200);
            return response;
        }

        /**
         * Creates an OK response
         *
         * @return a JSONObject with a status of "success" and a "statusCode" of
         * 200 in this format
         *
         * { "status": "success", "statusCode": 200 }
         */
        public static JSONObject createOKResponse() {
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("statusCode", 200);
            return response;
        }

    }//Class closer

    public static class JWTAuth {

        public boolean isDataMissing(String data) {
            return data.trim().isEmpty() || data == null;
        }
    }

    public static class userAuth {

        private static final Pattern EMAIL_PATTERN
                = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

        // Legalább 8 karakter, tartalmaz nagybetűt, számot és speciális karaktert
        private static final Pattern PASSWORD_PATTERN
                = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

        public boolean isDataMissing(String data) {
            return (data == null || data.trim().isEmpty());
        }

        public boolean isDataMissing(Integer data) {
            return (data == null);
        }

        public boolean isDataMissing(ArrayList<Users> data) {
            return (data == null || data.isEmpty());
        }

        public boolean isDataMissing(Boolean data) {
            return (data == null);
        }

        public boolean isDataMissing(Users data) {
            return (data == null);
        }

        public boolean isDataMissing(List<Object[]> data) {
            return (data == null || data.isEmpty());
        }

        //ToDO: add a try catch for each isValid method 💀💀 vagy megnezni azt hogy object instanceog <x>
        public boolean isValidId(Integer id) {
            return id > 0 && id.toString().length() <= 11;
        }

        public boolean isValidEmail(String email) {
            return EMAIL_PATTERN.matcher(email).matches();
        }

        public boolean isValidPassword(String password) {
            return PASSWORD_PATTERN.matcher(password).matches();
        }

        //ToDo: is username in db? 
        public boolean isValidUsername(String username) {
            return username.length() <= 30 && username.length() >= 3;
        }

        public boolean isValidFirstName(String firstName) {
            return firstName.length() <= 50;
        }

        public boolean isValidLastName(String lastName) {
            return lastName.length() <= 50;
        }

        public boolean isValidPhone(String phone) {
            return phone.length() <= 50;
        }

        public boolean isValidRole(String role) {
            return role.length() <= 20;
        }

        public boolean isValidIsActive(Boolean isActive) {
            return isActive instanceof Boolean;
        }

        public boolean isValidAuthSecret(String authSecret) {
            try {
                return authSecret.length() <= 255;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        public boolean isValidRegistrationToken(String regToken) {
            try {
                return regToken.length() <= 255;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }

        public boolean isValidIsSubscribed(Boolean isSubscribed) {
            return true; //ToDo: create Authentication for a boolean :O
        }

        public boolean isUserDeleted(Boolean isDeleted) {
            return (isDeleted == true);
        }

        /**
         * Checks if the plain password is the same as the Encrypted password in
         * the DB
         *
         * @param password The password that needs to be checked
         * @param userId Users Id
         * @return true / false based if the password is the same as the one in
         * the db
         * @throws Exception If somehow one of the methods in the return went
         * wrong
         */
        public boolean isPasswordSame(String password, Integer userId) {
            Users userdata = Users.getUserById(userId);
            if (userdata == null) {
                System.err.println("isPasswordSame: Could not find user via id");
            }
            try {
                //debug:
                /*System.out.println("isPasswordSame:");
            System.out.println(userdata.getPassword() + " == " + Encrypt.encrypt(password) + " (" + password + ")");*/
                return userdata.getPassword().equals(Encrypt.encrypt(password));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Checks if the plain password is the same as the Encrypted password in
         * the DB
         *
         * @param password The password that needs to be checked
         * @param email User Email
         * @return true / false based if the password is the same as the one in
         * the db
         * @throws Exception and returns false
         */
        public boolean isPasswordSame(String password, String email) {
            Users userdata = Users.getUserByEmail(email);
            if (userdata == null) {
                System.err.println("isPasswordSame: Could not find user via email");
            }
            try {
                //debug:
                /*System.out.println("isPasswordSame:");
                System.out.println(userdata.getPassword() + " == " + Encrypt.encrypt(password) + " (" + password + ")");*/
                return userdata.getPassword().equals(Encrypt.encrypt(password));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Checks if the searchCriteria is Integer (id) or String (email) and
         * runs the coresponding method
         *
         * @param password The password that needs to be checked
         * @param searchCriteria The Object that needs to be checked
         *
         * @return true / false based if the password is the same as the one in
         * the db
         * @throws IlleagalArgumentExeption with message of "Not acceptable
         * search criteria: {Object's type that is not supported} "
         */
        public boolean isPasswordSame(String password, Object searchCriteria) {
            if (searchCriteria instanceof Integer) {
                return isPasswordSame(password, (Integer) searchCriteria);
            } else if (searchCriteria instanceof String) {
                return isPasswordSame(password, (String) searchCriteria);
            } else {
                throw new IllegalArgumentException("Not acceptable search criteria: " + (searchCriteria != null ? searchCriteria.getClass().getName() : "null"));
            }
        }

        /**
         * Checks if email is existing in DB
         *
         * @param email Email that needs checking
         *
         * @return true / false based if the email is existing in DB
         */
        public boolean isEmailSame(String email) {
            Users userdata = Users.getUserByEmail(email);
            //if user is not found, return false
            if (userdata == null) {
                return false;
            }
            return true;
        }

        /**
         * Checks if username is existing in DB
         *
         * @param username Username that needs checking
         *
         * @return true / false based if the username is existing in DB
         */
        public boolean isUsernameSame(String username) {
            ArrayList<Users> users = Users.getUsers();
            //if user is not found, return false
            for (Users user : users) {
                if (user.getUsername().equals(username)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Checks if phone is existing in DB
         *
         * @param phone phone number that needs checking
         *
         * @return true / false based if the phone is existing in DB
         */
        public boolean isPhoneSame(String phone) {
            ArrayList<Users> users = Users.getUsers();
            //if user is not found, return false
            for (Users user : users) {
                if (user.getPhone().equals(phone)) {
                    return true;
                }
            }
            return false;
        }

    } //User Auth Class closer

    //ADDRESS 
    public static class addressAuth {

        public boolean isDataMissing(String data) {
            return (data == null || data.trim().isEmpty());
        }

        public boolean isDataMissing(Users data) {
            return (data == null);
        }

        public boolean isDataMissing(Integer data) {
            return (data == null);
        }

        public boolean isDataMissing(ArrayList<Addresses> data) {
            return (data == null || data.isEmpty());
        }

        public boolean isDataMissing(Boolean data) {
            return (data == null);
        }

        public boolean isDataMissing(Address data) {
            return (data == null);
        }

        public boolean isDataMissing(List<Object[]> data) {
            return (data == null || data.isEmpty());
        }

        public boolean isValidId(Integer id) {
            return id > 0 && id.toString().length() <= 11;
        }

        public boolean isValidUserId(Users user) {
            Integer userId = user.getId();
            return userId > 0 && userId.toString().length() <= 11;
        }

        public boolean isValidUserId(Integer userId) {
            return userId > 0 && userId.toString().length() <= 11;
        }

        public boolean isValidFirstName(String firstName) {
            return firstName.length() <= 50;
        }

        public boolean isValidLastName(String lastName) {
            return lastName.length() <= 50;
        }

        public boolean isValidCompany(String company) {
            return company.length() <= 50;
        }

        public boolean isValidTaxNumber(String taxNumber) {
            return taxNumber.length() <= 50;
        }

        public boolean isValidZipCode(String zipCode) {
            return zipCode.length() <= 50;
        }

        public boolean isValidStreet(String street) {
            return street.length() <= 50;
        }

        public boolean isValidCity(String city) {
            return city.length() <= 50;
        }

        public boolean isValidCountry(String country) {
            return country.length() <= 50;
        }

        public boolean isValidIsDefault(Boolean isDefault) {
            return isDefault instanceof Boolean;
        }

        public boolean isAddressDeleted(Boolean isDeleted) {
            return (isDeleted == true);
        }

        public boolean isDataMissing(Addresses data) {
            return (data == null);
        }

    } //Address Auth Class closer

    public static class userLogsAuth {

        public boolean isDataMissing(Integer data) {
            return data == null;
        }

        public boolean isDataMissing(String data) {
            return data == null || data.trim().isEmpty();
        }

        public boolean isDataMissing(Boolean data) {
            return data == null;
        }
        
        public boolean isDataMissing(JSONObject data) {
            return data.isEmpty() || data == null;
        }

        public boolean isValidId(Integer id) {
            return id != null && id > 0;
        }

        public boolean isValidUserId(Integer userId) {
            return userId != null && userId > 0;
        }

        public boolean isValidAction(String action) {
            if (action == null) {
                return false;
            }
            return action.length() >= 3 && action.length() <= 255;
        }

        public boolean isValidDetail(String details) {
            if (details == null || details.trim().isEmpty()) {
                return true;
            }
            return details.length() <= 5000;
        }
    }//userLogs closer

    //Parts
    public static class partsAuth {

        public boolean isDataMissing(String data) {
            return (data == null || data.trim().isEmpty());
        }

        public boolean isDataMissing(Manufacturers data) {
            return (data == null);
        }

        public boolean isDataMissing(Integer data) {
            return (data == null);
        }

        public boolean isDataMissing(ArrayList<Parts> data) {
            return (data == null || data.isEmpty());
        }

        public boolean isDataMissing(Boolean data) {
            return (data == null);
        }

        public boolean isDataMissing(Parts data) {
            return (data == null);
        }

        public boolean isDataMissing(BigDecimal data) {
            return (data == null);
        }

        public boolean isDataMissing(List<Object[]> data) {
            return (data == null || data.isEmpty());
        }

        public boolean isValidId(Integer id) {
            return id > 0 && id.toString().length() <= 11;
        }

        public boolean isValidManufacturerId(Manufacturers manufacturer) {
            Integer manufacturerId = manufacturer.getId();
            return manufacturerId > 0 && manufacturerId.toString().length() <= 11;
        }

        public boolean isValidManufacturerId(Integer manufacturerId) {
            return manufacturerId > 0 && manufacturerId.toString().length() <= 11;
        }

        public boolean isValidSku(String sku) {
            return sku.length() <= 50;
        }

        public boolean isValidName(String name) {
            return name.length() <= 50;
        }

        public boolean isValidCategory(String category) {
            return category.length() <= 50;
        }

        //BigDecimal 
        public boolean isValidPrice(BigDecimal price) {
            return price.compareTo(new BigDecimal("0.00")) > 0;
        }

        //Integer
        public boolean isValidStock(Integer stock) {
            return stock > 0 && stock.toString().length() <= 11;
        }

        public boolean isValidStatus(String status) {
            return status.length() <= 50;
        }

        // we will have to talk about this
        public boolean isValidActive(Boolean isActive) {
            return isActive instanceof Boolean;
        }

        public boolean isPartsDeleted(Boolean isDeleted) {
            return (isDeleted == true);
        }

        public boolean isDataMissing(Addresses data) {
            return (data == null);
        }

    } //Parts Auth Class closer

    public static class manufacturersAuth {

        public boolean isDataMissing(String data) {
            return (data == null || data.trim().isEmpty());
        }

        public boolean isDataMissing(Manufacturers data) {
            return (data == null);
        }

        public boolean isDataMissing(Integer data) {
            return (data == null);
        }

        public boolean isDataMissing(ArrayList<Manufacturers> data) {
            return (data == null || data.isEmpty());
        }

        public boolean isDataMissing(Boolean data) {
            return (data == null);
        }

        public boolean isDataMissing(List<Object[]> data) {
            return (data == null || data.isEmpty());
        }

        public boolean isValidId(Integer id) {
            return id > 0 && id.toString().length() <= 11;
        }

        public boolean isValidName(String name) {
            return name.length() <= 50;
        }

        public boolean isValidCountry(String name) {
            return name.length() <= 50;
        }

        public boolean isManufacturersDeleted(Boolean isDeleted) {
            return (isDeleted == true);
        }

    } //Manufacturers Auth Class closer

    public static class partvariantsAuth {

        public boolean isDataMissing(String data) {
            return (data == null || data.trim().isEmpty());
        }

        public boolean isDataMissing(PartVariants data) {
            return (data == null);
        }

        public boolean isDataMissing(Integer data) {
            return (data == null);
        }

        public boolean isDataMissing(ArrayList<PartVariants> data) {
            return (data == null || data.isEmpty());
        }

        public boolean isDataMissing(BigDecimal data) {
            return (data == null);
        }

        public boolean isValidAdditionalPrice(BigDecimal price) {
            return price.compareTo(new BigDecimal("0.00")) > 0;
        }

        public boolean isValidPartsId(Parts parts) {
            Integer partsId = parts.getId();
            return partsId > 0 && partsId.toString().length() <= 11;
        }

        public boolean isValidPartsId(Integer partsId) {
            return partsId > 0 && partsId.toString().length() <= 11;
        }

        public boolean isDataMissing(Boolean data) {
            return (data == null);
        }

        public boolean isDataMissing(List<Object[]> data) {
            return (data == null || data.isEmpty());
        }

        public boolean isValidId(Integer id) {
            return id > 0 && id.toString().length() <= 11;
        }

        public boolean isValidName(String name) {
            return name.length() <= 50;
        }

        public boolean isValidValue(String name) {
            return name.length() <= 50;
        }

    } //PartsVariants Auth Class closer

}//Auth Service Class closer

