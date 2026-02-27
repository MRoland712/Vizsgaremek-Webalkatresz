/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.CartItems;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class CartItemsService {

    private final AuthenticationService.cartItemsAuth cartItemsAuth = new AuthenticationService.cartItemsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createCartItemsService(CartItems createCartItems) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (cartItemsAuth.isDataMissing(createCartItems.getUserId())) {
            errors.put("MissingUserId");
        }
        if (cartItemsAuth.isDataMissing(createCartItems.getPartId())) {
            errors.put("MissingPartId");
        }

        if (cartItemsAuth.isDataMissing(createCartItems.getQuantity())) {
            errors.put("MissingQuantity");
        }

        if (!cartItemsAuth.isDataMissing(createCartItems.getUserId()) && !cartItemsAuth.isValidUserId(createCartItems.getUserId())) {
            errors.put("InvalidUserId");
        }

        if (!cartItemsAuth.isDataMissing(createCartItems.getPartId()) && !cartItemsAuth.isValidPartId(createCartItems.getPartId())) {
            errors.put("InvalidPartId");
        }

        if (!cartItemsAuth.isDataMissing(createCartItems.getQuantity()) && !cartItemsAuth.isValidQuantity(createCartItems.getQuantity())) {
            errors.put("InvalidQuantity");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (CartItems.createCartItems(createCartItems)) {
            toReturn.put("message", "CartItems Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "CartItems Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }

    }
// createCartItems Closer

    public JSONObject getAllCartItemsService() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<CartItems> modelResult = CartItems.getAllCartItems();

        // VALIDÁCIÓ 
        if (cartItemsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        JSONArray carItemsArray = new JSONArray();

        for (CartItems cartItems : modelResult) {
            JSONObject partObj = new JSONObject();
            partObj.put("id", cartItems.getId());
            partObj.put("userId", cartItems.getUserId().getId());
            partObj.put("partId", cartItems.getPartId().getId());
            partObj.put("quantity", cartItems.getQuantity());
            partObj.put("addedAt", cartItems.getAddedAt());
            partObj.put("isDeleted", cartItems.getIsDeleted());

            carItemsArray.put(partObj);
        }

        toReturn.put("success", true);
        toReturn.put("CartItems", carItemsArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllParts

    public JSONObject getCartItemByIdService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (cartItemsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        CartItems cartItems = CartItems.getCartItemById(id);

        if (cartItemsAuth.isDataMissing(cartItems)) {
            errors.put("CartItemsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject cartItemsObj = new JSONObject();
        cartItemsObj.put("id", cartItems.getId());
        cartItemsObj.put("userId", cartItems.getUserId().getId());
        cartItemsObj.put("partId", cartItems.getPartId().getId());
        cartItemsObj.put("quantity", cartItems.getQuantity());
        cartItemsObj.put("addedAt", cartItems.getAddedAt());
        cartItemsObj.put("isDeleted", cartItems.getIsDeleted());

        toReturn.put("success", true);
        toReturn.put("cartItems", cartItemsObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getCartItemById

    public JSONObject getCartItemsByPartIdService(Integer partId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (cartItemsAuth.isDataMissing(partId)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<CartItems> cartItemsList = CartItems.getCartItemsByPartId(partId);

        if (cartItemsAuth.isDataMissing(cartItemsList)) {
            errors.put("CartItemsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray cartItemsArray = new JSONArray();
        for (CartItems item : cartItemsList) {
            JSONObject cartItemsObj = new JSONObject();
            cartItemsObj.put("id", item.getId());
            cartItemsObj.put("userId", item.getUserId().getId());
            cartItemsObj.put("partId", item.getPartId().getId());
            cartItemsObj.put("quantity", item.getQuantity());
            cartItemsObj.put("addedAt", item.getAddedAt());
            cartItemsObj.put("isDeleted", item.getIsDeleted());

        }

        toReturn.put("success", true);
        toReturn.put("CartItems", cartItemsArray);
        toReturn.put("count", cartItemsList.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getCartItemsByPartId

    public JSONObject getCartItemsByUserIdService(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (cartItemsAuth.isDataMissing(userId)) {
            errors.put("MissingId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<CartItems> cartItemsList = CartItems.getCartItemsByUserId(userId);

        if (cartItemsAuth.isDataMissing(cartItemsList)) {
            errors.put("CartItemsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray cartItemsArray = new JSONArray();
        for (CartItems item : cartItemsList) {
            JSONObject cartItemsObj = new JSONObject();
            cartItemsObj.put("id", item.getId());
            cartItemsObj.put("userId", item.getUserId().getId());
            cartItemsObj.put("partId", item.getPartId().getId());
            cartItemsObj.put("partName", item.getPartId().getName());
            cartItemsObj.put("partPrice", item.getPartId().getPrice());
            cartItemsObj.put("quantity", item.getQuantity());
            cartItemsObj.put("addedAt", item.getAddedAt());
            cartItemsObj.put("isDeleted", item.getIsDeleted());
            cartItemsArray.put(cartItemsObj);
        }

        toReturn.put("success", true);
        toReturn.put("cartItems", cartItemsArray);
        toReturn.put("count", cartItemsList.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }

    public JSONObject softDeleteCartItemService(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        //If id is Missing
        if (cartItemsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        //If id is Invalid
        if (!cartItemsAuth.isDataMissing(id) && !cartItemsAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        //if id is invalid or missing
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        //get data from spq
        CartItems modelResult = CartItems.getCartItemById(id);

        //if spq gives null data
        if (modelResult == null) {
            errors.put("CartItemNotFound");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("CartItemIsSoftDeleted");
        }

        //if parts is soft deleted
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = CartItems.softDeleteCartItem(id);

        if (!result) {
            errors.put("ServerError");
        }

        //if serverError
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted CartItems Succesfully");
        return toReturn;
    }//softDeleteCartItem

    public JSONObject updateCartItemService(CartItems updatedCartItems) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK KERESÉSI PARAMÉTEREK 
        // Ha nincs SEMMILYEN keresési paraméter id 
        if (cartItemsAuth.isDataMissing(updatedCartItems.getId())) {
            errors.put("MissingSearchParameter");
        }

        // Ha id mint keresési paraméter NEM hiányzik ÉS ÉRVÉNYTELEN
        if (!cartItemsAuth.isDataMissing(updatedCartItems.getId())
                && !cartItemsAuth.isValidId(updatedCartItems.getId())) {
            errors.put("InvalidId");
        }

        // Hiba ellenőrzés keresési paraméterek
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // CÍM LEKÉRDEZÉSE 
        CartItems existingCartItems = null;

        // ID alapján keresés
        if (!cartItemsAuth.isDataMissing(updatedCartItems.getId())) {
            existingCartItems = CartItems.getCartItemById(updatedCartItems.getId());
        }

        // Ha nem található a cím
        if (cartItemsAuth.isDataMissing(existingCartItems)) {
            errors.put("CartItemsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        //  MEZŐK MÓDOSÍTÁSA (csak a megadottak!)
        if (!cartItemsAuth.isDataMissing(updatedCartItems.getQuantity())) {
            if (cartItemsAuth.isValidQuantity(updatedCartItems.getQuantity())) {
                existingCartItems.setQuantity(updatedCartItems.getQuantity());
            } else {
                errors.put("InvalidQuantity");
            }
        }

        if (!cartItemsAuth.isDataMissing(updatedCartItems.getIsDeleted())) {
            if (cartItemsAuth.isCartItemsDeleted(updatedCartItems.getIsDeleted())) {
                existingCartItems.setIsDeleted(updatedCartItems.getIsDeleted());
            } else {
                errors.put("InvalidIsDeleted");
            }
        }

        // Hiba ellenőrzés validációk
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // ADATBÁZIS UPDATE 
        try {
            Boolean result = CartItems.updateCartItem(existingCartItems);

            if (!result) {
                errors.put("ServerError");
            }

        } catch (Exception ex) {
            errors.put("DatabaseError");
            ex.printStackTrace();
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // SIKERES VÁLASZ 
        toReturn.put("success", true);
        toReturn.put("message", "CartItems updated successfully");
        toReturn.put("statusCode", 200);

        return toReturn;
    }//updateCartItems

}
