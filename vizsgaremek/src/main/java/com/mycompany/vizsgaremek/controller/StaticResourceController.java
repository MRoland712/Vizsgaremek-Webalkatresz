package com.mycompany.vizsgaremek.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.file.Files;

@Path("images")
public class StaticResourceController {
    
    private static final String IMAGE_BASE_PATH = "C:\\carcompsImages\\";
    
    @GET
    @Path("{folder}/{filename}")
    @Produces("image/*")
    public Response getImage(
            @PathParam("folder") String folder, 
            @PathParam("filename") String filename
    ) {
        try {
            String imagePath = IMAGE_BASE_PATH + folder + File.separator + filename;
            
            System.out.println("=== IMAGE REQUEST ===");
            System.out.println("Requested: " + folder + "/" + filename);
            System.out.println("Full path: " + imagePath);
            
            File imageFile = new File(imagePath);
            
            if (!imageFile.exists()) {
                System.out.println(" Image not found!");
                System.out.println("=====================");
                return Response.status(404).entity("Image not found").build();
            }
            
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            
            System.out.println(" Image served successfully! (" + imageBytes.length + " bytes)");
            
            // Content-Type meghatározás
            String contentType = "image/jpeg";
            String lowerFilename = filename.toLowerCase();
            
            if (lowerFilename.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerFilename.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerFilename.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            }
            
            System.out.println(" Content-Type: " + contentType);
            System.out.println("=====================");
            
            return Response.ok(imageBytes)
                    .type(contentType)
                    .build();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Error: " + e.getMessage());
            System.out.println("=====================");
            return Response.status(500).entity("Error: " + e.getMessage()).build();
        }
    }
}