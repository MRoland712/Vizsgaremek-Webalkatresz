/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.vizsgaremek.service;

import com.mycompany.vizsgaremek.model.Reviews;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author neblgergo
 */
public class ReviewsService {

    private final AuthenticationService.reviewsAuth reviewsAuth = new AuthenticationService.reviewsAuth();
    private final AuthenticationService.errorAuth errorAuth = new AuthenticationService.errorAuth();

    public JSONObject createReviews(Reviews createReviews) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓK...
        if (reviewsAuth.isDataMissing(createReviews.getUserId())) {
            errors.put("MissingUserId");
        }
        if (reviewsAuth.isDataMissing(createReviews.getPartId())) {
            errors.put("MissingPartId");
        }

        if (reviewsAuth.isDataMissing(createReviews.getComment())) {
            errors.put("MissingComment");
        }

        if (reviewsAuth.isDataMissing(createReviews.getRating())) {
            errors.put("MissingRating");
        }

        if (!reviewsAuth.isDataMissing(createReviews.getUserId()) && !reviewsAuth.isValidUserId(createReviews.getUserId())) {
            errors.put("InvalidUserId");
        }

        if (!reviewsAuth.isDataMissing(createReviews.getPartId()) && !reviewsAuth.isValidPartId(createReviews.getPartId())) {
            errors.put("InvalidPartId");
        }

        if (!reviewsAuth.isDataMissing(createReviews.getComment()) && !reviewsAuth.isValidComment(createReviews.getComment())) {
            errors.put("InvalidComment");
        }

        if (!reviewsAuth.isDataMissing(createReviews.getRating()) && !reviewsAuth.isValidRating(createReviews.getRating())) {
            errors.put("InvalidRating");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // MODEL HÍVÁS
        if (Reviews.createReviews(createReviews)) {
            toReturn.put("message", "Reviews Created Successfully");
            toReturn.put("statusCode", 201);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Reviews Creation Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // createReviews Closer

    public JSONObject getAllReviews() {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // MODEL HÍVÁS
        ArrayList<Reviews> modelResult = Reviews.getAllReviews();

        // VALIDÁCIÓ - If no data in DB
        if (reviewsAuth.isDataMissing(modelResult)) {
            errors.put("ModelException");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        // KONVERZIÓ ArrayList --> JSONArray
        JSONArray reviewArray = new JSONArray();

        for (Reviews review : modelResult) {
            JSONObject reviewObj = new JSONObject();
            reviewObj.put("id", review.getId());
            reviewObj.put("userId", review.getUserId().getId());
            reviewObj.put("partId", review.getPartId().getId());
            reviewObj.put("rating", review.getRating());
            reviewObj.put("comment", review.getComment());
            reviewObj.put("createdAt", review.getCreatedAt());
            reviewObj.put("isDeleted", review.getIsDeleted());
            reviewObj.put("deletedAt", review.getDeletedAt());

            reviewArray.put(reviewObj);
        }

        toReturn.put("success", true);
        toReturn.put("Reviews", reviewArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getAllReviews

    public JSONObject getReviewsById(Integer Id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (reviewsAuth.isDataMissing(Id)) {
            errors.put("MissingId");
        }

        // If modelexeption
        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        Reviews review = Reviews.getReviewsById(Id);

        if (reviewsAuth.isDataMissing(review)) {
            errors.put("ReviewsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONObject reviewObj = new JSONObject();
        reviewObj.put("id", review.getId());
        reviewObj.put("userId", review.getUserId().getId());
        reviewObj.put("partId", review.getPartId().getId());
        reviewObj.put("rating", review.getRating());
        reviewObj.put("comment", review.getComment());
        reviewObj.put("createdAt", review.getCreatedAt());
        reviewObj.put("isDeleted", review.getIsDeleted());
        reviewObj.put("deletedAt", review.getDeletedAt());

        toReturn.put("success", true);
        toReturn.put("Reviews", reviewObj);
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getReviewsById

    public JSONObject getReviewsByPartId(Integer partId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (reviewsAuth.isDataMissing(partId)) {
            errors.put("MissingPartId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<Reviews> modelResult = Reviews.getReviewsByPartId(partId);

        if (reviewsAuth.isDataMissing(modelResult)) {
            errors.put("ReviewsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray reviewArray = new JSONArray();

        for (Reviews review : modelResult) {
            JSONObject reviewObj = new JSONObject();
            reviewObj.put("id", review.getId());
            reviewObj.put("userId", review.getUserId().getId());
            reviewObj.put("partId", review.getPartId().getId());
            reviewObj.put("rating", review.getRating());
            reviewObj.put("comment", review.getComment());
            reviewObj.put("createdAt", review.getCreatedAt());
            reviewObj.put("isDeleted", review.getIsDeleted());
            reviewObj.put("deletedAt", review.getDeletedAt());

            reviewArray.put(reviewObj);
        }

        toReturn.put("success", true);
        toReturn.put("Reviews", reviewArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getReviewsByPartId

    public JSONObject getReviewsByUserId(Integer userId) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (reviewsAuth.isDataMissing(userId)) {
            errors.put("MissingUserId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<Reviews> modelResult = Reviews.getReviewsByUserId(userId);

        if (reviewsAuth.isDataMissing(modelResult)) {
            errors.put("ReviewsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray reviewArray = new JSONArray();

        for (Reviews review : modelResult) {
            JSONObject reviewObj = new JSONObject();
            reviewObj.put("id", review.getId());
            reviewObj.put("userId", review.getUserId().getId());
            reviewObj.put("partId", review.getPartId().getId());
            reviewObj.put("rating", review.getRating());
            reviewObj.put("comment", review.getComment());
            reviewObj.put("createdAt", review.getCreatedAt());
            reviewObj.put("isDeleted", review.getIsDeleted());
            reviewObj.put("deletedAt", review.getDeletedAt());

            reviewArray.put(reviewObj);
        }

        toReturn.put("success", true);
        toReturn.put("Reviews", reviewArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getReviewsByUserId

    public JSONObject getReviewsByRating(Integer rating) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        if (reviewsAuth.isDataMissing(rating)) {
            errors.put("MissingRating");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        ArrayList<Reviews> modelResult = Reviews.getReviewsByRating(rating);

        if (reviewsAuth.isDataMissing(modelResult)) {
            errors.put("ReviewsNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        JSONArray reviewArray = new JSONArray();

        for (Reviews review : modelResult) {
            JSONObject reviewObj = new JSONObject();
            reviewObj.put("id", review.getId());
            reviewObj.put("userId", review.getUserId().getId());
            reviewObj.put("partId", review.getPartId().getId());
            reviewObj.put("rating", review.getRating());
            reviewObj.put("comment", review.getComment());
            reviewObj.put("createdAt", review.getCreatedAt());
            reviewObj.put("isDeleted", review.getIsDeleted());
            reviewObj.put("deletedAt", review.getDeletedAt());

            reviewArray.put(reviewObj);
        }

        toReturn.put("success", true);
        toReturn.put("Reviews", reviewArray);
        toReturn.put("count", modelResult.size());
        toReturn.put("statusCode", 200);

        return toReturn;
    }//getReviewsByRating

    public JSONObject updateReviews(Reviews updatedReviews) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // VALIDÁCIÓ

        if (!reviewsAuth.isDataMissing(updatedReviews.getId()) && !reviewsAuth.isValidId(updatedReviews.getId())) {
            errors.put("InvalidId");
        }

   
        if (!reviewsAuth.isDataMissing(updatedReviews.getRating()) && !reviewsAuth.isValidRating(updatedReviews.getRating())) {
            errors.put("InvalidRating");
        }

    
        if (!reviewsAuth.isDataMissing(updatedReviews.getComment()) && !reviewsAuth.isValidComment(updatedReviews.getComment())) {
            errors.put("InvalidComment");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // Ellenőrzés - létezik-e
        Reviews existingReview = Reviews.getReviewsById(updatedReviews.getId());

        if (reviewsAuth.isDataMissing(existingReview)) {
            errors.put("ReviewNotFound");
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (existingReview.getIsDeleted() == true) {
            errors.put("ReviewIsDeleted");
            return errorAuth.createErrorResponse(errors, 409);
        }
        
        // MEZŐK MÓDOSÍTÁSA (csak a megadottak!)

        if (!reviewsAuth.isDataMissing(updatedReviews.getRating())) {
            if (reviewsAuth.isValidRating(updatedReviews.getRating())) {
                existingReview.setRating(updatedReviews.getRating());
            } else {
                errors.put("InvalidRating");
            }
        }
        
        if (!reviewsAuth.isDataMissing(updatedReviews.getComment())) {
            if (reviewsAuth.isValidComment(updatedReviews.getComment())) {
                existingReview.setComment(updatedReviews.getComment());
            } else {
                errors.put("InvalidComment");
            }
        }
        
        if (!reviewsAuth.isDataMissing(updatedReviews.getIsDeleted())) {
            if (reviewsAuth.isReviewsDeleted(updatedReviews.getIsDeleted())) {
                existingReview.setIsDeleted(updatedReviews.getIsDeleted());
            } else {
                errors.put("InvalidIsDeleted");
            }
        }

        
        // MODEL HÍVÁS
        if (Reviews.updateReviews(existingReview)) {
            toReturn.put("message", "Review Updated Successfully");
            toReturn.put("statusCode", 200);
            toReturn.put("success", true);
            return toReturn;
        } else {
            JSONObject error = new JSONObject();
            error.put("message", "Review Update Failed");
            error.put("statusCode", 500);
            error.put("success", false);
            return error;
        }
    } // updateReviews Closer

    public JSONObject softDeleteReviews(Integer id) {
        JSONObject toReturn = new JSONObject();
        JSONArray errors = new JSONArray();

        // Validáció - ID hiányzik
        if (reviewsAuth.isDataMissing(id)) {
            errors.put("MissingId");
        }

        // Validáció - ID invalid
        if (!reviewsAuth.isDataMissing(id) && !reviewsAuth.isValidId(id)) {
            errors.put("InvalidId");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 400);
        }

        // Ellenőrzés - létezik-e
        Reviews modelResult = Reviews.getReviewsById(id);

        if (modelResult == null) {
            errors.put("ReviewsNotFound");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 404);
        }

        if (modelResult.getIsDeleted() == true) {
            errors.put("ReviewIsDeleted");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 409);
        }

        Boolean result = Reviews.softDeleteReviews(id);

        if (!result) {
            errors.put("ServerError");
        }

        if (errorAuth.hasErrors(errors)) {
            return errorAuth.createErrorResponse(errors, 500);
        }

        toReturn.put("status", "success");
        toReturn.put("statusCode", 200);
        toReturn.put("Message", "Deleted Review Successfully");
        return toReturn;
    } // softDeleteReviews Closer

}
