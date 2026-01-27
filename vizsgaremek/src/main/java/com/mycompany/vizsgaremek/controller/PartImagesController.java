/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.PartImages;
import com.mycompany.vizsgaremek.model.Parts;
import com.mycompany.vizsgaremek.service.ImageUploadService;
import com.mycompany.vizsgaremek.service.PartImagesService;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author neblg
 */
@Path("partimages")
public class PartImagesController {

    @Context
    private UriInfo context;
    private PartImagesService layer = new PartImagesService();
    private ImageUploadService imageUploadService = new ImageUploadService();

    /**
     * Creates a new instance of PartImages
     */
    public PartImagesController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.PartImagesController
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of PartImagesController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createPartImages")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPartImages(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Parts parts = new Parts();
        parts.setId(bodyObject.has("partId") ? bodyObject.getInt("partId") : null);

        PartImages createPartImages = new PartImages(
                bodyObject.has("url") ? bodyObject.getString("url") : null,
                bodyObject.has("isDefault") ? bodyObject.getBoolean("isPrimary") : false,
                parts
        );

        JSONObject toReturn = layer.createPartImages(createPartImages);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllPartImages")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllPartImages() {
        PartImagesService partimagesService = new PartImagesService();
        JSONObject toReturn = partimagesService.getAllPartImages();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getPartImagesById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartImagesById(@QueryParam("id") Integer id) {

        JSONObject toReturn = layer.getPartImagesById(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getPartImagesByPartId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartImagesByPartId(@QueryParam("partId") Integer partId) {

        JSONObject toReturn = layer.getPartImagesByPartId(partId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getPartImagesByUrl")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartImagesByUrl(@QueryParam("urlIN") String url) {

        JSONObject toReturn = layer.getPartImagesByUrl(url);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeletePartImages")
    @Produces(MediaType.APPLICATION_JSON)
    public Response softDeletePartImages(@QueryParam("idIN") Integer id) {

        JSONObject toReturn = layer.softDeletePartImages(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updatePartImages")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePartImages(
            @QueryParam("id") Integer IdIN,
            @QueryParam("url") String url,
            String body) {

        JSONObject bodyObject = new JSONObject(body);

        PartImages updatedPartImages = new PartImages();

        if (IdIN != null) {
            updatedPartImages.setId(IdIN);
        }

        if (url != null) {
            updatedPartImages.setUrl(url);
        }

        /*if (bodyObject.has("partId")) {
            updatedPartImages.setPartId(bodyObject.getString("partId"));
        }*/
        if (bodyObject.has("url")) {
            updatedPartImages.setUrl(bodyObject.getString("url"));
        }

        if (bodyObject.has("isPrimary")) {
            updatedPartImages.setIsPrimary(bodyObject.getBoolean("isPrimary"));
        }

        if (bodyObject.has("isDeleted")) {
            updatedPartImages.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updatePartImages(updatedPartImages);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("uploadPartImage")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadPartImage(MultipartFormDataInput input) {
        try {
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

            List<InputPart> partIdParts = uploadForm.get("partId");
            if (partIdParts == null || partIdParts.isEmpty()) {
                JSONObject error = new JSONObject();
                error.put("error", "partId is required");
                return Response.status(400).entity(error.toString()).build();
            }

            String partIdStr = partIdParts.get(0).getBodyAsString();
            Integer partId;
            try {
                partId = Integer.parseInt(partIdStr);
            } catch (NumberFormatException e) {
                JSONObject error = new JSONObject();
                error.put("error", "Invalid partId format");
                return Response.status(400).entity(error.toString()).build();
            }

            if (partId <= 0) {
                JSONObject error = new JSONObject();
                error.put("error", "partId must be positive");
                return Response.status(400).entity(error.toString()).build();
            }

            Boolean isPrimary = false;
            List<InputPart> isPrimaryParts = uploadForm.get("isPrimary");
            if (isPrimaryParts != null && !isPrimaryParts.isEmpty()) {
                String isPrimaryStr = isPrimaryParts.get(0).getBodyAsString();
                isPrimary = Boolean.parseBoolean(isPrimaryStr);
            }

            List<InputPart> fileParts = uploadForm.get("file");
            if (fileParts == null || fileParts.isEmpty()) {
                JSONObject error = new JSONObject();
                error.put("error", "No file uploaded");
                return Response.status(400).entity(error.toString()).build();
            }

            InputPart filePart = fileParts.get(0);
            MultivaluedMap<String, String> header = filePart.getHeaders();
            String fileName = getFileName(header);

            if (!imageUploadService.isValidImageType(fileName)) {
                JSONObject error = new JSONObject();
                error.put("error", "Invalid file type. Only jpg, jpeg, png, webp, gif allowed");
                return Response.status(400).entity(error.toString()).build();
            }

            InputStream inputStream = filePart.getBody(InputStream.class, null);

            String imageUrl = imageUploadService.uploadImage(inputStream, fileName, "parts");

            Parts part = new Parts();
            part.setId(partId);

            PartImages newImage = new PartImages(imageUrl, isPrimary, part);
            Integer newId = PartImages.createPartImages(newImage);

            if (newId == null || newId <= 0) {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to save image to database");
                return Response.status(500).entity(error.toString()).build();
            }

            JSONObject response = new JSONObject();
            response.put("message", "Image uploaded successfully");
            response.put("id", newId);
            response.put("url", imageUrl);
            response.put("partId", partId);
            response.put("isPrimary", isPrimary);

            return Response.status(201).entity(response.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", "Upload failed: " + e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }
}
