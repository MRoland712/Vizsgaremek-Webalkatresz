/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.config.Encrypt;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ddori
 */
public class AuthenticationService {

    public static class errorAuth {

        public static boolean hasErrors(JSONArray errors) {
            return errors.length() > 0;
        }

        public static JSONObject createErrorResponse(JSONArray errors, int statusCode) {
            JSONObject response = new JSONObject();
            response.put("errors", errors);
            response.put("status", "failed");
            response.put("statusCode", statusCode);
            return response;
        }

        public static JSONObject createOKResponse(JSONObject result) {
            JSONObject response = new JSONObject();
            response.put("result", result);
            response.put("status", "success");
            response.put("statusCode", 200);
            return response;
        }

        public static JSONObject createOKResponse() {
            JSONObject response = new JSONObject();
            response.put("status", "success");
            response.put("statusCode", 200);
            return response;
        }

    }//Class closer

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
            try {
                return isActive instanceof Boolean;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
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
                System.out.println("isPasswordSame:");
                System.out.println(userdata.getPassword() + " == " + Encrypt.encrypt(password) + " (" + password + ")");
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

    } //Class closer
}//Class closer
