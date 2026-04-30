/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author ddori
 */
@Path("JWT")
public class JWTController {

    private static final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of JWTController
     */
    public JWTController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.JWTController
     *
     * @return an instance of java.lang.String
     */
    /*@GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }*/

    /**
     * PUT method for updating or creating an instance of JWTController
     *
     * @param content representation for the resource
     */
    /*@PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }*/

    @GET
    @Path("validateJWT")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateJWTEndpoint(@HeaderParam("token") String jwtToken) {
        Response jwtError = JwtUtil.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        return Response.status(200)
                .entity(errorAuth.createOKResponse().toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("extractJWTData")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response extractData(@HeaderParam("token") String jwtToken) {

        Response jwtError = JwtUtil.validateJwtAndReturnError(jwtToken);

        if (jwtError != null) {
            return jwtError;
        }
        
        Claims jwtData = JwtUtil.extractAllClaims(jwtToken);

        return Response.status(200)
                .entity(errorAuth.createOKResponse(jwtData).toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
