/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package com.mycompany.vizsgaremek.controller;

import com.mycompany.vizsgaremek.config.JwtUtil;
import com.mycompany.vizsgaremek.util.CloudflareAnalitics;
import com.mycompany.vizsgaremek.model.Orders;
import com.mycompany.vizsgaremek.model.Users;
import com.mycompany.vizsgaremek.model.Payments;
import com.mycompany.vizsgaremek.model.OrderItems;
import com.mycompany.vizsgaremek.model.Parts;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author ddori
 */
@Path("Analitics")
public class AnaliticsController {

    private final JwtUtil jwt = new JwtUtil();
    private CloudflareAnalitics layer = new CloudflareAnalitics();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AnaliticsController
     */
    public AnaliticsController() {
    }

    /**
     * Retrieves representation of an instance of
     * com.mycompany.vizsgaremek.controller.AnaliticsController
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
     * PUT method for updating or creating an instance of AnaliticsController
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    @GET
    @Path("getAllStat")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllStats(@QueryParam("days") Integer days, @HeaderParam("token") String jwtToken) {
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

        /**
         * ✔ ️❌ total visitors ✔ page views ✔ total customer (aki
         * vásárolt/vásárló) ✔ getAllOrders ✔ active users (ez lehet komplikált
         * és nemis nektek kell nemtudom nézzétek meg) ✔ getAllTransaction ❌ -
         * getAllDeliveredOrders ✔ regisztrált felhasználók száma ✔
         * legkeresetebb termékek ❌ legvásárolt termékek ❌ vásárlók (user)
         * keresése pl ügyfélszolgálathoz ✔ (ha kell pl username vagy valami
         * alapjan akkor szolj de amugy van getUserByEmail) jó vélmények
         * keresése termékek alapján ezis ügyfélgecihez jó ✔ (kivéve ha egy
         * range kell pl 3-5 csillagig) promóció kiküldése kezelése ? (ig
         * megvan??? clarify please hogy mi kell ezen belul)
         */
        JSONObject result = new JSONObject();

        JSONObject viewsAndVisitors = layer.getPageViewsAndUniqueVisitors(days);
        result.put("pageViews", viewsAndVisitors.get("pageViews"));
        result.put("uniqueVisitors", viewsAndVisitors.get("uniqueVisitors"));

        ArrayList<Orders> allOrder = Orders.getAllOrders();
        result.put("ordersCount", allOrder.toArray().length);
        result.put("allOrders", allOrder);

        ArrayList<Users> allUsers = Users.getUsers();
        ArrayList<Users> activatedUsers = new ArrayList<Users>();

        for (Users user : allUsers) {
            if (user.getIsActive() == true) {
                activatedUsers.add(user);
            }
        }

        result.put("activeUsers", activatedUsers);

        //ToDo: transactions CRUD (getAllTransactions)
        result.put("allTransactions", "");

        ArrayList<Orders> deliveredOrders = new ArrayList<Orders>();

        for (Orders order : allOrder) {
            if (order.getStatus().equals("delivered")) {
                deliveredOrders.add(order);
            }
        }
        result.put("allDeliveredOrders", deliveredOrders);

        result.put("allRegisteredUserCount", allUsers.toArray().length);

        ArrayList<OrderItems> allOrderItems = OrderItems.getAllOrderItems();

        HashMap<Integer, Integer> mostPurchasedPart = new HashMap<Integer, Integer>();

        //feltöltjük a mostPurchasedPart hashmapet
        for (OrderItems orderItem : allOrderItems) {
            Integer partId = orderItem.getPartId().getId();
            Integer quantity = orderItem.getQuantity();

            if (mostPurchasedPart.containsKey(partId)) {
                mostPurchasedPart.put(partId, mostPurchasedPart.get(partId) + quantity);
            } else {
                mostPurchasedPart.put(partId, quantity);
            }
        }
        //System.out.println("mostpurchasedpart.get1 " +mostPurchasedPart.get(2));
        
        List<Map.Entry<Integer, Integer>> sortedList = new ArrayList<>(mostPurchasedPart.entrySet());
        sortedList.sort((a,b) -> b.getValue().compareTo(a.getValue()));
        
        JSONArray top10 = new JSONArray();
        int limit = Math.min(10, sortedList.size());
        for (int i = 0; i < limit; i++) {
            JSONObject item = new JSONObject();
            item.put("partName", Parts.getPartsById(sortedList.get(i).getKey()).getName());
            item.put("quantity", sortedList.get(i).getValue());
            //System.out.println("partName" + Parts.getPartsById(sortedList.get(i).getKey()).getName());
            //System.out.println("quantity" + sortedList.get(i).getValue());
            top10.put(item);
        }
        //System.out.println("top10 " + top10);
        result.put("mostPurchasedPart", top10);

        //ToDo: mostsearched part
        JSONObject successResponse = new JSONObject();
        successResponse.put("result", result);
        successResponse.put("status", "success");
        successResponse.put("statusCode", 200);

        return Response.status(Integer.parseInt(successResponse.get("statusCode").toString()))
                .entity(successResponse.toString())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private void HashMap() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
