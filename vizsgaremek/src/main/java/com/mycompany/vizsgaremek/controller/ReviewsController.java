/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.Parts;
import com.mycompany.vizsgaremek.model.Reviews;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.service.ReviewsService;
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
@Path("reviews")
public class ReviewsController {

    @Context
    private UriInfo context;
    private ReviewsService layer = new ReviewsService();

    /**
     * Creates a new instance of ReviewsController
     */
    public ReviewsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.ReviewsController
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
     * PUT method for updating or creating an instance of ReviewsController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @POST
    @Path("createReviews")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createReviews(String body) {

        JSONObject bodyObject = new JSONObject(body);

        Users user = new Users();
        user.setId(bodyObject.has("userId") ? bodyObject.getInt("userId") : null);

        Parts part = new Parts();
        part.setId(bodyObject.has("partId") ? bodyObject.getInt("partId") : null);

        Reviews createdReviews = new Reviews(
                (bodyObject.has("ratingIN") ? bodyObject.getInt("ratingIN") : null),
                (bodyObject.has("commentIN") ? bodyObject.getString("commentIN") : null)
        );

        createdReviews.setUserId(user);
        createdReviews.setPartId(part);

        JSONObject toReturn = layer.createReviews(createdReviews);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getAllReviews")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllReviews() {
        ReviewsService reviewsService = new ReviewsService();
        JSONObject toReturn = reviewsService.getAllReviews();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getReviewsById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getReviewsById(@QueryParam("id") Integer Id) {
        ReviewsService reviewsService = new ReviewsService();

        JSONObject toReturn = reviewsService.getReviewsById(Id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getReviewsByPartId")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getReviewsByPartId(@QueryParam("partId") Integer partId) {
        ReviewsService reviewsService = new ReviewsService();

        JSONObject toReturn = reviewsService.getReviewsByPartId(partId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getReviewsByUserId")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getReviewsByUserId(@QueryParam("userId") Integer userId) {
        ReviewsService reviewsService = new ReviewsService();

        JSONObject toReturn = reviewsService.getReviewsByUserId(userId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getReviewsByRating")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getReviewsByRating(@QueryParam("rating") Integer rating) {
        ReviewsService reviewsService = new ReviewsService();

        JSONObject toReturn = reviewsService.getReviewsByRating(rating);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateReviews")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateReviews(
            @QueryParam("id") Integer idIN, String body) {
        JSONObject bodyObject = new JSONObject(body);

        Reviews updatedReviews = new Reviews();

        if (idIN != null) {
            updatedReviews.setId(idIN);
        }

        if (bodyObject.has("rating")) {
            updatedReviews.setRating(bodyObject.getInt("rating"));
        }

        if (bodyObject.has("comment")) {
            updatedReviews.setComment(bodyObject.getString("comment"));
        }

        if (bodyObject.has("isDeleted")) {
            updatedReviews.setIsDeleted(bodyObject.getBoolean("isDeleted"));
        }

        ReviewsService reviewsService = new ReviewsService();
        JSONObject toReturn = reviewsService.updateReviews(updatedReviews);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteReviews")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteReviews(@QueryParam("id") Integer id) {
        ReviewsService reviewsService = new ReviewsService();
        JSONObject toReturn = reviewsService.softDeleteReviews(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
