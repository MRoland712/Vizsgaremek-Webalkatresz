/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.UserVehicles;
import com.mycompany.vizsgaremek.service.UserVehiclesService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
@Path("userVehicles")
public class UserVehiclesController {

    @Context
    private UriInfo context;
    private UserVehiclesService layer = new UserVehiclesService();
    private final JwtUtil jwt = new JwtUtil();

    /**
     * Creates a new instance of UserVehiclesController
     */
    public UserVehiclesController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.UserVehiclesController
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
     * PUT method for updating or creating an instance of UserVehiclesController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createUserVehicle")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUserVehicleController(String body, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        Integer userId = jwt.extractUserId(jwtToken);

        JSONObject bodyObject = new JSONObject(body);

        UserVehicles uv = new UserVehicles(
                userId,
                bodyObject.has("vehicleType") ? bodyObject.getString("vehicleType") : null,
                bodyObject.has("vehicleId") ? bodyObject.getInt("vehicleId") : null,
                bodyObject.has("year") ? bodyObject.getInt("year") : null
        );

        JSONObject toReturn = layer.createUserVehicleService(uv);
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getUserVehiclesByUserId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserVehiclesByUserIdController(@HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

   
        Integer userId = jwt.extractUserId(jwtToken);

        JSONObject toReturn = layer.getUserVehiclesByUserIdService(userId);
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteUserVehicle")
    public Response softDeleteUserVehicleController(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.softDeleteUserVehicleService(id);
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
