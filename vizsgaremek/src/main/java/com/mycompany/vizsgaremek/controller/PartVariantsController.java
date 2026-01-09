/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Parts;
import com.mycompany.vizsgaremek.model.PartVariants;
import com.mycompany.vizsgaremek.service.PartVariantsService;
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
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author neblg
 */
@Path("partVariants")
public class PartVariantsController {

    @Context
    private UriInfo context;
    private PartVariantsService layer = new PartVariantsService();

    /**
     * Creates a new instance of PartVariantsController
     */
    public PartVariantsController() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.vizsgaremek.controller.PartVariantsController
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of PartVariantsController
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @POST
    @Path("createPartVariants")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPartVaraints(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Parts part = new Parts();
        part.setId(bodyObject.has("partId") ? bodyObject.getInt("partId") : null);
        PartVariants createdPartVariants = new PartVariants(
                part,
                bodyObject.has("name") ? bodyObject.getString("name") : null,
                bodyObject.has("value") ? bodyObject.getString("value") : null,
                bodyObject.has("additionalPrice") ? bodyObject.getBigDecimal("additionalPrice") : null
        );
        JSONObject toReturn = layer.createPartVariants(createdPartVariants);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("getAllPartVariants")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllPartVariants() {
        PartVariantsService partvariantsService = new PartVariantsService();
        JSONObject toReturn = partvariantsService.getAllPartVariants();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("getPartVariantsById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPartVariantsById(@QueryParam("id") Integer Id) {
        PartVariantsService partsVariantsService = new PartVariantsService();
        
        JSONObject toReturn = partsVariantsService.getPartVariantsById(Id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @DELETE
    @Path("softDeletePartVariants")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeletePartVariants(@QueryParam("id") Integer partVariantsId) {

        JSONObject toReturn = layer.softDeletePartVariants(partVariantsId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @PUT
    @Path("updatePartVariants")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePartVariants(
            @QueryParam("id") Integer partVariantsId,
            String body) {

        JSONObject bodyObject = new JSONObject(body);

        PartVariants updatedPartVariants = new PartVariants();

        if (partVariantsId != null) {
            updatedPartVariants.setId(partVariantsId);
        }


        if (bodyObject.has("name")) {
            updatedPartVariants.setName(bodyObject.getString("name"));
        }

        if (bodyObject.has("value")) {
            updatedPartVariants.setValue(bodyObject.getString("value"));
        }

        if (bodyObject.has("additionalPrice")) {
            updatedPartVariants.setAdditionalPrice(bodyObject.getBigDecimal("additionalPrice"));
        }
        
        if (bodyObject.has("isDeleted")) {
            updatedPartVariants.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updatePartVariants(updatedPartVariants);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("getPartVariantsByName")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPartVariantsByName(@QueryParam("name") String name) {
        PartVariantsService partsVariantsService = new PartVariantsService();
        
        JSONObject toReturn = partsVariantsService.getPartVariantsByName(name);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("getPartVariantsByValue")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPartVariantsByValue(@QueryParam("value") String value) {
        PartVariantsService partsVariantsService = new PartVariantsService();
        
        JSONObject toReturn = partsVariantsService.getPartVariantsByValue(value);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
}