package com.rocketFoodDelivery.rocketFood.controller.api;

import com.rocketFoodDelivery.rocketFood.dtos.ApiCreateRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiRestaurantDTO;
import com.rocketFoodDelivery.rocketFood.dtos.ApiErrorDTO;
import com.rocketFoodDelivery.rocketFood.service.RestaurantService;
import com.rocketFoodDelivery.rocketFood.util.ResponseBuilder;
import com.rocketFoodDelivery.rocketFood.exception.*;
import com.rocketFoodDelivery.rocketFood.dtos.ApiDeleteRestaurantResponseDTO;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class RestaurantApiController {
    private RestaurantService restaurantService;

    @Autowired
    public RestaurantApiController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

     /**
     * Returns a list of restaurants given a rating and price range(GET).
     *
     * @param rating integer from 1 to 5 (optional)
     * @param priceRange integer from 1 to 3 (optional)
     * @return A list of restaurants that match the specified criteria
     * 
     * @see RestaurantService#findRestaurantsByRatingAndPriceRange(Integer, Integer) for details on retrieving restaurant information.
     */

    // @GetMapping("/api/restaurants")
    // public ResponseEntity<Object> getAllRestaurants(
    //     @RequestParam(name = "rating", required = false) Integer rating,
    //     @RequestParam(name = "price_range", required = false) Integer priceRange) {
    //     return ResponseBuilder.buildOkResponse(restaurantService.findRestaurantsByRatingAndPriceRange(rating, priceRange));
    // }


    // /**
    //  * Retrieves details for a restaurant, including its average rating, based on the provided restaurant ID.(GET)
    //  *
    //  * @param id The unique identifier of the restaurant to retrieve.
    //  * @return ResponseEntity with HTTP 200 OK if the restaurant is found, HTTP 404 Not Found otherwise.
    //  *
    //  * @see RestaurantService#findRestaurantWithAverageRatingById(int) for details on retrieving restaurant information.
    //  */
    // @GetMapping("/api/restaurants/{id}")
    // public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
    //     Optional<ApiRestaurantDTO> restaurantWithRatingOptional = restaurantService.findRestaurantWithAverageRatingById(id);
    //     if (!restaurantWithRatingOptional.isPresent()) throw new ResourceNotFoundException(String.format("Restaurant with id %d not found", id));
    //     return ResponseBuilder.buildOkResponse(restaurantWithRatingOptional.get());
    // }


    // /**
    //  * Creates a new restaurant. (POST)
    //  *
    //  * @param restaurant The data for the new restaurant.
    //  * @return ResponseEntity with the created restaurant's data, or a BadRequestException if creation fails.
    //  */
    // @PostMapping("/api/restaurants")
    // public ResponseEntity<Object> createRestaurant(@RequestBody @Valid ApiCreateRestaurantDTO restaurant, BindingResult result) {
    //     // Check for validation errors first
    //     if (result.hasErrors()) {
    //         throw new BadRequestException("Invalid or missing parameters for restaurant creation");
    //     }        
    //     try {
    //         Optional<ApiCreateRestaurantDTO> createdRestaurant = restaurantService.createRestaurant(restaurant);
    //         if (createdRestaurant.isPresent()) {
    //             return ResponseBuilder.buildCreatedResponse(createdRestaurant.get());
    //         } else {
    //             throw new BadRequestException("Failed to create restaurant. User may not exist or invalid data provided");
    //         }
    //     } catch (Exception e) {
    //         throw new BadRequestException("Error creating restaurant: " + e.getMessage());
    //     }
    // }


    // /**
    //  * Updates an existing restaurant by ID.(PUT)
    //  *
    //  * @param id                    The ID of the restaurant to update.
    //  * @param restaurantUpdateData  The updated data for the restaurant.
    //  * @param result                BindingResult for validation.
    //  * @return ResponseEntity with the updated restaurant's data
    //  */
    // @PutMapping("/api/restaurants/{id}")
    // public ResponseEntity<Object> updateRestaurant(
    //     @PathVariable("id") int id, 
    //     @Validated(ApiCreateRestaurantDTO.UpdateValidation.class) @RequestBody ApiCreateRestaurantDTO restaurantUpdateData, 
    //     BindingResult result) {
        
    //     if (result.hasErrors()) {
    //         // Create detailed validation error message
    //         StringBuilder errorDetails = new StringBuilder();
    //         result.getFieldErrors().forEach(error -> {
    //             errorDetails.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
    //         });
    //         throw new ValidationException(errorDetails.toString().trim());
    //     }        
        
    //     try {
    //         Optional<ApiCreateRestaurantDTO> updatedRestaurant = restaurantService.updateRestaurant(id, restaurantUpdateData);
            
    //         if (updatedRestaurant.isPresent()) {
    //             return ResponseBuilder.buildOkResponse(updatedRestaurant.get());
    //         } else {
    //             throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
    //         }
    //     } catch (ResourceNotFoundException e) {
    //         throw e;  // Re-throw to be handled by GlobalExceptionHandler
    //     } catch (Exception e) {
    //         throw new BadRequestException("Error updating restaurant: " + e.getMessage());
    //     }
    // }


    /**
     * Deletes a restaurant by ID.(DELETE)
     *
     * @param id The ID of the restaurant to delete.
     * @return ResponseEntity with the deleted restaurant's data, or a ResourceNotFoundException if the restaurant is not found.
     */
    @DeleteMapping("/api/restaurants/{id}")
    public ResponseEntity<Object> deleteRestaurant(@PathVariable int id) {
        try {
            Optional<ApiDeleteRestaurantResponseDTO> deletedRestaurant = restaurantService.deleteRestaurant(id);
            if (deletedRestaurant.isPresent()) {
                return ResponseBuilder.buildOkResponse(deletedRestaurant.get());
            } else {
                throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Restaurant with id " + id + " not found");
        }
    }

}



