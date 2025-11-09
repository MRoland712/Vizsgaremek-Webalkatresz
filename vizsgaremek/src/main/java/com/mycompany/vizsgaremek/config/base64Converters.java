/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 *
 * @author ddori
 */
public class base64Converters {
    public static String base64Converter(String b64String) {
            byte[] b64InByte = Base64.getDecoder().decode(b64String);
            String convertedString = new String(b64InByte, StandardCharsets.UTF_8);
            return convertedString;
    }
}
