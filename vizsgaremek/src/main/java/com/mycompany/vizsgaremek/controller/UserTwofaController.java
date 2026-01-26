/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.service.AuthenticationService;
import com.mycompany.vizsgaremek.service.UserTwofaService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author ddori
 */
@Path("UserTwofa")
public class UserTwofaController {

    private UserTwofaService layer = new UserTwofaService();
    public static final AuthenticationService.userTwofaAuth userTFAAuth = new AuthenticationService.userTwofaAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();
    private final JwtUtil jwt = new JwtUtil();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of UserTwofaController
     */
    public UserTwofaController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.UserTwofaController
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
     * PUT method for updating or creating an instance of UserTwofaController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createUserTwofa")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUserTwofaController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        JSONObject toReturn = new JSONObject();

        String email = bodyObject.getString("email");
        
        toReturn = layer.createUserTwofaService(email);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
