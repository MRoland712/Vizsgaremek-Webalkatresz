/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mycompany.vizsgaremek.config.Encrypt;
import com.mycompany.vizsgaremek.config.JwtUtil;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @co-author neblg
 * @author ddori
 */
public class UsersService {

    private final AuthenticationService.userAuth userAuth = new AuthenticationService.userAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    private Users layer = new Users();

    /**
     * commonly used error codes 400 - Bad request (validation error / client
     * sends wrong data) 401 - Unauthorised (Authentication error ex: Wrong
     * password entered) 404 - Missing (ex: Couldnt find user with given data)
     * 409 - Conflict (something is the same as in db ex: Email is same as in
     * DB) 500 - Internal Server Error (Missing required data in DB ex:
     * isDeleted == null)
     */
    //ToDo: checkolni hogy kell-e break a kód közben (úgy mint a updateUser-ben error check)
    public JSONObject createUser(Users createdUser) {
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
        //If errors has data -> return errors and stop code
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
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
            
            //if role is empty / not set
            if (createdUser.getRole() == null || createdUser.getRole().isEmpty()) {
                createdUser.setRole(null);
            }

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
    }
    
    public JSONObject getUsers() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        ArrayList<Users> modelResult = Users.getUsers();

        //If no data in DB
        if (userAuth.isDataMissing(modelResult)) {
            errors.put("ModelExeption");
        }

        //if modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        JSONArray result = new JSONArray();

        for (Users actualUser : modelResult) {
            JSONObject actualUserObject = new JSONObject();

            actualUserObject.put("id", actualUser.getId());
            actualUserObject.put("email", actualUser.getEmail());
            actualUserObject.put("username", actualUser.getUsername());
            actualUserObject.put("email", actualUser.getEmail());
            actualUserObject.put("firstName", actualUser.getFirstName());
            actualUserObject.put("lastName", actualUser.getLastName());
            actualUserObject.put("phone", actualUser.getPhone());
            actualUserObject.put("guid", actualUser.getGuid());
            actualUserObject.put("role", actualUser.getRole());
            actualUserObject.put("isActive", actualUser.getIsActive());
            actualUserObject.put("lastLogin", actualUser.getLastLogin() == null ? null : actualUser.getLastLogin().toString());
            actualUserObject.put("createdAt", actualUser.getCreatedAt() == null ? null : actualUser.getCreatedAt().toString());
            actualUserObject.put("updatedAt", actualUser.getUpdatedAt() == null ? null : actualUser.getUpdatedAt().toString());
            actualUserObject.put("isDeleted", actualUser.getIsDeleted());

            result.put(actualUserObject);
        }

        toReturn.put("result", result);
        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        return toReturn;

    }
    //ToDo: add validation if User is deleted or is Inactive?? 

    public JSONObject getUserById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if id is missing
        if (userAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //if id is invalid
        if (userAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        //error check if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Users modelResult = Users.getUserById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("UserNotFound");
        }

        //error check if user not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        } else {
            JSONObject result = new JSONObject();

            result.put("id", modelResult.getId());
            result.put("guid", modelResult.getGuid());
            result.put("email", modelResult.getEmail());
            result.put("username", modelResult.getUsername());
            result.put("password", modelResult.getPassword());
            result.put("firstName", modelResult.getFirstName());
            result.put("lastName", modelResult.getLastName());
            result.put("phone", modelResult.getPhone());
            result.put("isActive", modelResult.getIsActive());
            result.put("role", modelResult.getRole());
            result.put("createdAt", modelResult.getCreatedAt() == null ? "" : modelResult.getCreatedAt().toString());
            result.put("updateAt", modelResult.getUpdatedAt() == null ? "" : modelResult.getUpdatedAt().toString());
            result.put("lastLogin", modelResult.getLastLogin() == null ? "" : modelResult.getLastLogin().toString());
            result.put("isDeleted", modelResult.getIsDeleted());
            result.put("authSecret", modelResult.getAuthSecret());
            result.put("registrationToken", modelResult.getRegistrationToken());

            toReturn.put("result", result);
            toReturn.put("status", "success");
            toReturn.put("statusCode", 200);
            return toReturn;
        }
    }
//ToDo: checkolni hogy kell-e break a kód közben (úgy mint a updateUser-ben error check)
    //ToDo: add validation if User is deleted or is Inactive?? 

    public JSONObject getUserByEmail(String email) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if email is missing
        if (userAuth.isDataMissing(email)) {
            errors.put("MissingEmail");
        }

        //if email is invalid
        if (!userAuth.isValidEmail(email)) {
            errors.put("MissingEmail");
        }

        //if email is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Users modelResult = Users.getUserByEmail(email);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("UserNotFound");
        }

        //if user is not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        } else {
            JSONObject result = new JSONObject();

            result.put("id", modelResult.getId());
            result.put("guid", modelResult.getGuid());
            result.put("email", modelResult.getEmail());
            result.put("username", modelResult.getUsername());
            result.put("password", modelResult.getPassword());
            result.put("firstName", modelResult.getFirstName());
            result.put("lastName", modelResult.getLastName());
            result.put("phone", modelResult.getPhone());
            result.put("isActive", modelResult.getIsActive());
            result.put("role", modelResult.getRole());
            result.put("createdAt", modelResult.getCreatedAt() == null ? "" : modelResult.getCreatedAt().toString());
            result.put("updateAt", modelResult.getUpdatedAt() == null ? "" : modelResult.getUpdatedAt().toString());
            result.put("lastLogin", modelResult.getLastLogin() == null ? "" : modelResult.getLastLogin().toString());
            result.put("isDeleted", modelResult.getIsDeleted());
            result.put("authSecret", modelResult.getAuthSecret());
            result.put("registrationToken", modelResult.getRegistrationToken());

            toReturn.put("result", result);
            toReturn.put("status", "success");
            toReturn.put("statusCode", 200);
            return toReturn;
        }
    }

    public JSONObject softDeleteUser(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (!userAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!userAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Users modelResult = Users.getUserById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("UserNotFound");
        }

        //if user not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("UserIsSoftDeleted");
        }

        //if user is soft deleted
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Users.softDeleteUser(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted User Succesfully");
        return toReturn;
    }

    public JSONObject updateUser(Users updatedUser) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (userAuth.isDataMissing(updatedUser.getId()) && userAuth.isDataMissing(updatedUser.getEmail())) {
            errors.put("MissingIdAndEmail");
        }
        if (!userAuth.isDataMissing(updatedUser.getId()) && !userAuth.isValidId(updatedUser.getId())) {
            errors.put("InvalidId");
        }
        if (!userAuth.isDataMissing(updatedUser.getEmail()) && !userAuth.isValidEmail(updatedUser.getEmail())) {
            errors.put("InvalidEmail");
        }

        //if email and id is missing OR id or email is invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //feltöltjük az alapból létező adatokkal
        Users existingUser = null;
        Object searchData = null;

        if (!userAuth.isDataMissing(updatedUser.getId())) {
            existingUser = Users.getUserById(updatedUser.getId());
            searchData = updatedUser.getId();
        } else {
            existingUser = Users.getUserByEmail(updatedUser.getEmail());
            searchData = updatedUser.getEmail();
        }

        //ha nem talál usert
        if (existingUser == null) {

            errors.put("UserNotFound");

        }

        //ha nem hiányzik az email ÉS valid az email
        if (!userAuth.isDataMissing(updatedUser.getEmail()) && userAuth.isValidEmail(updatedUser.getEmail())) {

            existingUser.setEmail(updatedUser.getEmail());

        }
        //nem kell több validáció mivel már elöbb lechekkoltuk

        //ha nem hiányzik a Username ÉS valid a Username
        if (!userAuth.isDataMissing(updatedUser.getUsername()) && userAuth.isValidUsername(updatedUser.getUsername())) {

            existingUser.setUsername(updatedUser.getUsername());

        } else if (!userAuth.isDataMissing(updatedUser.getUsername()) && !userAuth.isValidUsername(updatedUser.getUsername())) { //ha nem hiányzik ÉS NEM valid a username

            errors.put("InvalidUsername");

        }

        if (!userAuth.isDataMissing(updatedUser.getFirstName()) && userAuth.isValidFirstName(updatedUser.getFirstName())) {

            existingUser.setFirstName(updatedUser.getFirstName());

        } else if (!userAuth.isDataMissing(updatedUser.getFirstName()) && !userAuth.isValidFirstName(updatedUser.getFirstName())) {

            errors.put("InvalidFirstName");

        }

        if (!userAuth.isDataMissing(updatedUser.getLastName()) && userAuth.isValidFirstName(updatedUser.getLastName())) {

            existingUser.setLastName(updatedUser.getLastName());

        } else if (!userAuth.isDataMissing(updatedUser.getLastName()) && !userAuth.isValidLastName(updatedUser.getLastName())) {

            errors.put("InvalidLastName");

        }

        if (!userAuth.isDataMissing(updatedUser.getPhone()) && userAuth.isValidPhone(updatedUser.getPhone())) {

            existingUser.setPhone(updatedUser.getPhone());

        } else if (!userAuth.isDataMissing(updatedUser.getPhone()) && !userAuth.isValidPhone(updatedUser.getPhone())) {

            errors.put("InvalidPhone");

        }

        if (!userAuth.isDataMissing(updatedUser.getRole()) && userAuth.isValidRole(updatedUser.getRole())) {

            existingUser.setRole(updatedUser.getRole());

        } else if (!userAuth.isDataMissing(updatedUser.getRole()) && !userAuth.isValidRole(updatedUser.getRole())) {

            errors.put("InvalidRole");

        }

        if (!userAuth.isDataMissing(updatedUser.getIsActive()) && userAuth.isValidIsActive(updatedUser.getIsActive())) {

            existingUser.setIsActive(updatedUser.getIsActive());

        } else if (!userAuth.isDataMissing(updatedUser.getIsActive()) && !userAuth.isValidIsActive(updatedUser.getIsActive())) {

            errors.put("InvalidIsActive");

        }

        if (!userAuth.isDataMissing(updatedUser.getAuthSecret()) && userAuth.isValidAuthSecret(updatedUser.getAuthSecret())) {

            existingUser.setAuthSecret(updatedUser.getAuthSecret());

        } else if (!userAuth.isDataMissing(updatedUser.getAuthSecret()) && !userAuth.isValidAuthSecret(updatedUser.getAuthSecret())) {

            errors.put("InvalidAuthSecret");

        }

        if (!userAuth.isDataMissing(updatedUser.getRegistrationToken()) && userAuth.isValidRegistrationToken(updatedUser.getRegistrationToken())) {

            existingUser.setRegistrationToken(updatedUser.getRegistrationToken());

        } else if (!userAuth.isDataMissing(updatedUser.getRegistrationToken()) && !userAuth.isValidRegistrationToken(updatedUser.getRegistrationToken())) {

            errors.put("InvalidRegistrationToken");

        }

        if (!userAuth.isDataMissing(updatedUser.getPassword()) && !userAuth.isValidPassword(updatedUser.getPassword())) {

            errors.put("InvalidPassword");

        } else if (!userAuth.isDataMissing(updatedUser.getPassword()) && userAuth.isPasswordSame(updatedUser.getPassword(), searchData)) {
            //Ha a password nem hiányzik és benne van a db-ben már

            errors.put("PasswordInDB");

        } else {
            try {
                String encryptedPassword = Encrypt.encrypt(updatedUser.getPassword());
                existingUser.setPassword(encryptedPassword);
            } catch (Exception ex) {

                errors.put("EncryptionError");

                ex.printStackTrace();
            }
        }

        Boolean result = Users.updateUser(existingUser);

        if (!result) {

            errors.put("ServerError");

        }
        if (errors.length() > 0) {

            toReturn.put("errorMessage", errors);
            return toReturn;
        } else {

            toReturn.put("result", result);
            toReturn.put("status", "success");
            toReturn.put("statusCode", 200);

            return toReturn;
        }
    }

    public JSONObject loginUser(Users logInUser) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //IF DATA IS MISSING
        if (userAuth.isDataMissing(logInUser.getEmail())) {

            errors.put("MissingEmail");

        }
        if (userAuth.isDataMissing(logInUser.getPassword())) {

            errors.put("MissingPassword");

        }

        //IF DATA IS INVALID
        if (!userAuth.isValidEmail(logInUser.getEmail())) {

            errors.put("InvalidEmail");

        }
        if (!userAuth.isValidPassword(logInUser.getPassword())) {

            errors.put("InvalidPassword");

        }

        //error check
        if (errors.length() > 0) {
            toReturn.put("errorMessage", errors);
            toReturn.put("status", "failed");
            toReturn.put("statusCode", 400);
            return toReturn;
        }

        Users userData = Users.getUserByEmail(logInUser.getEmail());

        if (userAuth.isDataMissing(userData)) {

            errors.put("UserNotFound");

        }

        //error check for is userData missing
        if (errors.length() > 0) {
            toReturn.put("errorMessage", errors);
            toReturn.put("status", "failed");
            toReturn.put("statusCode", 404);
            return toReturn;
        }

        if (userAuth.isUserDeleted(userData.getIsDeleted())) {

            errors.put("UserIsSoftDeleted");

        }

        //error check for is user Deleted
        if (errors.length() > 0) {
            toReturn.put("errorMessage", errors);
            toReturn.put("status", "failed");
            toReturn.put("statusCode", 409);
            return toReturn;
        }

        try {
            if (!userAuth.isPasswordSame(userData.getPassword(), logInUser.getEmail())) {
                errors.put("InvalidPassword");
            }
        } catch (Exception ex) {
            ex.printStackTrace();

            errors.put("EncryptionError");

        }

        //error check for is password same
        if (errors.length() > 0) {
            toReturn.put("errorMessage", errors);
            toReturn.put("status", "failed");
            toReturn.put("statusCode", 401); //unauthorised
            return toReturn;
        }
        //get data from spq
        Boolean modelResult = Users.loginUser(userData);

        //if spq gives null data
        if (userAuth.isDataMissing(modelResult)) {

            errors.put("ModelError");

        }

        //If errors has data -> return errors and stop code
        if (errors.length() > 0) {
            toReturn.put("errorMessage", errors);
            toReturn.put("status", "failed");
            toReturn.put("statusCode", 500);
            return toReturn;
        } else {
            toReturn.put("status", "success");
            toReturn.put("statusCode", 200);
            toReturn.put("JWTToken", JwtUtil.generateToken(userData.getId(), userData.getEmail(), userData.getRole(), userData.getUsername()));
            toReturn.put("Message", "Logged in User Successfully");
            return toReturn;
        }
    }
} // DONT DELETE, THIS IS THE CLASS CLOSER

