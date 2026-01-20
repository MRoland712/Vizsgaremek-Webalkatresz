package com.mycompany.vizsgaremek.service;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@ApplicationScoped
public class ImageUploadService {
    
    // DEVELOPMENT útvonal (Windows)
    private static final String UPLOAD_DIR_DEV = getWebappPath() + "images" + File.separator;
    
    // PRODUCTION útvonal (Linux szerver)
    private static final String UPLOAD_DIR_PROD = "/var/www/carcomps/images/";
    
    // Automatikus választás OS alapján
    private static final String UPLOAD_DIR = System.getProperty("os.name").toLowerCase().contains("win") 
        ? UPLOAD_DIR_DEV 
        : UPLOAD_DIR_PROD;
    
    // BASE URL - módosítsd a projekted nevére!
    private static final String BASE_URL = System.getProperty("os.name").toLowerCase().contains("win")
        ? "http://localhost:8080/vizsgaremek/images/"  // Development
        : "https://carcomps.hu/images/";                // Production
    
    /**
     * Webapp útvonal lekérése
     */
    private static String getWebappPath() {
        // WildFly deployed path
        String jbossBase = System.getProperty("jboss.server.base.dir");
        if (jbossBase != null) {
            return jbossBase + File.separator + "deployments" + File.separator + "vizsgaremek.war" + File.separator;
        }
        
        // Development fallback
        String userDir = System.getProperty("user.dir");
        return userDir + File.separator + "src" + File.separator + "main" + File.separator + "webapp" + File.separator;
    }
    
    /**
     * Kép feltöltése InputStream-ből
     */
    public String uploadImage(InputStream fileInputStream, String originalFileName, String folder) throws IOException {
        // 1. Validáció
        if (!isValidImageType(originalFileName)) {
            throw new IOException("Invalid file type. Only jpg, jpeg, png, webp, gif allowed.");
        }
        
        // 2. Egyedi fájlnév generálása
        String extension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        
        // 3. Teljes útvonal
        String folderPath = UPLOAD_DIR + folder + File.separator;
        String filePath = folderPath + uniqueFileName;
        
        // Debug log
        System.out.println("IMAGE UPLOAD DEBUG");
        System.out.println("Upload directory: " + UPLOAD_DIR);
        System.out.println("Folder path: " + folderPath);
        System.out.println("Full file path: " + filePath);
        
        // 4. Könyvtár létrehozása, ha nem létezik
        Files.createDirectories(Paths.get(folderPath));
        
        // 5. Fájl mentése
        try {
            Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File saved successfully!");
        } finally {
            fileInputStream.close();
        }
        
        // 6. Publikus URL visszaadása
        String finalUrl = BASE_URL + folder + "/" + uniqueFileName;
        System.out.println("Generated URL: " + finalUrl);
        
        return finalUrl;
    }
    
    /**
     * Kép feltöltése byte array-ből
     */
    public String uploadImage(byte[] imageBytes, String originalFileName, String folder) throws IOException {
        if (!isValidImageType(originalFileName)) {
            throw new IOException("Invalid file type.");
        }
        
        String extension = getFileExtension(originalFileName);
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        
        String folderPath = UPLOAD_DIR + folder + File.separator;
        String filePath = folderPath + uniqueFileName;
        
        Files.createDirectories(Paths.get(folderPath));
        Files.write(Paths.get(filePath), imageBytes);
        
        return BASE_URL + folder + "/" + uniqueFileName;
    }
    
    /**
     * Kép törlése URL alapján
     */
    public boolean deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || !imageUrl.contains(BASE_URL)) {
                return false;
            }
            
            String relativePath = imageUrl.replace(BASE_URL, "");
            String fullPath = UPLOAD_DIR + relativePath.replace("/", File.separator);
            
            System.out.println("Deleting image: " + fullPath);
            return Files.deleteIfExists(Paths.get(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * File extension kinyerése
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return ".jpg"; // alapértelmezett
        }
        return fileName.substring(lastDot).toLowerCase();
    }
    
    /**
     * Fájl méret validáció (max 5MB)
     */
    public boolean isValidFileSize(long fileSize) {
        long maxSize = 5 * 1024 * 1024; // 5MB
        return fileSize > 0 && fileSize <= maxSize;
    }
    
    /**
     * Fájl típus validáció
     */
    public boolean isValidImageType(String fileName) {
        if (fileName == null) {
            return false;
        }
        String extension = getFileExtension(fileName);
        return extension.equals(".jpg") || 
               extension.equals(".jpeg") || 
               extension.equals(".png") || 
               extension.equals(".webp") ||
               extension.equals(".gif");
    }
}