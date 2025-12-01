/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Addresses;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.service.AddressService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author neblg
 */
@Path("addresses")
public class AddressesController {

    @Context
    private UriInfo context;
    private AddressService layer = new AddressService();

    /**
     * Creates a new instance of AddressesController
     */
    public AddressesController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.AddressesController
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
     * PUT method for updating or creating an instance of AddressesController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createAddress")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAddress(String body, Integer userId) {

        JSONObject bodyObject = new JSONObject(body);

        Users user = Users.getUserById(userId);
        
        Addresses createdAddress = new Addresses(
                user, // Users entitás
                bodyObject.has("firstName") ? bodyObject.getString("firstName") : null,
                bodyObject.has("lastName") ? bodyObject.getString("lastName") : null,
                bodyObject.has("company") ? bodyObject.getString("company") : null,
                bodyObject.has("taxNumber") ? bodyObject.getString("taxNumber") : null,
                bodyObject.has("country") ? bodyObject.getString("country") : null,
                bodyObject.has("city") ? bodyObject.getString("city") : null,
                bodyObject.has("zipCode") ? bodyObject.getString("zipCode") : null,
                bodyObject.has("street") ? bodyObject.getString("street") : null,
                bodyObject.has("isDefault") ? bodyObject.getBoolean("isDefault") : false // ✅ getBoolean használata!
        );

        JSONObject toReturn = layer.createAddress(createdAddress);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
