package com.mycompany.vizsgaremek.controller;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.File;

@Path("/images")
public class ImagesResourcesController {

    private static final String BASE_PATH = "/var/carcomps/images/";

    @GET
    @Path("/{folder}/{filename}")
    public Response getImage(@PathParam("folder") String folder,
                             @PathParam("filename") String filename) {

        File file = new File(BASE_PATH + folder + "/" + filename);

        if (!file.exists()) {
            System.out.println("NOT FOUND: " + file.getAbsolutePath());
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(file)
                .type(getMimeType(file.getName()))
                .header("Content-Disposition", "inline; filename=\"" + file.getName() + "\"")
                .build();
    }

    private String getMimeType(String fileName) {
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".webp")) return "image/webp";
        if (fileName.endsWith(".gif")) return "image/gif";
        return "application/octet-stream";
    }
}