/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import java.io.File;
import java.nio.file.Files;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author neblg
 */
@Path("images")
public class StaticResourceController {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of StaticResourceController
     */
    public StaticResourceController() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.vizsgaremek.controller.StaticResourceController
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of StaticResourceController
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
        // Képek tárolási helye
    private static final String IMAGE_BASE_PATH = "C:\\carcompsImages\\";
    
    /**
     * Kép kiszolgálása HTTP-n keresztül
     * 
     * GET /api/images/{folder}/{filename}
     * Példa: http://localhost:8080/vizsgaremek/api/images/parts/abc-uuid.jpg
     * 
     * @param folder Mappa neve (pl. "parts")
     * @param filename Fájl neve (pl. "abc-uuid.jpg")
     * @return Kép byte array vagy 404 hiba
     */
    
    @GET
    @Path("{folder}/{filename}")
    @Produces("image/*")
    public Response getImage(
            @PathParam("folder") String folder, 
            @PathParam("filename") String filename
    ) {
        try {
            // 1. Teljes fájl útvonal összeállítása
            String imagePath = IMAGE_BASE_PATH + folder + File.separator + filename;
            
            // Debug log
            System.out.println("IMAGE REQUEST");
            System.out.println("Requested: " + folder + "/" + filename);
            System.out.println("Full path: " + imagePath);
            
            File imageFile = new File(imagePath);
            
            // 2. Ellenőrzés: Létezik a fájl?
            if (!imageFile.exists()) {
                System.out.println("Image not found!");
                System.out.println("=====================");
                return Response.status(404).entity("Image not found").build();
            }
            
            // 3. Fájl olvasása byte array-be
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            
            System.out.println("Image served successfully! (" + imageBytes.length + " bytes)");
            System.out.println("=====================");
            
            // 4. Kép visszaadása
            return Response.ok(imageBytes).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            System.out.println("=====================");
            return Response.status(500).entity("Error loading image: " + e.getMessage()).build();
        }
    }
}
