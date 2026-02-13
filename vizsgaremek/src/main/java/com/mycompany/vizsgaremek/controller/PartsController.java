/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Manufacturers;
import com.mycompany.vizsgaremek.service.PartsService;
import com.mycompany.vizsgaremek.model.Parts;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
@Path("parts")
public class PartsController {

    @Context
    private UriInfo context;
    private PartsService layer = new PartsService();

    /**
     * Creates a new instance of PartsController
     */
    public PartsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.PartsController
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
     * PUT method for updating or creating an instance of PartsController
     *
     * @param content representation for the resource
     */
    /*@PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }*/

    @POST
    @Path("createParts")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createParts(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Manufacturers manufacturer = new Manufacturers();
        manufacturer.setId(bodyObject.has("manufacturerId") ? bodyObject.getInt("manufacturerId") : null);

        Parts createdParts = new Parts(
                bodyObject.has("sku") ? bodyObject.getString("sku") : null,
                bodyObject.has("name") ? bodyObject.getString("name") : null,
                bodyObject.has("category") ? bodyObject.getString("category") : null,
                bodyObject.has("price") ? bodyObject.getBigDecimal("price") : null,
                bodyObject.has("stock") ? bodyObject.getInt("stock") : null,
                bodyObject.has("status") ? bodyObject.getString("status") : null,
                bodyObject.has("isActive") ? bodyObject.getBoolean("isActive") : false,
                manufacturer
        );

        JSONObject toReturn = layer.createParts(createdParts);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllParts")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllParts() {
        PartsService partsService = new PartsService();
        JSONObject toReturn = partsService.getAllParts();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getPartsById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPartsById(@QueryParam("id") Integer Id) {
        PartsService partsService = new PartsService();
        
        JSONObject toReturn = partsService.getPartsById(Id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("getPartsByManufacturerId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartsByManufacturerId(@QueryParam("manufacturerId") Integer id) {

        JSONObject toReturn = layer.getPartsByManufacturerId(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @DELETE
    @Path("softDeleteParts")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteParts(@QueryParam("id") Integer partsId) {

        JSONObject toReturn = layer.softDeleteParts(partsId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("getPartsCategory")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPartsCategory() {
        PartsService partsService = new PartsService();
        JSONObject toReturn = partsService.getPartsCategory();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @PUT
    @Path("updateParts")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateParts(
            @QueryParam("id") Integer partsId,
            @QueryParam("sku") String sku,
            String body) {

        JSONObject bodyObject = new JSONObject(body);

        Parts updatedParts = new Parts();

        if (partsId != null) {
            updatedParts.setId(partsId);
        }

        if (sku != null) {
            updatedParts.setSku(sku);
        }
        
        if (bodyObject.has("name")) {
            updatedParts.setName(bodyObject.getString("name"));
        }

        if (bodyObject.has("category")) {
            updatedParts.setCategory(bodyObject.getString("category"));
        }

        if (bodyObject.has("price")) {
            updatedParts.setPrice(bodyObject.getBigDecimal("price"));
        }

        if (bodyObject.has("stock")) {
            updatedParts.setStock(bodyObject.getInt("stock"));
        }

        if (bodyObject.has("status")) {
            updatedParts.setStatus(bodyObject.getString("status"));
        }
        
        if (bodyObject.has("isActive")) {
            updatedParts.setIsActive(bodyObject.getBoolean("isActive"));
        }
        
        if (bodyObject.has("isDeleted")) {
            updatedParts.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updateParts(updatedParts);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("getPartsBySku")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPartsBySku(@QueryParam("sku") String sku) {
        PartsService partsService = new PartsService();
        
        JSONObject toReturn = partsService.getPartsBySku(sku);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
