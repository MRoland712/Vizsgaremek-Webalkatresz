package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.service.AuthenticationService;
import com.mycompany.vizsgaremek.service.UsersService;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
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

    // ============================================
    // ⭐ CORS Preflight Handler (OPTIONS) ⭐
    // ============================================
    @OPTIONS
    @Path("{path:.*}")
    public Response handleCorsPreFlight() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, token")
                .header("Access-Control-Allow-Credentials", "true")
                .build();
    }

    // ============================================
    // ⭐ Helper method CORS header-ökhöz ⭐
    // ============================================
    private Response.ResponseBuilder addCorsHeaders(Response.ResponseBuilder responseBuilder) {
        return responseBuilder
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, token")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    }

    // ============================================
    // ⭐ Helper method JWT validációhoz ⭐
    // ============================================
    private Response validateJwtAndReturnError(String jwtToken) {
        JSONArray errors = new JSONArray();
        
        if (userAuth.isDataMissing(jwtToken)) {
            errors.put("MissingToken");
            return addCorsHeaders(Response.status(401))
                    .entity(errorAuth.createErrorResponse(errors, 401).toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        
        Boolean validJwt = jwt.validateToken(jwtToken);
        
        if (validJwt == null) {
            errors.put("TokenExpired");
            return addCorsHeaders(Response.status(401))
                    .entity(errorAuth.createErrorResponse(errors, 401).toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        
        if (validJwt == false) {
            errors.put("InvalidToken");
            return addCorsHeaders(Response.status(401))
                    .entity(errorAuth.createErrorResponse(errors, 401).toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        
        return null; // Token valid
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

        return addCorsHeaders(Response.status(Integer.parseInt(toReturn.get("statusCode").toString())))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getUsers")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUsers(@HeaderParam("token") String jwtToken) {
        Response jwtError = validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.getUsers();
        
        return addCorsHeaders(Response.status(Integer.parseInt(toReturn.get("statusCode").toString())))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getUserById")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserById(@QueryParam("id") Integer id, @HeaderParam("token") String jwtToken) {
        Response jwtError = validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        if (userAuth.isDataMissing(id)) {
            id = null;
        }
        
        JSONObject toReturn = layer.getUserById(id);
        
        return addCorsHeaders(Response.status(Integer.parseInt(toReturn.get("statusCode").toString())))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @GET
    @Path("getUserByEmail")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserByEmail(@QueryParam("email") String email, @HeaderParam("token") String jwtToken) {
        Response jwtError = validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.getUserByEmail(email);
        
        return addCorsHeaders(Response.status(Integer.parseInt(toReturn.get("statusCode").toString())))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("softDeleteUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response softDeleteUser(@QueryParam("id") Integer userId, @HeaderParam("token") String jwtToken) {
        Response jwtError = validateJwtAndReturnError(jwtToken);
        if (jwtError != null) {
            return jwtError;
        }

        JSONObject toReturn = layer.softDeleteUser(userId);

        return addCorsHeaders(Response.status(Integer.parseInt(toReturn.get("statusCode").toString())))
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
        
        Response jwtError = validateJwtAndReturnError(jwtToken);
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

        return addCorsHeaders(Response.status(Integer.parseInt(toReturn.get("statusCode").toString())))
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

        return addCorsHeaders(Response.status(Integer.parseInt(toReturn.get("statusCode").toString())))
                .entity(toReturn.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}