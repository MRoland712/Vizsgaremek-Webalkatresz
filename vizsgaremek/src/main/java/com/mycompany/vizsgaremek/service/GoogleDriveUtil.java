package com.mycompany.vizsgaremek.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleDriveUtil {
    
    /**
     * Google Drive megosztott link átalakítása közvetlen kép URL-re
     * 
     * Input: https://drive.google.com/file/d/1a2b3c4d5e6f7g8h9/view?usp=sharing
     * Output: https://drive.google.com/uc?export=view&id=1a2b3c4d5e6f7g8h9
     */
    public static String convertToDirectUrl(String driveUrl) {
        if (driveUrl == null || driveUrl.isEmpty()) {
            return driveUrl;
        }
        
        // Ha már közvetlen URL, akkor visszaadjuk
        if (driveUrl.contains("uc?export=view")) {
            return driveUrl;
        }
        
        // File ID kinyerése regex-szel
        Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(driveUrl);
        
        if (matcher.find()) {
            String fileId = matcher.group(1);
            return "https://drive.google.com/uc?export=view&id=" + fileId;
        }
        
        // Ha nem Google Drive link, akkor visszaadjuk változatlanul
        return driveUrl;
    }
    
    /**
     * Ellenőrzi, hogy Google Drive link-e
     */
    public static boolean isGoogleDriveUrl(String url) {
        if (url == null) {
            return false;
        }
        return url.contains("drive.google.com");
    }
}