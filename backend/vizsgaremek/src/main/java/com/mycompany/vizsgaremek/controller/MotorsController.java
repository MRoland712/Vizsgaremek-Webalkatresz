/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Motors;
import com.mycompany.vizsgaremek.service.MotorsService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author neblgergo
 */
@Path("motors")
public class MotorsController {

    @Context
    private UriInfo context;
    private MotorsService layer = new MotorsService();
    private final JwtUtil jwt = new JwtUtil();


    /**
     * Creates a new instance of MotorsController
     */
    public MotorsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.MotorsController
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
     * PUT method for updating or creating an instance of MotorsController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createMotors")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createMotors(String body, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        //System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
        if (!jwtRole.equals("admin")) {
            errors.put("userNotAuthorised");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 401);

            return Response.status(401)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject bodyObject = new JSONObject(body);

        Motors createdMotors = new Motors(
                bodyObject.has("brand") ? bodyObject.getString("brand") : null,
                bodyObject.has("model") ? bodyObject.getString("model") : null,
                bodyObject.has("yearFrom") ? bodyObject.getInt("yearFrom") : null,
                bodyObject.has("yearTo") ? bodyObject.getInt("yearTo") : null
        );

        JSONObject toReturn = layer.createMotors(createdMotors);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllMotors")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllMotors(){
        MotorsService motorsService = new MotorsService();
        JSONObject toReturn = motorsService.getAllMotors();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getMotorsById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMotorsById(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        //System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
        if (!jwtRole.equals("admin")) {
            errors.put("userNotAuthorised");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 401);

            return Response.status(401)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject toReturn = layer.getMotorsById(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getMotorsByModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMotorsByModel(@QueryParam("model") String model) {

        JSONObject toReturn = layer.getMotorsByModel(model);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getMotorsByBrand")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMotorsByBrand(@QueryParam("brand") String brand) {

        JSONObject toReturn = layer.getMotorsByBrand(brand);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteMotors")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteMotors(@QueryParam("id") Integer carsId, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        //System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
        if (!jwtRole.equals("admin")) {
            errors.put("userNotAuthorised");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 401);

            return Response.status(401)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject toReturn = layer.softDeleteMotors(carsId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @PUT
    @Path("updateMotors")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMotors(@QueryParam("id") Integer motorsId, String body, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        //System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
        if (!jwtRole.equals("admin")) {
            errors.put("userNotAuthorised");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 401);

            return Response.status(401)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject bodyObject = new JSONObject(body);

        Motors updatedMotors = new Motors();
        updatedMotors.setId(motorsId); 

        if (bodyObject.has("brand")) {
            updatedMotors.setBrand(bodyObject.getString("brand"));
        }

        if (bodyObject.has("model")) {
            updatedMotors.setModel(bodyObject.getString("model"));
        }
        
        if (bodyObject.has("yearFrom")) {
            updatedMotors.setYearFrom(bodyObject.getInt("yearFrom"));
        }
        
        if (bodyObject.has("yearTo")) {
            updatedMotors.setYearTo(bodyObject.getInt("yearTo"));
        }
        if (bodyObject.has("isDeleted")) {
            updatedMotors.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updateMotors(updatedMotors);

        int statusCode = toReturn.getInt("statusCode");
        return Response.status(statusCode)
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
