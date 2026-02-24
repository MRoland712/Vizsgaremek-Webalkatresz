/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.CartItems;
import com.mycompany.vizsgaremek.model.Parts;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.service.CartItemsService;
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
@Path("cartItems")
public class CartItemsController {

    @Context
    private UriInfo context;
    private CartItemsService layer = new CartItemsService();

    /**
     * Creates a new instance of CartItemsController
     */
    public CartItemsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.CartItemsController
     * 
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        // TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of CartItemsController
     * 
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createCartItems")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCartItemsController(String body, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject bodyObject = new JSONObject(body);

        Users user = new Users();
        user.setId(bodyObject.has("userId") ? bodyObject.getInt("userId") : null);

        Parts part = new Parts();
        part.setId(bodyObject.has("partId") ? bodyObject.getInt("partId") : null);

        CartItems createdCartItems = new CartItems(
                bodyObject.has("quantity") ? bodyObject.getInt("quantity") : null,
                part,
                user);

        JSONObject toReturn = layer.createCartItemsService(createdCartItems);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllCartItems")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllCartItemsController(@HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONArray errors = new JSONArray();
        String jwtRole = jwt.extractRole(jwtToken);
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

        CartItemsService cartItemsService = new CartItemsService();
        JSONObject toReturn = cartItemsService.getAllCartItemsService();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getCartItemById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCartItemByIdController(@QueryParam("id") Integer Id) {
        CartItemsService cartItemsService = new CartItemsService();

        JSONObject toReturn = cartItemsService.getCartItemByIdService(Id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getCartItemsByPartId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCartItemsByPartIdController(@QueryParam("partId") Integer partId) {

        JSONObject toReturn = layer.getCartItemsByPartIdService(partId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getCartItemsByUserId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCartItemsByUserIdController(@QueryParam("userId") Integer userId) {

        JSONObject toReturn = layer.getCartItemsByUserIdService(userId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteCartItem")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteCartItemController(@QueryParam("id") Integer id) {

        JSONObject toReturn = layer.softDeleteCartItemService(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateCartItem")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCartItemController(
            @QueryParam("id") Integer id,
            String body) {

        JSONObject bodyObject = new JSONObject(body);

        CartItems updatedCartItem = new CartItems();

        if (id != null) {
            updatedCartItem.setId(id);
        }

        if (bodyObject.has("quantity")) {
            updatedCartItem.setQuantity(bodyObject.getInt("quantity"));
        }

        if (bodyObject.has("isDeleted")) {
            updatedCartItem.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        JSONObject toReturn = layer.updateCartItemService(updatedCartItem);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
