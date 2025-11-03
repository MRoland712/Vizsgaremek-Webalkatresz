/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblg
 */
public class UsersService {

    private Users layer = new Users();
    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Legalább 8 karakter, tartalmaz nagybetűt, számot és speciális karaktert
    private static final Pattern PASSWORD_PATTERN
            = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public JSONObject createUser(Users createdUser) {
        JSONObject toReturn = new JSONObject();
        String status = "success";
        Integer statusCode = 200;

        if (isValidEmail(createdUser.getEmail()) == false) {
            status = "InvalidEmail";
            statusCode = 417;
        } else if (isValidPassword(createdUser.getPassword()) == false) {
            status = "InvalidPassword";
            statusCode = 417;
        } else {
            Boolean modelResult = Users.createUser(createdUser);
            if (modelResult == false) {
                status = "ServerError";
                statusCode = 500;
            }
            toReturn.put("result", modelResult);
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
                //actualUserObject.put("isActive", actualUser.getIsActive());
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
                result.put("firstName", modelResult.getFirstName());
                result.put("lastName", modelResult.getLastName());
                result.put("phone", modelResult.getPhone());
                result.put("guid", modelResult.getGuid());
                result.put("role", modelResult.getRole());
                result.put("lastLogin", modelResult.getLastLogin() == null ? "" : modelResult.getLastLogin().toString());
                result.put("createdAt", modelResult.getCreatedAt() == null ? "" : modelResult.getCreatedAt().toString());
                result.put("updateAt", modelResult.getUpdatedAt() == null ? "" : modelResult.getUpdatedAt().toString());
                toReturn.put("result", result);
            } else{
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

}
