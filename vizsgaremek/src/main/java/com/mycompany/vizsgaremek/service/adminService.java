/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Admin;
import com.mycompany.vizsgaremek.service.AuthenticationService.userAuth;
import com.mycompany.vizsgaremek.service.AuthenticationService.errorAuth;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ddori
 */
public class adminService {
    
    private final AuthenticationService.userAuth userAuth = new AuthenticationService.userAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();
    
    public JSONObject loginAdmin(Admin logInAdmin) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //IF DATA IS MISSING
        if (userAuth.isDataMissing(logInAdmin.getEmail())) {
            errors.put("MissingEmail");
        }
        if (userAuth.isDataMissing(logInAdmin.getPassword())) {
            errors.put("MissingPassword");
        }

        //IF DATA IS INVALID
        if (!userAuth.isValidEmail(logInAdmin.getEmail()) && !userAuth.isDataMissing(logInAdmin.getEmail())) {
            errors.put("InvalidEmail");
        }
        if (!userAuth.isValidPassword(logInAdmin.getPassword()) && !userAuth.isDataMissing(logInAdmin.getPassword())) {
            errors.put("InvalidPassword");
        }

        //error check if email or psw is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Admin adminData = Admin.getAdminByEmail(logInAdmin.getEmail());

        if (userAuth.isDataMissing(adminData)) {
            errors.put("UserNotFound");
        }

        //error check for is userData missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (userAuth.isUserDeleted(adminData.getIsDeleted())) {
            errors.put("UserIsSoftDeleted");
        }

        //error check for is Admin Deleted
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        try {
            if (!userAuth.isPasswordSame(logInAdmin.getPassword(), logInAdmin.getEmail())) {
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
        Boolean modelResult = Users.loginAdmin(adminData);


        //if spq gives null data
        if (userAuth.isDataMissing(modelResult)) {
            errors.put("ModelError");
        }

        //if ModelError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        /*UserLogs createdUserLog = new UserLogs();
        createdUserLog.setAction("loginUser");
        createdUserLog.setDetails("User " + userData.getUsername() + " logged in.");

        Boolean userLog = UserLogs.createUserLogs(createdUserLog, userData.getId());

        if (!userLog) {
            errors.put("UserLogError");
        }

        if (errorAuth.hasErrors(errors)) {
            toReturn.put("message", "Failed To Log User Action");
        }*/

        toReturn.put("JWTToken", JwtUtil.generateToken(adminData.getId(), adminData.getEmail(), adminData.getRole(), adminData.getUsername()));
        toReturn.put("message", "Logged in Admin Successfully");
        if (adminData.getIsActive() == false) {
            toReturn.put("message", "Admin Is Not Activated");
        }
        return errorAuth.createOKResponse(toReturn);
    }
}
