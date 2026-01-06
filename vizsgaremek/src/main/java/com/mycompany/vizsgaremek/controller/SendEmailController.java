/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import com.mycompany.vizsgaremek.config.SendEmail;
import com.mycompany.vizsgaremek.model.Users;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author ddori
 */
@Path("email")
public class SendEmailController {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SendEmailController
     */
    public SendEmailController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.SendEmailController
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
     * PUT method for updating or creating an instance of SendEmailController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    @POST
    @Path("sendPromotion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendPromotionEmail(String body) {
        JSONObject bodyObject = new JSONObject(body);
        JSONArray errors = new JSONArray();

        String messageContent = bodyObject.has("message") ? bodyObject.getString("message") : null;

        if (messageContent == null || messageContent.trim().isEmpty()) {
            errors.put("MissingMessage");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        try {
            SendEmail.sendPromotionEmailToSubscribedUsers(messageContent);

            JSONObject successResponse = new JSONObject();
            successResponse.put("status", "success");
            successResponse.put("statusCode", 200);
            successResponse.put("message", "Promotion emails sent successfully");

            return Response.status(200)
                    .entity(successResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception ex) {
            ex.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("statusCode", 500);
            errorResponse.put("message", "Failed to send emails: " + ex.getMessage());

            return Response.status(500)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
    
    @POST
    @Path("sendOTP")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendOTPEmailAndSetAuthSecretController(@QueryParam("email") String email) {
        JSONArray errors = new JSONArray();

        Users userdata = Users.getUserByEmail(email);
        System.out.println("sendOTPEmailAndSetAuthSecretController: "+ email + " | " + userdata);
        if (userdata == null) {
            errors.put("UserNotFound");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 404);

            return Response.status(404)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        
        try {
            SendEmail.sendOTPEmailAndSetAuthSecret(email, userdata);

            JSONObject successResponse = new JSONObject();
            successResponse.put("status", "success");
            successResponse.put("statusCode", 200);
            successResponse.put("message", "OTP email sent successfully to "+ email);

            return Response.status(200)
                    .entity(successResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception ex) {
            ex.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("statusCode", 500);
            errorResponse.put("message", "Failed to send emails: " + ex.getMessage());

            return Response.status(500)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

}
