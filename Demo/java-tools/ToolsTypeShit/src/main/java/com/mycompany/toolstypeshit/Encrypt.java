/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toolstypeshit;

import com.mycompany.toolstypeshit.KvFetcher;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import com.mycompany.toolstypeshit.base64Converters;

/**
 *
 * @author ddori
 */
public class Encrypt extends KvFetcher {
    private static final String AES_KEY = base64Converters.base64Converter(KvFetcher.getDataFromKV("EncryptionKey"));

    public static String encrypt(String plainText) throws Exception{
        byte[] keyBytes = AES_KEY.getBytes(); 
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String cipherText) throws Exception {
        byte[] keyBytes = AES_KEY.getBytes();
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decoded = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }

    public static void main(String[] args) throws Exception {
        String original = "hashedpass2blahahahehrje";
        String encrypted = encrypt(original);
        String decrypted = decrypt(encrypted);

        System.out.println("Eredeti: " + original);
        System.out.println("Titkosítva: " + encrypted);
        System.out.println("Visszafejtve: " + decrypted);
    }

}
