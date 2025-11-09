/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.config.Encrypt;
import java.util.regex.Pattern;

/**
 *
 * @author ddori
 */
public class AuthenticationService {

    private static final Pattern EMAIL_PATTERN
            = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Legalább 8 karakter, tartalmaz nagybetűt, számot és speciális karaktert
    private static final Pattern PASSWORD_PATTERN
            = Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public boolean isValidUsername(String username) {
        return username.length() <= 30;
    }

    /**
     * Checks if the plain password is the same as the Encrypted password in the
     * DB
     *
     * @param password The password that needs to be checked
     * @param userId Users Id
     * @return true / false based if the password is the same as the one in the
     * db
     * @throws Exception If somehow one of the methods in the return went wrong
     */
    public boolean isPasswordSame(String password, Integer userId) {
        Users userdata = Users.ReadUserById(userId);
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
     * Checks if the plain password is the same as the Encrypted password in the
     * DB
     *
     * @param password The password that needs to be checked
     * @param email User Email
     * @return true / false based if the password is the same as the one in the
     * db
     * @throws Exception and returns false
     */
    public boolean isPasswordSame(String password, String email) {
        Users userdata = Users.ReadUserByEmail(email);
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
}
