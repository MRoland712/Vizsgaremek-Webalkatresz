/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.config;

import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import java.security.SecureRandom;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 *
 * @author ddori
 */
public class TFA {

    /**
     * Generál egy random secret key-t
     */
    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    /**
     * Generál egy 6 jegyű TOTP kódot
     */
    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    /**
     * Validálja a kódot
     */
    public static boolean validateCode(String secretKey, String code) {
        String currentCode = getTOTPCode(secretKey);
        return currentCode.equals(code);
    }

    public static String generateQRBase64(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        byte[] qrCodeBytes = outputStream.toByteArray();

        String base64Image = Base64.getEncoder().encodeToString(qrCodeBytes);
        return "data:image/png;base64," + base64Image;
    }

    // Generate a QR code URL for Google Authenticator
    public static String generateQRUrl(String secret, String username) {
        String url = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(
                "Carcomps",
                username,
                new GoogleAuthenticatorKey.Builder(secret).build());
        try {
            return generateQRBase64(url);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) throws WriterException, IOException{

        String secretKey = "P5QGQO4P5XGWDTDWHZC3C2L7XL7Q64JQ";
        System.out.println("secret key: " + secretKey);

        String TOTPCode = getTOTPCode(secretKey);
        System.out.println("TOTPCode: " + TOTPCode);
        System.out.println("validateCode: " + validateCode(secretKey, TOTPCode));

        String QR = generateQRUrl(secretKey, "janos");
        System.out.println("QR: " + QR);

    }
}
