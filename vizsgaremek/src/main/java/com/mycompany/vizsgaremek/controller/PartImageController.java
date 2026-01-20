package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.PartImage;
import com.mycompany.vizsgaremek.service.GoogleDriveUtil;
import com.mycompany.vizsgaremek.service.ImageUploadService;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("part-images")
@Produces(MediaType.APPLICATION_JSON)
public class PartImageController {

    @Inject
    private ImageUploadService imageUploadService;

    @GET
    @Path("getAllPartImages")
    public Response getAllPartImages() {
        try {
            JSONObject toReturn = new JSONObject();
            ArrayList<PartImage> images = PartImage.getAllPartImages();

            if (images == null) {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to retrieve images");
                return Response.status(500).entity(error.toString()).build();
            }

            JSONArray jsonArray = new JSONArray();

            for (PartImage image : images) {
                JSONObject json = new JSONObject();
                json.put("id", image.getId());
                json.put("partId", image.getPartId());
                json.put("url", image.getUrl());
                json.put("isPrimary", image.getIsPrimary());
                json.put("createdAt", image.getCreatedAt());
                jsonArray.put(json);
            }
            toReturn.put("result", jsonArray);

            return Response.ok(toReturn.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @GET
    @Path("getPartImagesByPartId")
    public Response getPartImagesByPartId(@QueryParam("partId") Integer partId) {
        try {
            // Validáció
            if (partId == null || partId <= 0) {
                JSONObject error = new JSONObject();
                error.put("error", "Invalid partId");
                return Response.status(400).entity(error.toString()).build();
            }

            ArrayList<PartImage> images = PartImage.getPartImagesByPartId(partId);

            if (images == null) {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to retrieve images");
                return Response.status(500).entity(error.toString()).build();
            }

            JSONArray jsonArray = new JSONArray();

            for (PartImage image : images) {
                JSONObject json = new JSONObject();
                json.put("id", image.getId());
                json.put("partId", image.getPartId());
                json.put("url", image.getUrl());
                json.put("isPrimary", image.getIsPrimary());
                json.put("createdAt", image.getCreatedAt());
                jsonArray.put(json);
            }

            return Response.ok(jsonArray.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @GET
    @Path("getPartImageById")
    public Response getPartImageById(@QueryParam("id") Integer id) {
        try {
            // Validáció
            if (id == null || id <= 0) {
                JSONObject error = new JSONObject();
                error.put("error", "Invalid image ID");
                return Response.status(400).entity(error.toString()).build();
            }

            PartImage image = PartImage.getPartImageById(id);

            if (image == null) {
                JSONObject error = new JSONObject();
                error.put("error", "Image not found");
                return Response.status(404).entity(error.toString()).build();
            }

            JSONObject json = new JSONObject();
            json.put("id", image.getId());
            json.put("partId", image.getPartId());
            json.put("url", image.getUrl());
            json.put("isPrimary", image.getIsPrimary());
            json.put("createdAt", image.getCreatedAt());

            return Response.ok(json.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    /**
     * POST /api/part-images/upload Kép feltöltése (multipart/form-data)
     */
    @POST
    @Path("uploadPartImage")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadPartImage(MultipartFormDataInput input) {
        try {
            // 1. Form adatok kinyerése
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();

            // partId
            List<InputPart> partIdParts = uploadForm.get("partId");
            if (partIdParts == null || partIdParts.isEmpty()) {
                JSONObject error = new JSONObject();
                error.put("error", "partId is required");
                return Response.status(400).entity(error.toString()).build();
            }
            String partIdStr = partIdParts.get(0).getBodyAsString();
            Integer partId = Integer.parseInt(partIdStr);

            // isPrimary (opcionális, default: false)
            Boolean isPrimary = false;
            List<InputPart> isPrimaryParts = uploadForm.get("isPrimary");
            if (isPrimaryParts != null && !isPrimaryParts.isEmpty()) {
                String isPrimaryStr = isPrimaryParts.get(0).getBodyAsString();
                isPrimary = Boolean.parseBoolean(isPrimaryStr);
            }

            // file
            List<InputPart> fileParts = uploadForm.get("file");
            if (fileParts == null || fileParts.isEmpty()) {
                JSONObject error = new JSONObject();
                error.put("error", "No file uploaded");
                return Response.status(400).entity(error.toString()).build();
            }

            InputPart filePart = fileParts.get(0);

            // 2. Fájlnév kinyerése
            MultivaluedMap<String, String> header = filePart.getHeaders();
            String fileName = getFileName(header);

            // 3. Validáció
            if (!imageUploadService.isValidImageType(fileName)) {
                JSONObject error = new JSONObject();
                error.put("error", "Invalid file type. Only jpg, jpeg, png, webp, gif allowed");
                return Response.status(400).entity(error.toString()).build();
            }

            // 4. InputStream
            InputStream inputStream = filePart.getBody(InputStream.class, null);

            // 5. Feltöltés szerverre (ImageUploadService)
            String imageUrl = imageUploadService.uploadImage(inputStream, fileName, "parts");

            // 6. URL mentése adatbázisba
            PartImage newImage = new PartImage(partId, imageUrl, isPrimary);
            Integer newId = PartImage.createPartImage(newImage);

            if (newId == null || newId <= 0) {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to save image to database");
                return Response.status(500).entity(error.toString()).build();
            }

            // 7. Válasz
            JSONObject response = new JSONObject();
            response.put("message", "Image uploaded successfully");
            response.put("id", newId);
            response.put("url", imageUrl);
            response.put("partId", partId);
            response.put("isPrimary", isPrimary);

            return Response.status(201).entity(response.toString()).build();

        } catch (NumberFormatException e) {
            JSONObject error = new JSONObject();
            error.put("error", "Invalid partId format");
            return Response.status(400).entity(error.toString()).build();
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @POST
    @Path("createPartImage")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPartImage(String body) {
        try {
            JSONObject json = new JSONObject(body);

            // Validáció
            if (!json.has("partId") || !json.has("url")) {
                JSONObject error = new JSONObject();
                error.put("error", "partId and url are required");
                return Response.status(400).entity(error.toString()).build();
            }

            Integer partId = json.getInt("partId");
            String url = json.getString("url");
            Boolean isPrimary = json.optBoolean("isPrimary", false);

            if (GoogleDriveUtil.isGoogleDriveUrl(url)) {
                url = GoogleDriveUtil.convertToDirectUrl(url);
            }

            // Validáció
            if (partId <= 0) {
                JSONObject error = new JSONObject();
                error.put("error", "Invalid partId");
                return Response.status(400).entity(error.toString()).build();
            }

            if (url == null || url.trim().isEmpty()) {
                JSONObject error = new JSONObject();
                error.put("error", "URL cannot be empty");
                return Response.status(400).entity(error.toString()).build();
            }

            // URL mentése
            PartImage newImage = new PartImage(partId, url, isPrimary);
            Integer newId = PartImage.createPartImage(newImage);

            if (newId == null || newId <= 0) {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to create image");
                return Response.status(500).entity(error.toString()).build();
            }

            JSONObject response = new JSONObject();
            response.put("message", "Image created successfully");
            response.put("id", newId);

            return Response.status(201).entity(response.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @PUT
    @Path("updatePartImage")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePartImage(@QueryParam("id") Integer id, String body) {
        try {
            // Validáció
            if (id == null || id <= 0) {
                JSONObject error = new JSONObject();
                error.put("error", "Invalid image ID");
                return Response.status(400).entity(error.toString()).build();
            }

            JSONObject json = new JSONObject(body);

            if (!json.has("url")) {
                JSONObject error = new JSONObject();
                error.put("error", "url is required");
                return Response.status(400).entity(error.toString()).build();
            }

            String url = json.getString("url");
            Boolean isPrimary = json.optBoolean("isPrimary", false);
            Boolean isDeleted = json.optBoolean("isDeleted", false);

            // Létező kép ellenőrzése
            PartImage existingImage = PartImage.getPartImageById(id);
            if (existingImage == null) {
                JSONObject error = new JSONObject();
                error.put("error", "Image not found");
                return Response.status(404).entity(error.toString()).build();
            }

            // Frissítés
            existingImage.setUrl(url);
            existingImage.setIsPrimary(isPrimary);
            existingImage.setIsDeleted(isDeleted);

            Boolean success = PartImage.updatePartImage(existingImage);

            if (!success) {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to update image");
                return Response.status(500).entity(error.toString()).build();
            }

            JSONObject response = new JSONObject();
            response.put("message", "Image updated successfully");

            return Response.ok(response.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    @DELETE
    @Path("softDeletePartImage")
    public Response softDeletePartImage(@QueryParam("id") Integer id) {
        try {
            // Validáció
            if (id == null || id <= 0) {
                JSONObject error = new JSONObject();
                error.put("error", "Invalid image ID");
                return Response.status(400).entity(error.toString()).build();
            }

            // Létező kép ellenőrzése
            PartImage image = PartImage.getPartImageById(id);
            if (image == null) {
                JSONObject error = new JSONObject();
                error.put("error", "Image not found");
                return Response.status(404).entity(error.toString()).build();
            }

            // Soft delete
            Boolean success = PartImage.softDeletePartImage(id);

            if (!success) {
                JSONObject error = new JSONObject();
                error.put("error", "Failed to delete image");
                return Response.status(500).entity(error.toString()).build();
            }

            // Opcionális a fizikai fájl törlése is
            // imageUploadService.deleteImage(image.getUrl());
            JSONObject response = new JSONObject();
            response.put("message", "Image deleted successfully");

            return Response.ok(response.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("error", e.getMessage());
            return Response.status(500).entity(error.toString()).build();
        }
    }

    /**
     * Helper Fájlnév kinyerése a header-ből ezt ötletnek kaptam ennek utána
     * kell nézni
     */
    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown.jpg";
    }
}
