/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import org.json.JSONObject;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.config.OTPVerifyer;
import javax.ws.rs.core.Response;
import org.json.JSONArray;

/**
 * REST Web Service
 *
 * @author ddori
 */
@Path("OTP")
public class OTPController {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of OTPController
     */
    public OTPController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.OTPController
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
     * PUT method for updating or creating an instance of OTPController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    @POST
    @Path("verifyOTP")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response verifyOTPController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        JSONArray errors = new JSONArray();

        if (bodyObject.getString("email").trim() == null || !bodyObject.has("email")) {
            errors.put("missingEmail");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 404);

            return Response.status(404)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        Users userdata = Users.getUserByEmail(bodyObject.getString("email"));

        System.out.println(
                "AuthSecret: " + userdata.getAuthSecret() + "\n"
                + "Email: " + userdata.getEmail() + "\n"
                + "FirstName: " + userdata.getFirstName() + "\n"
                + "Guid: " + userdata.getGuid() + "\n"
                + "LastName: " + userdata.getLastName() + "\n"
                + "Password: " + userdata.getPassword() + "\n"
                + "Phone: " + userdata.getPhone() + "\n"
                + "RegistrationToken: " + userdata.getRegistrationToken() + "\n"
                + "Role: " + userdata.getRole() + "\n"
                + "Timezone: " + userdata.getTimezone() + "\n"
                + "Username: " + userdata.getUsername()
        );

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

        if (!bodyObject.has("OTP")) {
            errors.put("missingOTP");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 404);

            return Response.status(404)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (String.valueOf(bodyObject.getInt("OTP")).length() != 6) {
            errors.put("invalidOTP");

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
            Boolean isOTPValid = OTPVerifyer.verifyOTP(userdata, bodyObject.getInt("OTP"));

            JSONObject successResponse = new JSONObject();
            if (!isOTPValid) {
                successResponse.put("status", "success");
                successResponse.put("statusCode", 200);
                successResponse.put("result", "invalid");
            } else {
                successResponse.put("status", "success");
                successResponse.put("statusCode", 200);
                successResponse.put("result", "valid");
            }
            return Response.status(200)
                    .entity(successResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception ex) {
            ex.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("statusCode", 500);
            errorResponse.put("message", "Failed to verify OTP " + ex.getMessage());

            return Response.status(500)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

}
