/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Invoices;
import com.mycompany.vizsgaremek.model.Orders;
import com.mycompany.vizsgaremek.service.InvoicesService;
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
@Path("invoices")
public class InvoicesController {

    @Context
    private UriInfo context;
    private InvoicesService layer = new InvoicesService();

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
    public Response createInvoiceController(String body) {

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
    public Response getAllInvoicesController() {
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
    public Response getInvoiceByIdController(@QueryParam("id") Integer id) {

        JSONObject toReturn = layer.getInvoiceByIdService(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getInvoicesByOrderId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInvoicesByOrderIdController(@QueryParam("orderId") Integer orderId) {

        JSONObject toReturn = layer.getInvoicesByOrderIdService(orderId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteInvoice")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteInvoiceController(@QueryParam("id") Integer id) {

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
            String body) {

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
