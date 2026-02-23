/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.PartCompatibility;
import com.mycompany.vizsgaremek.model.Parts;
import com.mycompany.vizsgaremek.service.PartCompatibilityService;
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
 * @author neblgergo
 */
@Path("partCompatibility")
public class PartCompatibilityController {

    @Context
    private UriInfo context;
    private PartCompatibilityService layer = new PartCompatibilityService();

    /**
     * Creates a new instance of PartCompatibilityController
     */
    public PartCompatibilityController() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.vizsgaremek.controller.PartCompatibilityController
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of PartCompatibilityController
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @POST
    @Path("createPartCompatibility")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPartCompatibilityController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        
        Parts part = new Parts();
        part.setId(bodyObject.has("partId") ? bodyObject.getInt("partId") : null);
        
        PartCompatibility createdPartCompatibility = new PartCompatibility(
            bodyObject.has("vehicleType") ? bodyObject.getString("vehicleType") : null,
            bodyObject.has("vehicleId") ? bodyObject.getInt("vehicleId") : null,
            part
        );
        
        JSONObject toReturn = layer.createPartCompatibilityService(createdPartCompatibility);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllPartCompatibility")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPartCompatibilityController() {
        JSONObject toReturn = layer.getAllPartCompatibilityService();
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getPartCompatibilityById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartCompatibilityByIdController(@QueryParam("id") Integer id) {
        JSONObject toReturn = layer.getPartCompatibilityByIdService(id);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getPartCompatibilityByVehicleType")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartCompatibilityByVehicleTypeController(@QueryParam("vehicleType") String vehicleType) {
        JSONObject toReturn = layer.getPartCompatibilityByVehicleTypeService(vehicleType);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getPartCompatibilityByVehicleId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPartCompatibilityByVehicleIdController(@QueryParam("vehicleId") Integer vehicleId) {
        JSONObject toReturn = layer.getPartCompatibilityByVehicleIdService(vehicleId);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeletePartCompatibility")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeletePartCompatibilityController(@QueryParam("id") Integer id) {
        JSONObject toReturn = layer.softDeletePartCompatibilityService(id);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updatePartCompatibility")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePartCompatibilityController(
            @QueryParam("id") Integer id,
            String body) {
        
        JSONObject bodyObject = new JSONObject(body);
        PartCompatibility updatedPartCompatibility = new PartCompatibility();
        
        if (id != null) {
            updatedPartCompatibility.setId(id);
        }
        if (bodyObject.has("vehicleType")) {
            updatedPartCompatibility.setVehicleType(bodyObject.getString("vehicleType"));
        }
        if (bodyObject.has("vehicleId")) {
            updatedPartCompatibility.setVehicleId(bodyObject.getInt("vehicleId"));
        }
        if (bodyObject.has("isDeleted")) {
            updatedPartCompatibility.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }
        
        JSONObject toReturn = layer.updatePartCompatibilityService(updatedPartCompatibility);
        
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
