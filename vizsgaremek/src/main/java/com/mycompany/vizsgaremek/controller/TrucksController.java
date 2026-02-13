/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Trucks;
import com.mycompany.vizsgaremek.service.TrucksService;
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
 * @author neblgergo
 */
@Path("trucks")
public class TrucksController {

    @Context
    private UriInfo context;
    private TrucksService layer = new TrucksService();
    /**
     * Creates a new instance of TrucksController
     */
    public TrucksController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.TrucksController
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
     * PUT method for updating or creating an instance of TrucksController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createTrucks")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTrucks(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Trucks createdTrucks = new Trucks(
                bodyObject.has("brand") ? bodyObject.getString("brand") : null,
                bodyObject.has("model") ? bodyObject.getString("model") : null,
                bodyObject.has("yearFrom") ? bodyObject.getInt("yearFrom") : null,
                bodyObject.has("yearTo") ? bodyObject.getInt("yearTo") : null
        );

        JSONObject toReturn = layer.createTrucks(createdTrucks);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllTrucks")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllTrucks() {
        TrucksService trucksService = new TrucksService();
        JSONObject toReturn = trucksService.getAllTrucks();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getTrucksById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrucksById(@QueryParam("id") Integer id) {

        JSONObject toReturn = layer.getTrucksById(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getTrucksByModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrucksByModel(@QueryParam("model") String model) {

        JSONObject toReturn = layer.getTrucksByModel(model);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getTrucksByBrand")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrucksByBrand(@QueryParam("brand") String brand) {

        JSONObject toReturn = layer.getTrucksByBrand(brand);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteTrucks")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteTrucks(@QueryParam("id") Integer trucksId) {

        JSONObject toReturn = layer.softDeleteTrucks(trucksId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @PUT
    @Path("updateTrucks")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTrucks(@QueryParam("id") Integer trucksId, String body) {

        JSONObject bodyObject = new JSONObject(body);

        Trucks updatedTrucks = new Trucks();
        updatedTrucks.setId(trucksId); 

        if (bodyObject.has("brand")) {
            updatedTrucks.setBrand(bodyObject.getString("brand"));
        }

        if (bodyObject.has("model")) {
            updatedTrucks.setModel(bodyObject.getString("model"));
        }
        
        if (bodyObject.has("yearFrom")) {
            updatedTrucks.setYearFrom(bodyObject.getInt("yearFrom"));
        }
        
        if (bodyObject.has("yearTo")) {
            updatedTrucks.setYearTo(bodyObject.getInt("yearTo"));
        }
        if (bodyObject.has("isDeleted")) {
            updatedTrucks.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updateTrucks(updatedTrucks);

        int statusCode = toReturn.getInt("statusCode");
        return Response.status(statusCode)
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
