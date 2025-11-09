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

/**
 *
 * @author neblg
 * @editor ddori
 */
public class UsersService {

    private AuthenticationService authService = new AuthenticationService();

    private Users layer = new Users();

    /**
     * commonly used error codes 400 - Bad request (validation error / client
     * sends wrong data) 401 - Unauthorised (Authentication error ex: Wrong
     * password entered) 404 - Missing (ex: Couldnt find user with given data)
     * 409 - Conflict (something is the same as in db ex: Email is same as in
     * DB) 417 - Expectation failed 500 - Internal Server Error (Missing
     * required data in DB ex: isDeleted == null)
     */
    public JSONObject createUser(Users createdUser) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;
        //ToDo: other validations (username ect..)
        if (authService.isValidEmail(createdUser.getEmail()) == false) {
            status = "InvalidEmail";
            statusCode = 400;
        } else if (authService.isValidPassword(createdUser.getPassword()) == false) {
            status = "InvalidPassword";
            statusCode = 400;
        } else {
            try {
                String encryptedPassword = Encrypt.encrypt(createdUser.getPassword());
                createdUser.setPassword(encryptedPassword);

                Random random = new Random();
                createdUser.setAuthSecret(
                        Integer.toString(random.nextInt(900000) + 100000)
                );

                if (createdUser.getRole() == null || createdUser.getRole().isEmpty()) {
                    createdUser.setRole(null);
                }

                createdUser.setRegistrationToken(JwtUtil.generateToken(createdUser.getId(), createdUser.getEmail(), createdUser.getRole(), createdUser.getUsername()));

                Boolean modelResult = Users.createUser(createdUser);

                if (modelResult == false) {
                    status = "ServerError";
                    statusCode = 500;
                }
                /*else {

                    Users newUser = Users.ReadUserByEmail(createdUser.getEmail());

                    if (newUser != null) {
                        String registrationToken = JwtUtil.generateToken(
                                newUser.getId(),
                                newUser.getEmail()
                        );

                        newUser.setRegistrationToken(registrationToken);
                        Users.updateUser(newUser);

                        toReturn.put("registrationToken", registrationToken);
                    } else {
                        status = "ServerError";
                        statusCode = 500;
                    }}*/

                toReturn.put("result", modelResult);

            } catch (Exception e) {
                status = "EncryptionError";
                statusCode = 500;
                e.printStackTrace();
            }
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }

    public JSONObject ReadUsers() {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        ArrayList<Users> modelResult = Users.ReadUsers();

        if (modelResult == null) {
            statusCode = 500;
            status = "Modelexception";
        } else if (modelResult.isEmpty()) {
            status = "NoRecordFound";

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
                actualUserObject.put("lastLogin", actualUser.getLastLogin() == null ? "" : actualUser.getLastLogin().toString());
                actualUserObject.put("createdAt", actualUser.getCreatedAt() == null ? "" : actualUser.getCreatedAt().toString());
                actualUserObject.put("updatedAt", actualUser.getUpdatedAt() == null ? "" : actualUser.getUpdatedAt().toString());

                result.put(actualUserObject);
            }
            toReturn.put("result", result);
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }

    public JSONObject ReadUserById(Integer id) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        if (id > 0) {

            Users modelResult = Users.ReadUserById(id);

            if (modelResult != null) {
                JSONObject result = new JSONObject();
                
                result.put("id", modelResult.getId());
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
                result.put("guid", modelResult.getGuid());
                result.put("registrationToken", modelResult.getRegistrationToken());

                toReturn.put("result", result);
            } else {
                status = "UserNotFound";
                statusCode = 404;
            }

        } else {
            status = "InvalidParamValue";
            statusCode = 417;
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }

    public JSONObject ReadUserByEmail(String email) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        if (email == null || email.trim().isEmpty()) {
            status = "MissingEmail";
            statusCode = 417;
        } else if (!authService.isValidEmail(email)) {
            status = "InvalidEmail";
            statusCode = 417;
        } else {
            Users modelResult = Users.ReadUserByEmail(email);

            if (modelResult != null && modelResult.getEmail() != null) {

                JSONObject result = new JSONObject();

                result.put("id", modelResult.getId());
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
                result.put("guid", modelResult.getGuid());
                result.put("registrationToken", modelResult.getRegistrationToken());

                toReturn.put("result", result);
            } else {
                status = "UserNotFound";
                statusCode = 404;
            }
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }

    public JSONObject softDeleteUser(Integer id) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        Users user = Users.ReadUserById(id);

        if (id == null || id <= 0) {
            status = "InvalidId";
            statusCode = 400;
        } else if (user.getIsDeleted() != null && user.getIsDeleted() == true) {
            status = "UserAlreadySoftDeleted";
            statusCode = 409; // Conflict
        } else {
            Boolean result = Users.softDeleteUser(id); // Ez a metódus kell a Users.java-ba
            if (!result) {
                status = "ServerError";
                statusCode = 500;
            }
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }

    public JSONObject updateUser(Users updatedUser) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        if (updatedUser.getId() != null && updatedUser.getId() <= 0) {
            status = "InvalidId";
            statusCode = 400;
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            return toReturn;
        }

        if (updatedUser.getId() == null && updatedUser.getEmail() == null) {
            status = "MissingIdAndEmail";
            statusCode = 404;
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            return toReturn;
        } else {

            Users existingUser = null;
            if (updatedUser.getId() != null) {
                existingUser = Users.ReadUserById(updatedUser.getId());
            } else {
                existingUser = Users.ReadUserByEmail(updatedUser.getEmail());
            }

            if (existingUser == null) {
                status = "UserNotFound";
                statusCode = 404;
                toReturn.put("status", status);
                toReturn.put("statusCode", statusCode);
                return toReturn;
            } else {
                if (updatedUser.getEmail() != null || !updatedUser.getEmail().isEmpty()) {
                    if (!authService.isValidEmail(updatedUser.getEmail())) {
                        status = "InvalidEmail";
                        statusCode = 417; //expect failed
                        toReturn.put("status", status);
                        toReturn.put("statusCode", statusCode);
                        return toReturn;
                    }
                    existingUser.setEmail(updatedUser.getEmail());
                }

                if (updatedUser.getUsername() != null) {
                    if (!authService.isValidUsername(updatedUser.getUsername())) {
                        status = "InvalidUsername";
                        statusCode = 417; //expect failed
                        toReturn.put("status", status);
                        toReturn.put("statusCode", statusCode);
                        return toReturn;
                    }
                    existingUser.setUsername(updatedUser.getUsername());
                }

                if (updatedUser.getFirstName() != null) {
                    existingUser.setFirstName(updatedUser.getFirstName());
                }

                if (updatedUser.getLastName() != null) {
                    existingUser.setLastName(updatedUser.getLastName());
                }

                if (updatedUser.getPhone() != null) {
                    existingUser.setPhone(updatedUser.getPhone());
                }

                if (updatedUser.getRole() != null) {
                    existingUser.setRole(updatedUser.getRole());
                }

                if (updatedUser.getIsActive() != null) {
                    existingUser.setIsActive(updatedUser.getIsActive());
                }

                if (updatedUser.getAuthSecret() != null) {
                    existingUser.setAuthSecret(updatedUser.getAuthSecret());
                }

                if (updatedUser.getRegistrationToken() != null) {
                    existingUser.setRegistrationToken(updatedUser.getRegistrationToken());
                }

                if (updatedUser.getPassword() != null) {

                    if (!authService.isValidPassword(updatedUser.getPassword())) {
                        status = "InvalidPassword";
                        statusCode = 417; //expect failed
                        toReturn.put("status", status);
                        toReturn.put("statusCode", statusCode);
                        return toReturn;
                    }

                    if (authService.isPasswordSame(updatedUser.getPassword(), updatedUser.getId())) {
                        status = "PasswordIsSameAsDB";
                        statusCode = 409; //conflict
                        toReturn.put("status", status);
                        toReturn.put("statusCode", statusCode);
                        return toReturn;
                    }
                    try {
                        //encrypt password
                        String encryptedPassword = Encrypt.encrypt(updatedUser.getPassword());
                        existingUser.setPassword(encryptedPassword);
                    } catch (Exception e) {
                        status = "EncryptionError";
                        statusCode = 500;
                        e.printStackTrace();
                    }
                }

                if (updatedUser.getAuthSecret() != null) {
                    existingUser.setAuthSecret(updatedUser.getAuthSecret());
                    System.out.println("update user's auth secret: " + updatedUser.getAuthSecret());
                    System.out.println("existing user's auth secret: " + existingUser.getAuthSecret());
                }

                if (updatedUser.getRegistrationToken() != null) {
                    existingUser.setRegistrationToken(updatedUser.getRegistrationToken());
                    System.out.println("update user's reg token: " + updatedUser.getRegistrationToken());
                    System.out.println("existing user's reg token: " + existingUser.getRegistrationToken());
                }

                Boolean result = Users.updateUser(existingUser);

                if (!result) {
                    status = "ServerError";
                    statusCode = 500;
                }

                toReturn.put("result", result);

            }
        }

        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }

    public JSONObject login(Users user) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        // Validation checks
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            status = "MissingEmail";
            statusCode = 400; // Bad request
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            return toReturn;
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            status = "MissingPassword";
            statusCode = 400; // Bad request
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            return toReturn;
        }

        Users userData = Users.ReadUserByEmail(user.getEmail());

        if (userData == null) {
            status = "UserNotFound";
            statusCode = 404; //missing
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            return toReturn;
        }

        if (userData.getIsActive() == null) {
            status = "InternalServerError";
            statusCode = 500; // Internal Server Error
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            toReturn.put("message", "Data integrity violation: isActive field is null");
            return toReturn;
        }

        if (userData.getIsDeleted() == null) {
            status = "InternalServerError";
            statusCode = 500; // Internal Server Error
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            toReturn.put("message", "Data integrity violation: isDeleted field is null");
            return toReturn;
        }

        if (userData.getIsDeleted() == true) {
            status = "UserIsSoftDeleted";
            statusCode = 409; // conflict
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            return toReturn;
        }
        
        if (userData.getIsActive() == false) {
            status = "UserIsInactive";
            statusCode = 403; // Forbidden
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            return toReturn;
        }

        // Verify password
        try {
            if (!authService.isPasswordSame(user.getPassword(), userData.getEmail())) {
                status = "InvalidPassword";
                statusCode = 401; // Unauthorized
                toReturn.put("status", status);
                toReturn.put("statusCode", statusCode);
                return toReturn;
            }
        } catch (Exception e) {
            status = "EncryptionError";
            statusCode = 500;
            e.printStackTrace();
            toReturn.put("status", status);
            toReturn.put("statusCode", statusCode);
            return toReturn;
        }

        Users.login(userData);

        //if user is logged in
        String JWTToken = JwtUtil.generateToken(
                userData.getId(),
                userData.getEmail(),
                userData.getRole(),
                userData.getUsername()
        );

        userData.setRegistrationToken(JWTToken);
        System.out.println("userData regtoken: " + userData.getRegistrationToken());
        Users.updateUser(userData);

        JSONObject returnUserData = new JSONObject();
        returnUserData.put("id", userData.getId());
        returnUserData.put("email", userData.getEmail());
        returnUserData.put("username", userData.getUsername());
        returnUserData.put("firstName", userData.getFirstName());
        returnUserData.put("lastName", userData.getLastName());
        returnUserData.put("phone", userData.getPhone());
        returnUserData.put("role", userData.getRole());
        returnUserData.put("guid", userData.getGuid());
        returnUserData.put("token", JWTToken);

        toReturn.put("user", returnUserData);
        toReturn.put("status", status);
        toReturn.put("statusCode", statusCode);
        return toReturn;
    }
}
