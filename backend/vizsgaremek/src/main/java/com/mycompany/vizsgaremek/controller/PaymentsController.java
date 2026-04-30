/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Orders;
import com.mycompany.vizsgaremek.model.Payments;
import com.mycompany.vizsgaremek.service.PaymentsService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author neblgergo
 */
@Path("payments")
public class PaymentsController {

    @Context
    private UriInfo context;
    private PaymentsService layer = new PaymentsService();
    private final JwtUtil jwt = new JwtUtil();

    /**
     * Creates a new instance of PaymentsController
     */
    public PaymentsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.PaymentsController
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
     * PUT method for updating or creating an instance of PaymentsController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createPayments")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPaymentsController(String body, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject bodyObject = new JSONObject(body);

        Orders order = new Orders();
        order.setId(bodyObject.has("orderId") ? bodyObject.getInt("orderId") : null);

        Payments createdPayments = new Payments(
                bodyObject.has("amount") ? bodyObject.getBigDecimal("amount") : null,
                bodyObject.has("method") ? bodyObject.getString("method") : null,
                bodyObject.has("status") ? bodyObject.getString("status") : null,
                null,
                order
        );

        JSONObject toReturn = layer.createPaymentsService(createdPayments);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllPayments")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllPaymentsController(@HeaderParam("token") String jwtToken) {

        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        //System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
        if (!jwtRole.equals("admin")) {
            errors.put("userNotAuthorised");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 401);

            return Response.status(401)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        PaymentsService paymentsSerice = new PaymentsService();
        JSONObject toReturn = paymentsSerice.getAllPaymentsService();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getPaymentById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getPaymentByIdController(@QueryParam("id") Integer Id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        //System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
        if (!jwtRole.equals("admin")) {
            errors.put("userNotAuthorised");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 401);

            return Response.status(401)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        PaymentsService paymentsSerice = new PaymentsService();

        JSONObject toReturn = paymentsSerice.getPaymentByIdService(Id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getPaymentsByOrderId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaymentsByOrderIdController(@QueryParam("orderId") Integer orderId, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        //System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
        if (!jwtRole.equals("admin")) {
            errors.put("userNotAuthorised");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 401);

            return Response.status(401)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject toReturn = layer.getPaymentsByOrderIdService(orderId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeletePayment")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeletePaymentController(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        //System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
        if (!jwtRole.equals("admin")) {
            errors.put("userNotAuthorised");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 401);

            return Response.status(401)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject toReturn = layer.softDeletePaymentService(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updatePayment")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePaymentController(
            @QueryParam("id") Integer id,
            String body, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        //System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
        if (!jwtRole.equals("admin")) {
            errors.put("userNotAuthorised");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 401);

            return Response.status(401)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        JSONObject bodyObject = new JSONObject(body);

        Payments updatedPayments = new Payments();

        if (id != null) {
            updatedPayments.setId(id);
        }

        if (bodyObject.has("amount")) {
            updatedPayments.setAmount(bodyObject.getBigDecimal("amount"));
        }

        if (bodyObject.has("method")) {
            updatedPayments.setMethod(bodyObject.getString("method"));
        }

        if (bodyObject.has("status")) {
            updatedPayments.setStatus(bodyObject.getString("status"));
        }

        if (bodyObject.has("isDeleted")) {
            updatedPayments.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updatePaymentService(updatedPayments);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Path("processPayment")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response processPaymentController(String body, @HeaderParam("token") String jwtToken) {

        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject bodyObject = new JSONObject(body);

        Orders order = new Orders();
        order.setId(bodyObject.has("orderId") ? bodyObject.getInt("orderId") : null);

        Payments processPayment = new Payments(
                bodyObject.has("method") ? bodyObject.getString("method") : null,
                order
        );

        JSONObject toReturn = layer.processPaymentService(processPayment);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }   
    
}
