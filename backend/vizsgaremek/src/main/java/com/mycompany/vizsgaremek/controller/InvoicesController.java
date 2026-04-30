/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Invoices;
import com.mycompany.vizsgaremek.model.Orders;
import com.mycompany.vizsgaremek.service.InvoicesService;

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
@Path("invoices")
public class InvoicesController {

    @Context
    private UriInfo context;
    private InvoicesService layer = new InvoicesService();
    private final JwtUtil jwt = new JwtUtil();

    /**
     * Creates a new instance of InvoicesController
     */
    public InvoicesController() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.vizsgaremek.controller.InvoicesController
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of InvoicesController
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @POST
    @Path("createInvoice")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createInvoiceController(String body, @HeaderParam("token") String jwtToken) {

        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject bodyObject = new JSONObject(body);

        Orders order = new Orders();
        order.setId(bodyObject.has("orderId") ? bodyObject.getInt("orderId") : null);

        Invoices createdInvoices = new Invoices(
                bodyObject.has("pdfUrl") ? bodyObject.getString("pdfUrl") : null,
                order
        );

        JSONObject toReturn = layer.createInvoiceService(createdInvoices);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllInvoices")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllInvoicesController(@HeaderParam("token") String jwtToken) {
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
        InvoicesService invoicesService = new InvoicesService();
        JSONObject toReturn = invoicesService.getAllInvoicesService();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getInvoiceById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoiceByIdController(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {

        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.getInvoiceByIdService(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getInvoicesByOrderId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoicesByOrderIdController(@QueryParam("orderId") Integer orderId, @HeaderParam("token") String jwtToken) {
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

        JSONObject toReturn = layer.getInvoicesByOrderIdService(orderId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteInvoice")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteInvoiceController(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
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

        JSONObject toReturn = layer.softDeleteInvoiceService(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateInvoice")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateInvoiceController(
            @QueryParam("id") Integer idIN,
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

        Invoices updatedInvoice = new Invoices();

        if (idIN != null) {
            updatedInvoice.setId(idIN);
        }
        
        if (bodyObject.has("pdfUrl")) {
            updatedInvoice.setPdfUrl(bodyObject.getString("pdfUrl"));
        }

        if (bodyObject.has("isDeleted")) {
            updatedInvoice.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updateInvoiceService(updatedInvoice);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
