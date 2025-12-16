/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Manufacturers;
import com.mycompany.vizsgaremek.service.ManufacturersService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
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
@Path("manufacturers")
public class ManufacturersController {

    @Context
    private UriInfo context;
    private ManufacturersService layer = new ManufacturersService();

    /**
     * Creates a new instance of ManufacturersController
     */
    public ManufacturersController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.ManufacturersController
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
     * PUT method for updating or creating an instance of
     * ManufacturersController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createManufacturers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createManufacturers(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Manufacturers createdManufacturers = new Manufacturers(
                bodyObject.has("name") ? bodyObject.getString("name") : null,
                bodyObject.has("country") ? bodyObject.getString("country"): null
        );
        
        JSONObject toReturn = layer.createManufacturers(createdManufacturers);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();

    }
}
