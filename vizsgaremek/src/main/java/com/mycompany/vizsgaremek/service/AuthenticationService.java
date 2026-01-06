package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.config.Encrypt;
import com.mycompany.vizsgaremek.model.Manufacturers;
import com.mycompany.vizsgaremek.model.Users;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class AuthenticationService {

    public static boolean isDataMissing(String data) {
        return data == null || data.trim().isEmpty();
    }

    public static boolean isDataMissing(Integer data) {
        return data == null;
    }

    public static boolean isDataMissing(Boolean data) {
        return data == null;
    }

    public static boolean isDataMissing(Object data) {
        return data == null;
    }

    public static <T> boolean isDataMissing(ArrayList<T> data) {
        return data == null || data.isEmpty();
    }

    public static <T> boolean isDataMissing(Collection<T> data) {
        return data == null || data.isEmpty();
    }

    public static class userAuth {

        private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

        private static final String PHONE_REGEX = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$";
        private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);

        public boolean isValidId(Integer id) {
            return id != null && id > 0;
        }

        public boolean isValidEmail(String email) {
            if (email == null || email.trim().isEmpty()) {
                return false;
            }
            return email.length() <= 50 && EMAIL_PATTERN.matcher(email.trim()).matches();
        }

        public boolean isValidUsername(String username) {
            if (username == null) {
                return false;
            }
            return username.length() >= 3 && username.length() <= 30
                    && username.matches("^[a-zA-Z0-9_]+$");
        }

        public boolean isValidPassword(String password) {
            if (password == null) {
                return false;
            }
            return password.length() >= 8 && password.length() <= 255
                    && password.matches(".*[a-zA-Z].*")
                    && password.matches(".*[0-9].*");
        }

        public boolean isValidFirstName(String firstName) {
            if (firstName == null) {
                return false;
            }
            return firstName.length() >= 2 && firstName.length() <= 50
                    && firstName.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
        }

        public boolean isValidLastName(String lastName) {
            if (lastName == null) {
                return false;
            }
            return lastName.length() >= 2 && lastName.length() <= 50
                    && lastName.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
        }

        public boolean isValidPhone(String phone) {
            if (phone == null || phone.trim().isEmpty()) {
                return false;
            }
            return phone.length() <= 50 && PHONE_PATTERN.matcher(phone.trim()).matches();
        }

        public boolean isValidRole(String role) {
            if (role == null) {
                return false;
            }
            return role.length() <= 20 && (role.equals("user") || role.equals("admin"));
        }

        public boolean isValidIsActive(Boolean isActive) {
            return isActive != null;
        }
        
        public boolean isValidIsSubscribed(Boolean isSubbed) {
            return isSubbed != null;
        }

        public boolean isValidAuthSecret(String authSecret) {
            if (authSecret == null) {
                return false;
            }
            return authSecret.length() >= 16 && authSecret.length() <= 255;
        }

        public boolean isValidRegistrationToken(String registrationToken) {
            if (registrationToken == null) {
                return false;
            }
            return registrationToken.length() >= 32 && registrationToken.length() <= 255;
        }

        public boolean allOtherFieldsMissing(Users user) {
            return isDataMissing(user.getUsername())
                    && isDataMissing(user.getFirstName())
                    && isDataMissing(user.getLastName())
                    && isDataMissing(user.getPhone())
                    && isDataMissing(user.getPassword())
                    && isDataMissing(user.getRole());
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
                //System.out.println("isPasswordSame:");
                //System.out.println(userdata.getPassword() + " == " + Encrypt.encrypt(password) + " (" + password + ")");
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

        public boolean isUserDeleted(Boolean isDeleted) {
            return (isDeleted == true);
        }
    }//userAuth closer

    public static class addressAuth {

        public boolean isValidId(Integer id) {
            return id != null && id > 0;
        }

        public boolean isValidUserId(Integer userId) {
            return userId != null && userId > 0 && userId.toString().length() <= 11;
        }

        public boolean isValidUserId(Users userId) {
            return true; //ToDo: Figure out a Validation for this shit
        }

        public boolean isValidFirstName(String firstName) {
            if (firstName == null || firstName.trim().isEmpty()) {
                return true;
            }
            return firstName.length() <= 50
                    && firstName.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
        }

        public boolean isValidLastName(String lastName) {
            if (lastName == null || lastName.trim().isEmpty()) {
                return true;
            }
            return lastName.length() <= 50
                    && lastName.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
        }

        public boolean isValidCompany(String company) {
            if (company == null || company.trim().isEmpty()) {
                return true;
            }
            return company.length() <= 50;
        }

        public boolean isValidTaxNumber(String taxNumber) {
            if (taxNumber == null || taxNumber.trim().isEmpty()) {
                return true;
            }
            return taxNumber.length() <= 50
                    && taxNumber.matches("^[0-9-]+$");
        }

        public boolean isValidCountry(String country) {
            if (country == null) {
                return false;
            }
            return country.length() >= 2 && country.length() <= 50
                    && country.matches("^[a-zA-ZàáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
        }

        public boolean isValidCity(String city) {
            if (city == null) {
                return false;
            }
            return city.length() >= 2 && city.length() <= 50
                    && city.matches("^[a-zA-Z0-9àáâäãåąčćęèéêëėįìíîïłńòóôöõøùúûüųūÿýżźñçčšžÀÁÂÄÃÅĄĆČĖĘÈÉÊËÌÍÎÏĮŁŃÒÓÔÖÕØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð ,.'-]+$");
        }

        public boolean isValidZipCode(String zipCode) {
            if (zipCode == null) {
                return false;
            }
            return zipCode.length() >= 3 && zipCode.length() <= 20
                    && zipCode.matches("^[0-9A-Z -]+$");
        }

        public boolean isValidStreet(String street) {
            if (street == null) {
                return false;
            }
            return street.length() >= 3 && street.length() <= 100;
        }

        public boolean isValidIsDefault(Boolean isDefault) {
            return isDefault != null;
        }
    }//addressAuth closer

    public static class partsAuth {

        public boolean isValidId(Integer id) {
            return id != null && id > 0;
        }

        public boolean isValidManufacturerId(Integer manufacturerId) {
            return manufacturerId != null && manufacturerId > 0;
        }

        public boolean isValidManufacturerId(Manufacturers manufacturerId) {
            return true; //ToDo: figure out a validation for this shit
        }

        public boolean isValidSku(String sku) {
            if (sku == null) {
                return false;
            }
            return sku.length() >= 3 && sku.length() <= 100;
        }

        public boolean isValidName(String name) {
            if (name == null) {
                return false;
            }
            return name.length() >= 2 && name.length() <= 255;
        }

        public boolean isValidCategory(String category) {
            if (category == null) {
                return false;
            }
            return category.length() >= 2 && category.length() <= 100;
        }

        public boolean isValidStatus(String status) {
            if (status == null) {
                return false;
            }
            return status.length() <= 20 && (status.equals("Raktáron") || status.equals("Nincs raktáron") || status.equals("Nem elérhető"));
        }

        public boolean isValidIsActive(Boolean isActive) {
            return isActive != null;
        }

        public boolean isValidStock(Integer stock) {
            return stock != null && stock >= 0;
        }
    }//PartsAuth closer

    public static class userLogsAuth {

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
         * Creates an JSONObject OK response with the given JSONArray result
         * data
         *
         * @param result The JSONArray that contains the result datas
         *
         * @return a JSONObject with the result JSONObject as a "result" a
         * status of "success" and a "statusCode" of 200 in this format {
         * "result": [ { #result data# } ], "status": "success", "statusCode":
         * 200 }
         */
        public static JSONObject createOKResponse(JSONArray result) {
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

    }//errorAuth closer
}//AuthenticationService class closer
