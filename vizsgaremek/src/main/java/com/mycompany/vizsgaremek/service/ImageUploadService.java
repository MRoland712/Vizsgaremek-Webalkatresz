package com.mycompany.vizsgaremek.service;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@ApplicationScoped
public class ImageUploadService {
    
    //  MANUÁLIS ÚTVONAL Ide mentődnek a képek
    private static final String UPLOAD_DIR = "C:\\carcompsImages\\";
    
    // URL alap
    private static final String BASE_URL = "http://localhost:8080/vizsgaremek/images/";
    
    /**
     * Kép feltöltése
     * 
     * @param fileInputStream Fájl input stream
     * @param originalFileName Eredeti fájlnév (pl. "photo.jpg")
     * @param folder Almappa neve (pl. "parts")
     * @return URL a képhez
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
        
        // Debug információk
        System.out.println("=== IMAGE UPLOAD ===");
        System.out.println("Folder: " + folderPath);
        System.out.println("File: " + filePath);
        
        // 4. Mappa létrehozása (ha nem létezik)
        Files.createDirectories(Paths.get(folderPath));
        
        // 5. Fájl mentése
        try {
            Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved successfully!");
        } finally {
            fileInputStream.close();
        }
        
        // 6. URL generálása
        String imageUrl = BASE_URL + folder + "/" + uniqueFileName;
        System.out.println("URL: " + imageUrl);
        System.out.println("====================");
        
        return imageUrl;
    }
    
    /**
     * Kép törlése
     */
    
    public boolean deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || !imageUrl.contains(BASE_URL)) {
                return false;
            }
            
            String relativePath = imageUrl.replace(BASE_URL, "");
            String fullPath = UPLOAD_DIR + relativePath.replace("/", File.separator);
            
            System.out.println("Deleting: " + fullPath);
            return Files.deleteIfExists(Paths.get(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Fájl extension kinyerése
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1) {
            return ".jpg";
        }
        return fileName.substring(lastDot).toLowerCase();
    }
    
    /**
     * Képformátum ellenőrzése
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
