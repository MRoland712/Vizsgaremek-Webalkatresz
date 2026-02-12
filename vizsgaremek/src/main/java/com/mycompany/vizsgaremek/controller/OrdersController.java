/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Orders;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.service.OrdersService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
@Path("orders")
public class OrdersController {

    @Context
    private UriInfo context;
    private OrdersService layer = new OrdersService();

    /**
     * Creates a new instance of OrdersController
     */
    public OrdersController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.OrdersController
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
     * PUT method for updating or creating an instance of OrdersController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createOrders")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrders(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Users user = new Users();
        user.setId(bodyObject.has("userId") ? bodyObject.getInt("userId") : null);

        Orders createdOrders = new Orders(
                bodyObject.has("status") ? bodyObject.getString("status") : null,
                user
        );

        JSONObject toReturn = layer.createOrders(createdOrders);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllOrders")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllOrders() {
        OrdersService ordersService = new OrdersService();
        JSONObject toReturn = ordersService.getAllOrders();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getOrdersById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersById(@QueryParam("id") Integer id) {

        JSONObject toReturn = layer.getOrdersById(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getOrdersByUserId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrdersByUserId(@QueryParam("idIN") Integer id) {

        JSONObject toReturn = layer.getOrdersByUserId(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteOrders")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteOrders(@QueryParam("id") Integer idIN) {

        JSONObject toReturn = layer.softDeleteOrders(idIN);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateOrders")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOrders(
            @QueryParam("id") Integer ordersId,
            String body) {

        JSONObject bodyObject = new JSONObject(body);

        Orders updatedOrders = new Orders();

        if (ordersId != null) {
            updatedOrders.setId(ordersId);
        }
        
        if (bodyObject.has("status")) {
            updatedOrders.setStatus(bodyObject.getString("status"));
        }

        if (bodyObject.has("isDeleted")) {
            updatedOrders.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updateOrders(updatedOrders);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
