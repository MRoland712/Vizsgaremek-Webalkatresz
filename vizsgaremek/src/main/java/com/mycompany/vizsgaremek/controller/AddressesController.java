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
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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
    public Response createAddress(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Users user = new Users();
        user.setId(bodyObject.has("userId") ? bodyObject.getInt("userId") : null);

        Addresses createdAddress = new Addresses(
                user,
                bodyObject.has("firstName") ? bodyObject.getString("firstName") : null,
                bodyObject.has("lastName") ? bodyObject.getString("lastName") : null,
                bodyObject.has("company") ? bodyObject.getString("company") : null,
                bodyObject.has("taxNumber") ? bodyObject.getString("taxNumber") : null,
                bodyObject.has("country") ? bodyObject.getString("country") : null,
                bodyObject.has("city") ? bodyObject.getString("city") : null,
                bodyObject.has("zipCode") ? bodyObject.getString("zipCode") : null,
                bodyObject.has("street") ? bodyObject.getString("street") : null,
                bodyObject.has("isDefault") ? bodyObject.getBoolean("isDefault") : false
        );

        JSONObject toReturn = layer.createAddress(createdAddress);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllAddresses")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllAddresses() {
        AddressService addressService = new AddressService();
        JSONObject toReturn = addressService.getAllAddresses();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAddressById")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAddressById(@QueryParam("id") Integer id) {

        JSONObject toReturn = layer.getAddressById(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET
    @Path("getAddressByUserId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAddressByUserId(@QueryParam("userId") Integer id) {

        JSONObject toReturn = layer.getAddressByUserId(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteAddress")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteAddress(@QueryParam("id") Integer addressId) {

        JSONObject toReturn = layer.softDeleteAddress(addressId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateAddress")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateAddress(
            @QueryParam("id") Integer addressId,
            @QueryParam("userId") Integer userId,
            @QueryParam("street") String street,
            String body) {

        JSONObject bodyObject = new JSONObject(body);

        Addresses updatedAddress = new Addresses();

        if (addressId != null) {
            updatedAddress.setId(addressId);
        }

        if (street != null) {
            updatedAddress.setStreet(street);
        }

        if (userId != null) {
            Users user = new Users();
            user.setId(userId);
            updatedAddress.setUserId(user);
        } else if (addressId != null || street != null){
            Users user = new Users();
            Integer idFromAddressId = Addresses.getAddressById(addressId).getId();
            Integer idFromStreet = null; //Addresses.getAddressByStreet(street).getId();
            if (idFromAddressId != null || idFromStreet != null) {
                user.setId(idFromAddressId != null ? idFromAddressId : idFromStreet);
            } else {
                return Response.status(400).entity("invalidSearchParameter").type(MediaType.APPLICATION_JSON).build();
            }
            updatedAddress.setUserId(user);
        } else {
            return Response.status(400).entity("invalidSearchParameter").type(MediaType.APPLICATION_JSON).build();
        }

        if (bodyObject.has("firstName")) {
            updatedAddress.setFirstName(bodyObject.getString("firstName"));
        }

        if (bodyObject.has("lastName")) {
            updatedAddress.setLastName(bodyObject.getString("lastName"));
        }

        if (bodyObject.has("company")) {
            updatedAddress.setCompany(bodyObject.getString("company"));
        }

        if (bodyObject.has("taxNumber")) {
            updatedAddress.setTaxNumber(bodyObject.getString("taxNumber"));
        }

        if (bodyObject.has("country")) {
            updatedAddress.setCountry(bodyObject.getString("country"));
        }
        
        if (bodyObject.has("city")) {
            updatedAddress.setCity(bodyObject.getString("city"));
        }
        
        if (bodyObject.has("zipCode")) {
            updatedAddress.setZipCode(bodyObject.getString("zipCode"));
        }
        
        if (bodyObject.has("street")) {
            updatedAddress.setStreet(bodyObject.getString("street"));
        }
        
        if (bodyObject.has("isDefault")) {
            updatedAddress.setIsDefault(bodyObject.getBoolean("isDefault"));
        }

        JSONObject toReturn = layer.updateAddress(updatedAddress);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

