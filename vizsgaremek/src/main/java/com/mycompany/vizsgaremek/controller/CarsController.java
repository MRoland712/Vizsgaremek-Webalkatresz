/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Cars;
import com.mycompany.vizsgaremek.service.CarsService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import com.mycompany.vizsgaremek.config.JwtUtil;
import javax.ws.rs.HeaderParam;
import org.json.JSONArray;

/**
 * REST Web Service
 *
 * @author neblgergo
 */
@Path("cars")
public class CarsController {

    @Context
    private UriInfo context;
    private CarsService layer = new CarsService();
    private final JwtUtil jwt = new JwtUtil();

    /**
     * Creates a new instance of CarsController
     */
    public CarsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.CarsController
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
     * PUT method for updating or creating an instance of CarsController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createCars")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCars(String body, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONArray errors = new JSONArray();
        String jwtRole = jwt.extractRole(jwtToken);
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

        Cars createdCars = new Cars(
                bodyObject.has("brand") ? bodyObject.getString("brand") : null,
                bodyObject.has("model") ? bodyObject.getString("model") : null,
                bodyObject.has("yearFrom") ? bodyObject.getInt("yearFrom") : null,
                bodyObject.has("yearTo") ? bodyObject.getInt("yearTo") : null
        );

        JSONObject toReturn = layer.createCars(createdCars);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllCars")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllCars() {
        CarsService carsService = new CarsService();
        JSONObject toReturn = carsService.getAllCars();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getCarsById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarsById(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONArray errors = new JSONArray();
        String jwtRole = jwt.extractRole(jwtToken);
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

        JSONObject toReturn = layer.getCarsById(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getCarsByModel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarsByModel(@QueryParam("model") String model, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.getCarsByModel(model);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getCarsByBrand")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCarsByBrand(@QueryParam("brand") String brand, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.getCarsByBrand(brand);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteCars")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteCars(@QueryParam("id") Integer carsId, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONArray errors = new JSONArray();
        String jwtRole = jwt.extractRole(jwtToken);
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

        JSONObject toReturn = layer.softDeleteCars(carsId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateCars")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCars(@QueryParam("id") Integer carsId, String body, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONArray errors = new JSONArray();
        String jwtRole = jwt.extractRole(jwtToken);
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

        Cars updatedCars = new Cars();
        updatedCars.setId(carsId);

        if (bodyObject.has("brand")) {
            updatedCars.setBrand(bodyObject.getString("brand"));
        }

        if (bodyObject.has("model")) {
            updatedCars.setModel(bodyObject.getString("model"));
        }

        if (bodyObject.has("yearFrom")) {
            updatedCars.setYearFrom(bodyObject.getInt("yearFrom"));
        }

        if (bodyObject.has("yearTo")) {
            updatedCars.setYearTo(bodyObject.getInt("yearTo"));
        }
        if (bodyObject.has("isDeleted")) {
            updatedCars.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updateCars(updatedCars);

        int statusCode = toReturn.getInt("statusCode");
        return Response.status(statusCode)
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
