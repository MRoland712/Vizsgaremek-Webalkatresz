package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.service.AuthenticationService;
import com.mycompany.vizsgaremek.service.UsersService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;
import org.json.JSONArray;
import com.mycompany.vizsgaremek.model.UserLogs;

@Path("user")
public class UsersController {

    @Context
    private UriInfo context;

    private UsersService layer = new UsersService();
    private final AuthenticationService.userAuth userAuth = new AuthenticationService.userAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();
    private final JwtUtil jwt = new JwtUtil();

    public UsersController() {
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        throw new UnsupportedOperationException();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    @POST
    @Path("createUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String body) {
        JSONObject bodyObject = new JSONObject(body);

        Users createdUser = new Users(
                bodyObject.has("email") ? bodyObject.getString("email") : null,
                bodyObject.has("username") ? bodyObject.getString("username") : null,
                bodyObject.has("password") ? bodyObject.getString("password") : null,
                bodyObject.has("firstName") ? bodyObject.getString("firstName") : null,
                bodyObject.has("lastName") ? bodyObject.getString("lastName") : null,
                bodyObject.has("phone") ? bodyObject.getString("phone") : null,
                null
        );

        JSONObject toReturn = layer.createUser(createdUser);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getUsers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.getUsers();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getActivatedUsers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getActivatedUsers(@HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.getActivatedUsersService();

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getUserById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserById(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        if (userAuth.isDataMissing(id)) {
            id = null;
        }

        JSONObject toReturn = layer.getUserById(id);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getUserByEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserByEmailController(@QueryParam("email") String email, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }

        if (userAuth.isDataMissing(email)) {
            email = null;
        }

        JSONObject toReturn = layer.getUserByEmail(email);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("activateUser")
    @Produces(MediaType.TEXT_HTML)
    public Response activateUser(@QueryParam("activationToken") String token) {

        JSONObject serviceResult = layer.activateUserService(token);
        int statusCode = serviceResult.getInt("statusCode");

        String html;

        if (statusCode == 200) {
            // Success
            JSONObject result = serviceResult.getJSONObject("result");
            String username = result.optString("username", "");

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
                    + "<p>Köszönjük " + username + "! A fiókod aktiválva lett.</p>"
                    + "<p>Most már bejelentkezhetsz és használhatod a CarComps webshop szolgáltatásait.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            return Response.ok(html).build();

        } else if (statusCode == 404) {
            // User not found
            html = "<!DOCTYPE html>"
                    + "<html lang=\"hu\">"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "<title>Hibás link - CarComps</title>"
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
                    + "<h1>Érvénytelen aktiválási link</h1>"
                    + "<p>Ez az aktiválási link már nem érvényes vagy hibás.</p>"
                    + "<p>Kérjük, ellenőrizd az e-mailben kapott linket vagy regisztrálj újra.</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            return Response.status(404).entity(html).build();

        } else if (statusCode == 409) {
            // Already activated
            html = "<!DOCTYPE html>"
                    + "<html lang=\"hu\">"
                    + "<head>"
                    + "<meta charset=\"UTF-8\">"
                    + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                    + "<title>Már aktiválva - CarComps</title>"
                    + "<style>"
                    + "* { margin: 0; padding: 0; box-sizing: border-box; }"
                    + "body { font-family: system-ui, -apple-system, 'Segoe UI', Roboto, sans-serif; background: #111; color: #fff; display: flex; align-items: center; justify-content: center; min-height: 100vh; padding: 20px; }"
                    + ".container { max-width: 500px; background: #2b2b2b; padding: 40px; border-radius: 12px; text-align: center; box-shadow: 0 8px 24px rgba(0,0,0,0.4); }"
                    + ".icon { font-size: 64px; margin-bottom: 20px; }"
                    + "h1 { color: #ffa500; margin-bottom: 16px; font-size: 28px; }"
                    + "p { color: #eaeaea; margin-bottom: 24px; line-height: 1.6; }"
                    + ".btn { display: inline-block; padding: 14px 32px; background: #ff6600; color: #fff; text-decoration: none; border-radius: 8px; font-weight: 600; transition: background 0.3s; }"
                    + ".btn:hover { background: #e55a00; }"
                    + "</style>"
                    + "</head>"
                    + "<body>"
                    + "<div class=\"container\">"
                    + "<div class=\"icon\">ℹ️</div>"
                    + "<h1>Fiók már aktiválva</h1>"
                    + "<p>Ez a fiók már korábban aktiválásra került.</p>"
                    + "<p>Bejelentkezhetsz az email címeddel és jelszavaddal.</p>"
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

    @GET
    @Path("getAdminByEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAdminByEmailController(@QueryParam("email") String email, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        String jwtRole = jwt.extractRole(jwtToken);
        JSONArray errors = new JSONArray();
        if (jwtError != null) {
            return jwtError;
        }
        System.out.println("\n\n\n" + jwtRole + jwtRole.equals("admin") + "\n\n\n");
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

        if (userAuth.isDataMissing(email)) {
            email = null;
        }

        JSONObject toReturn = layer.getAdminByEmailService(email);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @DELETE
    @Path("softDeleteUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteUser(@QueryParam("id") Integer userId, @HeaderParam("token") String jwtToken) {
        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.softDeleteUser(userId);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("updateUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response UpdateUser(@HeaderParam("token") String jwtToken,
            @QueryParam("id") Integer userId,
            @QueryParam("email") String email,
            String body) {

        Response jwtError = jwt.validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject bodyObject = new JSONObject(body);
        Users updatedUser = new Users();

        if (userId != null) {
            updatedUser.setId(userId);
        }
        if (email != null) {
            updatedUser.setEmail(email);
        }
        if (bodyObject.has("email") && email == null) {
            updatedUser.setEmail(bodyObject.getString("email"));
        }
        if (bodyObject.has("username")) {
            updatedUser.setUsername(bodyObject.getString("username"));
        }
        if (bodyObject.has("firstName")) {
            updatedUser.setFirstName(bodyObject.getString("firstName"));
        }
        if (bodyObject.has("lastName")) {
            updatedUser.setLastName(bodyObject.getString("lastName"));
        }
        if (bodyObject.has("phone")) {
            updatedUser.setPhone(bodyObject.getString("phone"));
        }
        if (bodyObject.has("isActive")) {
            updatedUser.setIsActive(bodyObject.getBoolean("isActive"));
        }
        if (bodyObject.has("isSubscribed")) {
            updatedUser.setIsSubscribed(bodyObject.getBoolean("isSubscribed"));
        }
        //ToDo: email verified, phone verified
        if (bodyObject.has("password")) {
            updatedUser.setPassword(bodyObject.getString("password"));
        }
        if (bodyObject.has("authSecret")) {
            updatedUser.setAuthSecret(bodyObject.getString("authSecret"));
        }
        if (bodyObject.has("registrationToken")) {
            updatedUser.setRegistrationToken(bodyObject.getString("registrationToken"));
        }

        JSONObject toReturn = layer.updateUser(updatedUser);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("loginUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loginUser(String body) {
        JSONObject bodyObject = new JSONObject(body);

        Users user = new Users(
                bodyObject.has("email") ? bodyObject.getString("email") : null,
                bodyObject.has("password") ? bodyObject.getString("password") : null
        );

        JSONObject toReturn = layer.loginUser(user);

        return Response.status(Integer.parseInt(toReturn.get("statusCode").toString()))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
