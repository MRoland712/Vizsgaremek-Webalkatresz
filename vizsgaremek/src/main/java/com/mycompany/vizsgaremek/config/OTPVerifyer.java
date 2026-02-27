/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.config;

import com.mycompany.vizsgaremek.model.Users;
import java.util.Date;

/**
 *
 * @author ddori
 */
public class OTPVerifyer {

    private static final long OTP_VALIDITY = 10 * 60 * 1000; // 10perc
    
    public static boolean verifyOTP(Users userData, Integer otpCodeInp) {
        
        System.out.println("verifyOTP: "+ userData.getAuthSecret() + " | " + otpCodeInp);
        
        if (otpCodeInp.equals(Integer.valueOf(userData.getAuthSecret())) == false) {
            return false;
        }

        Date otpGeneratedAt = userData.getLastLogin(); // Timestamp vagy Date
        
        if (otpGeneratedAt == null) {
            otpGeneratedAt = userData.getUpdatedAt(); // failsafe ha a LastLogin null
        }
        
        Date now = new Date();

        Date expiryDate = new Date(otpGeneratedAt.getTime() + OTP_VALIDITY);
        
        System.out.println("verifyOTP expiry: "+ otpGeneratedAt + " | " + expiryDate + " | " + now.before(expiryDate));
        
        if (!now.before(expiryDate)) {
            return false;
        }
        
        return true;
    }
}
