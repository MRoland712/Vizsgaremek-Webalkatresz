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
            JSONObject e = new JSONObject();
            e.put("status", "MissingEmail");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if Username is missing
        if (userAuth.isDataMissing(createdUser.getUsername())) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingUsername");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if Password is missing
        if (userAuth.isDataMissing(createdUser.getPassword())) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingPassword");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if firstName is missing
        if (userAuth.isDataMissing(createdUser.getFirstName())) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingFirstName");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if lastName is missing
        if (userAuth.isDataMissing(createdUser.getLastName())) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingLastName");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if phone is missing
        if (userAuth.isDataMissing(createdUser.getPhone())) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingPhone");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //IF DATAS ARE INVALID
        //if email is invalid
        if (!userAuth.isDataMissing(createdUser.getEmail()) && !userAuth.isValidEmail(createdUser.getEmail())) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidEmail");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if Username is invalid
        if (!userAuth.isDataMissing(createdUser.getUsername()) && !userAuth.isValidUsername(createdUser.getUsername())) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidUsername");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if Password is invalid
        if (!userAuth.isDataMissing(createdUser.getPassword()) && !userAuth.isValidPassword(createdUser.getPassword())) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidPassword");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if firstName is invalid
        if (!userAuth.isDataMissing(createdUser.getFirstName()) && !userAuth.isValidFirstName(createdUser.getFirstName())) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidFirstName");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if lastName is invalid
        if (!userAuth.isDataMissing(createdUser.getLastName()) && !userAuth.isValidLastName(createdUser.getLastName())) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidLastName");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if phone is invalid
        if (!userAuth.isDataMissing(createdUser.getPhone()) && !userAuth.isValidPhone(createdUser.getPhone())) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidPhone");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //If errors has data -> return errors and stop code
        if (errors.length() > 0) {
            toReturn.put("errors", errors);
            return toReturn;
        } else {
            try {
                // set Password to Encrypted version of entered password
                String encryptedPassword = Encrypt.encrypt(createdUser.getPassword());
                createdUser.setPassword(encryptedPassword);

                //Create OTP
                Random random = new Random();
                createdUser.setAuthSecret(
                        Integer.toString(random.nextInt(900000) + 100000)
                );

                if (createdUser.getRole() == null || createdUser.getRole().isEmpty()) {
                    createdUser.setRole(null);
                }

                String token = UUID.randomUUID().toString();
                createdUser.setRegistrationToken(token);

                Boolean modelResult = Users.createUser(createdUser);

                if (modelResult == false) {
                    toReturn.put("status", "InternalServerError");
                    toReturn.put("statusCode", 500);
                    return toReturn;
                }

                toReturn.put("result", modelResult);

            } catch (Exception e) {
                e.printStackTrace();
                toReturn.put("status", "InternalServerError");
                toReturn.put("statusCode", 500);
                return toReturn;
            }
            toReturn.put("status", "success");
            toReturn.put("statusCode", 200);
            toReturn.put("JWTToken", JwtUtil.generateToken(createdUser.getId(), createdUser.getEmail(), createdUser.getRole(), createdUser.getUsername()));
            return toReturn;
        }
    }
//ToDo: checkolni hogy kell-e break a kód közben (úgy mint a updateUser-ben error check)

    public JSONObject ReadUsers() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        ArrayList<Users> modelResult = Users.ReadUsers();

        //If no data in DB
        if (userAuth.isDataMissing(modelResult)) {
            JSONObject e = new JSONObject();
            e.put("status", "ModelExeption");
            e.put("statusCode", 500);
            errors.put(e);
        }

        //If errors has data -> return errors and stop code
        if (errors.length() > 0) {
            toReturn.put("errors", errors);
            return toReturn;
        } else {

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
    }
//ToDo: checkolni hogy kell-e break a kód közben (úgy mint a updateUser-ben error check)
    //ToDo: add validation if User is deleted or is Inactive?? 

    public JSONObject ReadUserById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if id is missing
        if (userAuth.isDataMissing(id)) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingId");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //if id is 0 or lower
        if (userAuth.isValidId(id)) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidId");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //get data from spq
        Users modelResult = Users.ReadUserById(id);

        //if spq gives null data
        if (modelResult == null) {
            JSONObject e = new JSONObject();
            e.put("status", "UserNotFound");
            e.put("statusCode", 404);
            errors.put(e);
        }

        //If errors has data -> return errors and stop code
        if (errors.length() > 0) {
            toReturn.put("errors", errors);
            return toReturn;
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

    public JSONObject ReadUserByEmail(String email) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if email is missing
        if (userAuth.isDataMissing(email)) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingEmail");
            e.put("statusCode", 500);
            errors.put(e);
        }

        if (!userAuth.isValidEmail(email)) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingEmail");
            e.put("statusCode", 500);
            errors.put(e);
        }

        //get data from spq
        Users modelResult = Users.ReadUserByEmail(email);

        //if spq gives null data
        if (modelResult == null) {
            JSONObject e = new JSONObject();
            e.put("status", "UserNotFound");
            e.put("statusCode", 404);
            errors.put(e);
        }

        //If errors has data -> return errors and stop code
        if (errors.length() > 0) {
            toReturn.put("errors", errors);
            return toReturn;
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

    public JSONObject softDeleteUser(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (!userAuth.isDataMissing(id)) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingId");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //If id is Invalid
        if (!userAuth.isValidId(id)) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidId");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //get data from spq
        Users modelResult = Users.ReadUserById(id);

        //if spq gives null data
        if (modelResult == null) {
            JSONObject e = new JSONObject();
            e.put("status", "UserNotFound");
            e.put("statusCode", 404);
            errors.put(e);
        }

        //ToDo: add validation if User is deleted or is Inactive?? 
        Boolean result = Users.softDeleteUser(id);

        if (!result) {
            JSONObject e = new JSONObject();
            e.put("status", "ServerError");
            e.put("statusCode", 500);
            errors.put(e);
        }
        //If errors has data -> return errors and stop code
        if (errors.length() > 0) {
            toReturn.put("errors", errors);
            return toReturn;
        } else {
            toReturn.put("status", "success");
            toReturn.put("statusCode", 200);
            toReturn.put("Message", "Deleted User Succesfully");
            return toReturn;
        }
    }

    public JSONObject updateUser(Users updatedUser) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (userAuth.isDataMissing(updatedUser.getId()) && userAuth.isDataMissing(updatedUser.getEmail())) {
            JSONObject e = new JSONObject();
            e.put("status", "MissingIdAndEmail");
            e.put("statusCode", 400);
            errors.put(e);
        }
        if (!userAuth.isDataMissing(updatedUser.getId()) && !userAuth.isValidId(updatedUser.getId())) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidId");
            e.put("statusCode", 400);
            errors.put(e);
        }
        if (!userAuth.isDataMissing(updatedUser.getEmail()) && !userAuth.isValidEmail(updatedUser.getEmail())) {
            JSONObject e = new JSONObject();
            e.put("status", "InvalidEmail");
            e.put("statusCode", 400);
            errors.put(e);
        }

        //Error check hogy ne lépjen tovább mert csak teledobná sok fölösleges error-al
        if (errors.length() > 0) {
            toReturn.put("errors", errors);
            return toReturn;
        }

        //feltöltjük az alapból létező adatokkal
        Users existingUser = null;
        Object searchData = null;
        if (!userAuth.isDataMissing(updatedUser.getId())) {
            existingUser = Users.ReadUserById(updatedUser.getId());
            searchData = updatedUser.getId();
        } else {
            existingUser = Users.ReadUserByEmail(updatedUser.getEmail());
            searchData = updatedUser.getEmail();
        }

        //ha nem talál usert
        if (existingUser == null) {
            JSONObject e = new JSONObject();
            e.put("status", "UserNotFound");
            e.put("statusCode", 400);
            errors.put(e);
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

            JSONObject e = new JSONObject();
            e.put("status", "InvalidUsername");
            e.put("statusCode", 400);
            errors.put(e);
        }

        if (!userAuth.isDataMissing(updatedUser.getFirstName()) && userAuth.isValidFirstName(updatedUser.getFirstName())) {

            existingUser.setFirstName(updatedUser.getFirstName());

        } else if (!userAuth.isDataMissing(updatedUser.getFirstName()) && !userAuth.isValidFirstName(updatedUser.getFirstName())) {

            JSONObject e = new JSONObject();
            e.put("status", "InvalidFirstName");
            e.put("statusCode", 400);
            errors.put(e);
        }

        if (!userAuth.isDataMissing(updatedUser.getLastName()) && userAuth.isValidFirstName(updatedUser.getLastName())) {

            existingUser.setLastName(updatedUser.getLastName());

        } else if (!userAuth.isDataMissing(updatedUser.getLastName()) && !userAuth.isValidLastName(updatedUser.getLastName())) {

            JSONObject e = new JSONObject();
            e.put("status", "InvalidLastName");
            e.put("statusCode", 400);
            errors.put(e);
        }

        if (!userAuth.isDataMissing(updatedUser.getPhone()) && userAuth.isValidPhone(updatedUser.getPhone())) {

            existingUser.setPhone(updatedUser.getPhone());

        } else if (!userAuth.isDataMissing(updatedUser.getPhone()) && !userAuth.isValidPhone(updatedUser.getPhone())) {

            JSONObject e = new JSONObject();
            e.put("status", "InvalidPhone");
            e.put("statusCode", 400);
            errors.put(e);
        }

        if (!userAuth.isDataMissing(updatedUser.getRole()) && userAuth.isValidRole(updatedUser.getRole())) {

            existingUser.setRole(updatedUser.getRole());

        } else if (!userAuth.isDataMissing(updatedUser.getRole()) && !userAuth.isValidRole(updatedUser.getRole())) {

            JSONObject e = new JSONObject();
            e.put("status", "InvalidRole");
            e.put("statusCode", 400);
            errors.put(e);
        }

        if (!userAuth.isDataMissing(updatedUser.getIsActive()) && userAuth.isValidIsActive(updatedUser.getIsActive())) {

            existingUser.setIsActive(updatedUser.getIsActive());

        } else if (!userAuth.isDataMissing(updatedUser.getIsActive()) && !userAuth.isValidIsActive(updatedUser.getIsActive())) {

            JSONObject e = new JSONObject();
            e.put("status", "InvalidIsActive");
            e.put("statusCode", 400);
            errors.put(e);
        }

        if (!userAuth.isDataMissing(updatedUser.getAuthSecret()) && userAuth.isValidAuthSecret(updatedUser.getAuthSecret())) {

            existingUser.setAuthSecret(updatedUser.getAuthSecret());

        } else if (!userAuth.isDataMissing(updatedUser.getAuthSecret()) && !userAuth.isValidAuthSecret(updatedUser.getAuthSecret())) {

            JSONObject e = new JSONObject();
            e.put("status", "InvalidAuthSecret");
            e.put("statusCode", 400);
            errors.put(e);
        }

        if (!userAuth.isDataMissing(updatedUser.getRegistrationToken()) && userAuth.isValidRegistrationToken(updatedUser.getRegistrationToken())) {

            existingUser.setRegistrationToken(updatedUser.getRegistrationToken());

        } else if (!userAuth.isDataMissing(updatedUser.getRegistrationToken()) && !userAuth.isValidRegistrationToken(updatedUser.getRegistrationToken())) {

            JSONObject e = new JSONObject();
            e.put("status", "InvalidRegistrationToken");
            e.put("statusCode", 400);
            errors.put(e);
        }

        if (!userAuth.isDataMissing(updatedUser.getPassword()) && !userAuth.isValidPassword(updatedUser.getPassword())) {

            JSONObject e = new JSONObject();
            e.put("status", "InvalidPassword");
            e.put("statusCode", 400);
            errors.put(e);

        } else if (!userAuth.isDataMissing(updatedUser.getPassword()) && userAuth.isPasswordSame(updatedUser.getPassword(), searchData)) {
            //Ha a password nem hiányzik és benne van a db-ben már

            JSONObject e = new JSONObject();
            e.put("status", "PasswordInDB");
            e.put("statusCode", 407);
            errors.put(e);
        } else { 
           try {
                String encryptedPassword = Encrypt.encrypt(updatedUser.getPassword());
                existingUser.setPassword(encryptedPassword);
            } catch (Exception ex) {
                JSONObject e = new JSONObject();
                e.put("status", "EncryptionError");
                e.put("statusCode", 500);
                errors.put(e);
                ex.printStackTrace();
            }
        }

        Boolean result = Users.updateUser(existingUser);

        if (!result) {
            JSONObject e = new JSONObject();
            e.put("status", "ServerError");
            e.put("statusCode", 500);
            errors.put(e);
        }
        if (errors.length() > 0) {

            toReturn.put("errors", errors);
            return toReturn;
        } else {

            toReturn.put("result", result);
            toReturn.put("status", "success");
            toReturn.put("statusCode", 200);

            return toReturn;
        }
    }
} // DONT DELETE, THIS IS THE CLASS CLOSER

