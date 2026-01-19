/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.model.UserLogs;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mycompany.vizsgaremek.config.Encrypt;
import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.service.AuthenticationService;
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
        } catch (Exception e) {
            e.printStackTrace();
            errors.put("InternalServerError");
            return errorAuth.createErrorResponse(errors, 500);
        }

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

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        UserLogs createdUserLog = new UserLogs();
        createdUserLog.setAction("createUser");
        createdUserLog.setDetails("User " + createdUser.getUsername() + " registered.");

        Boolean userLog = UserLogs.createUserLogs(createdUserLog, Users.getUserByEmail(createdUser.getEmail()).getId());

        if (!userLog) {
            errors.put("UserLogError");
        }

        if (errorAuth.hasErrors(errors)) {
            toReturn.put("message", "Failed To Log User Action");
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
            actualUserObject.put("isSubscribed", actualUser.getIsSubscribed());

            result.put(actualUserObject);
        }

        return errorAuth.createOKResponse(result);

    }

    public JSONObject getUserById(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if id is missing
        if (userAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //if id is invalid
        if (!userAuth.isValidId(id) && !userAuth.isDataMissing(id)) {
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
        }
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
        result.put("isSubscribed", modelResult.getIsSubscribed());
        result.put("role", modelResult.getRole());
        result.put("createdAt", modelResult.getCreatedAt() == null ? "" : modelResult.getCreatedAt().toString());
        result.put("updateAt", modelResult.getUpdatedAt() == null ? "" : modelResult.getUpdatedAt().toString());
        result.put("lastLogin", modelResult.getLastLogin() == null ? "" : modelResult.getLastLogin().toString());
        result.put("isDeleted", modelResult.getIsDeleted());
        result.put("authSecret", modelResult.getAuthSecret());
        result.put("registrationToken", modelResult.getRegistrationToken());

        return errorAuth.createOKResponse(result);
    }

    public JSONObject getUserByEmail(String email) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if email is missing
        if (userAuth.isDataMissing(email)) {
            errors.put("MissingEmail");
        }

        //if email is invalid
        if (!userAuth.isValidEmail(email) && !userAuth.isDataMissing(email)) {
            errors.put("InvalidEmail");
        }

        //if email is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        Users modelResult = Users.getUserByEmail(email);

        //if spq gives null data
        if (userAuth.isDataMissing(modelResult)) {
            errors.put("UserNotFound");
        }

        //if user is not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }
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
        result.put("isSubscribed", modelResult.getIsSubscribed());
        result.put("role", modelResult.getRole());
        result.put("createdAt", modelResult.getCreatedAt() == null ? "" : modelResult.getCreatedAt().toString());
        result.put("updateAt", modelResult.getUpdatedAt() == null ? "" : modelResult.getUpdatedAt().toString());
        result.put("lastLogin", modelResult.getLastLogin() == null ? "" : modelResult.getLastLogin().toString());
        result.put("isDeleted", modelResult.getIsDeleted());
        result.put("authSecret", modelResult.getAuthSecret());
        result.put("registrationToken", modelResult.getRegistrationToken());

        return errorAuth.createOKResponse(result);

    }

    public JSONObject softDeleteUser(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (userAuth.isDataMissing(id)) {
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
        if (userAuth.isDataMissing(modelResult)) {
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
        System.out.println("softDeleteUser result:"+ result);
        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        UserLogs createdUserLog = new UserLogs();
        createdUserLog.setAction("softDeleteUser");
        createdUserLog.setDetails("User " + modelResult.getUsername() + " Has deleted their account. ");

        Boolean userLog = UserLogs.createUserLogs(createdUserLog, modelResult.getId());

        if (!userLog) {
            errors.put("UserLogError");
        }

        if (errorAuth.hasErrors(errors)) {
            toReturn.put("warning", "Failed To Log User Action");
            return errorAuth.createOKResponse(toReturn);
        }
        
        return errorAuth.createOKResponse();
    }

    public JSONObject updateUser(Users updatedUser) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //if no search parameter (id or email) is given
        if (userAuth.isDataMissing(updatedUser.getId()) && userAuth.isDataMissing(updatedUser.getEmail())) {
            errors.put("MissingSearchParameter");
        }

        //if email is the only parameter given in body
        if (userAuth.isDataMissing(updatedUser.getId())
                && !userAuth.isDataMissing(updatedUser.getEmail())
                && userAuth.isDataMissing(updatedUser.getUsername())
                && userAuth.isDataMissing(updatedUser.getFirstName())
                && userAuth.isDataMissing(updatedUser.getLastName())
                && userAuth.isDataMissing(updatedUser.getPhone())
                && userAuth.isDataMissing(updatedUser.getIsActive())
                && userAuth.isDataMissing(updatedUser.getPassword())
                && userAuth.isDataMissing(updatedUser.getAuthSecret())
                && userAuth.isDataMissing(updatedUser.getRegistrationToken())
                && userAuth.isDataMissing(updatedUser.getIsSubscribed())) {

            errors.put("InvalidSearchParameter");
        }

        //if id as a search parameter is NOT missing AND IS INVALID
        if (!userAuth.isDataMissing(updatedUser.getId()) && !userAuth.isValidId(updatedUser.getId())) {
            errors.put("InvalidId");
        }

        //if email as a search parameter is NOT missing AND IS INVALID
        if (!userAuth.isDataMissing(updatedUser.getEmail()) && !userAuth.isValidEmail(updatedUser.getEmail())) {
            errors.put("InvalidEmail");
        }

        //error check if Search Parameters are wrong
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Users existingUser = null;
        Object searchData = null;

        //if id or email is set in the queryparam, set it as the search parameter and find the user via the given search parameter
        if (!userAuth.isDataMissing(updatedUser.getId())) {
            existingUser = Users.getUserById(updatedUser.getId());
            searchData = updatedUser.getId();
        } else {
            existingUser = Users.getUserByEmail(updatedUser.getEmail());
            searchData = updatedUser.getEmail();
        }

        //if User not found
        if (userAuth.isDataMissing(existingUser)) {
            errors.put("UserNotFound");
        }

        //error check if user not found
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        ArrayList<String> updatedDatas = new ArrayList<String>();

        //if email is NOT missing AND IS VALID
        if (!userAuth.isDataMissing(updatedUser.getEmail()) && userAuth.isValidEmail(updatedUser.getEmail())) {
            existingUser.setEmail(updatedUser.getEmail());
            if (searchData != updatedUser.getEmail()) {
                updatedDatas.add("email");
            }
        }

        String oldUsername = existingUser.getUsername();;

        //if username is NOT missing AND IS VALID
        if (!userAuth.isDataMissing(updatedUser.getUsername()) && userAuth.isValidUsername(updatedUser.getUsername())) {
            existingUser.setUsername(updatedUser.getUsername());
            updatedDatas.add("username");
        }

        //if firstName is NOT missing AND IS VALID
        if (!userAuth.isDataMissing(updatedUser.getFirstName()) && userAuth.isValidFirstName(updatedUser.getFirstName())) {
            existingUser.setFirstName(updatedUser.getFirstName());
            updatedDatas.add("first name");
        }

        //if lastName is NOT missing AND IS VALID
        if (!userAuth.isDataMissing(updatedUser.getLastName()) && userAuth.isValidFirstName(updatedUser.getLastName())) {
            existingUser.setLastName(updatedUser.getLastName());
            updatedDatas.add("last name");
        }

        //if phone is NOT missing AND IS VALID
        if (!userAuth.isDataMissing(updatedUser.getPhone()) && userAuth.isValidPhone(updatedUser.getPhone())) {
            existingUser.setPhone(updatedUser.getPhone());
            updatedDatas.add("phone");
        }

        //if isActive is NOT missing AND IS VALID
        if (!userAuth.isDataMissing(updatedUser.getIsActive()) && userAuth.isValidIsActive(updatedUser.getIsActive())) {
            existingUser.setIsActive(updatedUser.getIsActive());
            updatedDatas.add("is active");
        }

        //if authSecret is NOT missing AND IS VALID
        if (!userAuth.isDataMissing(updatedUser.getAuthSecret()) && userAuth.isValidAuthSecret(updatedUser.getAuthSecret())) {
            existingUser.setAuthSecret(updatedUser.getAuthSecret());
            updatedDatas.add("auth secret");
        }

        //if registrationToken is NOT missing AND IS VALID
        if (!userAuth.isDataMissing(updatedUser.getRegistrationToken()) && userAuth.isValidRegistrationToken(updatedUser.getRegistrationToken())) {
            existingUser.setRegistrationToken(updatedUser.getRegistrationToken());
            updatedDatas.add("registration token");
        }
        
        //if isSubscribed is NOT missing AND IS VALID
        if (!userAuth.isDataMissing(updatedUser.getIsSubscribed()) && userAuth.isValidIsSubscribed(updatedUser.getIsSubscribed())) {
            existingUser.setIsSubscribed(updatedUser.getIsSubscribed());
            updatedDatas.add("is subscribed");
        }

        //if username is NOT missing AND is NOT VALID
        if (!userAuth.isDataMissing(updatedUser.getUsername()) && !userAuth.isValidUsername(updatedUser.getUsername())) {
            errors.put("InvalidUsername");
        }

        //if firstName is NOT missing AND is NOT VALID
        if (!userAuth.isDataMissing(updatedUser.getFirstName()) && !userAuth.isValidFirstName(updatedUser.getFirstName())) {
            errors.put("InvalidFirstName");
        }

        //if lastName is NOT missing AND is NOT VALID
        if (!userAuth.isDataMissing(updatedUser.getLastName()) && !userAuth.isValidLastName(updatedUser.getLastName())) {
            errors.put("InvalidLastName");
        }

        //if phone is NOT missing AND is NOT VALID
        if (!userAuth.isDataMissing(updatedUser.getPhone()) && !userAuth.isValidPhone(updatedUser.getPhone())) {
            errors.put("InvalidPhone");
        }

        //if isActive is NOT missing AND is NOT VALID
        if (!userAuth.isDataMissing(updatedUser.getIsActive()) && !userAuth.isValidIsActive(updatedUser.getIsActive())) {
            errors.put("InvalidIsActive");
        }
        
        //if isSubscribed is NOT missing AND is NOT VALID
        if (!userAuth.isDataMissing(updatedUser.getIsSubscribed()) && !userAuth.isValidIsSubscribed(updatedUser.getIsSubscribed())) {
            errors.put("InvalidIsSubscribed");
        }

        //if authSecret is NOT missing AND is NOT VALID
        if (!userAuth.isDataMissing(updatedUser.getAuthSecret()) && !userAuth.isValidAuthSecret(updatedUser.getAuthSecret())) {
            errors.put("InvalidAuthSecret");
        }

        //if registrationToken is NOT missing AND is NOT VALID
        if (!userAuth.isDataMissing(updatedUser.getRegistrationToken()) && !userAuth.isValidRegistrationToken(updatedUser.getRegistrationToken())) {
            errors.put("InvalidRegistrationToken");
        }

        //if password is NOT missing AND is NOT VALID
        if (!userAuth.isDataMissing(updatedUser.getPassword()) && !userAuth.isValidPassword(updatedUser.getPassword())) {
            errors.put("InvalidPassword");
        }

        //if password is not missing AND is already EXISTING in db
        if (!userAuth.isDataMissing(updatedUser.getPassword()) && userAuth.isPasswordSame(updatedUser.getPassword(), searchData)) {
            errors.put("PasswordInDB");
        }

        //error check if datas are invalid
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        if (!userAuth.isDataMissing(updatedUser.getPassword())) {
            try {
                String encryptedPassword = Encrypt.encrypt(updatedUser.getPassword());
                existingUser.setPassword(encryptedPassword);
            } catch (Exception ex) {
                errors.put("EncryptionError");
                ex.printStackTrace();
            }
        }

        //error check if encryption error has occured
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }
        try {
            Boolean result = Users.updateUser(existingUser);

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

        UserLogs createdUserLog = new UserLogs();
        createdUserLog.setAction("updateUser");
        createdUserLog.setDetails("User " + oldUsername + " Updated the following data(s): " + updatedDatas.toString());

        Boolean userLog = UserLogs.createUserLogs(createdUserLog, existingUser.getId());

        if (!userLog) {
            errors.put("UserLogError");
        }

        if (errorAuth.hasErrors(errors)) {
            toReturn.put("warning", "Failed To Log User Action");
        }

        return errorAuth.createOKResponse();
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
        if (!userAuth.isValidEmail(logInUser.getEmail()) && !userAuth.isDataMissing(logInUser.getEmail())) {
            errors.put("InvalidEmail");
        }
        if (!userAuth.isValidPassword(logInUser.getPassword()) && !userAuth.isDataMissing(logInUser.getPassword())) {
            errors.put("InvalidPassword");
        }

        //error check if email or psw is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Users userData = Users.getUserByEmail(logInUser.getEmail());

        if (userAuth.isDataMissing(userData)) {
            errors.put("UserNotFound");
        }

        //error check for is userData missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (userAuth.isUserDeleted(userData.getIsDeleted())) {
            errors.put("UserIsSoftDeleted");
        }

        //error check for is user Deleted
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        try {
            if (!userAuth.isPasswordSame(logInUser.getPassword(), logInUser.getEmail())) {
                errors.put("InvalidPassword");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errors.put("EncryptionError");
        }

        //error check for is password same
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 401);
        }

        //get data from spq
        Boolean modelResult = Users.loginUser(userData);


        //if spq gives null data
        if (userAuth.isDataMissing(modelResult)) {
            errors.put("ModelError");
        }

        //if ModelError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        UserLogs createdUserLog = new UserLogs();
        createdUserLog.setAction("loginUser");
        createdUserLog.setDetails("User " + userData.getUsername() + " logged in.");

        Boolean userLog = UserLogs.createUserLogs(createdUserLog, userData.getId());

        if (!userLog) {
            errors.put("UserLogError");
        }

        if (errorAuth.hasErrors(errors)) {
            toReturn.put("message", "Failed To Log User Action");
        }

        toReturn.put("JWTToken", JwtUtil.generateToken(userData.getId(), userData.getEmail(), userData.getRole(), userData.getUsername()));
        toReturn.put("message", "Logged in User Successfully");
        if (userData.getIsActive() == false) {
            toReturn.put("message", "User Is Not Activated");
        }
        return errorAuth.createOKResponse(toReturn);
    }
} // DONT DELETE, THIS IS THE CLASS CLOSER

