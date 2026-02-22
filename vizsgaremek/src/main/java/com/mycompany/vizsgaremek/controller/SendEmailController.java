/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import com.mycompany.vizsgaremek.config.SendEmail;
import com.mycompany.vizsgaremek.model.Users;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mycompany.vizsgaremek.model.EmailInfo;
import java.util.List;
import javax.ws.rs.DELETE;

/**
 * REST Web Service
 *
 * @author ddori
 */
@Path("email")
public class SendEmailController {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SendEmailController
     */
    public SendEmailController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.SendEmailController
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of SendEmailController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }
    
    /*@PUT
    @Path("sendActivateUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendActivateUserEmailLink(String body) {
        JSONObject bodyObject = new JSONObject(body);
        JSONArray errors = new JSONArray();

        String promotionImageLink = bodyObject.has("promotionImageLink") ? bodyObject.getString("promotionImageLink") : null;
        String bodyText = bodyObject.has("bodyText") ? bodyObject.getString("bodyText") : null;
        String code = bodyObject.has("promotionCode") ? bodyObject.getString("promotionCode") : null;
        String expirationDate = bodyObject.has("expirationDate") ? bodyObject.getString("expirationDate") : null;

        if (promotionImageLink == null || promotionImageLink.trim().isEmpty()) {
            errors.put("MissingPromotionImageLink");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (bodyText == null || bodyText.trim().isEmpty()) {
            errors.put("MissingBodyText");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (code == null || code.trim().isEmpty()) {
            errors.put("MissingPromotionCode");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (expirationDate == null || expirationDate.trim().isEmpty()) {
            errors.put("MissingExpirationDate");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }*/

    @POST
    @Path("sendPromotion")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendPromotionEmailToSubscribedUsersController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        JSONArray errors = new JSONArray();

        String promotionImageLink = bodyObject.has("promotionImageLink") ? bodyObject.getString("promotionImageLink") : null;
        String bodyText = bodyObject.has("bodyText") ? bodyObject.getString("bodyText") : null;
        String code = bodyObject.has("promotionCode") ? bodyObject.getString("promotionCode") : null;
        String expirationDate = bodyObject.has("expirationDate") ? bodyObject.getString("expirationDate") : null;

        if (promotionImageLink == null || promotionImageLink.trim().isEmpty()) {
            errors.put("MissingPromotionImageLink");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (bodyText == null || bodyText.trim().isEmpty()) {
            errors.put("MissingBodyText");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (code == null || code.trim().isEmpty()) {
            errors.put("MissingPromotionCode");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (expirationDate == null || expirationDate.trim().isEmpty()) {
            errors.put("MissingExpirationDate");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        try {
            SendEmail.sendPromotionEmailToSubscribedUsers(promotionImageLink, bodyText, code, expirationDate);

            JSONObject successResponse = new JSONObject();
            successResponse.put("status", "success");
            successResponse.put("statusCode", 200);
            successResponse.put("message", "Promotion emails sent successfully");

            return Response.status(200)
                    .entity(successResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception ex) {
            ex.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("statusCode", 500);
            errorResponse.put("message", "Failed to send emails: " + ex.getMessage());

            return Response.status(500)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    @Path("sendOTP")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendOTPEmailAndSetAuthSecretController(@QueryParam("email") String email) {
        JSONArray errors = new JSONArray();

        Users userdata = Users.getUserByEmail(email);
        System.out.println("sendOTPEmailAndSetAuthSecretController: " + email + " | " + userdata);
        if (userdata == null) {
            errors.put("UserNotFound");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 404);

            return Response.status(404)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        try {
            SendEmail.sendOTPEmailAndSetAuthSecret(email, userdata);

            JSONObject successResponse = new JSONObject();
            successResponse.put("status", "success");
            successResponse.put("statusCode", 200);
            successResponse.put("message", "OTP email sent successfully to " + email);

            return Response.status(200)
                    .entity(successResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception ex) {
            ex.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("statusCode", 500);
            errorResponse.put("message", "Failed to send emails: " + ex.getMessage());

            return Response.status(500)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    @Path("sendCustomerSupport")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendCustomerSupportEmailController(String body) {
        JSONObject bodyObject = new JSONObject(body);
        JSONArray errors = new JSONArray();

        String recipientEmail = bodyObject.has("recipientEmail") ? bodyObject.getString("recipientEmail") : null;
        String emailSubject = bodyObject.has("emailSubject") ? bodyObject.getString("emailSubject") : null;
        String customerName = bodyObject.has("customerName") ? bodyObject.getString("customerName") : null;
        String emailMessage = bodyObject.has("emailMessage") ? bodyObject.getString("emailMessage") : null;
        String inReplyToMessageId = bodyObject.has("inReplyToMessageId") ? bodyObject.getString("inReplyToMessageId") : null;

        if (recipientEmail == null || recipientEmail.trim().isEmpty()) {
            errors.put("MissingRecipientEmail");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (emailSubject == null || emailSubject.trim().isEmpty()) {
            errors.put("MissingEmailSubject");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (customerName == null || customerName.trim().isEmpty()) {
            errors.put("MissingCustomerName");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (emailMessage == null || emailMessage.trim().isEmpty()) {
            errors.put("MissingEmailMessage");

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("errors", errors);
            errorResponse.put("status", "failed");
            errorResponse.put("statusCode", 400);

            return Response.status(400)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        try {
            if (inReplyToMessageId != null || inReplyToMessageId != "") {
                SendEmail.sendCustomerSupportEmail(recipientEmail, emailSubject, customerName, emailMessage, inReplyToMessageId);
            } else {
                SendEmail.sendCustomerSupportEmail(recipientEmail, emailSubject, customerName, emailMessage, null);
            }

            JSONObject successResponse = new JSONObject();
            successResponse.put("status", "success");
            successResponse.put("statusCode", 200);
            successResponse.put("message", "Customer Support email sent successfully to: " + recipientEmail);

            return Response.status(200)
                    .entity(successResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();

        } catch (Exception ex) {
            ex.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("statusCode", 500);
            errorResponse.put("message", "Failed to send email to " + recipientEmail + " : " + ex.getMessage());

            return Response.status(500)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @GET
    @Path("getMessages")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getMessagesController() {

        try {
            List<EmailInfo> result = SendEmail.getMessages();

            if (result.isEmpty()) {
                JSONObject errorResponse = new JSONObject();

                errorResponse.put("status", "error");
                errorResponse.put("statusCode", 500);
                errorResponse.put("message", "ResultIsMissing");

                return Response.status(500)
                        .entity(errorResponse)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            JSONObject successResponse = new JSONObject();
            successResponse.put("result", result);
            successResponse.put("status", "success");
            successResponse.put("statusCode", 200);

            return Response.status(200)
                    .entity(successResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("statusCode", 500);
            errorResponse.put("message", "Failed to get Messages: " + ex.getMessage());

            return Response.status(500)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @DELETE
    @Path("archiveEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response archiveEmailByMessageIdController(@QueryParam("messageId") String messageId) {

        if (messageId == null || messageId.trim().isEmpty()) {
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("statusCode", 400);
            errorResponse.put("message", "messageIdIsMissing");

            return Response.status(500)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        try {
            Boolean result = SendEmail.archiveEmailByMessageId(messageId);

            if (result == false) {
                JSONObject errorResponse = new JSONObject();
                errorResponse.put("status", "error");
                errorResponse.put("statusCode", 404);
                errorResponse.put("message", "No message found with Message-ID");

                return Response.status(500)
                        .entity(errorResponse.toString())
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            JSONObject successResponse = new JSONObject();
            successResponse.put("message", "Archived email successfully");
            successResponse.put("status", "success");
            successResponse.put("statusCode", 200);

            return Response.status(200)
                    .entity(successResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();

            JSONObject errorResponse = new JSONObject();
            errorResponse.put("status", "error");
            errorResponse.put("statusCode", 500);
            errorResponse.put("message", "Failed to archive email");

            return Response.status(500)
                    .entity(errorResponse.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
