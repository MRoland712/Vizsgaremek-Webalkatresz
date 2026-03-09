/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.service.PasswordResetsService;
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

/**
 * REST Web Service
 *
 * @author neblgergo
 */
@Path("passwordReset")
public class PasswordResetsController {

    @Context
    private UriInfo context;
    private PasswordResetsService layer = new PasswordResetsService();
    private final JwtUtil jwt = new JwtUtil();

    /**
     * Creates a new instance of PasswordResetController
     */
    public PasswordResetsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.PasswordResetsController
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
     * PUT method for updating or creating an instance of
     * PasswordResetsController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    // Elfelejtett jelszó email küldés
    @POST
    @Path("createPasswordReset")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPasswordResetController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        String email = bodyObject.has("email") ? bodyObject.getString("email") : null;

        JSONObject toReturn = layer.createPasswordResetService(email);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // Token validálás
    @GET
    @Path("getPasswordResetByToken")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPasswordResetByTokenController(@QueryParam("token") String token) {
        JSONObject toReturn = layer.getPasswordResetByTokenService(token);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // Jelszó visszaállítás token + új jelszó
    @PUT
    @Path("updatePasswordReset")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePasswordResetController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        String token = bodyObject.has("token") ? bodyObject.getString("token") : null;
        String newPassword = bodyObject.has("newPassword") ? bodyObject.getString("newPassword") : null;

        JSONObject toReturn = layer.updatePasswordResetService(token, newPassword);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeletePasswordReset")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeletePasswordResetController(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.softDeletePasswordResetService(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    // Jelszó visszaállítás token + új jelszó (transaction SP)
    @PUT
    @Path("resetPassword")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPasswordController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        String token = bodyObject.has("token") ? bodyObject.getString("token") : null;
        String newPassword = bodyObject.has("newPassword") ? bodyObject.getString("newPassword") : null;

        JSONObject toReturn = layer.updatePasswordResetService(token, newPassword);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    

}
