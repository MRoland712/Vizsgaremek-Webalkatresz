/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.service.UsersService;
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
 * @author neblg
 */
@Path("user")
public class UsersController {

    @Context
    private UriInfo context;

    private UsersService layer = new UsersService();

    /**
     * Creates a new instance of UsersController
     */
    public UsersController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.UsersController
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
     * PUT method for updating or creating an instance of UsersController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    @POST
    @Path("createUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String body) {
        JSONObject bodyObject = new JSONObject(body);

        Users createdUser = new Users(
                bodyObject.getString("email"),
                bodyObject.getString("username"),
                bodyObject.getString("password"),
                bodyObject.getString("firstName"),
                bodyObject.getString("lastName"),
                bodyObject.getString("phone"),
                bodyObject.has("role") ? bodyObject.getString("role") : null
        );

        JSONObject toReturn = layer.createUser(createdUser);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    /*@POST
    @Path("createUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String body) {
        JSONObject bodyObject = new JSONObject(body);
        Users createdUser = new Users();
        try {
            Users createdUser = Users(
                    bodyObject.getString("email"),
                    bodyObject.getString("username"),
                    bodyObject.getString("password"),
                    bodyObject.getString("firstName"),
                    bodyObject.getString("lastName"),
                    bodyObject.getString("phone"),
                    bodyObject.has("role") ? bodyObject.getString("role") : "-"
            );
        } catch (Exception e) {
            Users createdUser = Users(
                    bodyObject.getString("email"),
                    bodyObject.getString("username"),
                    bodyObject.getString("password"),
                    bodyObject.getString("firstName"),
                    bodyObject.getString("lastName"),
                    bodyObject.getString("phone"),
                    bodyObject.has("role") ? bodyObject.getString("role") : "-"
            );
        }
        JSONObject toReturn = layer.createUser(createdUser);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }*/
    @GET
    @Path("ReadUsers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ReadUsers() {

        JSONObject toReturn = layer.ReadUsers();
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("ReadUserById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ReadUserById(@QueryParam("id") Integer id) {

        JSONObject toReturn = layer.ReadUserById(id);
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("ReadUserByEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ReadUserByEmail(@QueryParam("email") String email) {

        JSONObject toReturn = layer.ReadUserByEmail(email);
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("softDeleteUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteUser(@QueryParam("id") Integer userId) {
        JSONObject toReturn = layer.softDeleteUser(userId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @PUT
    @Path("UpdateUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response UpdateUser(String body) {
        JSONObject bodyObject = new JSONObject(body);

        Users updatedUser = new Users();
        
        if (bodyObject.has("id")) {
            updatedUser.setId(bodyObject.getInt("id"));
        }
        if (bodyObject.has("email")) {
            updatedUser.setEmail(bodyObject.getString("email"));
        }

        if (bodyObject.has("username")) {
            updatedUser.setUsername(bodyObject.getString("username"));
        }

        if (bodyObject.has("firstName")) {
            updatedUser.setFirstName(bodyObject.getString("firstName"));
        }

        if (bodyObject.has("lastName")) {
            updatedUser.setLastName(bodyObject.getString("lastName"));
        }

        if (bodyObject.has("phone")) {
            updatedUser.setPhone(bodyObject.getString("phone"));
        }

        if (bodyObject.has("role")) {
            updatedUser.setRole(bodyObject.getString("role"));
        }

        if (bodyObject.has("isActive")) {
            updatedUser.setIsActive(bodyObject.getBoolean("isActive"));
        }

        if (bodyObject.has("password")) {
            updatedUser.setPassword(bodyObject.getString("password"));
        }

        if (bodyObject.has("authSecret")) {
            updatedUser.setAuthSecret(bodyObject.getString("authSecret"));
        }

        if (bodyObject.has("registrationToken")) {
            updatedUser.setRegistrationToken(bodyObject.getString("registrationToken"));
        }

        JSONObject toReturn = layer.updateUser(updatedUser);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Users user = new Users(
                bodyObject.has("email") ? bodyObject.getString("email") : null,
                bodyObject.has("password") ? bodyObject.getString("password") : null
        );

        JSONObject toReturn = layer.login(user);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
