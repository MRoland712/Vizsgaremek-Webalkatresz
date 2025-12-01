/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.UserLogs;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ddori
 */
public class UserLogsService {

    private final AuthenticationService.userLogsAuth userLogsAuth = new AuthenticationService.userLogsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createUserLogs(UserLogs createdUserLog, Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();
    }
}
