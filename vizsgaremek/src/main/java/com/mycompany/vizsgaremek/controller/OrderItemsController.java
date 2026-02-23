/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.service.orderItemsService;
import com.mycompany.vizsgaremek.model.OrderItems;
import com.mycompany.vizsgaremek.model.Orders;
import com.mycompany.vizsgaremek.model.Parts;
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
 * @author ddori
 */
@Path("OrderItems")
public class OrderItemsController {

    @Context
    private UriInfo context;
    private orderItemsService layer = new orderItemsService();

    /**
     * Creates a new instance of OrderItemsController
     */
    public OrderItemsController() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.vizsgaremek.controller.OrderItemsController
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of OrderItemsController
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @GET
    @Path("getAllOrderItemsAdmin")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllOrderItemsAdminController() {
        orderItemsService orderItemsService = new orderItemsService();
        JSONObject toReturn = orderItemsService.getAllOrderItemsAdminService();
        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @POST
    @Path("createOrderItems")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrderItemsController(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Orders order = new Orders();
        order.setId(bodyObject.has("orderId") ? bodyObject.getInt("orderId") : null);
        
        Parts part = new Parts();
        part.setId(bodyObject.has("partId") ? bodyObject.getInt("partId") : null);

        OrderItems createdOrderItems = new OrderItems(
                bodyObject.has("quantity") ? bodyObject.getInt("quantity") : null,
                bodyObject.has("price") ? bodyObject.getBigDecimal("price") : null,
                order,
                part
        );

        JSONObject toReturn = layer.createOrderItemsService(createdOrderItems);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllOrderItems")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllOrderItemsController() {
        orderItemsService orderItemsService = new orderItemsService();
        JSONObject toReturn = orderItemsService.getAllOrderItemsService();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getOrderItemById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderItemByIdController(@QueryParam("id") Integer id) {

        JSONObject toReturn = layer.getOrderItemByIdService(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getOrderItemsByOrderId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderItemsByOrderIdController(@QueryParam("orderId") Integer orderId) {

        JSONObject toReturn = layer.getOrderItemsByOrderIdService(orderId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("getOrderItemsByPartId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderItemsByPartIdController(@QueryParam("partId") Integer partId) {

        JSONObject toReturn = layer.getOrderItemsByPartIdService(partId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteOrderItem")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteOrderItemController(@QueryParam("id") Integer id) {

        JSONObject toReturn = layer.softDeleteOrderItemService(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateOrderItem")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateOrderItemController(
            @QueryParam("id") Integer idIN,
            String body) {

        JSONObject bodyObject = new JSONObject(body);

        OrderItems updatedOrderItem = new OrderItems();

        if (idIN != null) {
            updatedOrderItem.setId(idIN);
        }
        
        if (bodyObject.has("quantity")) {
            updatedOrderItem.setQuantity(bodyObject.getInt("quantity"));
        }
        
        if (bodyObject.has("price")) {
            updatedOrderItem.setPrice(bodyObject.getBigDecimal("price"));
        }

        if (bodyObject.has("isDeleted")) {
            updatedOrderItem.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updateOrderItemService(updatedOrderItem);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
