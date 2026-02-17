/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Orders;
import com.mycompany.vizsgaremek.model.Payments;
import com.mycompany.vizsgaremek.service.PaymentsService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
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
@Path("payments")
public class PaymentsController {

    @Context
    private UriInfo context;
    private PaymentsService layer = new PaymentsService();

    /**
     * Creates a new instance of PaymentsController
     */
    public PaymentsController() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.vizsgaremek.controller.PaymentsController
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
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @POST
    @Path("createPayments")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPaymentsController(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Orders order = new Orders();
        order.setId(bodyObject.has("orderId") ? bodyObject.getInt("orderId") : null);
        
        Payments createdPayments = new Payments(
                bodyObject.has("amount") ? bodyObject.getBigDecimal("amount") : null,
                bodyObject.has("method") ? bodyObject.getString("method"): null,
                bodyObject.has("status") ? bodyObject.getString("status"): null,
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
    public Response getAllPaymentsController() {
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
    public Response getPaymentByIdController(@QueryParam("id") Integer Id) {
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
    public Response getPaymentsByOrderIdController(@QueryParam("orderId") Integer orderId) {

        JSONObject toReturn = layer.getPaymentsByOrderIdService(orderId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @DELETE
    @Path("softDeletePayment")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeletePaymentController(@QueryParam("id") Integer id) {

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
            String body) {

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
}
