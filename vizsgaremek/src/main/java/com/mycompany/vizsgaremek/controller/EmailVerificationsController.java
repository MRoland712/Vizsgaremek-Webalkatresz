/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.model.EmailVerifications;
import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.service.EmailVerificationsService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
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
 * @author ddori
 */
@Path("EmailVerifications")
public class EmailVerificationsController {

    @Context
    private UriInfo context;
    
    private EmailVerificationsService layer = new EmailVerificationsService();
    private final JwtUtil jwt = new JwtUtil();

    /**
     * Creates a new instance of EmailVerificationsController
     */
    public EmailVerificationsController() {
    }

    /**
     * Retrieves representation of an instance of com.mycompany.vizsgaremek.controller.EmailVerificationsController
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of EmailVerificationsController
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }
    
    @POST
    @Path("createEmailVerificationAndSendEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createEmailVerificationAndSendEmailController(String body, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject bodyObject = new JSONObject(body);
        Integer userId = bodyObject.has("userId") ? bodyObject.getInt("userId") : null;

        JSONObject toReturn = layer.createEmailVerificationAndSendEmailService(userId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
    
    @GET //mivel html válasz
    @Path("activateEmail")
    @Produces(MediaType.TEXT_HTML)
    public Response verifyEmailVerification(@QueryParam("activationToken") String token) {

        JSONObject serviceResult = layer.verifyEmailVerificationService(token);
        int statusCode = serviceResult.getInt("statusCode");

        String html;

        if (statusCode == 200) {
            // Success
            JSONObject result = serviceResult.getJSONObject("result");
            String lastName = result.optString("lastName", "");

            html = "<!DOCTYPE html>"
                    + "<html lang=\"hu\">"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "<title>Fiók aktiválva - CarComps</title>"
                    + "<style>"
                    + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                    + "body { font-family: system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif; background: #111; color: #fff; display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 20px; }"
                    + ".container { max-width: 500px; background: #2b2b2b; padding: 40px; border-radius: 12px; text-align: center; box-shadow: 0 8px 24px rgba(0,0,0,0.4); }"
                    + ".icon { font-size: 64px; margin-bottom: 20px; }"
                    + "h1 { color: #4caf50; margin-bottom: 16px; font-size: 28px; }"
                    + "p { color: #eaeaea; margin-bottom: 24px; line-height: 1.6; }"
                    + ".btn { display: inline-block; padding: 14px 32px; background: #ff6600; color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; transition: background 0.3s; }"
                    + ".btn:hover { background: #e55a00; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"container\">"
                    + "<div class=\"icon\">✅</div>"
                    + "<h1>Fiók sikeresen aktiválva!</h1>"
                    + "<p>Köszönjük!"+" A fiókod aktiválva lett.</p>"
                    + "<p>Most már bejelentkezhetsz és használhatod a CarComps webshop szolgáltatásait.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            return Response.ok(html).build();

        } else if (statusCode == 409) {
            // Already activated
            html = "<!DOCTYPE html>"
                + "<html lang=\"hu\">"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>Hiba - CarComps</title>"
                + "<style>"
                + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                + "body { font-family: system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif; background: #111; color: #fff; display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 20px; }"
                + ".container { max-width: 500px; background: #2b2b2b; padding: 40px; border-radius: 12px; text-align: center; box-shadow: 0 8px 24px rgba(0,0,0,0.4); }"
                + ".icon { font-size: 64px; margin-bottom: 20px; }"
                + "h1 { color: #ff4444; margin-bottom: 16px; font-size: 28px; }"
                + "p { color: #eaeaea; margin-bottom: 24px; line-height: 1.6; }"
                + ".btn { display: inline-block; padding: 14px 32px; background: #ff6600; color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; transition: background 0.3s; }"
                + ".btn:hover { background: #e55a00; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"container\">"
                + "<div class=\"icon\">❌</div>"
                + "<h1>Érvénytelen vagy lejárt link</h1>"
                + "<p>A megerősítő link érvénytelen, már felhasználásra került, vagy lejárt a 10 perces időhatár.</p>"
                + "<p>Kérjük, próbáld újra vagy kérj új megerősítő emailt.</p>"
                + "<a href=\"https://carcomps.hu\" class=\"btn\">Vissza a főoldalra</a>"
                + "</div>"
                + "</body>"
                + "</html>";

            return Response.status(409).entity(html).build();

        } else {
            // Generic error
            html = "<!DOCTYPE html>"
                    + "<html lang=\"hu\">"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "<title>Hiba - CarComps</title>"
                    + "<style>"
                    + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                    + "body { font-family: system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif; background: #111; color: #fff; display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 20px; }"
                    + ".container { max-width: 500px; background: #2b2b2b; padding: 40px; border-radius: 12px; text-align: center; box-shadow: 0 8px 24px rgba(0,0,0,0.4); }"
                    + ".icon { font-size: 64px; margin-bottom: 20px; }"
                    + "h1 { color: #ff4444; margin-bottom: 16px; font-size: 28px; }"
                    + "p { color: #eaeaea; margin-bottom: 24px; line-height: 1.6; }"
                    + ".btn { display: inline-block; padding: 14px 32px; background: #ff6600; color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; transition: background 0.3s; }"
                    + ".btn:hover { background: #e55a00; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"container\">"
                    + "<div class=\"icon\">⚠️</div>"
                    + "<h1>Hiba történt</h1>"
                    + "<p>Sajnos hiba történt a fiók aktiválása során.</p>"
                    + "<p>Kérjük, próbáld újra később vagy vedd fel velünk a kapcsolatot.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            return Response.status(statusCode).entity(html).build();
        }
    }
    
    
}
