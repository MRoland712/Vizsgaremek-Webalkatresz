/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.TFA;
import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.service.AuthenticationService;

import static com.mycompany.vizsgaremek.config.TFA.generateQRUrl;
import static com.mycompany.vizsgaremek.config.TFA.validateCode;
import static com.mycompany.vizsgaremek.config.TFA.generateSecretKey;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
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
@Path("TFA")
public class TFAController {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TFAController
     */
    public TFAController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.TFAController
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
     * PUT method for updating or creating an instance of TFAController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    private static final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    private static String secretKey = "H2TNYT7AW6AMP2J5P352XNI6YAYH4GEQ"; //TEMP
    
    @POST
    @Path("generateQR")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateQRController(@HeaderParam("token") String jwtToken, String body) {
        Response jwtError = JwtUtil.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject bodyObject = new JSONObject(body);
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (!bodyObject.has("email/username")) {
            errors.put("missingEmail/Username");

            return Response.status(400)
                    .entity(errorAuth.createErrorResponse(errors, 400).toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        /*TEMPCOMMENT: String*/ secretKey = generateSecretKey();
        toReturn.put("secretKey", secretKey);
        String QR = generateQRUrl(secretKey, bodyObject.getString("email/username"));
        toReturn.put("QR", QR);

        return Response.status(200)
                .entity(errorAuth.createOKResponse(toReturn).toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @POST
    @Path("validateTFACode")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response validateTFACode(@HeaderParam("token") String jwtToken, String body) {
        
        Response jwtError = JwtUtil.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject bodyObject = new JSONObject(body);
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (!bodyObject.has("code")) {
            errors.put("missingCode");

            return Response.status(400)
                    .entity(errorAuth.createErrorResponse(errors, 400).toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        //ToDo: getTwoFaSecret
        String isValid;
        if (validateCode(secretKey, bodyObject.getString("code"))) {
              isValid = "valid";
        } else {
              isValid = "invalid";
        }
        
        return Response.status(200)
                .entity(errorAuth.createOKResponse(isValid).toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
