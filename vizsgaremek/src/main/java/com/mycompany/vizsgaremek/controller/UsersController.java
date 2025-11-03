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
                bodyObject.getString("role"),
                bodyObject.getString("authSecret"),
                bodyObject.getString("registrationToken")
        );

        JSONObject toReturn = layer.createUser(createdUser);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }

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
    public Response ReadUserById(@QueryParam("id") Integer id){
        
        JSONObject toReturn = layer.ReadUserById(id);
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString())).entity(toReturn.toString()).type(MediaType.APPLICATION_JSON).build();
    }
}
