/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.UserLogs;
import com.mycompany.vizsgaremek.service.AuthenticationService;
import com.mycompany.vizsgaremek.service.UserLogsService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
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
 * @author ddori
 */
@Path("userLogs")
public class UserLogsController {

    private UserLogsService layer = new UserLogsService();
    private final JwtUtil jwt = new JwtUtil();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of UserLogsController
     */
    public UserLogsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.UserLogsController
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of UserLogsController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    @POST
    @Path("createUserLog")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUserLogs(String body, @QueryParam("userId") Integer userId, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject bodyObject = new JSONObject(body);

        UserLogs createdUserLog = new UserLogs(
                bodyObject.has("action") ? bodyObject.getString("action") : null,
                bodyObject.has("details") ? bodyObject.getString("details") : null
        );

        JSONObject toReturn = layer.createUserLogs(createdUserLog, userId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();

    }

    /*@PUT
    @Path("updateUserLogs")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserLogs(String body, @QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }
        JSONObject bodyObject = new JSONObject(body);
        
        Integer userId = bodyObject.has("userId") ? bodyObject.getInt("userId") : null; //olcsó megoldás, nem tudtam jobbatt :C
                
        UserLogs updatedUserLog = new UserLogs(
                bodyObject.has("action") ? bodyObject.getString("action") : null,
                bodyObject.has("details") ? bodyObject.getString("details") : null
        );

        JSONObject toReturn = layer.updateUserLogs(updatedUserLog, id, userId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getUserLogById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserLogById(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        if (AuthenticationService.isDataMissing(id)) {
            id = null;
        }

        JSONObject toReturn = layer.getUserLogById(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }*/
}
