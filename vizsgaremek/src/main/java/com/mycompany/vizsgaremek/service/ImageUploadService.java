package com.mycompany.vizsgaremek.service;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.nio.file.*;

@ApplicationScoped
public class ImageUploadService {

    /**
     * Get upload directory based on OS type parameter
     * @param osType 'w'/'W' = Windows, 'l'/'L'/'s'/'S' = Linux, 'm'/'M' = Mac
     */
    private String getUploadDirectory(String osType) {
        if (osType == null || osType.isEmpty()) {
            osType = "w"; // Default Windows
        }

        char os = osType.toLowerCase().charAt(0);

        switch (os) {
            case 'w':
                return "C:\\carcompsImages\\";
            case 'l':
            case 's':
                return "/var/carcomps/images/";
            case 'm':
                return System.getProperty("user.home") + "/carcomps/images/";
            default:
                return "C:\\carcompsImages\\"; // Fallback Windows
        }
    }

    /**
     * Get base URL - always production
     */
    private static final String BASE_URL = "https://api.carcomps.hu/vizsgaremek-1.0-SNAPSHOT/webresources/images/";

    /**
     * Kép feltöltése OS típus alapján
     *
     * @param fileInputStream Fájl input stream
     * @param originalFileName Eredeti fájlnév (pl. "photo.jpg")
     * @param folder Almappa neve (pl. "parts")
     * @param osType 'w' = Windows, 'l'/'s' = Linux, 'm' = Mac
     * @return URL a képhez
     */
    public String uploadImage(InputStream fileInputStream, String originalFileName, String folder, String osType) throws IOException {

        // 1. Validáció
        if (!isValidImageType(originalFileName)) {
            throw new IOException("Invalid file type. Only jpg, jpeg, png, webp, gif allowed.");
        }

        // 2. Upload directory OS alapján
        String UPLOAD_DIR = getUploadDirectory(osType);

        // 3. Egyedi fájlnév
        String extension = getFileExtension(originalFileName);
        String uniqueFileName = originalFileName;

        // 4. Teljes útvonal
        String folderPath = UPLOAD_DIR + folder + File.separator;
        String filePath = folderPath + uniqueFileName;

        // Debug információk
        System.out.println("=== IMAGE UPLOAD ===");
        System.out.println("OS Type: " + osType);
        System.out.println("Upload dir: " + UPLOAD_DIR);
        System.out.println("Folder: " + folderPath);
        System.out.println("File: " + filePath);

        // 5. Mappa létrehozása
        Files.createDirectories(Paths.get(folderPath));

        // 6. Fájl mentése
        try {
            Files.copy(fileInputStream, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved successfully!");
        } finally {
            fileInputStream.close();
        }

        // 7. URL generálása
        String imageUrl = BASE_URL + folder + "/" + uniqueFileName;
        System.out.println("URL: " + imageUrl);
        System.out.println("====================");

        return imageUrl;
    }

    /**
     * Kép törlése OS típus alapján
     */
    public boolean deleteImage(String imageUrl, String osType) {
        try {
            if (imageUrl == null || !imageUrl.contains(BASE_URL)) {
                return false;
            }

            String UPLOAD_DIR = getUploadDirectory(osType);
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