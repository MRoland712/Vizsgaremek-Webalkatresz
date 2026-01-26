/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.PartImages;
import com.mycompany.vizsgaremek.model.Parts;
import com.mycompany.vizsgaremek.service.PartImagesService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    /**
     * Creates a new instance of PartImages
     */
    public PartImagesController() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.vizsgaremek.controller.PartImagesController
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
}
