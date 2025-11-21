/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author ddori
 */
public class Encrypt {

    private static final String AES_KEY = base64Converters.base64Converter(KvFetcher.getDataFromKV("EncryptionKey"));

    public static String encrypt(String plainText) throws Exception {
        // Use UTF-8 encoding explicitly ← CHANGED
        byte[] keyBytes = AES_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // Use UTF-8 encoding for the plain text ← CHANGED
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String cipherText) throws Exception {
        // Use UTF-8 encoding explicitly ← CHANGED
        byte[] keyBytes = AES_KEY.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decoded);

        // Use UTF-8 encoding when converting back to string ← CHANGED
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        // Test passwords with Hungarian characters
        String[] testPasswords = {
            "simple123",
            "titkosJelszó12!",
            "árvíztűrő_tükörfúrógép",
            "ÓÜÖÚŐŰÁÉÍ",
            "Password123!éáű"
        };

        for (String password : testPasswords) {
            String encrypted = encrypt(password);
            String decrypted = decrypt(encrypted);
            boolean matches = password.equals(decrypted);

            System.out.println("Original:  " + password);
            System.out.println("Encrypted: " + encrypted);
            System.out.println("Decrypted: " + decrypted);
            System.out.println("Match:     " + matches);
            System.out.println("---");
        }
    }
}
